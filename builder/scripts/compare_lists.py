"""Compare the live track list with a newly built one, before it goes live.

    python scripts/compare_lists.py LIVE_DIR NEW_DIR [--availability cache-rights/availability.jsonl]

LIVE_DIR and NEW_DIR each hold an index.json with its s-*/x-* shards (NEW_DIR
also holds the build's excluded.csv and flagged.csv). Writes into NEW_DIR:

  review.md    counts of tracks removed and added, by reason and by genre,
               with examples of each reason
  removed.csv  every removed track with its reason

A track counts as removed when it is playable in the live list (main or ext
shards) and not in the new one. Its reason comes from the new build: the item's
exclusion reason, the reason it was moved to review, a streaming/sale match, or
"no longer in the archive search" when the build no longer saw the item at all.
"""
from __future__ import annotations

import argparse
import collections
import csv
import json
from pathlib import Path


def tracks(d: Path) -> dict[str, dict]:
    index = json.loads((d / "index.json").read_text(encoding="utf-8"))
    out = {}
    for s in list(index.get("shards") or []) + list(index.get("ext") or []):
        p = d / s["f"]
        if not p.exists():
            continue
        for t in json.loads(p.read_text(encoding="utf-8")).get("t", []):
            out[f"{t['i']}/{t['f']}"] = t
    return out


def read_csv(p: Path) -> list[dict]:
    if not p.exists():
        return []
    with open(p, newline="", encoding="utf-8") as fh:
        return list(csv.DictReader(fh))


def reason_group(reason: str) -> str:
    """Short heading for a reason (drops the item-specific detail after a colon)."""
    return reason.split(":")[0].strip() or reason


def main(argv=None) -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("live", type=Path)
    ap.add_argument("new", type=Path)
    ap.add_argument("--availability", type=Path)
    ap.add_argument("--examples", type=int, default=12)
    a = ap.parse_args(argv)

    live, new = tracks(a.live), tracks(a.new)
    names = {g["k"]: g["name"] for g in json.loads((a.new / "index.json").read_text(encoding="utf-8")).get("genres", [])}
    excluded = {r["id"]: r["reason"] for r in read_csv(a.new / "excluded.csv")}
    flagged = {r["id"]: r["reasons"].split(" || ")[0] for r in read_csv(a.new / "flagged.csv")}
    on_sale = set()
    if a.availability and a.availability.exists():
        for line in a.availability.read_text(encoding="utf-8").splitlines():
            try:
                r = json.loads(line)
            except ValueError:
                continue
            if r.get("found"):
                on_sale.add(r.get("k"))
    new_items = {k.split("/", 1)[0] for k in new}

    removed = []
    for key, t in live.items():
        if key in new:
            continue
        item = t["i"]
        if key in on_sale:
            why = "on sale or streaming today (iTunes)"
        elif item in excluded:
            why = excluded[item]
        elif item in flagged:
            why = "moved to review: " + flagged[item]
        elif item in new_items:
            why = "track no longer picked from this item"
        else:
            why = "item no longer in the archive search"
        removed.append((key, t, why))
    added = [k for k in new if k not in live]

    by_reason = collections.Counter(reason_group(w) for _k, _t, w in removed)
    by_genre = collections.Counter(t.get("g") for _k, t, _w in removed)
    examples = collections.defaultdict(list)
    for k, t, w in removed:
        g = reason_group(w)
        if len(examples[g]) < a.examples:
            examples[g].append((t, w))

    with open(a.new / "removed.csv", "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["reason", "detail", "title", "artist", "year", "genre", "item", "file"])
        for k, t, why in sorted(removed, key=lambda x: (reason_group(x[2]), x[1].get("g") or "", x[1].get("t") or "")):
            w.writerow([reason_group(why), why, t.get("t", ""), t.get("a", ""), t.get("y", ""), t.get("g", ""), t["i"], t["f"]])

    L = ["# Track list review", "",
         f"Live list: **{len(live):,}** tracks. New list: **{len(new):,}** tracks.", "",
         f"- Removed: **{len(removed):,}**",
         f"- Added: **{len(added):,}**",
         f"- Net change: **{len(new) - len(live):+,}**", "",
         "## Removed, by reason", "", "| reason | tracks |", "|---|---:|"]
    L += [f"| {r} | {n:,} |" for r, n in by_reason.most_common()]
    L += ["", "## Removed, by genre", "", "| genre | tracks |", "|---|---:|"]
    L += [f"| {names.get(g, g or '?')} | {n:,} |" for g, n in by_genre.most_common()]
    L += ["", "## Examples", ""]
    for r, _n in by_reason.most_common():
        L += [f"### {r}", ""]
        for t, why in examples[r]:
            detail = why[len(r):].lstrip(": ") if why.startswith(r) else why
            L.append(f"- {t.get('t', '')} · {t.get('a') or 'unknown'} · {t.get('y') or 'undated'}"
                     + (f" · {detail}" if detail else ""))
        L.append("")
    L += ["The full list is in removed.csv."]
    (a.new / "review.md").write_text("\n".join(L) + "\n", encoding="utf-8")
    print(json.dumps({"live": len(live), "new": len(new), "removed": len(removed), "added": len(added),
                      "by_reason": dict(by_reason.most_common())}))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
