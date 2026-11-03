"""Library of Congress (loc.gov) as a second source of recordings.

Harvesting is polite and resumable, like the Internet Archive side:

* one request at a time, with a pause between requests (rules/loc.yaml);
* retries with backoff on 429/5xx, honouring Retry-After;
* everything lands in the same SQLite cache (table `ext_items`), so a run can
  be stopped and restarted; `build` works offline from the cache.

The collection search pages already carry most of what we need (title, date,
rights, subjects, notes and usually the MP3 link). Items whose search record
has no MP3 link get one extra request for the item's own JSON.

Classification reuses the Internet Archive classifier: each loc.gov record is
reshaped into the same {metadata, files} form and passed to `classify_item`,
so restricted/ceremonial/Indigenous wording, spoken word, genre and the 1970
date rule all apply unchanged. On top of that, the item's rights statement
must say there are no known restrictions, or the item goes to review.
"""
from __future__ import annotations

import json
import logging
import re
import time
from dataclasses import dataclass, field
from pathlib import Path

import yaml

from .classify import EXCLUDE, FLAG, INCLUDE, ItemResult, as_list, classify_item, find_words
from .config import RULES_DIR, Rules

log = logging.getLogger(__name__)

SOURCE = "loc"
BASE = "https://www.loc.gov"
PAGE_SIZE = 100
MP3_RE = re.compile(r"https://tile\.loc\.gov/[^\"\s]+?\.mp3")
FIELD_TAG = re.compile(r"^\s*field recordings?\s*$", re.I)
AFAM_TAG = re.compile(r"african[- ]americans?", re.I)
PERFORMER_ROLES = ("performer", "singer", "vocalist", "musician", "fiddler", "instrumentalist",
                   "guitarist", "banjoist", "harmonica", "pianist", "violinist", "accordionist")


@dataclass
class LocConfig:
    collections: list[dict]
    rights_ok: tuple[str, ...]
    music_genres: frozenset[str]
    spoken_genres: frozenset[str]
    poor_quality_phrases: tuple[str, ...]
    user_agent: str = "OldFolkAirListBuilder/1.0"
    pause: float = 1.0
    by_slug: dict[str, dict] = field(default_factory=dict)

    def __post_init__(self):
        self.by_slug = {c["slug"]: c for c in self.collections}


def load_config(path: Path = RULES_DIR / "loc.yaml") -> LocConfig:
    with open(path, encoding="utf-8") as fh:
        d = yaml.safe_load(fh) or {}
    low = lambda xs: tuple(str(x).lower() for x in (xs or []))  # noqa: E731
    return LocConfig(
        collections=list(d.get("collections") or []),
        rights_ok=low(d.get("rights_ok")),
        music_genres=frozenset(low(d.get("music_genres"))),
        spoken_genres=frozenset(low(d.get("spoken_genres"))),
        poor_quality_phrases=low(d.get("poor_quality_phrases")),
        user_agent=str(d.get("user_agent") or "OldFolkAirListBuilder/1.0"),
        pause=float(d.get("pause", 1.0)),
    )


# --------------------------------------------------------------------------- harvesting

class LocClient:
    def __init__(self, cfg: LocConfig):
        import requests
        from requests.adapters import HTTPAdapter
        from urllib3.util.retry import Retry
        self.pause = cfg.pause
        self.session = requests.Session()
        retry = Retry(total=6, backoff_factor=3.0,
                      status_forcelist=(429, 500, 502, 503, 504, 520, 521, 522, 523, 524),
                      allowed_methods=frozenset({"GET"}), respect_retry_after_header=True)
        self.session.mount("https://", HTTPAdapter(max_retries=retry, pool_maxsize=1))
        self.session.headers["User-Agent"] = cfg.user_agent
        self._last = 0.0

    def get_json(self, url: str, params: dict | None = None, attempts: int = 5) -> dict:
        """GET with a pause between requests; retries broken transfers with backoff."""
        import requests
        for attempt in range(1, attempts + 1):
            wait = self._last + self.pause - time.monotonic()
            if wait > 0:
                time.sleep(wait)
            try:
                r = self.session.get(url, params=params, timeout=60)
                r.raise_for_status()
                return json.loads(r.content)   # UTF-8; requests may guess Latin-1 from the headers
            except (requests.ConnectionError, requests.exceptions.ChunkedEncodingError,
                    requests.exceptions.RetryError, requests.Timeout, ValueError) as exc:
                if attempt == attempts:
                    raise
                delay = 2.0 ** attempt
                log.warning("loc %s: %s; retry %d in %.0fs", url, exc, attempt, delay)
                time.sleep(delay)
            finally:
                self._last = time.monotonic()
        raise RuntimeError("unreachable")


def item_id(record: dict) -> str:
    """'http://www.loc.gov/item/flwpa000003/' -> 'flwpa000003'."""
    return record.get("id", "").rstrip("/").rsplit("/", 1)[-1]


def harvest(store, cfg: LocConfig, client: LocClient | None = None, details: bool = True,
            limit: int | None = None, rules: Rules | None = None) -> dict:
    """Fetch every audio record of the configured collections (resumable)."""
    client = client or LocClient(cfg)
    found = 0
    for coll in cfg.collections:
        slug = coll["slug"]
        last = store.ext_last_page(SOURCE, slug)     # known once the final page was read
        page = 1
        while not (last and page > last):
            key = f"{slug}#{page}"
            if store.ext_page_done(SOURCE, key):
                page += 1
                continue
            try:
                d = client.get_json(f"{BASE}/collections/{slug}/",
                                    {"fo": "json", "c": PAGE_SIZE, "sp": page,
                                     "fa": "online-format:audio"})
            except Exception as exc:  # noqa: BLE001 - resume this collection next run
                log.warning("loc %s page %d failed (%s); skipping the rest for now", slug, page, exc)
                break
            records = [r for r in d.get("results", []) if item_id(r)]
            store.save_ext_records(SOURCE, slug, [(item_id(r), r) for r in records])
            found += len(records)
            log.info("loc %s page %d: %d records", slug, page, len(records))
            if not (d.get("pagination") or {}).get("next"):
                store.set_ext_last_page(SOURCE, slug, page)
                last = page
            store.mark_ext_page(SOURCE, key)
            page += 1
    if details:
        # Only items that would make the list (or the review set) get the extra
        # request; anything excluded for another reason never needs its MP3 link.
        from .config import load_rules
        rules = rules or load_rules()
        colls = {i: c for i, c, _r, _d in store.ext_items(SOURCE)}
        todo = [i for i, rec in store.ext_needing_detail(SOURCE)
                if not audio_urls(rec, None) and worth_detail(i, colls.get(i, ""), rec, rules, cfg)]
        if limit:
            todo = todo[:limit]
        log.info("loc: %d items need their own JSON for the audio link", len(todo))
        for n, ident in enumerate(todo, 1):
            try:
                d = client.get_json(f"{BASE}/item/{ident}/", {"fo": "json"})
                store.save_ext_detail(SOURCE, ident, {"resources": d.get("resources") or []})
            except Exception as exc:  # noqa: BLE001
                log.warning("loc item %s: %s", ident, exc)
                store.save_ext_error(SOURCE, ident, str(exc))
            if n % 100 == 0:
                log.info("loc details %d/%d", n, len(todo))
    return {"records": found, **store.ext_stats(SOURCE)}


# --------------------------------------------------------------------------- reshaping

def audio_urls(record: dict, detail: dict | None) -> list[tuple[str, str | None]]:
    """[(mp3 url, segment title)] from the search record or the item JSON."""
    out: list[tuple[str, str | None]] = []
    seen = set()

    def add(url, title=None):
        if url and url.lower().endswith(".mp3") and url not in seen:
            seen.add(url)
            out.append((url, title))

    for res in (detail or {}).get("resources") or []:
        files = [f for group in res.get("files") or [] for f in (group or [])]
        segs = [f for f in files if str(f.get("download", "")).lower().endswith(".mp3")]
        for f in segs:
            add(f["download"], f.get("shortName") if len(segs) > 1 else None)
        if not segs:
            add(res.get("audio"))
    if not out:
        for res in record.get("resources") or []:
            add(res.get("audio"))
            add(res.get("media"))
    return out


MOJIBAKE = re.compile(r"[ÃÅÄ][\x80-\xbf]|â€")


def fix_text(text: str) -> str:
    """Undo UTF-8 read as Latin-1 ('blyskÃ¡' -> 'blyská'), which loc.gov has in some records."""
    if text and MOJIBAKE.search(text):
        try:
            return text.encode("cp1252").decode("utf-8")
        except UnicodeError:
            try:
                return text.encode("latin-1").decode("utf-8")
            except UnicodeError:
                return text
    return text


def _fix(value):
    if isinstance(value, str):
        return fix_text(value)
    if isinstance(value, list):
        return [_fix(v) for v in value]
    return value


def _person(name: str) -> str:
    """'Hurston, Zora Neale, 1891-1960 (Performer)' -> 'Zora Neale Hurston'."""
    name = re.sub(r"\s*\(.*?\)\s*$", "", name).strip()
    name = re.sub(r",?\s*\d{4}\s*-\s*(\d{4})?\.?$", "", name).strip()
    if name.count(",") == 1:
        last, first = [p.strip() for p in name.split(",")]
        if first and last:
            return f"{first} {last}"
    return name


def performers(record: dict) -> str | None:
    item = record.get("item") or {}
    names = []
    for c in as_list(item.get("contributors")):
        role = (re.search(r"\(([^)]*)\)\s*$", c) or re.search(r"--\s*([^-]+)$", c))
        if role and any(r in role.group(1).lower() for r in PERFORMER_ROLES):
            names.append(_person(c.split(" -- ")[0]))
    if not names:
        names = [_person(n) for n in as_list(record.get("contributor_primary"))]
    names = list(dict.fromkeys(n for n in names if n))
    return ", ".join(names[:3]) or None


def to_ia_shape(record: dict, detail: dict | None, coll: dict | None, cfg: LocConfig) -> dict:
    """A loc.gov record as {metadata, files}, the shape `classify_item` expects."""
    item = record.get("item") or {}
    genres = [g for g in as_list(item.get("genre"))]
    low_genres = {g.lower() for g in genres}
    subjects = as_list(item.get("subjects")) or as_list(record.get("subject"))
    tags = genres + subjects + list((coll or {}).get("tags") or [])
    # Every item here is a field recording; style tags (or the region) pick the
    # genre, and "field" is only the fallback (see classify_record). "African
    # Americans" is a US subject heading, not an African region.
    # Place headings ("United States -- Florida -- Seminole County") belong in
    # coverage: as tags, county names like Seminole read as Indigenous content.
    tags = [t for t in tags if not FIELD_TAG.search(t) and not AFAM_TAG.search(t) and " -- " not in t]
    if low_genres & cfg.music_genres:
        # A song with a spoken introduction is still a song.
        tags = [t for t in tags if t.lower() not in cfg.spoken_genres]
    else:
        tags += [g for g in genres if g.lower() in cfg.spoken_genres]
    title = item.get("title") or record.get("title") or ""
    files = [{"name": url, "format": "VBR MP3", "source": "original", "title": seg}
             for url, seg in audio_urls(record, detail)]
    rights = item.get("rights") or record.get("rights") or ""
    meta = {
            "identifier": item_id(record),
            "title": title,
            "creator": performers(record),
            "date": record.get("date") or item.get("date") or "",
            "subject": tags,
            "description": as_list(item.get("notes")) + as_list(item.get("summary")),
            "language": as_list(item.get("language")) or as_list(record.get("language")),
            "coverage": "; ".join(as_list(item.get("location")) or as_list(record.get("location"))),
            "rights": " ".join(as_list(rights)),
            "collection": [s for s in as_list(record.get("partof"))],
    }
    return {"metadata": {k: _fix(v) for k, v in meta.items()},
            "files": [{**f, "title": _fix(f["title"])} for f in files]}


def page_url(ident: str) -> str:
    return f"{BASE}/item/{ident}/"


def classify_record(ident: str, coll_slug: str, record: dict, detail: dict | None,
                    rules: Rules, cfg: LocConfig) -> ItemResult:
    coll = cfg.by_slug.get(coll_slug)
    item = record.get("item") or {}
    if coll and coll.get("only_field_recordings") and \
            "field recordings" not in {g.lower() for g in as_list(item.get("genre"))}:
        return ItemResult(id=f"{SOURCE}:{ident}", status=EXCLUDE,
                          reasons=["loc: not a field recording (sheet music or Jukebox disc)"],
                          title=item.get("title") or "")
    data = to_ia_shape(record, detail, coll, cfg)
    meta = data["metadata"]
    notes = " ".join(as_list(meta["description"])).lower()
    poor = find_words(notes, cfg.poor_quality_phrases)
    if poor:
        return ItemResult(id=f"{SOURCE}:{ident}", status=EXCLUDE,
                          reasons=[f"poor transfer: {poor[0]}"], title=meta["title"])
    if not data["files"]:
        return ItemResult(id=f"{SOURCE}:{ident}", status=EXCLUDE,
                          reasons=["no playable MP3"], title=meta["title"])
    res = classify_item(ident, "other", data, rules)
    res.id = f"{SOURCE}:{ident}"
    for t in res.tracks:
        t.id = res.id
        t.url = t.file
        t.file = t.file.rsplit("/", 1)[-1]
        t.page = page_url(ident)
        t.picture = None
        t.source = "Library of Congress"
    if res.status == INCLUDE or res.status == FLAG:
        rights = meta["rights"].lower()
        checked = (coll or {}).get("rights_checked")
        if not checked and not any(p in rights for p in cfg.rights_ok):
            note = f"loc rights statement needs checking: {meta['rights'][:80] or 'none'}"
            res.reasons = ([] if res.status == INCLUDE else res.reasons) + [note]
            res.status = FLAG
    fallback = (coll or {}).get("genre") or "field"
    if res.genre in (None, "world", "american"):
        for t in res.tracks:
            if t.genre in ("world", "american"):
                t.genre = fallback
        res.genre = fallback
    return res


def worth_detail(ident: str, coll: str, record: dict, rules: Rules, cfg: LocConfig) -> bool:
    """Would this item be listed (or reviewed) if it had an MP3? Checked with a placeholder file."""
    probe = dict(record, resources=[{"audio": "https://tile.loc.gov/probe.mp3"}])
    return classify_record(ident, coll, probe, None, rules, cfg).status != EXCLUDE


def classify_all(store, rules: Rules, cfg: LocConfig | None = None) -> list[ItemResult]:
    cfg = cfg or load_config()
    seen = set()
    out = []
    for ident, coll, record, detail in store.ext_items(SOURCE):
        if ident in seen:        # an item can be in several collections
            continue
        seen.add(ident)
        out.append(classify_record(ident, coll, record, detail, rules, cfg))
    return out


def dump_records(store, path: Path) -> None:
    """Debug helper: write the cached records as JSON lines."""
    with open(path, "w", encoding="utf-8") as fh:
        for ident, coll, record, detail in store.ext_items(SOURCE):
            fh.write(json.dumps({"id": ident, "coll": coll, "record": record, "detail": detail}) + "\n")
