"""Command line: oldfolk-build {search,sample,fetch,build}.

Typical use:

    oldfolk-build search                 # find candidate items (cheap)
    oldfolk-build sample --n 200         # pick a random test sample
    oldfolk-build fetch --sample         # download metadata for the sample only
    oldfolk-build build --sample         # classify + write shards to out-sample/

    oldfolk-build fetch                  # full run; safe to interrupt and re-run
    oldfolk-build build                  # writes out/
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


def cmd_build(args, rules, store):
    out = Path(args.out) if args.out else BUILDER_DIR / ("out-sample" if args.sample else "out")
    results = [classify_item(i, kind, data, rules)
               for i, _src, kind, data in store.fetched(sample_only=args.sample)]
    if not results:
        sys.exit("no fetched items - run `fetch` first")
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

    s = sub.add_parser("status", help="show cache statistics")
    s.add_argument("--require-complete", action="store_true",
                   help="exit 1 if some items still need fetching")

    args = p.parse_args(argv)
    logging.basicConfig(level=logging.DEBUG if args.verbose else logging.INFO,
                        format="%(asctime)s %(levelname)s %(message)s", datefmt="%H:%M:%S")
    rules = load_rules()
    store = Store(Path(args.db))
    {"search": cmd_search, "sample": cmd_sample, "fetch": cmd_fetch,
     "build": cmd_build, "status": cmd_status}[args.cmd](args, rules, store)


if __name__ == "__main__":
    main()
