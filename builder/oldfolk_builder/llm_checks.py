"""Rights results from the model-checked passes over the check list.

rights/sonnet/w*.jsonl   first pass on songs whose writers MusicBrainz couldn't settle:
                         protected / clear / unknown, keyed by item id
rights/sonnet/c*.jsonl   first pass on possible covers of protected songs (by title):
                         pop / folk / unknown, keyed "cover:<title>"
rights/opus/v*.jsonl     second, careful pass on every first-pass protected or pop
                         verdict: confirmed / rejected / unsure

Only a confirmed second-pass verdict removes anything. A first-pass "clear" (or a
rejected one whose writer died by the cutoff) takes the song off the check list.
Everything else stays in the list and on the check list.
"""
from __future__ import annotations

import json
import re
from dataclasses import dataclass, field
from pathlib import Path


@dataclass
class LlmChecks:
    protected: dict[str, str] = field(default_factory=dict)        # item -> "Name (d. 1964)"
    cleared: set[str] = field(default_factory=set)                 # items whose writer check is settled
    covers: dict[tuple[str, str], str] = field(default_factory=dict)   # (item, title) -> "Name (d. 1990)"


_DANCE_PREFIX = re.compile(r"(?i)^(tango|rumba|rhumba|bolero|samba|paso ?doble|beguine|mambo|conga)\s*[:-]?\s+")
_NOTE = re.compile(r"\((restored|instrumental|vocal|fox[- ]?trot|tango|rumba|bolero|waltz|from [^)]*)\)", re.I)


def title_parts(title: str) -> list[str]:
    """The whole title and each song in a medley ("1. Rudolph...; 2. White Christmas"),
    lower-cased and without notes like "(restored)", for matching cover verdicts."""
    out = [re.sub(r"\s+", " ", (title or "").strip().lower())]
    for p in re.split(r"\s*(?:;|/|\b\d\.\s)\s*", title or ""):
        p = _NOTE.sub("", p)
        p = re.sub(r"^\s*\d+[.)]\s*", "", p).strip(" \"'“”-–;,.")
        p = _DANCE_PREFIX.sub("", p)
        p = re.split(r"\s+-\s+", p)[0]
        p = re.sub(r"\s+", " ", p).strip().lower()
        if len(p) >= 3 and p not in out:
            out.append(p)
    return out


def _lines(path: Path):
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if line:
            try:
                yield json.loads(line)
            except ValueError:
                continue


def _who(r: dict) -> str:
    died = r.get("died")
    when = "living" if died == "living" else (f"d. {died}" if died else "death year not given")
    return f"{r.get('writer') or 'writer'} ({when})"


def load(root: Path | None, cutoff: int = 1955) -> LlmChecks:
    out = LlmChecks()
    if not root or not root.exists():
        return out
    inputs: dict[str, dict] = {}
    first: dict[str, dict] = {}
    for f in sorted((root / "sonnet").glob("*.jsonl")):
        for r in _lines(f):
            first[r["id"]] = r
    for f in sorted((root / "opus").glob("v*.jsonl")):
        for r in _lines(f):
            inputs[r["id"]] = r
    # Cover inputs carry the item ids for each title; they are kept beside the results.
    cover_items: dict[str, list[str]] = {}
    for f in sorted((root / "inputs").glob("c*.jsonl")) if (root / "inputs").exists() else []:
        for r in _lines(f):
            cover_items.setdefault(r["id"], []).extend(r.get("items") or [])
    for key, r in first.items():
        second = inputs.get(key)
        is_cover = key.startswith("cover:")
        if second and second.get("check") == "confirmed":
            if is_cover:
                title = key.split(":", 1)[1].split("#", 1)[0]   # "cover:white christmas#medleys"
                for item in cover_items.get(key, []):
                    out.covers[(item, title)] = _who(second)
            else:
                out.protected[key] = _who(second)
        elif not is_cover:
            died = (second or {}).get("died")
            if r.get("verdict") == "clear" or (second and second.get("check") == "rejected"
                                                and isinstance(died, int) and died <= cutoff):
                out.cleared.add(key)
    return out
