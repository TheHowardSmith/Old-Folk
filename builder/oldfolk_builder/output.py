"""Write sharded JSON, the index, and the human-review reports."""
from __future__ import annotations

import collections
import csv
import hashlib
import json
from datetime import datetime, timezone
from pathlib import Path

from .classify import EXCLUDE, FLAG, INCLUDE, ItemResult, Track

SHARD_SIZE = 5000
SCHEMA_VERSION = 1


def track_json(t: Track) -> dict:
    """Short field names; empty fields omitted."""
    d = {"i": t.id, "f": t.file, "t": t.title}
    if t.artist:
        d["a"] = t.artist
    if t.year:
        d["y"] = t.year
    d["r"] = t.region
    d["g"] = t.genre
    return d


def _write_json(path: Path, obj) -> str:
    data = json.dumps(obj, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
    path.write_bytes(data)
    return hashlib.sha256(data).hexdigest()


def write_shards(tracks: list[Track], out: Path, prefix: str, genres: dict[str, str],
                 shard_size: int = SHARD_SIZE) -> list[dict]:
    by_genre: dict[str, list[Track]] = collections.defaultdict(list)
    for t in tracks:
        by_genre[t.genre].append(t)
    entries = []
    for genre in genres:  # stable, display order
        items = sorted(by_genre.get(genre, []), key=lambda t: (t.id, t.file))
        for n, start in enumerate(range(0, len(items), shard_size), start=1):
            chunk = items[start:start + shard_size]
            name = f"{prefix}-{genre}-{n}.json"
            sha = _write_json(out / name, {"v": SCHEMA_VERSION, "g": genre,
                                           "t": [track_json(t) for t in chunk]})
            entries.append({"f": name, "g": genre, "n": len(chunk), "sha256": sha,
                            "bytes": (out / name).stat().st_size})
    return entries


def write_all(results: list[ItemResult], out: Path, genres: dict[str, str],
              version: int | None = None, shard_size: int = SHARD_SIZE) -> dict:
    out.mkdir(parents=True, exist_ok=True)
    for old in list(out.glob("s-*.json")) + list(out.glob("r-*.json")):
        old.unlink()
    version = version or int(datetime.now(timezone.utc).strftime("%Y%m%d%H%M"))

    included = [t for r in results if r.status == INCLUDE for t in r.tracks]
    review = [t for r in results if r.status == FLAG for t in r.tracks]
    main_entries = write_shards(included, out, "s", genres, shard_size)
    review_entries = write_shards(review, out, "r", genres, shard_size)

    counts = collections.Counter(t.genre for t in included)
    index = {
        "v": version,
        "schema": SCHEMA_VERSION,
        "built": datetime.now(timezone.utc).isoformat(timespec="seconds"),
        "n": len(included),
        "genres": [{"k": g, "name": name, "n": counts.get(g, 0)} for g, name in genres.items()
                   if counts.get(g, 0)],
        "shards": main_entries,
        "review": review_entries,
        "url": "https://archive.org/download/{i}/{f}",
    }
    (out / "index.json").write_text(json.dumps(index, ensure_ascii=False, indent=1), "utf-8")

    write_reports(results, out)
    summary = summarize(results)
    summary["version"] = version
    write_markdown_report(results, summary, out)
    (out / "summary.json").write_text(json.dumps(summary, ensure_ascii=False, indent=1), "utf-8")
    return summary


def write_reports(results: list[ItemResult], out: Path) -> None:
    flagged = [r for r in results if r.status == FLAG]
    with open(out / "flagged.csv", "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["id", "url", "genre", "region", "year", "year_note", "tracks", "title",
                    "artist", "writers", "reasons", "decision (include/exclude)"])
        for r in sorted(flagged, key=lambda r: (r.reasons[0] if r.reasons else "", r.id)):
            w.writerow([r.id, f"https://archive.org/details/{r.id}", r.genre, r.region,
                        r.year or "", r.year_note or "", len(r.tracks), r.title, r.artist or "",
                        "; ".join(r.writers), " || ".join(r.reasons), ""])

    # Writers, most frequent first, so whole writers can be approved at once.
    writer_items: dict[str, list[str]] = collections.defaultdict(list)
    for r in flagged:
        for wname in r.writers:
            writer_items[wname].append(r.id)
    with open(out / "writers.csv", "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["writer", "flagged_items", "example_item", "died (fill in)", "approve? (y/n)"])
        for wname, ids in sorted(writer_items.items(), key=lambda kv: (-len(kv[1]), kv[0])):
            w.writerow([wname, len(ids), f"https://archive.org/details/{ids[0]}", "", ""])

    with open(out / "excluded.csv", "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["id", "reason", "title", "artist"])
        for r in sorted((r for r in results if r.status == EXCLUDE),
                        key=lambda r: (r.reasons[0], r.id)):
            w.writerow([r.id, r.reasons[0], r.title, r.artist or ""])


def _reason_key(reason: str) -> str:
    return reason.split(":")[0].strip()


def summarize(results: list[ItemResult]) -> dict:
    status = collections.Counter(r.status for r in results)
    tracks = collections.Counter()
    for r in results:
        tracks[r.status] += len(r.tracks)
    inc = [t for r in results if r.status == INCLUDE for t in r.tracks]
    return {
        "items": dict(status),
        "tracks": dict(tracks),
        "included_by_genre": dict(collections.Counter(t.genre for t in inc).most_common()),
        "included_by_region": dict(collections.Counter(t.region for t in inc).most_common()),
        "included_undated": sum(1 for t in inc if not t.year),
        "flag_reasons": dict(collections.Counter(
            _reason_key(x) for r in results if r.status == FLAG for x in r.reasons).most_common()),
        "exclude_reasons": dict(collections.Counter(
            _reason_key(r.reasons[0]) for r in results if r.status == EXCLUDE).most_common()),
    }


def _md(text) -> str:
    return str(text or "").replace("|", "/").replace("\n", " ")[:70]


def write_markdown_report(results: list[ItemResult], summary: dict, out: Path,
                          max_rows: int = 400) -> None:
    """A readable overview of one build (report.md)."""
    lines = [f"# Track list build {summary['version']}", ""]
    lines += ["| | items | tracks |", "|---|---:|---:|"]
    for st in (INCLUDE, FLAG, EXCLUDE):
        lines.append(f"| {st} | {summary['items'].get(st, 0)} | {summary['tracks'].get(st, 0)} |")
    lines += ["", f"Included tracks with no date (undated 78s): {summary['included_undated']}", ""]
    for title, key in (("Included tracks by genre", "included_by_genre"),
                       ("Included tracks by region", "included_by_region"),
                       ("Flag reasons (items)", "flag_reasons"),
                       ("Exclude reasons (items)", "exclude_reasons")):
        lines += [f"## {title}", "", "| | count |", "|---|---:|"]
        lines += [f"| {_md(k)} | {v} |" for k, v in summary[key].items()]
        lines.append("")

    def table(rows, header, fmt):
        cols = header.strip().strip("|").split("|")
        lines.extend([header, "|" + "|".join("---" for _ in cols) + "|"])
        for r in rows[:max_rows]:
            lines.append(fmt(r))
        if len(rows) > max_rows:
            lines.append(f"\n... and {len(rows) - max_rows} more (see the CSV files)")
        lines.append("")

    inc = [r for r in results if r.status == INCLUDE]
    lines += ["## Included items", ""]
    table(inc, "| year | genre | region | tracks | title | artist |",
          lambda r: f"| {r.year or '78 (undated)'} | {r.genre} | {r.region} | {len(r.tracks)} | "
                    f"[{_md(r.title)}](https://archive.org/details/{r.id}) | {_md(r.artist)} |")
    flg = [r for r in results if r.status == FLAG]
    lines += ["## Flagged items (review set)", ""]
    table(flg, "| year | genre | title | reasons |",
          lambda r: f"| {r.year or '-'} | {r.genre} | "
                    f"[{_md(r.title)}](https://archive.org/details/{r.id}) | "
                    f"{_md(' / '.join(r.reasons))} |")
    exc = [r for r in results if r.status == EXCLUDE]
    lines += ["## Excluded items", ""]
    table(exc, "| reason | title |",
          lambda r: f"| {_md(r.reasons[0])} | "
                    f"[{_md(r.title)}](https://archive.org/details/{r.id}) |")
    (out / "report.md").write_text("\n".join(lines), "utf-8")
