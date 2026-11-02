"""Is a listed recording on sale or streaming today? (Apple iTunes Search API)

Used to keep recordings that someone is still commercially exploiting out of the
app. Only Internet Archive tracks after the US public-domain year (1925) are
checked: older recordings are public domain in the US whoever reissues them,
and the archive sources (loc.gov, Finna, IMS, DSAL) are cleared by their
institutions.

The Search API allows about 20 requests a minute per address, so the full list
is split into shards that run in parallel (rights-checks.yml) and results are
kept between runs, so each track is only checked once.

Matching is deliberately strict (title words and a performer word must agree):
a false "on sale" only drops a track, but a false "not found" leaves one in,
so the check reduces risk rather than proving anything.
"""
from __future__ import annotations

import hashlib
import json
import logging
import re
import time
import unicodedata
from pathlib import Path

log = logging.getLogger(__name__)

ITUNES = "https://itunes.apple.com/search"
STOP = {"the", "and", "his", "her", "orchestra", "with", "y", "e", "de", "la", "el", "et", "und", "various", "artists"}


def norm(s: str | None) -> list[str]:
    s = unicodedata.normalize("NFKD", s or "").encode("ascii", "ignore").decode().lower()
    s = re.sub(r"\(.*?\)|\[.*?\]", " ", s)
    return re.sub(r"[^a-z0-9 ]+", " ", s).split()


def same_title(ours: str, theirs: str) -> bool:
    a, b = set(norm(ours)), set(norm(theirs))
    return bool(a) and len(a & b) / len(a) >= 0.75


def performer_words(artist: str | None) -> set[str]:
    first = re.split(r"[;/]", artist or "")[0]
    return set(norm(first)) - STOP


def is_match(title: str, artist: str | None, hit: dict) -> bool:
    if not same_title(title, hit.get("trackName", "")):
        return False
    words = performer_words(artist)
    if not words:          # "Various" or no performer: a title alone is too weak
        return False
    return bool(words & set(norm(hit.get("artistName", ""))))


def track_key(t: dict) -> str:
    return f"{t['i']}/{t['f']}"


def targets(tracklist: Path, us_public_domain_year: int = 1925) -> list[dict]:
    """Tracks to check, from a published track list folder (index.json + s-*.json)."""
    index = json.loads((tracklist / "index.json").read_text("utf-8"))
    out = []
    for s in index.get("shards", []):
        p = tracklist / s["f"]
        if not p.exists():
            continue
        for t in json.loads(p.read_text("utf-8"))["t"]:
            if t.get("u") or (t.get("y") and t["y"] <= us_public_domain_year):
                continue
            out.append(t)
    return out


def in_shard(key: str, shard: int, of: int) -> bool:
    return int(hashlib.sha1(key.encode()).hexdigest()[:8], 16) % of == shard


def load(path: Path | None) -> dict[str, dict]:
    """key -> {found, match, checked}."""
    out: dict[str, dict] = {}
    if path and path.exists():
        for line in path.read_text("utf-8").splitlines():
            if line.strip():
                r = json.loads(line)
                out[r["k"]] = r
    return out


class Client:
    def __init__(self, pause: float = 3.2):
        import requests
        self.s = requests.Session()
        self.s.headers["User-Agent"] = "OldFolkAirListBuilder/1.0 (+https://github.com/thehowardsmith/old-folk)"
        self.pause = pause
        self._last = 0.0

    def search(self, term: str) -> list[dict] | None:
        for attempt in range(5):
            wait = self._last + self.pause - time.monotonic()
            if wait > 0:
                time.sleep(wait)
            try:
                r = self.s.get(ITUNES, params={"term": term, "entity": "song", "limit": 10, "media": "music"},
                               timeout=30)
                self._last = time.monotonic()
                if r.status_code in (403, 429, 500, 502, 503):
                    time.sleep(30 * (attempt + 1))        # rate limited: back off
                    continue
                r.raise_for_status()
                return r.json().get("results", [])
            except Exception as exc:  # noqa: BLE001
                self._last = time.monotonic()
                log.warning("itunes %r: %s", term, exc)
                time.sleep(5 * (attempt + 1))
        return None


def check(client: Client, t: dict) -> dict:
    words = norm(t["t"])[:6] + sorted(performer_words(t.get("a")))[:3]
    results = client.search(" ".join(words)) if words else []
    if results is None:
        return {"k": track_key(t), "error": True}
    hit = next((h for h in results if is_match(t["t"], t.get("a"), h)), None)
    return {"k": track_key(t), "found": bool(hit), "checked": time.strftime("%Y-%m-%d"),
            "match": (f"{hit.get('artistName')} - {hit.get('trackName')} [{hit.get('collectionName')}, "
                      f"{str(hit.get('releaseDate', ''))[:4]}]") if hit else None}


def run(tracklist: Path, out: Path, shard: int = 0, of: int = 1, previous: Path | None = None,
        minutes: float | None = None, pause: float = 3.2) -> dict:
    done = load(previous)
    todo = [t for t in targets(tracklist) if in_shard(track_key(t), shard, of)
            and not (track_key(t) in done and not done[track_key(t)].get("error"))]
    log.info("availability shard %d/%d: %d to check", shard, of, len(todo))
    client = Client(pause)
    deadline = time.monotonic() + minutes * 60 if minutes else None
    n = found = 0
    with open(out, "a", encoding="utf-8") as fh:
        for t in todo:
            if deadline and time.monotonic() > deadline:
                break
            r = check(client, t)
            fh.write(json.dumps(r, ensure_ascii=False) + "\n")
            fh.flush()
            n += 1
            found += bool(r.get("found"))
            if n % 200 == 0:
                log.info("checked %d, on sale/streaming %d", n, found)
    return {"checked": n, "found": found, "remaining": len(todo) - n}


def merge(paths: list[Path], out: Path) -> dict:
    """Combine result files; a later successful check wins over an earlier one."""
    merged: dict[str, dict] = {}
    for p in paths:
        for k, r in load(p).items():
            if r.get("error") and k in merged:
                continue
            merged[k] = r
    with open(out, "w", encoding="utf-8") as fh:
        for r in merged.values():
            fh.write(json.dumps(r, ensure_ascii=False) + "\n")
    return {"tracks": len(merged), "found": sum(1 for r in merged.values() if r.get("found"))}
