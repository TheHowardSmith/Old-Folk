"""Linguistic Survey of India gramophone recordings (1913-1929), via DSAL.

The Digital South Asia Library's open search API lists each recording with its
language, place, speaker and file. Only songs are used. The recordings are
treated like 78s (undated individually, all made 1913-1929), and go through
the normal classifier for the remaining checks.
"""
from __future__ import annotations

import logging
import re
from dataclasses import dataclass
from pathlib import Path

import yaml

from .classify import EXCLUDE, INCLUDE, FLAG, ItemResult, classify_item
from .config import RULES_DIR, Rules
from .loc import LocClient

log = logging.getLogger(__name__)
SOURCE = "dsal"


@dataclass
class DsalConfig:
    api: str
    audio_base: str
    page: str
    credit: str
    queries: list[str]
    song_pattern: re.Pattern
    user_agent: str = "OldFolkAirListBuilder/1.0"
    pause: float = 1.0


def load_config(path: Path = RULES_DIR / "dsal.yaml") -> DsalConfig:
    with open(path, encoding="utf-8") as fh:
        d = yaml.safe_load(fh) or {}
    return DsalConfig(
        api=d["api"], audio_base=d["audio_base"], page=d["page"], credit=d["credit"],
        queries=list(d.get("queries") or ["song"]),
        song_pattern=re.compile(d.get("song_pattern") or r"\bsongs?\b", re.I),
        user_agent=str(d.get("user_agent") or "OldFolkAirListBuilder/1.0"),
        pause=float(d.get("pause", 1.0)),
    )


def harvest(store, cfg: DsalConfig, client: LocClient | None = None) -> dict:
    client = client or LocClient(cfg)
    found = 0
    for q in cfg.queries:
        try:
            d = client.get_json(cfg.api, {"q": q})
        except Exception as exc:  # noqa: BLE001
            log.warning("dsal search %r failed: %s", q, exc)
            continue
        rows = [r for r in d.get("results") or [] if r.get("recording_file")]
        store.save_ext_records(SOURCE, q, [(r["recording_file"], r) for r in rows])
        found += len(rows)
    return {"records": found, **store.ext_stats(SOURCE)}


def classify_record(ident: str, rec: dict, rules: Rules, cfg: DsalConfig) -> ItemResult:
    rid = f"{SOURCE}:{ident}"
    title = (rec.get("recording_title") or "").strip()
    language = (rec.get("language") or "").strip()
    if not cfg.song_pattern.search(title):
        return ItemResult(id=rid, status=EXCLUDE, reasons=["spoken word / non-music: LSI speech sample"], title=title)
    shown = f"{title} ({language})" if language and language.lower() not in title.lower() else title
    # The region check reads the title, so keep the language name in it.
    performer = re.split(r",\s*(?:son|daughter|wife) of\b", rec.get("narrator") or "", flags=re.I)[0].strip() or None
    place = ", ".join(x for x in (rec.get("location_populated_place"), rec.get("districts_where_spoken"),
                                  rec.get("location_province")) if x)
    fake = re.sub(r"\.\w+$", "", ident) + ".mp3"       # pick_files only takes .mp3 names
    data = {
        "metadata": {
            "identifier": ident, "title": shown, "creator": performer,
            "subject": ["folk songs", "traditional"],
            "description": [f"Linguistic Survey of India, {rec.get('gramophone_records_volume') or ''}. {place}"],
            "language": [language],
        },
        "files": [{"name": fake, "format": "VBR MP3", "source": "original"}],
    }
    res = classify_item(ident, "78rpm", data, rules)
    res.id = rid
    for t in res.tracks:
        t.id, t.url, t.page, t.source, t.picture = rid, cfg.audio_base + ident, cfg.page, cfg.credit, None
        if t.region in ("Unknown", "USA"):
            t.region = "South Asia"
        if t.region == "South Asia":
            t.genre = "southasia"
    if res.status in (INCLUDE, FLAG) and res.tracks:
        res.region, res.genre = res.tracks[0].region, res.tracks[0].genre
    return res


def classify_all(store, rules: Rules, cfg: DsalConfig | None = None) -> list[ItemResult]:
    cfg = cfg or load_config()
    return [classify_record(i, rec, rules, cfg) for i, _c, rec, _d in store.ext_items(SOURCE)]
