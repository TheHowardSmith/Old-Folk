"""Load the YAML rule files that drive searching and classification."""
from __future__ import annotations

import re
from dataclasses import dataclass, field
from pathlib import Path

import yaml

BUILDER_DIR = Path(__file__).resolve().parent.parent
RULES_DIR = BUILDER_DIR / "rules"


def _load(path: Path) -> dict:
    with open(path, encoding="utf-8") as fh:
        return yaml.safe_load(fh) or {}


def _lower_set(values) -> frozenset[str]:
    return frozenset(str(v).strip().lower() for v in (values or []))


@dataclass
class Rules:
    year_max: int
    sources: list[dict]
    genres: dict[str, str]
    genre_tags: dict[str, frozenset[str]]
    generic_folk_tags: frozenset[str]
    region_genre: dict[str, str]
    exclude_tags: frozenset[str]
    spoken_tags: frozenset[str]
    synthetic_tags: frozenset[str]
    exclude_collections: frozenset[str]
    review_collections: frozenset[str]
    restricted_phrases: tuple[str, ...]
    ceremonial_words: tuple[str, ...]
    indigenous_words: tuple[str, ...]
    reissue_words: tuple[str, ...]
    traditional_composers: frozenset[str]
    min_seconds: float
    max_seconds: float
    regions: dict[str, dict[str, tuple[str, ...]]]
    genre_region: dict[str, str]
    map_regions: dict = field(default_factory=dict)
    exclude_conditions: frozenset[str] = frozenset()
    flag_named_writers: bool = True
    instruction_ensembles: tuple[str, ...] = ()
    exclude_us_claimed: bool = False
    us_public_domain_year: int = 1925
    schedules: object = None          # ScheduleIndex, loaded on first use (see load_schedules)
    schedules_path: Path | None = None
    band_tags: frozenset[str] = frozenset()
    band_words: tuple[str, ...] = ()
    exclude_id_patterns: tuple[tuple[re.Pattern, str], ...] = ()
    traditional_art_tags: frozenset[str] = frozenset()
    traditional_art_words: tuple[str, ...] = ()
    traditional_art_regions: frozenset[str] = frozenset()
    western_art_words: tuple[str, ...] = ()
    ethnic_record_tags: frozenset[str] = frozenset()
    ethnic_waivable_tags: frozenset[str] = frozenset()
    weak_generic_tags: frozenset[str] = frozenset()
    pop_tags: frozenset[str] = frozenset()
    overrides: dict = field(default_factory=dict)
    # Credited writers: a track stays in the app only if every writer died at
    # least `writer_life_plus` years ago (deaths looked up by `writer-deaths`).
    writer_rule: bool = False
    writer_life_plus: int = 70
    writer_deaths: dict | None = None
    writer_deaths_path: Path | None = None
    writer_search: dict | None = None
    writer_search_path: Path | None = None
    # Model-checked rights results (rights/sonnet, rights/opus): see llm_checks.py.
    llm_checks: object = None
    llm_checks_dir: Path | None = None

    def load_llm_checks(self):
        if self.llm_checks is None:
            from .llm_checks import load
            self.llm_checks = load(self.llm_checks_dir)
        return self.llm_checks

    def load_writer_search(self) -> dict:
        """Song-specific writer results (writer_search.py), item -> result."""
        if self.writer_search is None:
            from .writer_search import load
            self.writer_search = load(self.writer_search_path)
        return self.writer_search

    def load_writer_deaths(self) -> dict:
        if self.writer_deaths is None:
            from .deaths import load
            self.writer_deaths = load(self.writer_deaths_path)
        return self.writer_deaths

    def load_schedules(self):
        """The US pre-1972 schedules index, loaded once on first use."""
        if self.schedules is None and self.schedules_path is not None:
            from .schedules import ScheduleIndex
            self.schedules = ScheduleIndex.load(self.schedules_path)
        return self.schedules

    # Tags that only count as folk evidence on 78rpm discs (on later LPs they
    # usually mean commercial country/pop; with a folk tag as well they file the
    # record under Country & Western, see classify.py).
    tags_78_only: frozenset[str] = frozenset({"country", "yodeling", "old time"})


def _schedules_path(cls: dict) -> Path | None:
    if not cls.get("exclude_us_claimed_recordings"):
        return None
    path = BUILDER_DIR / "cache" / "pre1972_schedules.jsonl"
    if not path.exists():
        import logging
        logging.getLogger(__name__).warning(
            "%s not found: US-claimed recordings will NOT be excluded "
            "(run scripts/fetch_pre1972_schedules.py)", path)
        return None
    return path


def load_rules(rules_dir: Path = RULES_DIR, overrides_path: Path | None = None) -> Rules:
    src = _load(rules_dir / "sources.yaml")
    cls = _load(rules_dir / "classify.yaml")
    reg = _load(rules_dir / "regions.yaml")
    if overrides_path is None:
        overrides_path = BUILDER_DIR / "overrides.yaml"
    overrides = _load(overrides_path) if overrides_path.exists() else {}

    regions = {}
    for name, spec in (reg.get("regions") or {}).items():
        regions[name] = {
            k: tuple(str(v).lower() for v in (spec.get(k) or []))
            for k in ("keywords", "languages", "coverage")
        }

    return Rules(
        year_max=int(src.get("year_max", 1970)),
        sources=list(src.get("sources") or []),
        genres=dict(cls["genres"]),
        genre_tags={g: _lower_set(t) for g, t in cls["genre_tags"].items()},
        generic_folk_tags=_lower_set(cls.get("generic_folk_tags")),
        region_genre=dict(cls.get("region_genre") or {}),
        exclude_tags=_lower_set(cls.get("exclude_tags")),
        spoken_tags=_lower_set(cls.get("spoken_tags")),
        synthetic_tags=_lower_set(cls.get("synthetic_tags")),
        exclude_collections=_lower_set(cls.get("exclude_collections")),
        review_collections=_lower_set(cls.get("review_collections")),
        restricted_phrases=tuple(str(p).lower() for p in cls.get("restricted_phrases") or []),
        ceremonial_words=tuple(str(p).lower() for p in cls.get("ceremonial_words") or []),
        indigenous_words=tuple(str(p).lower() for p in cls.get("indigenous_words") or []),
        reissue_words=tuple(str(p).lower() for p in cls.get("reissue_words") or []),
        traditional_composers=_lower_set(cls.get("traditional_composers")),
        min_seconds=float(cls.get("min_seconds", 30)),
        max_seconds=float(cls.get("max_seconds", 2400)),
        regions=regions,
        genre_region=dict(reg.get("genre_region") or {}),
        map_regions=dict(cls.get("map_regions") or {}),
        exclude_conditions=_lower_set(cls.get("exclude_conditions")),
        flag_named_writers=bool(cls.get("flag_named_writers", True)),
        exclude_us_claimed=bool(cls.get("exclude_us_claimed_recordings", False)),
        us_public_domain_year=int(cls.get("us_public_domain_year", 1925)),
        schedules_path=_schedules_path(cls),
        instruction_ensembles=tuple(str(p).lower() for p in cls.get("instruction_ensembles") or []),
        band_tags=_lower_set(cls.get("band_tags")),
        band_words=tuple(str(p).lower() for p in cls.get("band_words") or []),
        exclude_id_patterns=tuple((re.compile(str(e["pattern"]), re.I), str(e["reason"]))
                                  for e in cls.get("exclude_id_patterns") or []),
        traditional_art_tags=_lower_set(cls.get("traditional_art_tags")),
        traditional_art_words=tuple(str(p).lower() for p in cls.get("traditional_art_words") or []),
        traditional_art_regions=frozenset(cls.get("traditional_art_regions") or []),
        western_art_words=tuple(str(p).lower() for p in cls.get("western_art_words") or []),
        ethnic_record_tags=_lower_set(cls.get("ethnic_record_tags")),
        ethnic_waivable_tags=_lower_set(cls.get("ethnic_waivable_tags")),
        weak_generic_tags=_lower_set(cls.get("weak_generic_tags")),
        pop_tags=_lower_set(cls.get("pop_tags")),
        overrides=overrides,
        writer_rule=bool(cls.get("require_writers_died", False)),
        writer_life_plus=int(cls.get("writer_life_plus", 70)),
        writer_deaths_path=BUILDER_DIR / "cache-rights" / "writer_deaths.json",
        writer_search_path=BUILDER_DIR / "cache-rights" / "writer_search.jsonl",
        llm_checks_dir=BUILDER_DIR / "rights",
    )
