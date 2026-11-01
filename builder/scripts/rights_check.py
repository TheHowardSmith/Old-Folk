#!/usr/bin/env python3
"""Prototype: composition and recording rights status for writer-credited 78s.

For each item it combines:
  1. US Copyright Office Public Records (publicrecords.copyright.gov): a title
     search finds the song's registration (year, claimant), renewal evidence
     (R/RE numbers) and usually the writers' full names ("/By A & B").
  2. Death years for those full names from three sources, combined:
     Library of Congress Name Authority File (id.loc.gov), the German National
     Library's authority file GND (via lobid.org; strong on German, Austrian
     and Central European writers), and Wikidata (links most national
     libraries). VIAF blocks scripted access and is not used.

and reports, per track:
  nz_composition  life+50: clear if every writer died <= NZ_CUTOFF
  eu_composition  life+70 (EU and UK): clear if every writer died <= EU_CUTOFF
  us_composition  published <= US_COMP_CUTOFF -> public domain; 1931-1963 ->
                  protected only if renewed; 1964+ -> protected
  us_recording    pre-1972 recordings (Music Modernization Act): published
                  <= 1925 public domain as of 2026; later ones protected
  eu_recording    EU/UK: recordings released before 1963 stayed public domain
                  (the 2011 term extension only covered recordings still
                  protected in 2013); later ones 70 years from release

This is evidence for a human decision, not legal advice. "No renewal found"
means none was found in the searchable records, not proof none exists.

Usage:
  python scripts/rights_check.py --items items.jsonl --descriptions descs.json --sample 500
  python scripts/rights_check.py --enrich rights_check.csv     # re-assess an earlier run
"""
from __future__ import annotations

import argparse
import csv
import json
import random
import re
import sys
import time
from collections import Counter
from pathlib import Path

import requests

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))
from oldfolk_builder.classify import extract_writers, parse_year, as_text  # noqa: E402
from oldfolk_builder.config import load_rules  # noqa: E402

THIS_YEAR = 2026
NZ_CUTOFF = THIS_YEAR - 51            # life + 50, to the end of the year: died 1975 or earlier
EU_CUTOFF = THIS_YEAR - 71            # life + 70: died 1955 or earlier
US_COMP_CUTOFF = THIS_YEAR - 96       # published 1930 or earlier (95 years)
US_REC_CUTOFF = THIS_YEAR - 101       # pre-1947 recordings: 100 years -> 1925 or earlier

UA = "OldFolkRightsCheck/0.1 (https://github.com/TheHowardSmith/Old-Folk; thehowardsmith@gmail.com)"
CPRS = "https://api.publicrecords.copyright.gov/search_service_external/simple_search_dsl"
LOC = "https://id.loc.gov/authorities/names/suggest2"
GND = "https://lobid.org/gnd/search"
MUSIC_GND = re.compile(r"komponist|musiker|textdichter|liedtexter|librettist|s[äa]nger|dirigent|kapellmeister|"
                       r"lyriker|schlager|volksmusik|arrangeur|pianist|geiger|akkordeon|chorleiter|composer|musician",
                       re.I)
S = requests.Session()
S.headers["User-Agent"] = UA


def get_json(url, params, pause=0.6):
    for attempt in range(4):
        try:
            r = S.get(url, params=params, timeout=30)
            if r.status_code in (429, 500, 502, 503, 504):
                raise requests.HTTPError(str(r.status_code))
            r.raise_for_status()
            time.sleep(pause)
            return r.json()
        except Exception:
            time.sleep(2 ** (attempt + 1))
    return {}


def norm(s: str) -> str:
    s = re.sub(r"\(.*?\)|\[.*?\]", " ", s.lower())
    s = re.sub(r"[^a-z0-9 ]+", " ", s)
    return re.sub(r"\s+", " ", s).strip()


def clean_title(title: str) -> str:
    """First song of a disc side: drop quotes, 'a.'/'1.' prefixes, medley tails, translations."""
    t = title.replace('"', "").strip()
    t = re.split(r";|\s-\s|\s/\s", t)[0]
    t = re.sub(r"^\(?[a-d1-4][.)]\s*", "", t, flags=re.I)
    t = re.sub(r"\(.*?\)", "", t)
    return t.strip(" .,-")


def record_text(r: dict) -> str:
    parts = [str(r.get(k, "")) for k in ("title_concatenated", "title_of_work", "claimant", "claimants_list",
                                         "author", "authors", "names", "registration_number", "notes")]
    return " ".join(parts)


def search_cprs(title: str) -> list[dict]:
    d = get_json(CPRS, {"page_number": 1, "query": title, "field_type": "title", "records_per_page": 25})
    out = []
    for h in d.get("data", []) or []:
        out.append(h.get("hit", h))
    return out


def year_of(r: dict) -> int | None:
    m = re.match(r"(\d{4})", str(r.get("representative_date") or r.get("registration_date") or ""))
    return int(m.group(1)) if m else None


RENEWAL_RE = re.compile(r"\bR[E]?\s?-?\d{4,}\b")
# "/By Jimmie Rodgers & Clayton McMichen.  EU70891": names run until ". " + two spaces, a ";" or the end.
BY_RE = re.compile(r"/\s*(?:words and music\s+)?by\s+(.+?)(?:\.\s{2}|;|\.$|$)", re.I)
NOT_NAME_WORDS = re.compile(r"\b(recorded|production|supvd|featuring|orchestra|orch|op|folk ?dance|polish|scandinavian|"
                            r"swedish|vocal|music|arr|arranged|traditional|record|et al|and his|band|trio|quartet)\b", re.I)


def is_person_name(n: str) -> bool:
    """'Clayton McMichen', 'Wladimir A. Timm' yes; 'Production Supvd. by', 'Ringle Featuring' no."""
    toks = n.replace(".", ". ").split()
    if not 2 <= len(toks) <= 4 or NOT_NAME_WORDS.search(n) or re.search(r"\d", n):
        return False
    return all(t[0].isupper() for t in toks if t.lower() not in ("de", "van", "von", "la", "le", "da"))


def analyse_records(title: str, writers: list[str], records: list[dict], rec_year: int | None = None) -> dict:
    nt = norm(title)
    surnames = {norm(w).split()[-1] for w in writers if norm(w)}
    matched = []
    for r in records:
        rt = norm(str(r.get("title_of_work") or r.get("title_concatenated") or ""))
        if not rt.startswith(nt) and nt not in rt:
            continue
        text = record_text(r)
        kind = (str(r.get("cc_type_of_work")) + " " + str(r.get("type_of_work_to_english")) + " " +
                str(r.get("registration_class"))).lower()
        if "sound" in kind or "visual" in kind:
            continue
        if surnames and not any(s and s in norm(text) for s in surnames):
            continue   # same title, different song
        matched.append(r)
    if not matched:
        return {"found": False}
    regs = [r for r in matched if r.get("type_of_record") == "registration"
            and not RENEWAL_RE.match(str(r.get("registration_number", "")).strip("[]'\" "))]
    # A registration after the disc was made can't be the song's first publication
    # (later editions, arrangements); ignore those.
    latest = (rec_year + 1) if rec_year else 1965
    years = sorted(y for y in (year_of(r) for r in regs) if y and y <= latest)
    renewed = any(RENEWAL_RE.search(record_text(r)) for r in matched)
    full_names: list[str] = []
    for r in matched:
        for m in BY_RE.finditer(str(r.get("title_concatenated", ""))):
            for n in re.split(r"\s*(?:&|,| and | with )\s*", m.group(1)):
                n = re.sub(r"\s+", " ", n).strip(" .")
                if is_person_name(n) and n not in full_names:
                    full_names.append(n)
    return {"found": True, "first_year": years[0] if years else None, "renewed": renewed,
            "full_names": full_names, "records": len(matched)}


_loc_cache: dict[str, dict] = {}


def loc_death(full_name: str) -> dict:
    """Death year from the LoC name authority for 'Given Surname'."""
    if full_name in _loc_cache:
        return _loc_cache[full_name]
    parts = full_name.split()
    q = f"{parts[-1]}, {' '.join(parts[:-1])}"
    d = get_json(LOC, {"q": q}, pause=0.3)
    found = set()
    for h in d.get("hits", []) or []:
        label = h.get("aLabel", "")
        m = re.match(r"^([^,]+), ([^,]+?), (?:(?:\d{4})\??-(\d{4})?)", label)
        if not m or norm(m.group(1)) != norm(parts[-1]):
            continue
        if not norm(m.group(2)).startswith(norm(parts[0])[:4]):
            continue
        died = m.group(3)
        found.add(int(died) if died else None)
    if len(found) == 1:
        died = found.pop()
        res = {"status": "dead" if died else "no_death_date", "died": died}
    elif not found:
        res = {"status": "not_found"}
    elif None not in found and (max(found) <= NZ_CUTOFF or min(found) > NZ_CUTOFF):
        # Several people of that name, but all on the same side of the cutoff: still decidable.
        res = {"status": "dead", "died": max(found), "note": "several namesakes"}
    else:
        res = {"status": "ambiguous", "candidates": sorted(x for x in found if x)}
    _loc_cache[full_name] = res
    return res


_gnd_cache: dict[str, dict] = {}


def gnd_death(full_name: str) -> dict:
    """Death year from the German National Library authority file (lobid.org API)."""
    if full_name in _gnd_cache:
        return _gnd_cache[full_name]
    parts = full_name.split()
    q = f'preferredName:"{parts[-1]}, {" ".join(parts[:-1])}" OR variantName:"{parts[-1]}, {" ".join(parts[:-1])}"'
    d = get_json(GND, {"q": q, "filter": "type:Person", "format": "json", "size": 10}, pause=0.3)
    found = set()
    for m in d.get("member", []) or []:
        jobs = " ".join(p.get("label", "") for p in m.get("professionOrOccupation", []) or [])
        if jobs and not MUSIC_GND.search(jobs):
            continue
        died = next((int(x[:4]) for x in m.get("dateOfDeath", []) or [] if x[:4].isdigit()), None)
        born = next((int(x[:4]) for x in m.get("dateOfBirth", []) or [] if x[:4].isdigit()), None)
        if died is None and born is not None and born < 1880:
            continue   # no death date, but too old to matter
        found.add(died)
    if len(found) == 1:
        died = found.pop()
        res = {"status": "dead" if died else "no_death_date", "died": died}
    elif not found:
        res = {"status": "not_found"}
    elif None not in found and len({y > NZ_CUTOFF for y in found}) == 1 and len({y > EU_CUTOFF for y in found}) == 1:
        res = {"status": "dead", "died": max(found), "note": "several namesakes"}
    else:
        res = {"status": "ambiguous"}
    _gnd_cache[full_name] = res
    return res


_wd_cache: dict[str, dict] = {}


def wikidata_death(full_name: str) -> dict:
    if full_name not in _wd_cache:
        try:
            import writer_deaths  # same folder
            r = writer_deaths.lookup(full_name)
        except Exception:
            r = {"status": "unknown"}
        if r.get("status") == "dead":
            _wd_cache[full_name] = {"status": "dead", "died": r.get("died")}
        elif r.get("status") == "alive_or_late":
            _wd_cache[full_name] = ({"status": "dead", "died": r["died"]} if r.get("died")
                                    else {"status": "no_death_date"})
        else:
            _wd_cache[full_name] = {"status": "not_found"}
    return _wd_cache[full_name]


def writer_death(full_name: str) -> dict:
    """Combine LoC, GND and Wikidata. Returns {died, status, sources}.

    status: dead (died is set), living_or_late (a source has the person but no
    death date: treat as protected), unknown. Disagreeing death years resolve to
    the latest one (the cautious choice).
    """
    results = {"loc": loc_death(full_name), "gnd": gnd_death(full_name)}
    if all(r["status"] in ("not_found", "ambiguous") for r in results.values()):
        results["wikidata"] = wikidata_death(full_name)
    years = {k: r["died"] for k, r in results.items() if r["status"] == "dead" and r.get("died")}
    if years:
        return {"status": "dead", "died": max(years.values()), "sources": ",".join(sorted(years))}
    if any(r["status"] == "no_death_date" for r in results.values()):
        return {"status": "living_or_late", "died": None,
                "sources": ",".join(k for k, r in results.items() if r["status"] == "no_death_date")}
    return {"status": "unknown", "died": None, "sources": ""}


def composition_status(deaths: dict[str, dict], cutoff: int) -> str:
    if not deaths:
        return "unknown"
    if any(d["status"] == "living_or_late" for d in deaths.values()):
        return "protected (writer living or no death date)"
    late = [d["died"] for d in deaths.values() if d["status"] == "dead" and d["died"] > cutoff]
    if late:
        return f"protected (writer died {max(late)})"
    if all(d["status"] == "dead" for d in deaths.values()):
        return "public domain"
    return "unknown"


def eu_recording(year: int | None, is_78: bool = True) -> str:
    if year is None:
        return "public domain (78 rpm disc, released before 1963)" if is_78 else "unknown (undated)"
    if year < 1963:
        return "public domain (released before 1963)"
    return f"protected until {year + 70}"


def us_recording(year: int | None) -> str:
    if year is None:
        return "unknown (undated)"
    if year <= US_REC_CUTOFF:
        return "public domain"
    if year <= 1946:
        return f"protected until {year + 100}"
    if year <= 1956:
        return f"protected until {year + 110}"
    return "protected until 2067"


def us_composition(pub_year: int | None, renewed: bool | None, found: bool) -> str:
    if pub_year is None:
        return "unknown"
    if pub_year <= US_COMP_CUTOFF:
        return "public domain"
    if pub_year <= 1963:
        if not found:
            return "unknown (no registration found)"
        return f"protected until {pub_year + 95} (renewed)" if renewed else "likely public domain (no renewal found)"
    return f"protected until {pub_year + 95}"


def format_deaths(deaths: dict[str, dict]) -> str:
    return "; ".join(f"{k}: {v['died'] or v['status']}" + (f" [{v['sources']}]" if v.get("sources") else "")
                     for k, v in deaths.items())


def enrich(path: str) -> list[dict]:
    """Re-assess an earlier run's CSV with the current death sources and columns."""
    rows = list(csv.DictReader(open(path, encoding="utf-8")))
    out = []
    for n, r in enumerate(rows, 1):
        full = [x.strip() for x in r.get("full_names", "").split(";") if x.strip() and is_person_name(x.strip())]
        deaths = {name: writer_death(name) for name in full[:4]}
        rec_year = int(r["recorded"]) if str(r.get("recorded", "")).isdigit() else None
        r.update({
            "full_names": "; ".join(full), "deaths": format_deaths(deaths),
            "nz_composition": composition_status(deaths, NZ_CUTOFF),
            "eu_composition": composition_status(deaths, EU_CUTOFF),
            "us_recording": us_recording(rec_year),
            "eu_recording": eu_recording(rec_year),
        })
        out.append(r)
        if n % 50 == 0:
            print(f"  {n}/{len(rows)}", flush=True)
    return out


def summarize(out_rows: list[dict], out_path: str) -> None:
    order = ["id", "title", "recorded", "credited", "copyright_office", "registered", "renewed", "full_names",
             "deaths", "nz_composition", "eu_composition", "us_composition", "us_recording", "eu_recording"]
    fields = order + [k for k in out_rows[0] if k not in order]
    with open(out_path, "w", newline="", encoding="utf-8") as fh:
        w = csv.DictWriter(fh, fieldnames=fields)
        w.writeheader()
        w.writerows(out_rows)

    def bucket(s: str) -> str:
        return re.sub(r" until \d+| \(writer died \d+\)| \(renewed\)| \(released before 1963\)| \(78 rpm disc, released before 1963\)", "", s)
    total = len(out_rows)
    print(f"\n{total} writer-credited tracks")
    for col in ("copyright_office", "nz_composition", "eu_composition", "us_composition", "us_recording", "eu_recording"):
        c = Counter(bucket(str(x.get(col, ""))) for x in out_rows)
        print(f"{col:17}", ", ".join(f"{k}: {v} ({100 * v / total:.0f}%)" for k, v in c.most_common()))
    names = [d for x in out_rows for d in x["deaths"].split("; ") if d]
    resolved = sum(1 for d in names if re.search(r": \d{4}", d))
    print(f"writer names looked up: {len(names)}, death year found: {resolved} ({100 * resolved / max(1, len(names)):.0f}%)")
    print(f"results: {out_path}")


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--enrich", help="re-assess an earlier output CSV (no Copyright Office calls)")
    ap.add_argument("--items", help="JSONL of search rows (identifier, title, date, creator...)")
    ap.add_argument("--descriptions", help="JSON {identifier: description}")
    ap.add_argument("--sample", type=int, default=500)
    ap.add_argument("--seed", type=int, default=1)
    ap.add_argument("--out", default="rights_check.csv")
    args = ap.parse_args()
    if args.enrich:
        out = Path(args.enrich).with_name(Path(args.enrich).stem + "_enriched.csv")
        summarize(enrich(args.enrich), str(out))
        return
    if not (args.items and args.descriptions):
        ap.error("--items and --descriptions are required (or use --enrich)")

    rules = load_rules()
    rows = {}
    for line in open(args.items):
        r = json.loads(line)
        rows[r["identifier"]] = r
    descs = json.load(open(args.descriptions))
    credited = {}
    for ident, desc in descs.items():
        if ident not in rows:
            continue
        w = extract_writers({"description": desc}, [], rules)
        if w:
            credited[ident] = w
    ids = sorted(credited)
    random.Random(args.seed).shuffle(ids)
    ids = ids[: args.sample]
    print(f"checking {len(ids)} writer-credited items", flush=True)

    out_rows = []
    for n, ident in enumerate(ids, 1):
        r = rows[ident]
        title = clean_title(as_text(r.get("title")))
        rec_year, _ = parse_year(r)
        writers = credited[ident]
        cp = analyse_records(title, writers, search_cprs(title), rec_year) if title else {"found": False}
        full = cp.get("full_names") or [w for w in writers if is_person_name(w) and "." not in w.split()[0]]
        deaths = {name: writer_death(name) for name in full[:4]}
        pub = cp.get("first_year") or rec_year
        out_rows.append({
            "id": ident, "title": title, "recorded": rec_year or "", "credited": "; ".join(writers),
            "copyright_office": "found" if cp.get("found") else "not found",
            "registered": cp.get("first_year") or "", "renewed": cp.get("renewed", ""),
            "full_names": "; ".join(full), "deaths": format_deaths(deaths),
            "nz_composition": composition_status(deaths, NZ_CUTOFF),
            "eu_composition": composition_status(deaths, EU_CUTOFF),
            "us_composition": us_composition(pub, cp.get("renewed"), cp.get("found", False)),
            "us_recording": us_recording(rec_year),
            "eu_recording": eu_recording(rec_year),
        })
        if n % 25 == 0:
            print(f"  {n}/{len(ids)}", flush=True)

    summarize(out_rows, args.out)


if __name__ == "__main__":
    main()
