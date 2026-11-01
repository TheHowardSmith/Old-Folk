"""Load the YAML rule files that drive searching and classification."""
from __future__ import annotations

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
    restricted_phrases: tuple[str, ...]
    ceremonial_words: tuple[str, ...]
    indigenous_words: tuple[str, ...]
    reissue_words: tuple[str, ...]
    traditional_composers: frozenset[str]
    min_seconds: float
    max_seconds: float
    regions: dict[str, dict[str, tuple[str, ...]]]
    genre_region: dict[str, str]
    exclude_conditions: frozenset[str] = frozenset()
    flag_named_writers: bool = True
    instruction_ensembles: tuple[str, ...] = ()
    band_tags: frozenset[str] = frozenset()
    band_words: tuple[str, ...] = ()
    overrides: dict = field(default_factory=dict)

    # Tags that only count as folk evidence on 78rpm discs (on later LPs they
    # usually mean commercial country/pop).
    tags_78_only: frozenset[str] = frozenset({"country", "yodeling", "old time"})


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
        restricted_phrases=tuple(str(p).lower() for p in cls.get("restricted_phrases") or []),
        ceremonial_words=tuple(str(p).lower() for p in cls.get("ceremonial_words") or []),
        indigenous_words=tuple(str(p).lower() for p in cls.get("indigenous_words") or []),
        reissue_words=tuple(str(p).lower() for p in cls.get("reissue_words") or []),
        traditional_composers=_lower_set(cls.get("traditional_composers")),
        min_seconds=float(cls.get("min_seconds", 30)),
        max_seconds=float(cls.get("max_seconds", 2400)),
        regions=regions,
        genre_region=dict(reg.get("genre_region") or {}),
        exclude_conditions=_lower_set(cls.get("exclude_conditions")),
        flag_named_writers=bool(cls.get("flag_named_writers", True)),
        instruction_ensembles=tuple(str(p).lower() for p in cls.get("instruction_ensembles") or []),
        band_tags=_lower_set(cls.get("band_tags")),
        band_words=tuple(str(p).lower() for p in cls.get("band_words") or []),
        overrides=overrides,
    )
