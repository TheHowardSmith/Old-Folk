"""Discografia Brasileira (Instituto Moreira Salles): Brazilian 78s.

Recording pages carry schema.org JSON-LD with the title, performer, composer,
genre, disc, release year, the year the recording entered the public domain
in Brazil, and a public MP3 link. The site has no API and asks crawlers for a
5-second gap between requests, so the harvester walks the sitemap slowly,
time-boxed and resumable, into its own cache file (cache-ims/ims.sqlite).

Only recordings already in the public domain (publicDomainYear <= this year),
released by 1970 and in a traditional/regional genre (rules/ims.yaml) are
listed; the normal classifier then applies the remaining rules.
"""
from __future__ import annotations

import html
import json
import logging
import re
import time
from dataclasses import dataclass
from datetime import date
from pathlib import Path

import yaml

from .classify import EXCLUDE, INCLUDE, FLAG, ItemResult, classify_item
from .config import BUILDER_DIR, RULES_DIR, Rules
from .loc import LocClient

log = logging.getLogger(__name__)

SOURCE = "ims"
DEFAULT_DB = BUILDER_DIR / "cache-ims" / "ims.sqlite"
LD_RE = re.compile(r'<script[^>]*application/ld\+json[^>]*>(.*?)</script>', re.S)
LOC_RE = re.compile(r"<loc>([^<]+)</loc>")
FONO_RE = re.compile(r"/(?:fonograma|music-recording)/(\d+)(?:/|$)")


@dataclass
class ImsConfig:
    sitemap: str
    folk_genres: frozenset[str]
    credit: str = "Instituto Moreira Salles"
    user_agent: str = "OldFolkListBuilder/1.0"
    pause: float = 5.0


def load_config(path: Path = RULES_DIR / "ims.yaml") -> ImsConfig:
    with open(path, encoding="utf-8") as fh:
        d = yaml.safe_load(fh) or {}
    return ImsConfig(
        sitemap=d.get("sitemap", "https://discografiabrasileira.com.br/sitemap.xml"),
        folk_genres=frozenset(str(g).lower() for g in d.get("folk_genres") or []),
        credit=str(d.get("credit") or "Instituto Moreira Salles"),
        user_agent=str(d.get("user_agent") or "OldFolkListBuilder/1.0"),
        pause=float(d.get("pause", 5.0)),
    )


# --------------------------------------------------------------------------- harvesting

class _TextClient(LocClient):
    def get_text(self, url: str) -> str:
        import requests
        for attempt in range(1, 5):
            wait = self._last + self.pause - time.monotonic()
            if wait > 0:
                time.sleep(wait)
            try:
                r = self.session.get(url, timeout=60)
                r.raise_for_status()
                return r.content.decode("utf-8", "replace")
            except (requests.ConnectionError, requests.exceptions.ChunkedEncodingError,
                    requests.exceptions.RetryError, requests.Timeout) as exc:
                if attempt == 4:
                    raise
                log.warning("ims %s: %s; retry %d", url, exc, attempt)
                time.sleep(2.0 ** attempt)
            finally:
                self._last = time.monotonic()
        raise RuntimeError("unreachable")


def parse_page(page: str) -> dict | None:
    """The MusicRecording JSON-LD of a recording page."""
    for block in LD_RE.findall(page):
        try:
            d = json.loads(html.unescape(block) if "&quot;" in block else block)
        except ValueError:
            continue
        if isinstance(d, dict) and d.get("@type") == "MusicRecording":
            return d
    return None


def recording_urls(client: _TextClient, sitemap: str) -> list[str]:
    index = client.get_text(sitemap)
    urls = []
    for sm in LOC_RE.findall(index):
        if sm.endswith(".xml"):
            urls += [u for u in LOC_RE.findall(client.get_text(sm)) if FONO_RE.search(u)]
    return urls


def harvest(store, cfg: ImsConfig, minutes: float | None = None, limit: int | None = None) -> dict:
    """Fetch recording pages not yet cached, until `minutes` or `limit` runs out."""
    client = _TextClient(cfg)
    deadline = time.monotonic() + minutes * 60 if minutes else None
    have = {i for i, _c, _r, _d in store.ext_items(SOURCE)}
    # Page ids follow recording dates closely, so oldest first: those are the
    # ones that can be in the public domain (recording + 70 years). Stop once a
    # long run of pages is all too recent to qualify yet.
    by_id: dict[str, str] = {}
    for u in recording_urls(client, cfg.sitemap):      # the sitemap lists each page in pt and en
        i = FONO_RE.search(u).group(1)
        if i not in have and (i not in by_id or "/en/" in by_id[i]):
            by_id[i] = u
    todo = [by_id[i] for i in sorted(by_id, key=int)]
    newest_possible = date.today().year - 70
    # Once a run has reached the too-recent pages, later runs this year only
    # pick up pages added below that point (next year, more become public domain).
    stop_key = f"stop-{date.today().year}"
    stop_id = store.ext_last_page(SOURCE, stop_key)
    if stop_id:
        todo = [u for u in todo if int(FONO_RE.search(u).group(1)) < stop_id]
    log.info("ims: %d recordings cached, %d to fetch", len(have), len(todo))
    done = 0
    too_recent = 0
    for url in todo[:limit] if limit else todo:
        if deadline and time.monotonic() > deadline:
            break
        if too_recent >= 500:
            log.info("ims: 500 pages in a row released after %d; stopping", newest_possible)
            store.set_ext_last_page(SOURCE, stop_key, int(FONO_RE.search(url).group(1)))
            break
        ident = FONO_RE.search(url).group(1)
        try:
            rec = parse_page(client.get_text(url))
        except Exception as exc:  # noqa: BLE001 - retried next run
            log.warning("ims %s: %s", url, exc)
            continue
        store.save_ext_records(SOURCE, "fonograma", [(ident, dict(rec or {"missing": True}, pageUrl=url))])
        done += 1
        year = re.match(r"\d{4}", str(((rec or {}).get("inAlbum") or {}).get("dateBegin") or ""))
        too_recent = too_recent + 1 if year and int(year.group()) > newest_possible else 0
        if done % 200 == 0:
            log.info("ims: %d fetched this run", done)
    return {"fetched": done, "remaining": max(0, len(todo) - done), **store.ext_stats(SOURCE)}


# --------------------------------------------------------------------------- reshaping

def _name(x) -> str:
    if isinstance(x, dict):
        return str(x.get("name") or "").strip()
    if isinstance(x, list):
        return ", ".join(n for n in (_name(i) for i in x) if n)
    return str(x or "").strip()


def classify_record(ident: str, rec: dict, rules: Rules, cfg: ImsConfig,
                    this_year: int | None = None) -> ItemResult:
    rid = f"{SOURCE}:{ident}"
    title = _name(rec.get("name")) or rec.get("xInAlbum") or ""
    this_year = this_year or date.today().year
    if rec.get("missing"):
        return ItemResult(id=rid, status=EXCLUDE, reasons=["ims: page had no recording data"])
    audio = (rec.get("audio") or {}).get("contentUrl") if isinstance(rec.get("audio"), dict) else None
    if not audio:
        return ItemResult(id=rid, status=EXCLUDE, reasons=["no playable MP3"], title=title)
    try:
        pd_year = int(str(rec.get("publicDomainYear") or "").strip()[:4])
    except ValueError:
        pd_year = None
    if pd_year is None or pd_year > this_year:
        return ItemResult(id=rid, status=EXCLUDE, title=title,
                          reasons=[f"ims: not yet public domain in Brazil ({pd_year or 'no year'})"])
    genre = (_name(rec.get("genre")) or str(rec.get("xGenre") or "")).strip()
    if genre.lower() not in cfg.folk_genres:
        return ItemResult(id=rid, status=EXCLUDE, title=title,
                          reasons=[f"not folk: Brazilian {genre.lower() or 'untagged'} (popular song genre)"])
    album = rec.get("inAlbum") if isinstance(rec.get("inAlbum"), dict) else {}
    year = str(album.get("dateBegin") or rec.get("xAlbumDateBegin") or "")
    performer = _name(rec.get("creator")) or str(rec.get("xCreator") or "") or None
    composer = _name((rec.get("recordingOf") or {}).get("author")) if isinstance(rec.get("recordingOf"), dict) else ""
    data = {
        "metadata": {
            "identifier": ident, "title": title.title() if title.isupper() else title,
            "creator": performer, "date": year,
            "subject": [genre, "folk", "brazilian"],
            "description": [f"Composer: {composer}"] if composer else [],
            "language": ["por"], "publisher": album.get("name") or rec.get("xProducer") or "",
        },
        "files": [{"name": audio.rsplit("/", 1)[-1], "format": "VBR MP3", "source": "original"}],
    }
    res = classify_item(ident, "other", data, rules)
    res.id = rid
    # Polkas keep their own genre (so listeners can leave them out), as everywhere else.
    app_genre = "polka" if genre.lower() in ("polca", "polka") else "latin"
    for t in res.tracks:
        t.id, t.url, t.page, t.source, t.picture = rid, audio, rec.get("pageUrl"), cfg.credit, None
        t.region, t.genre = "Latin America", app_genre
    if res.status in (INCLUDE, FLAG):
        res.region, res.genre = "Latin America", app_genre
    return res


def classify_all(store, rules: Rules, cfg: ImsConfig | None = None) -> list[ItemResult]:
    cfg = cfg or load_config()
    return [classify_record(i, rec, rules, cfg) for i, _c, rec, _d in store.ext_items(SOURCE)]
