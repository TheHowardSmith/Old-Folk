"""US Pre-1972 Sound Recording Schedules: recordings claimed by rights owners.

The index (cache/pre1972_schedules.jsonl) is downloaded by
scripts/fetch_pre1972_schedules.py. A disc matches when its first song title
equals a scheduled title and the performer shares a name word with the
scheduled artist. Title-only matches are ignored (common titles).
"""
from __future__ import annotations

import json
import re
from collections import Counter, defaultdict
from pathlib import Path

GENERIC = set("""the and his her their with orchestra orch band trio quartet quintet sextet ensemble
company co chorus choir singers players boys girls brothers sisters family string strings
featuring feat vocal refrain accompaniment acc by of de la le el los las y e et und mit
mr mrs miss mme dr sir jr sr""".split())


def norm(s: str) -> str:
    s = re.sub(r"\(.*?\)|\[.*?\]", " ", str(s).lower())
    s = re.sub(r"[^\w ]+", " ", s)
    return re.sub(r"\s+", " ", s).strip()


def first_title(title: str) -> str:
    t = str(title).replace('"', "").strip()
    t = re.split(r";|\s-\s|\s/\s", t)[0]
    t = re.sub(r"^\(?[a-d1-4][.)]\s*", "", t, flags=re.I)
    return norm(t)


def name_words(s: str) -> set[str]:
    return {w for w in norm(s).split() if len(w) > 2 and w not in GENERIC}


def _as_list(v):
    if v is None:
        return []
    return [x for x in (v if isinstance(v, list) else [v]) if x and str(x).strip()]


class ScheduleIndex:
    def __init__(self, by_title: dict[str, list[tuple[frozenset, str]]]):
        self.by_title = by_title

    @classmethod
    def load(cls, path: Path) -> "ScheduleIndex":
        by_title: dict[str, list] = defaultdict(list)
        for line in open(path, encoding="utf-8"):
            d = json.loads(line)
            artists = frozenset(name_words(" ".join(_as_list(d.get("artist")) + _as_list(d.get("alt_artist")))))
            owner = ", ".join(_as_list(d.get("rights_owner")))
            for t in _as_list(d.get("title")) + _as_list(d.get("alt_title")):
                key = first_title(t)
                if key:
                    by_title[key].append((artists, owner))
        return cls(dict(by_title))

    def match(self, title: str, performer: str) -> tuple[str, str]:
        """('yes', owner) / ('title only', owner) / ('no', '')."""
        cands = self.by_title.get(first_title(title), [])
        if not cands:
            return "no", ""
        words = name_words(performer)
        hits = [owner for aw, owner in cands if words and aw & words]
        if hits:
            return "yes", Counter(hits).most_common(1)[0][0]
        return "title only", Counter(o for _, o in cands).most_common(1)[0][0]
