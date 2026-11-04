"""Finna (finna.fi) as a source of recordings: Finnish 78s and field recordings.

Finna's open search API needs no key. Only online sound records whose usage
rights code is open (rules/finna.yaml) are fetched, 100 per request, one
request at a time, into the same SQLite cache as everything else.

As with loc.gov, each record is reshaped into the Internet Archive form and
classified by `classify_item`, so the 1970 date rule, spoken-word exclusion
and restricted/ceremonial/Indigenous checks all apply.
"""
from __future__ import annotations

import logging
import re
from dataclasses import dataclass, field
from pathlib import Path

import yaml

from .classify import EXCLUDE, FLAG, INCLUDE, ItemResult, classify_item
from .config import RULES_DIR, Rules
from .loc import LocClient, fix_text

log = logging.getLogger(__name__)

SOURCE = "finna"
PAGE_SIZE = 100
FIELDS = ("id", "title", "nonPresenterAuthors", "year", "formats", "subjects", "genres",
          "onlineUrls", "urls", "buildings", "languages", "summary")
PART_RE = re.compile(r"_(\d+)\.mp3$", re.I)


@dataclass
class FinnaConfig:
    api: str
    rights: list[str]
    holders: dict[str, str]
    folk_subjects: frozenset[str]
    spoken_subjects: frozenset[str]
    indigenous_subjects: frozenset[str]
    ceremonial_subjects: frozenset[str]
    user_agent: str = "OldFolkAirListBuilder/1.0"
    pause: float = 1.0
    extra: dict = field(default_factory=dict)


def load_config(path: Path = RULES_DIR / "finna.yaml") -> FinnaConfig:
    with open(path, encoding="utf-8") as fh:
        d = yaml.safe_load(fh) or {}
    low = lambda xs: frozenset(str(x).lower() for x in (xs or []))  # noqa: E731
    return FinnaConfig(
        api=d.get("api", "https://api.finna.fi/v1/search"),
        rights=list(d.get("rights") or []),
        holders=dict(d.get("holders") or {}),
        folk_subjects=low(d.get("folk_subjects")),
        spoken_subjects=low(d.get("spoken_subjects")),
        indigenous_subjects=low(d.get("indigenous_subjects")),
        ceremonial_subjects=low(d.get("ceremonial_subjects")),
        user_agent=str(d.get("user_agent") or "OldFolkAirListBuilder/1.0"),
        pause=float(d.get("pause", 1.0)),
    )


# --------------------------------------------------------------------------- harvesting

def harvest(store, cfg: FinnaConfig, client: LocClient | None = None) -> dict:
    """Fetch every matching record (cheap: ~30 requests). Re-run to refresh."""
    client = client or LocClient(cfg)
    found = 0
    for rights in cfg.rights:
        page = 1
        while True:
            params = [("filter[]", 'format_ext_str_mv:"0/Sound/"'),
                      ("filter[]", 'online_boolean:"1"'),
                      ("filter[]", f'usage_rights_ext_str_mv:"{rights}"'),
                      ("limit", PAGE_SIZE), ("page", page), ("sort", "id asc")]
            params += [("field[]", f) for f in FIELDS]
            try:
                d = client.get_json(cfg.api, params)
            except Exception as exc:  # noqa: BLE001 - next run picks it up
                log.warning("finna %s page %d failed (%s)", rights, page, exc)
                break
            records = d.get("records") or []
            store.save_ext_records(SOURCE, rights, [(r["id"], r) for r in records if r.get("id")])
            found += len(records)
            log.info("finna %s page %d: %d records", rights, page, len(records))
            if page * PAGE_SIZE >= int(d.get("resultCount") or 0) or not records:
                break
            page += 1
    return {"records": found, **store.ext_stats(SOURCE)}


# --------------------------------------------------------------------------- reshaping

def holder(record: dict, cfg: FinnaConfig) -> str | None:
    for b in record.get("buildings") or []:
        code = str(b.get("value", "")).strip("/").split("/")
        if len(code) > 1 and code[1] in cfg.holders:
            return cfg.holders[code[1]]
    return None


def _person(name: str) -> str:
    name = re.sub(r"\s*\((esitt|säv|san|sov)\.?\)\.?\s*$", "", name).strip()
    if name.count(",") == 1:
        last, first = [p.strip() for p in name.split(",")]
        if first and last:
            return f"{first} {last}"
    return name


def performers(record: dict) -> str | None:
    names = []
    for a in record.get("nonPresenterAuthors") or []:
        n, role = a.get("name", ""), str(a.get("role", "")).lower()
        if "(esitt" in n or role in ("esittäjä", "prf", "performer"):
            names.append(_person(n))
    return ", ".join(dict.fromkeys(names[:3])) or None


def audio_files(record: dict) -> list[tuple[str, str]]:
    """[(mp3 url, file name)]; several transfers of the same part (`..._01.mp3`) count once."""
    urls = [u["url"] for u in record.get("onlineUrls") or [] if str(u.get("url", "")).lower().endswith(".mp3")]
    urls += [u["url"] for u in record.get("urls") or []
             if str(u.get("desc", "")).lower().endswith(".mp3") or str(u.get("url", "")).lower().endswith(".mp3")]
    out, parts = [], set()
    for u in dict.fromkeys(urls):
        desc = next((x.get("desc") for x in record.get("urls") or [] if x.get("url") == u), None) or u
        name = desc.rsplit("/", 1)[-1]
        m = PART_RE.search(name)
        part = m.group(1) if m else name
        if part in parts:
            continue
        parts.add(part)
        out.append((u, name))
    return out


def subjects(record: dict) -> list[str]:
    out = []
    for s in record.get("subjects") or []:
        for part in s if isinstance(s, list) else [s]:
            out.append(str(part).split(" - ")[0].strip())
    return out


def to_ia_shape(record: dict, cfg: FinnaConfig) -> dict:
    subs = subjects(record)
    low = {s.lower() for s in subs}
    tags = list(subs)
    if low & cfg.folk_subjects:
        tags.append("folk")
    if low & cfg.spoken_subjects:
        tags.append("spoken word")
    desc = list(record.get("summary") or [])
    title = fix_text(record.get("title") or "")
    files = audio_files(record)
    return {
        "metadata": {
            "identifier": record.get("id"),
            "title": title,
            "creator": performers(record),
            "date": str(record.get("year") or ""),
            "subject": tags,
            "description": desc,
            "language": list(record.get("languages") or []),
        },
        "files": [{"name": name, "url": u, "format": "VBR MP3", "source": "original",
                   "title": (f"{title} ({n})" if len(files) > 1 else None)}
                  for n, (u, name) in enumerate(files, 1)],
    }


def page_url(ident: str) -> str:
    return f"https://www.finna.fi/Record/{ident}"


def classify_record(ident: str, record: dict, rules: Rules, cfg: FinnaConfig) -> ItemResult:
    rid = f"{SOURCE}:{ident}"
    name = holder(record, cfg)
    if not name:
        return ItemResult(id=rid, status=EXCLUDE, reasons=["finna: holding institution not listed"],
                          title=record.get("title") or "")
    data = to_ia_shape(record, cfg)
    if not data["files"]:
        return ItemResult(id=rid, status=EXCLUDE, reasons=["no playable MP3"], title=data["metadata"]["title"])
    res = classify_item(ident, "other", data, rules)
    res.id = rid
    urls = {f["name"]: f["url"] for f in data["files"]}
    for t in res.tracks:
        t.id = rid
        t.url = urls[t.file]
        t.page = page_url(ident)
        t.picture = None
        t.source = name
        if t.region == "Unknown":
            t.region = "Nordic"          # Finnish institutions: Finnish/Finnic traditions
            if t.genre == "world":
                t.genre = rules.region_genre.get("Nordic", "nordic")
    if res.region == "Unknown":
        res.region = "Nordic"
    low = {x.lower() for x in subjects(record)}
    extra = []
    if low & cfg.indigenous_subjects:
        extra.append(f"Indigenous recording, check community protocols: {', '.join(sorted(low & cfg.indigenous_subjects))}")
    if low & cfg.ceremonial_subjects:
        extra.append(f"ceremonial content, check restrictions: {', '.join(sorted(low & cfg.ceremonial_subjects))}")
    if extra and res.status != EXCLUDE:
        res.reasons = ([] if res.status == INCLUDE else res.reasons) + extra
        res.status = FLAG
    if res.tracks and res.genre == "world":
        res.genre = res.tracks[0].genre
    assert res.status in (INCLUDE, FLAG, EXCLUDE)
    return res


def classify_all(store, rules: Rules, cfg: FinnaConfig | None = None) -> list[ItemResult]:
    cfg = cfg or load_config()
    return [classify_record(ident, record, rules, cfg) for ident, _c, record, _d in store.ext_items(SOURCE)]
