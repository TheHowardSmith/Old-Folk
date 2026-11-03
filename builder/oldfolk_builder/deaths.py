"""Death years of credited writers (songwriters/composers), for the composition rule.

Writer names come from the cached item metadata ("Writer:" credits on 78s,
composer fields on files). Each name is looked up once in the Library of
Congress name authority, the German National Library (GND) and Wikidata
(scripts/rights_check.py) and the result is kept in a JSON file between runs.

A track with credited writers stays in the app only if every writer is known
to have died at least 70 years ago (classify.py); everything else waits in the
review set. Surname-only credits ("Beach") usually can't be resolved, so they
stay in review.
"""
from __future__ import annotations

import json
import logging
import sys
import time
from pathlib import Path

from .config import BUILDER_DIR

log = logging.getLogger(__name__)


def load(path: Path | None) -> dict[str, dict]:
    if path and path.exists():
        return json.loads(path.read_text("utf-8"))
    return {}


def collect(store, rules) -> set[str]:
    from .classify import extract_writers
    from .tracks import pick_files
    names: set[str] = set()
    for _i, _src, _kind, data in store.fetched(sample_only=False):
        names.update(extract_writers(data.get("metadata", {}), pick_files(data.get("files", [])), rules))
    return names


def lookup_all(names: set[str], previous: Path | None, out: Path, minutes: float | None = None) -> dict:
    sys.path.insert(0, str(BUILDER_DIR / "scripts"))
    import rights_check  # noqa: E402  (builder/scripts)
    known = load(previous)
    todo = sorted(n for n in names if n not in known)
    log.info("writer deaths: %d names, %d known, %d to look up", len(names), len(known), len(todo))
    deadline = time.monotonic() + minutes * 60 if minutes else None
    done = 0
    for name in todo:
        if deadline and time.monotonic() > deadline:
            break
        try:
            r = rights_check.writer_death(name)
        except Exception as exc:  # noqa: BLE001
            log.warning("lookup %s: %s", name, exc)
            continue
        known[name] = {"status": r.get("status"), "died": r.get("died"), "sources": r.get("sources")}
        done += 1
        if done % 100 == 0:
            out.write_text(json.dumps(known, ensure_ascii=False, indent=0), "utf-8")
            log.info("looked up %d/%d", done, len(todo))
    out.write_text(json.dumps(known, ensure_ascii=False, indent=0), "utf-8")
    dead_clear = sum(1 for v in known.values() if v.get("status") == "dead")
    return {"names": len(names), "known": len(known), "looked_up": done, "dead": dead_clear,
            "remaining": len(todo) - done}
