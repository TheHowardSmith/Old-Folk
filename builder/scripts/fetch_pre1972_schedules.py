#!/usr/bin/env python3
"""Download the US Copyright Office's index of Pre-1972 Sound Recording Schedules.

Rights owners (mostly record labels) file these schedules to claim pre-1972
recordings under the Music Modernization Act. A recording listed here is
actively claimed in the US. The index is public and searched through the
Office's Solr proxy; this pages through it politely (one request per second)
and writes JSON lines to cache/pre1972_schedules.jsonl.
"""
import json
import sys
import time
from pathlib import Path

import requests

PROXY = "https://copyright.gov/music-modernization/pre1972-soundrecordings/proxy.php"
UA = "OldFolkRightsCheck/0.1 (https://github.com/TheHowardSmith/Old-Folk; info@oldfolkair.com)"
FIELDS = "id,title,alt_title,artist,alt_artist,rights_owner,label,album,pub_date_str,isrc"
ROWS = 1000


def page(session, start):
    q = f"q=*:*&wt=json&rows={ROWS}&start={start}&sort=id asc&fl={FIELDS}"
    for attempt in range(5):
        try:
            r = session.post(PROXY, data={"query": q}, timeout=60)
            r.raise_for_status()
            return r.json()["response"]
        except Exception as e:
            print(f"retry {attempt + 1} at {start}: {e}", file=sys.stderr)
            time.sleep(2 ** (attempt + 1))
    raise SystemExit(f"giving up at offset {start}")


def main():
    out = Path(sys.argv[1] if len(sys.argv) > 1 else "cache/pre1972_schedules.jsonl")
    s = requests.Session()
    s.headers["User-Agent"] = UA
    first = page(s, 0)
    total = first["numFound"]
    print(f"{total} scheduled recordings")
    n = 0
    with open(out, "w", encoding="utf-8") as fh:
        docs = first["docs"]
        start = 0
        while True:
            for d in docs:
                fh.write(json.dumps(d, ensure_ascii=False) + "\n")
                n += 1
            start += ROWS
            if start >= total:
                break
            time.sleep(1)
            docs = page(s, start)["docs"]
            if start % 20000 == 0:
                print(f"  {start}/{total}", flush=True)
    print(f"wrote {n} records to {out}")


if __name__ == "__main__":
    main()
