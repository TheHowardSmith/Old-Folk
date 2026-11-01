#!/usr/bin/env python3
"""Estimate how many writer-credited tracks would be removed under composition copyright.

New Zealand protects musical works for the author's life plus 50 years, so a
song is clear if every credited writer died in 1975 or earlier.

For a random sample of items with named writer credits, each writer is looked
up on Wikidata (search, then keep only humans with a music-related occupation
or description) and their death year read. Tracks are then classed as:

  clear    - every writer found and died <= CUTOFF
  remove   - at least one writer died after CUTOFF or is (probably) alive
  unknown  - otherwise (no confident match: surnames only, initials, obscure)

Usage:
  python scripts/writer_deaths.py --cache cache/oldfolk.sqlite --sample 400
  python scripts/writer_deaths.py --descriptions descs.json --sample 400   # {id: description}

Writes writer_deaths.csv (per writer) next to the output summary.
"""
from __future__ import annotations

import argparse
import csv
import json
import random
import re
import sqlite3
import sys
import time
from pathlib import Path

import requests

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from oldfolk_builder.classify import extract_writers  # noqa: E402
from oldfolk_builder.config import load_rules  # noqa: E402

CUTOFF = 1975
API = "https://www.wikidata.org/w/api.php"
UA = "OldFolkWriterCheck/1.0 (https://github.com/TheHowardSmith/Old-Folk; thehowardsmith@gmail.com)"
MUSIC_OCCUPATIONS = {
    "Q36834", "Q753110", "Q822146", "Q639669", "Q177220", "Q855091", "Q486748", "Q806349",
    "Q488205", "Q158852", "Q1259917", "Q1643514", "Q183945", "Q49757", "Q16145150",
    "Q1415090", "Q1198887", "Q584301", "Q2643890", "Q12800682", "Q9648008", "Q11063",
}
MUSIC_WORDS = re.compile(r"compos|songwriter|lyricist|musician|singer|bandleader|guitarist|pianist|"
                         r"violinist|fiddler|accordion|poet|hymn|conductor|arranger|vocalist", re.I)

session = requests.Session()
session.headers["User-Agent"] = UA


def api(**params):
    params.update(format="json")
    for attempt in range(5):
        try:
            r = session.get(API, params=params, timeout=30)
            if r.status_code == 429 or r.status_code >= 500:
                raise requests.HTTPError(str(r.status_code))
            r.raise_for_status()
            time.sleep(0.15)
            return r.json()
        except Exception:
            time.sleep(2 ** attempt)
    return {}


def year_of(claims: dict, prop: str) -> int | None:
    for c in claims.get(prop, []):
        t = c.get("mainsnak", {}).get("datavalue", {}).get("value", {}).get("time")
        if t:
            m = re.match(r"[+-](\d{4})", t)
            if m:
                return int(m.group(1))
    return None


def lookup(name: str) -> dict:
    """Return {status: dead|alive|unknown, died, qid, label, why}."""
    clean = re.sub(r"\s+", " ", name).strip(" .")
    if clean.isupper():
        clean = clean.title()
    tokens = [t for t in re.split(r"[\s.]+", clean) if t]
    if len(tokens) < 2 or all(len(t) <= 1 for t in tokens[:-1]):
        # Surname only ("Handy") or initials + surname ("W. A. Ford"): too ambiguous.
        return {"status": "unknown", "why": "surname only / initials"}
    hits = api(action="wbsearchentities", search=clean, language="en", type="item", limit=10).get("search", [])
    if not hits:
        return {"status": "unknown", "why": "no Wikidata match"}
    ents = api(action="wbgetentities", ids="|".join(h["id"] for h in hits),
               props="claims|labels|descriptions", languages="en").get("entities", {})
    people = []
    for qid, e in ents.items():
        claims = e.get("claims", {})
        human = any(c.get("mainsnak", {}).get("datavalue", {}).get("value", {}).get("id") == "Q5"
                    for c in claims.get("P31", []))
        if not human:
            continue
        occ = {c.get("mainsnak", {}).get("datavalue", {}).get("value", {}).get("id") for c in claims.get("P106", [])}
        desc = e.get("descriptions", {}).get("en", {}).get("value", "")
        if not (occ & MUSIC_OCCUPATIONS or MUSIC_WORDS.search(desc)):
            continue
        people.append({"qid": qid, "label": e.get("labels", {}).get("en", {}).get("value", ""),
                       "died": year_of(claims, "P570"), "born": year_of(claims, "P569"), "desc": desc})
    if not people:
        return {"status": "unknown", "why": "no music person among matches"}
    statuses = set()
    for p in people:
        if p["died"]:
            statuses.add("dead_ok" if p["died"] <= CUTOFF else "protected")
        elif p["born"] and p["born"] >= 1900:
            statuses.add("protected")          # no death date, born 1900+: probably alive or died recently
        else:
            statuses.add("unclear")
    best = people[0]
    if len(people) == 1 or statuses == {"dead_ok"} or statuses == {"protected"}:
        st = statuses.pop() if len(statuses) == 1 else "unclear"
        return {"status": {"dead_ok": "dead", "protected": "alive_or_late", "unclear": "unknown"}[st],
                "died": best["died"], "qid": best["qid"], "label": best["label"], "why": best["desc"]}
    return {"status": "unknown", "why": f"{len(people)} possible people disagree", "qid": best["qid"]}


def load_items(args) -> dict[str, str]:
    if args.descriptions:
        return json.load(open(args.descriptions))
    db = sqlite3.connect(args.cache)
    out = {}
    for ident, meta in db.execute("SELECT id, meta FROM items WHERE meta IS NOT NULL AND kind = '78rpm'"):
        out[ident] = json.loads(meta)["metadata"].get("description")
    return out


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--cache", default="cache/oldfolk.sqlite")
    ap.add_argument("--descriptions", help="JSON {item id: description} instead of the cache")
    ap.add_argument("--ids", help="optional file with item ids to restrict to (one per line)")
    ap.add_argument("--sample", type=int, default=400, help="writer-credited items to check (0 = all)")
    ap.add_argument("--seed", type=int, default=1)
    ap.add_argument("--out", default="writer_deaths.csv")
    args = ap.parse_args()

    rules = load_rules()
    items = load_items(args)
    if args.ids:
        keep = set(Path(args.ids).read_text().split())
        items = {k: v for k, v in items.items() if k in keep}
    credited = {}
    for ident, desc in items.items():
        w = extract_writers({"description": desc}, [], rules)
        if w:
            credited[ident] = w
    print(f"{len(items)} items, {len(credited)} with named writers ({100 * len(credited) / max(1, len(items)):.0f}%)")
    ids = sorted(credited)
    random.Random(args.seed).shuffle(ids)
    if args.sample:
        ids = ids[: args.sample]

    cache: dict[str, dict] = {}
    names = sorted({w for i in ids for w in credited[i]})
    print(f"checking {len(ids)} items, {len(names)} distinct writers on Wikidata...")
    for n, name in enumerate(names, 1):
        cache[name] = lookup(name)
        if n % 50 == 0:
            print(f"  {n}/{len(names)}")

    tally = {"clear": 0, "remove": 0, "unknown": 0}
    for i in ids:
        res = [cache[w]["status"] for w in credited[i]]
        if "alive_or_late" in res:
            tally["remove"] += 1
        elif all(r == "dead" for r in res):
            tally["clear"] += 1
        else:
            tally["unknown"] += 1
    with open(args.out, "w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["writer", "status", "died", "wikidata", "matched label", "note"])
        for name in names:
            r = cache[name]
            w.writerow([name, r["status"], r.get("died") or "", r.get("qid", ""), r.get("label", ""), r.get("why", "")])

    total = len(ids)
    wst = {}
    for r in cache.values():
        wst[r["status"]] = wst.get(r["status"], 0) + 1
    print("\nwriters:", {k: f"{v} ({100 * v / len(names):.0f}%)" for k, v in sorted(wst.items())})
    print("tracks:", {k: f"{v} ({100 * v / total:.0f}%)" for k, v in tally.items()})
    share = len(credited) / max(1, len(items))
    print(f"\nWriter-credited tracks are {100 * share:.0f}% of all 78s, so removals would be "
          f"{100 * share * tally['remove'] / total:.1f}% of the whole list for certain, and up to "
          f"{100 * share * (tally['remove'] + tally['unknown']) / total:.1f}% if every unknown had to go.")
    print(f"per-writer results: {args.out}")


if __name__ == "__main__":
    main()
