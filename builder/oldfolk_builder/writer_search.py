"""Search pass for writers the build couldn't trace (checks.csv).

The writer rule keeps a composed song unless a credited writer is confirmed to
have died less than 70 years ago. Writers it couldn't trace stay in the app and
are listed in checks.csv. This pass looks each of those songs up on
MusicBrainz, where works (songs) are linked to their composers and lyricists,
and composers carry birth and death dates:

1. clean the credit ("1 - C. Butler" -> "C. Butler", "Trad." -> traditional);
2. search for a work with the song's title whose writers include someone with
   the credited surname (and matching initials, if any);
3. read that writer's life span.

A title + name match is much stronger evidence than a name alone, so results
are stored per item: {item: {writer: {status, died, born, mbid, work}}}.
The build uses them before the name-only death years (classify.py), so a song
is cleared when every writer died 70+ years ago, removed when a writer is
confirmed to have died since, and otherwise stays on the check list for the
next, slower pass.

MusicBrainz asks for at most one request a second and a descriptive user
agent: https://musicbrainz.org/doc/MusicBrainz_API/Rate_Limiting
"""
from __future__ import annotations

import csv
import json
import logging
import re
import time
import unicodedata
from pathlib import Path

log = logging.getLogger(__name__)

API = "https://musicbrainz.org/ws/2"
UA = "OldFolkAirListBuilder/1.0 (https://github.com/TheHowardSmith/Old-Folk; info@oldfolkair.com)"
WRITER_RELS = {"composer", "lyricist", "writer", "librettist", "arranger", "translator", "instrument arranger", "vocal arranger", "orchestrator"}
TRADITIONAL = re.compile(r"^(trad\.?|traditional|public domain|p\.?\s?d\.?|anon\.?|anonymous|folk ?song|"
                         r"unknown|volkslied|folklore|popular|n\.?\s?n\.?|ukjent|okänd|tuntematon)$", re.I)


# --------------------------------------------------------------------------- names

def clean_credit(raw: str) -> str | None:
    """Strip numbering and punctuation around a writer credit; None if it isn't a name."""
    s = unicodedata.normalize("NFKC", raw or "").strip()
    s = re.sub(r"^[\s\-–—•*]*(\d+\s*[\.\-–:)]\s*)?", "", s)      # "1 - ", "– ", "2) "
    s = re.sub(r"\((arr|arranged|music|words|lyrics)[^)]*\)", "", s, flags=re.I)
    s = re.sub(r"^(arr\.?|arranged by|music by|words by|lyrics by|by)\s+", "", s, flags=re.I)
    s = s.strip(" .,;:-–*\"'")
    if len(s) < 3 or not re.search(r"[^\W\d_]{2}", s):
        return None
    return s


def is_traditional(name: str) -> bool:
    return bool(TRADITIONAL.match(name.strip()))


def _fold(s: str) -> str:
    s = unicodedata.normalize("NFKD", s)
    return "".join(c for c in s if not unicodedata.combining(c)).lower()


def name_parts(name: str) -> tuple[str, list[str]]:
    """(surname, initials of the other names) for a credit like "F. Wojnarowski"."""
    words = [w for w in re.split(r"[\s.]+", _fold(name)) if w]
    if not words:
        return "", []
    return words[-1], [w[0] for w in words[:-1]]


def names_match(credit: str, mb_name: str, mb_sort: str = "") -> bool:
    """Same surname, and the credit's initials (if any) fit the MusicBrainz name."""
    sur, inits = name_parts(credit)
    if not sur:
        return False
    for cand in filter(None, (mb_name, mb_sort)):
        c = _fold(cand)
        if "," in c:                                   # sort name "Wojnarowski, Frank"
            last, first = [x.strip() for x in c.split(",", 1)]
            c = f"{first} {last}"
        words = [w for w in re.split(r"[\s.\-]+", c) if w]
        if not words or sur not in words:
            continue
        others = [w[0] for w in words if w != sur]
        if all(i in others for i in inits):
            return True
    return False


def split_titles(title: str) -> list[str]:
    """'(1) Come o'er the stream Charlie; (2) Kelvin Grove' -> each song title."""
    parts = re.split(r"\s*(?:;|/|\s-\s)\s*", title or "")
    out = []
    for p in parts:
        p = re.sub(r"^\(?\d+\)?[\.\s]*", "", p).strip(" \"'")
        p = re.sub(r"\s*\((?:from|aus|de|du)\b[^)]*\)", "", p, flags=re.I)
        if len(p) >= 2:
            out.append(p)
            bare = re.sub(r"\s*[\(\[][^)\]]*[\)\]]", "", p).strip(" -\"'")
            if bare and bare != p:
                out.append(bare)
            inner = re.findall(r"\(([^)]{3,})\)", p)            # "(Roll Out the Barrel)", translations
            out += [x.strip() for x in inner if not re.match(r"(?i)from|aus|de |du ", x)]
    seen = []
    for x in out or [title]:
        if x.lower() not in [y.lower() for y in seen]:
            seen.append(x)
    return seen


def _year(s) -> int | None:
    m = re.match(r"(\d{4})", str(s or ""))
    return int(m.group(1)) if m else None


# --------------------------------------------------------------------------- MusicBrainz

class MB:
    def __init__(self, pause: float = 1.1, session=None):
        import requests
        self.s = session or requests.Session()
        self.s.headers["User-Agent"] = UA
        self.s.headers["Accept"] = "application/json"
        self.pause = pause
        self._last = 0.0
        self.artists: dict[str, dict] = {}
        self.works: dict = {}
        self.calls = 0
        self.seconds = 0.0

    def get(self, path: str, **params) -> dict:
        import requests
        params["fmt"] = "json"
        for attempt in range(1, 5):
            wait = self._last + self.pause - time.monotonic()
            if wait > 0:
                time.sleep(wait)
            try:
                t0 = time.monotonic()
                r = self.s.get(f"{API}/{path}", params=params, timeout=30)
                self.calls += 1
                self.seconds += time.monotonic() - t0
                if r.status_code in (429, 503):
                    raise requests.HTTPError(str(r.status_code))
                r.raise_for_status()
                return r.json()
            except (requests.RequestException, ValueError) as exc:
                if attempt == 4:
                    log.warning("musicbrainz %s: %s", path, exc)
                    return {}
                time.sleep(2.0 ** attempt)
            finally:
                self._last = time.monotonic()
        return {}

    def artist(self, mbid: str) -> dict:
        if mbid not in self.artists:
            a = self.get(f"artist/{mbid}", inc="url-rels")
            ls = a.get("life-span") or {}
            info = {"name": a.get("name"), "born": _year(ls.get("begin")),
                    "died": _year(ls.get("end")), "ended": bool(ls.get("ended")), "type": a.get("type")}
            if not info["died"]:
                # MusicBrainz knows the person but not the dates: try its Wikidata link.
                qid = next((u["url"]["resource"].rsplit("/", 1)[-1] for u in a.get("relations", [])
                            if u.get("type") == "wikidata" and u.get("url")), None)
                if qid:
                    born, died = wikidata_dates(self.s, qid)
                    info.update({"born": info["born"] or born, "died": died, "wikidata": qid})
                    if died:
                        info["ended"] = True
            self.artists[mbid] = info
        return self.artists[mbid]

    def find_work(self, title: str, credits: list[str]) -> tuple[dict | None, dict[str, dict]]:
        """A work with this title credited to one of these writers, and every
        credit matched against that work's writers (credit -> hit)."""
        for credit in credits:
            sur, _ = name_parts(credit)
            if not sur:
                continue
            key = (title.lower(), sur)
            if key not in self.works:
                self.works[key] = self.get("work", query=f'work:"{title}" AND artist:"{sur}"', limit=5)
            for w in self.works[key].get("works", []):
                if int(w.get("score", 0)) < 85:
                    continue
                rels = [r for r in w.get("relations", []) if r.get("type") in WRITER_RELS and (r.get("artist") or {}).get("id")]
                if not any(names_match(credit, r["artist"].get("name", ""), r["artist"].get("sort-name", "")) for r in rels):
                    continue
                hits = {}
                for c in credits:
                    for r in rels:
                        a = r["artist"]
                        if names_match(c, a.get("name", ""), a.get("sort-name", "")):
                            info = self.artist(a["id"])
                            hits[c] = {"mbid": a["id"], "work": w.get("id"), "work_title": w.get("title"),
                                       "role": r.get("type"), "mb_name": info.get("name") or a.get("name"),
                                       "born": info.get("born"), "died": info.get("died"), "ended": info.get("ended")}
                            break
                return w, hits
        return None, {}

    def find_writer(self, title: str, credit: str) -> dict | None:
        """The credited writer of a work with this title, with life span."""
        sur, _ = name_parts(credit)
        if not sur:
            return None
        q = f'work:"{title}" AND artist:"{sur}"'
        res = self.get("work", query=q, limit=5)
        for w in res.get("works", []):
            if int(w.get("score", 0)) < 85:
                continue
            for rel in w.get("relations", []):
                a = rel.get("artist") or {}
                if rel.get("type") not in WRITER_RELS or not a.get("id"):
                    continue
                if names_match(credit, a.get("name", ""), a.get("sort-name", "")):
                    info = self.artist(a["id"])
                    return {"mbid": a["id"], "work": w.get("id"), "work_title": w.get("title"),
                            "mb_name": info.get("name") or a.get("name"), "born": info.get("born"),
                            "died": info.get("died"), "ended": info.get("ended")}
        return None


def wikidata_dates(session, qid: str) -> tuple[int | None, int | None]:
    """(birth year, death year) from a Wikidata item."""
    try:
        r = session.get(f"https://www.wikidata.org/wiki/Special:EntityData/{qid}.json", timeout=30)
        ent = r.json()["entities"][qid]["claims"]
    except Exception:  # noqa: BLE001
        return None, None
    def year(prop):
        for c in ent.get(prop, []):
            t = ((c.get("mainsnak") or {}).get("datavalue") or {}).get("value", {}).get("time", "")
            m = re.match(r"\+(\d{4})", t)
            if m:
                return int(m.group(1))
        return None
    return year("P569"), year("P570")


# --------------------------------------------------------------------------- pass

def classify_result(hit: dict | None, cutoff: int, this_year: int) -> str:
    """dead_old (died <= cutoff), dead_recent, living, or unresolved."""
    if not hit:
        return "unresolved"
    if hit.get("died"):
        return "dead_old" if hit["died"] <= cutoff else "dead_recent"
    if hit.get("born") and hit["born"] > this_year - 100 and not hit.get("ended"):
        return "living"
    return "unresolved"


def run(checks_csv: Path, out: Path, cutoff: int, this_year: int, minutes: float | None = None,
        shard: int = 0, of: int = 1, previous: Path | None = None, mb: MB | None = None) -> dict:
    """Look up the unconfirmed writers in checks.csv; append results to out (jsonl)."""
    done: set[str] = set()
    for p in (previous, out):
        if p and p.exists():
            for line in p.read_text("utf-8").splitlines():
                if line.strip():
                    done.add(json.loads(line)["item"])
    rows = []
    with open(checks_csv, newline="", encoding="utf-8") as fh:
        for r in csv.DictReader(fh):
            if "writer not confirmed" not in r.get("checks", ""):
                continue
            if hash_shard(r["id"], of) != shard or r["id"] in done:
                continue
            names = r["checks"].split("died by", 1)[1].split(":", 1)[1].split(" || ")[0]
            rows.append((r["id"], r.get("title", ""), [n.strip() for n in names.split(";")]))
    mb = mb or MB()
    deadline = time.monotonic() + minutes * 60 if minutes else None
    stats = {"items": 0, "cleared": 0, "removed": 0, "unresolved": 0, "left": len(rows)}
    with open(out, "a", encoding="utf-8") as fh:
        for ident, title, credits in rows:
            if deadline and time.monotonic() > deadline:
                break
            writers, names = {}, {}
            for raw in credits:
                name = clean_credit(raw)
                if name is None:
                    writers[raw] = {"status": "not_a_name"}
                elif is_traditional(name):
                    writers[raw] = {"status": "traditional"}
                else:
                    names[raw] = name
            hits: dict[str, dict] = {}
            if names:
                for t in split_titles(title)[:4]:
                    _w, found = mb.find_work(t, list(names.values()))
                    for raw, name in names.items():
                        if name in found and raw not in hits:
                            hits[raw] = found[name]
                    if len(hits) == len(names):
                        break
            for raw, name in names.items():
                hit = hits.get(raw)
                writers[raw] = {"status": classify_result(hit, cutoff, this_year), "clean": name, **(hit or {})}
            sts = [w["status"] for w in writers.values()]
            verdict = ("removed" if "dead_recent" in sts or "living" in sts else
                       "cleared" if all(s in ("dead_old", "traditional", "not_a_name") for s in sts) else
                       "unresolved")
            fh.write(json.dumps({"item": ident, "title": title, "verdict": verdict, "writers": writers},
                                ensure_ascii=False) + "\n")
            fh.flush()
            stats["items"] += 1
            stats[verdict] += 1
            stats["left"] -= 1
    stats["requests"] = mb.calls
    stats["avg_response_s"] = round(mb.seconds / max(1, mb.calls), 2)
    return stats


def hash_shard(ident: str, of: int) -> int:
    import zlib
    return zlib.crc32(ident.encode("utf-8")) % max(1, of)


def load(path: Path | None) -> dict[str, dict]:
    """item -> {verdict, writers} (latest line wins)."""
    out: dict[str, dict] = {}
    if path and path.exists():
        for line in path.read_text("utf-8").splitlines():
            if line.strip():
                r = json.loads(line)
                out[r["item"]] = r
    return out
