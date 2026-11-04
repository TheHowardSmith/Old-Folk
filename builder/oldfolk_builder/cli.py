"""Command line: oldfolk-build {search,sample,fetch,build}.

Typical use:

    oldfolk-build search                 # find candidate items (cheap)
    oldfolk-build sample --n 200         # pick a random test sample
    oldfolk-build fetch --sample         # download metadata for the sample only
    oldfolk-build build --sample         # classify + write shards to out-sample/

    oldfolk-build fetch                  # full run; safe to interrupt and re-run
    oldfolk-build build                  # writes out/

    oldfolk-build ext-fetch              # harvest loc.gov (rules/loc.yaml); build includes it
"""
from __future__ import annotations

import argparse
import json
import logging
import random
import sys
from pathlib import Path

from .classify import classify_item
from .config import BUILDER_DIR, load_rules
from .output import SHARD_SIZE, write_all
from .store import Store

log = logging.getLogger("oldfolk")
DEFAULT_DB = BUILDER_DIR / "cache" / "oldfolk.sqlite"


def cmd_search(args, rules, store):
    from .iaclient import make_session, search_ids
    session = make_session()
    for src in rules.sources:
        if args.only and src["name"] not in args.only:
            continue
        log.info("searching %s ...", src["name"])
        ids = search_ids(session, " ".join(src["query"].split()))
        new = store.add_hits(src["name"], src.get("kind", "other"), ids)
        log.info("  %d hits, %d new", len(ids), new)
    log.info("cache: %s", store.stats())


def cmd_sample(args, rules, store):
    """Stratified random sample: every source gets at least `min_per_source`."""
    rng = random.Random(args.seed)
    by_source = store.ids_by_source()
    total = sum(len(v) for v in by_source.values())
    if not total:
        sys.exit("nothing found yet - run `search` first")
    picked: list[str] = []
    floor = min(args.min_per_source, args.n // max(1, len(by_source)))
    for source, ids in by_source.items():
        share = max(floor, round(args.n * len(ids) / total))
        picked += rng.sample(ids, min(share, len(ids)))
    rng.shuffle(picked)
    picked = picked[: args.n]
    store.mark_sample(picked)
    log.info("sampled %d items: %s", len(picked),
             {s: sum(1 for i in picked if i in set(v)) for s, v in by_source.items()})


def cmd_fetch(args, rules, store):
    from .iaclient import fetch_many, make_session
    todo = store.unfetched(sample_only=args.sample)
    if args.limit:
        todo = todo[: args.limit]
    log.info("%d items to fetch (%s)", len(todo), "sample" if args.sample else "all")
    fetch_many(make_session(), todo, store.save_meta, store.save_error,
               workers=args.workers, pause=args.pause)
    log.info("cache: %s", store.stats())


def cmd_ext_fetch(args, rules, store):
    from . import dsal, finna, ims, loc
    if args.source == "ims":      # slow (5 s per page): its own cache file and workflow
        ims_store = Store(Path(args.ims_db))
        print(json.dumps({"ims": ims.harvest(ims_store, ims.load_config(), minutes=args.minutes,
                                             limit=args.limit)}))
        return
    if args.source in ("finna", "all"):
        print(json.dumps({"finna": finna.harvest(store, finna.load_config())}))
    if args.source in ("dsal", "all"):
        print(json.dumps({"dsal": dsal.harvest(store, dsal.load_config())}))
    if args.source in ("loc", "all"):
        cfg = loc.load_config()
        if args.only:
            cfg.collections = [c for c in cfg.collections if c["slug"] in args.only]
        print(json.dumps({"loc": loc.harvest(store, cfg, details=not args.no_details, limit=args.limit)}))


def with_subjects(data: dict, subjects: list[str] | None) -> dict:
    """Add a source's collection-level subjects (sources.yaml `add_subjects`)."""
    if not subjects:
        return data
    meta = dict(data.get("metadata") or {})
    have = meta.get("subject") or []
    meta["subject"] = ([have] if isinstance(have, str) else list(have)) + subjects
    return {**data, "metadata": meta}


def apply_availability(results, path: Path) -> int:
    """Drop tracks found on sale or streaming (rights-checks.yml); returns how many."""
    from .availability import load
    from .classify import EXCLUDE
    found = {k for k, r in load(path).items() if r.get("found")}
    if not found:
        return 0
    dropped = 0
    for r in results:
        if r.status == EXCLUDE or not r.tracks:
            continue
        keep = [t for t in r.tracks if f"{t.id}/{t.file}" not in found]
        if len(keep) < len(r.tracks):
            dropped += len(r.tracks) - len(keep)
            if not keep:
                r.status, r.reasons = EXCLUDE, ["on sale or streaming today (iTunes)"]
            r.tracks = keep
    return dropped


def cmd_availability(args, rules, store):
    from . import availability
    print(json.dumps(availability.run(Path(args.tracklist), Path(args.out), args.shard, args.of,
                                      Path(args.previous) if args.previous else None, args.minutes)))


def cmd_availability_merge(args, rules, store):
    from . import availability
    print(json.dumps(availability.merge([Path(p) for p in args.parts], Path(args.out))))


def cmd_writer_search(args, rules, store):
    from datetime import date
    from . import writer_search
    cutoff = date.today().year - rules.writer_life_plus - 1
    print(json.dumps(writer_search.run(Path(args.checks), Path(args.out), cutoff, date.today().year,
                                       args.minutes, args.shard, args.of,
                                       Path(args.previous) if args.previous else None)))


def cmd_writer_deaths(args, rules, store):
    from . import deaths
    names = deaths.collect(store, rules)
    print(json.dumps(deaths.lookup_all(names, Path(args.previous) if args.previous else None,
                                       Path(args.out), args.minutes)))


def cmd_build(args, rules, store):
    out = Path(args.out) if args.out else BUILDER_DIR / ("out-sample" if args.sample else "out")
    extra = {s["name"]: list(s.get("add_subjects") or []) for s in rules.sources}
    results = [classify_item(i, kind, with_subjects(data, extra.get(src)), rules)
               for i, src, kind, data in store.fetched(sample_only=args.sample)]
    if not results:
        sys.exit("no fetched items - run `fetch` first")
    if not args.no_ext:
        from . import dsal, finna, loc
        for name, mod in (("loc.gov", loc), ("finna", finna), ("dsal", dsal)):
            ext = mod.classify_all(store, rules)
            log.info("%s: %d items in the cache", name, len(ext))
            results += ext
        from . import ims
        if Path(args.ims_db).exists():
            ext = ims.classify_all(Store(Path(args.ims_db)), rules)
            log.info("discografia brasileira: %d items in the cache", len(ext))
            results += ext
    dropped = apply_availability(results, BUILDER_DIR / "cache-rights" / "availability.jsonl")
    if dropped:
        log.info("dropped %d tracks that are on sale or streaming today", dropped)
    summary = write_all(results, out, rules.genres, version=args.version,
                        shard_size=args.shard_size)
    print(json.dumps(summary, indent=1, ensure_ascii=False))
    log.info("wrote %s", out)


def cmd_status(args, rules, store):
    stats = store.stats()
    remaining = len(store.unfetched(sample_only=False))
    stats["remaining"] = remaining
    print(json.dumps(stats))
    if args.require_complete and remaining:
        sys.exit(1)


def main(argv=None):
    p = argparse.ArgumentParser(prog="oldfolk-build", description=__doc__,
                                formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("--db", default=str(DEFAULT_DB), help="SQLite cache path")
    p.add_argument("-v", "--verbose", action="store_true")
    sub = p.add_subparsers(dest="cmd", required=True)

    s = sub.add_parser("search", help="find candidate items")
    s.add_argument("--only", nargs="*", help="source names to run")

    s = sub.add_parser("sample", help="mark a random sample of found items")
    s.add_argument("--n", type=int, default=200)
    s.add_argument("--seed", type=int, default=1)
    s.add_argument("--min-per-source", type=int, default=40)

    s = sub.add_parser("fetch", help="download item metadata into the cache")
    s.add_argument("--sample", action="store_true", help="only the sample")
    s.add_argument("--limit", type=int, help="stop after N items (for testing)")
    s.add_argument("--workers", type=int, default=2, help="parallel requests (max 2)")
    s.add_argument("--pause", type=float, default=0.25, help="seconds between requests per worker")

    s = sub.add_parser("build", help="classify cached items and write shards")
    s.add_argument("--sample", action="store_true", help="only the sample")
    s.add_argument("--out", help="output directory")
    s.add_argument("--version", type=int, help="list version (default: UTC yyyymmddHHMM)")
    s.add_argument("--shard-size", type=int, default=SHARD_SIZE)
    s.add_argument("--no-ext", action="store_true", help="leave out other archives (loc.gov, finna, IMS)")
    s.add_argument("--ims-db", default=str(BUILDER_DIR / "cache-ims" / "ims.sqlite"))

    s = sub.add_parser("availability", help="check listed tracks against the iTunes catalogue")
    s.add_argument("--tracklist", required=True, help="folder with a published index.json and s-*.json")
    s.add_argument("--out", required=True)
    s.add_argument("--previous", help="earlier results; tracks in it are skipped")
    s.add_argument("--shard", type=int, default=0)
    s.add_argument("--of", type=int, default=1)
    s.add_argument("--minutes", type=float)

    s = sub.add_parser("availability-merge", help="combine availability result files")
    s.add_argument("--out", required=True)
    s.add_argument("parts", nargs="+")

    s = sub.add_parser("writer-deaths", help="look up death years of credited writers")
    s.add_argument("--out", required=True)
    s.add_argument("--previous")
    s.add_argument("--minutes", type=float)

    s = sub.add_parser("writer-search", help="look up unconfirmed writers' songs on MusicBrainz (checks.csv)")
    s.add_argument("--checks", required=True, help="checks.csv from a build")
    s.add_argument("--out", required=True)
    s.add_argument("--previous")
    s.add_argument("--shard", type=int, default=0)
    s.add_argument("--of", type=int, default=1)
    s.add_argument("--minutes", type=float)

    s = sub.add_parser("ext-fetch", help="harvest other archives (loc.gov, finna.fi) into the cache")
    s.add_argument("--source", choices=("all", "loc", "finna", "dsal", "ims"), default="all",
                   help="'all' = loc + finna + dsal; 'ims' (Discografia Brasileira) runs on its own, slowly")
    s.add_argument("--minutes", type=float, help="ims: stop after this many minutes")
    s.add_argument("--ims-db", default=str(BUILDER_DIR / "cache-ims" / "ims.sqlite"))
    s.add_argument("--only", nargs="*", help="loc.gov collection slugs to harvest")
    s.add_argument("--no-details", action="store_true",
                   help="skip the per-item requests for items whose search record lacks the MP3")
    s.add_argument("--limit", type=int, help="at most N per-item requests (for testing)")

    s = sub.add_parser("status", help="show cache statistics")
    s.add_argument("--require-complete", action="store_true",
                   help="exit 1 if some items still need fetching")

    args = p.parse_args(argv)
    logging.basicConfig(level=logging.DEBUG if args.verbose else logging.INFO,
                        format="%(asctime)s %(levelname)s %(message)s", datefmt="%H:%M:%S")
    rules = load_rules()
    store = Store(Path(args.db))
    {"search": cmd_search, "sample": cmd_sample, "fetch": cmd_fetch,
     "build": cmd_build, "status": cmd_status, "ext-fetch": cmd_ext_fetch,
     "availability": cmd_availability, "availability-merge": cmd_availability_merge,
     "writer-deaths": cmd_writer_deaths, "writer-search": cmd_writer_search}[args.cmd](args, rules, store)


if __name__ == "__main__":
    main()
