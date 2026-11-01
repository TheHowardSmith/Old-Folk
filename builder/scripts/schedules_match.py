#!/usr/bin/env python3
"""Match 78s against the US Pre-1972 Sound Recording Schedules index.

A recording on a schedule is claimed by a rights owner (usually a major label)
under US law. Matching is by normalised title plus a shared performer word
(surname or group name); a title-only hit is reported separately because
common titles ("Home Sweet Home") match many unrelated recordings.

Usage:
  python scripts/schedules_match.py --schedules cache/pre1972_schedules.jsonl \
      --items items.jsonl [--rights rights_check.csv] --out schedules_match.csv
"""
from __future__ import annotations

import argparse
import csv
import json
import sys
from collections import Counter, defaultdict
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from oldfolk_builder.classify import as_text, parse_year  # noqa: E402

from oldfolk_builder.schedules import ScheduleIndex  # noqa: E402


def load_index(path: Path) -> ScheduleIndex:
    return ScheduleIndex.load(path)


def match(title: str, creator: str, index: ScheduleIndex) -> tuple[str, str]:
    return index.match(title, creator)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--schedules", required=True)
    ap.add_argument("--items", required=True, help="JSONL rows with identifier, title, creator, date")
    ap.add_argument("--rights", help="rights_check.csv to extend with the schedule result")
    ap.add_argument("--out", default="schedules_match.csv")
    args = ap.parse_args()

    index = load_index(Path(args.schedules))
    print(f"index: {sum(len(v) for v in index.by_title.values())} title entries, {len(index.by_title)} distinct titles")
    rows = {}
    for line in open(args.items):
        r = json.loads(line)
        rows[r["identifier"]] = r

    results = {}
    for ident, r in rows.items():
        results[ident] = match(as_text(r.get("title")), as_text(r.get("creator")), index)

    # Whole candidate set, by recording year band.
    def band(r):
        y, _ = parse_year(r)
        if y is None:
            return "undated"
        return "<=1925 (US public domain)" if y <= 1925 else ("1926-1946" if y <= 1946 else "1947+")
    table = defaultdict(Counter)
    for ident, (claimed, _) in results.items():
        table[band(rows[ident])][claimed] += 1
    total = Counter(c for c, _ in results.values())
    n = len(results)
    print(f"\nall {n} candidate 78s: " + ", ".join(f"{k}: {v} ({100 * v / n:.1f}%)" for k, v in total.most_common()))
    for b in ("<=1925 (US public domain)", "1926-1946", "1947+", "undated"):
        c = table[b]
        t = sum(c.values()) or 1
        print(f"  {b:26} {t:6} items  claimed {c['yes']:5} ({100 * c['yes'] / t:.1f}%)  title-only {c['title only']:5}")
    owners = Counter(o for c, o in results.values() if c == "yes")
    print("claimed by:", ", ".join(f"{o} {v}" for o, v in owners.most_common(8)))

    with open(args.out, "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["id", "title", "creator", "recorded", "us_recording_claimed", "claimed_by"])
        for ident, (claimed, owner) in results.items():
            r = rows[ident]
            y, _ = parse_year(r)
            w.writerow([ident, as_text(r.get("title")), as_text(r.get("creator")), y or "", claimed, owner])

    if args.rights:
        rc = list(csv.DictReader(open(args.rights, encoding="utf-8")))
        for row in rc:
            claimed, owner = results.get(row["id"], ("not checked", ""))
            row["us_recording_claimed"] = claimed
            row["claimed_by"] = owner
        out = Path(args.rights).with_name(Path(args.rights).stem + "_with_schedules.csv")
        with open(out, "w", newline="", encoding="utf-8") as fh:
            w = csv.DictWriter(fh, fieldnames=list(rc[0]))
            w.writeheader()
            w.writerows(rc)
        c = Counter(r["us_recording_claimed"] for r in rc)
        print(f"\nwriter-credited sample ({len(rc)}): " + ", ".join(f"{k}: {v}" for k, v in c.most_common()))
        print(f"wrote {out}")
    print(f"wrote {args.out}")


if __name__ == "__main__":
    main()
