"""Decide whether an Internet Archive item belongs in the track list.

Every item ends up in exactly one bucket:

* include - clearly folk/traditional and clearly 1970 or earlier.
* flag    - probably fine but needs a human look (date doubts, composer
            credits, ceremonial content, mixed genre tags...). Flagged
            tracks go into a separate "review" shard set, not the app's list.
* exclude - not folk, too recent, spoken word, restricted, unplayable...

Pure functions only: everything here works on cached metadata, so the rules
can be tuned and re-run without touching the network.
"""
from __future__ import annotations

import html
import re
from dataclasses import dataclass, field

from .config import Rules
from .tracks import FileChoice, pick_files, pick_image

INCLUDE, FLAG, EXCLUDE = "include", "flag", "exclude"

FIRST_SOUND_RECORDING_YEAR = 1888   # earliest commercial / field cylinder era
LP_FIRST_YEAR = 1948
YEAR_RE = re.compile(r"(?<!\d)(1[89]\d\d|20\d\d)(?!\d)")
TAG_SPLIT = re.compile(r"[,;:/|]+")
WRITER_RE = re.compile(
    r"\b(?:(?:writers?|composers?|lyricists?)\s*:"
    r"|(?:words and music|music and words|written|composed|lyrics|words) by)"
    r"\s*\|?\s*([^|\n]{2,80})",
    re.I,
)
# Credits that are really instrumentation or genre notes, not writer names.
INSTRUMENT_RE = re.compile(
    r"\b(piano|vocals?|instrumental|bass|guitars?|drums?|trumpet|tenor|sax(ophone)?|orch(estra)?|"
    r"trombone|clarinet|harmonica|violin|caller|accordion|banjo|fiddle|mandolin|organ|cornet|flute|"
    r"polka|oberek|waltz|mazurka|hudba|sung by|music by|music|refrain|chorus|duet|quartet|trio|"
    r"ensemble|band|unknown performer|written by|words by|lyrics by)\b", re.I)
NOT_A_NAME = re.compile(r"^(none|n/?a|not? (listed|legible|given|shown)|none legible|illegible|"
                        r"unknown|unlisted|various|-+|\?+)$", re.I)


@dataclass
class Track:
    id: str                 # item identifier
    file: str
    title: str
    artist: str | None
    year: int | None
    region: str
    genre: str
    seconds: float | None = None
    picture: str | int | None = None


@dataclass
class ItemResult:
    id: str
    status: str
    reasons: list[str] = field(default_factory=list)
    genre: str | None = None
    region: str | None = None
    year: int | None = None
    year_note: str | None = None
    writers: list[str] = field(default_factory=list)
    tracks: list[Track] = field(default_factory=list)
    title: str = ""
    artist: str | None = None


# --------------------------------------------------------------------------- helpers

def as_list(value) -> list[str]:
    if value is None:
        return []
    if isinstance(value, list):
        return [str(v) for v in value if v is not None]
    return [str(value)]


def as_text(value) -> str:
    return " ; ".join(as_list(value))


def strip_html(text: str) -> str:
    text = re.sub(r"<br\s*/?>|</p>|<p>|</?b>|</?i>|</?strong>", " | ", text, flags=re.I)
    text = re.sub(r"<[^>]+>", " ", text)
    return re.sub(r"[ \t]+", " ", html.unescape(text))


def extract_tags(meta: dict) -> set[str]:
    """Lower-cased genre/subject values, split into individual tags."""
    tags: set[str] = set()
    for raw in as_list(meta.get("genre")) + as_list(meta.get("subject")):
        s = raw.lower().strip()
        # Discogs' umbrella genre must stay one tag, or "country" leaks out of it.
        s = s.replace("folk, world, & country", "\x00fwc\x00")
        for part in TAG_SPLIT.split(s):
            part = part.replace("\x00fwc\x00", "folk, world, & country").strip(" .&-")
            part = re.sub(r"^(and|&)\s+", "", part).strip()
            if part:
                tags.add(part)
    return tags


def has_word(text: str, phrase: str) -> bool:
    return re.search(r"(?<![\w'])" + re.escape(phrase) + r"(?![\w'])", text) is not None


def find_words(text: str, phrases) -> list[str]:
    return [p for p in phrases if has_word(text, p)]


def parse_year(meta: dict) -> tuple[int | None, str | None]:
    """Best guess of the original year from `date`/`year`.

    Returns (year, note). For ranges ("1954-61", "1920s") the LATEST possible
    year is returned, so the rights check is conservative.
    """
    raw = " ".join(as_list(meta.get("date")) + as_list(meta.get("year"))).strip()
    if not raw:
        return None, None
    s = raw.lower()
    m = re.search(r"(1[89]\d)(\d)?\s*s\b|(1[89]\d)\?", s)          # 1920s, 192?
    if m and not re.search(r"1[89]\d\d-\d\d", s):
        decade = int(m.group(1) or m.group(3)) * 10
        return decade + 9, f"decade '{raw}'"
    m = re.search(r"(1[89]\d\d)\s*[-–/]\s*(1[89]\d\d|\d\d)(?!-\d)", s)  # 1954-61, 1954-1961
    if m and not re.match(r"\d{4}-\d{2}-\d{2}", s):
        start, end = m.group(1), m.group(2)
        end_year = int(end) if len(end) == 4 else int(start[:2] + end)
        if end_year >= int(start):
            return end_year, f"range '{raw}'"
    years = [int(y) for y in YEAR_RE.findall(s)]
    if not years:
        return None, f"unparsed date '{raw}'"
    note = None
    if any(w in s for w in ("ca", "c.", "circa", "approx", "?")):
        note = f"approximate '{raw}'"
    return max(years), note


def extract_writers(meta: dict, files: list[FileChoice], rules: Rules) -> list[str]:
    """Named, non-traditional composer/writer credits."""
    found: list[str] = []
    for key in ("composer", "lyricist"):
        found += as_list(meta.get(key))
    found += [f.composer for f in files if f.composer]
    desc = strip_html(as_text(meta.get("description")))
    for m in WRITER_RE.finditer(desc):
        found.append(m.group(1))
    names: list[str] = []
    for chunk in found:
        for name in re.split(r";|/|\band\b|&|,(?=\s*[A-Z])", chunk):
            name = name.strip(" .,:-|()[]\"'")
            low = name.lower()
            if not name or len(name) < 2 or len(name) > 40 or len(name.split()) > 5:
                continue
            if NOT_A_NAME.match(name) or INSTRUMENT_RE.search(name):
                continue
            if low in rules.traditional_composers or low.startswith(("trad", "arr", "traditional")):
                continue
            if re.search(r"\b(trad|traditional|folk ?song|anon|unknown|public domain)\b", low):
                continue
            if name not in names:
                names.append(name)
    return names


SCRIPT_REGIONS = (
    (re.compile(r"[\u0370-\u03ff]"), "Southern Europe"),          # Greek
    (re.compile(r"[\u0400-\u04ff]"), "Central & Eastern Europe"), # Cyrillic
    (re.compile(r"[\u0530-\u05ff\u0600-\u06ff\u10a0-\u10ff]"), "Middle East & Caucasus"),
    (re.compile(r"[\u0900-\u0dff]"), "South Asia"),
    (re.compile(r"[\u0e00-\u0eff\u1000-\u109f\u1780-\u17ff]"), "Southeast Asia"),
    (re.compile(r"[\u3040-\u30ff\u4e00-\u9fff\uac00-\ud7af]"), "East Asia"),
    (re.compile(r"[\u1200-\u137f]"), "Africa"),                   # Ethiopic
)


def region_from_text(text: str, rules: Rules) -> str | None:
    """Region from culture keywords or writing system in a short text (title, tags)."""
    low = text.lower()
    for region, spec in rules.regions.items():
        if find_words(low, spec["keywords"]):
            return region
    for pattern, region in SCRIPT_REGIONS:
        if pattern.search(text):
            return region
    return None


def guess_region(meta: dict, tags: set[str], title: str, desc: str, genre: str | None,
                 rules: Rules) -> tuple[str, str]:
    """Returns (region, where it came from)."""
    found = region_from_text(" | ".join(sorted(tags)) + " | " + title, rules)
    if found:
        return found, "title"
    languages = [l.strip().lower() for l in as_list(meta.get("language"))]
    for region, spec in rules.regions.items():
        if any(l in spec["languages"] for l in languages):
            return region, "language"
    found = region_from_text(desc, rules)
    if found:
        return found, "description"
    coverage = as_text(meta.get("coverage")).lower()
    for region, spec in rules.regions.items():
        if find_words(coverage, spec.get("coverage", ())):
            return region, "coverage"
    if genre and genre in rules.genre_region:
        return rules.genre_region[genre], "genre"
    return "Unknown", "none"


def clean_title(title: str) -> str:
    title = title.strip()
    if NOT_A_NAME.match(title) or not title:
        return "Untitled (label illegible)"
    return title


def specific_genre(tags: set[str], kind: str, rules: Rules) -> str | None:
    for genre, genre_tags in rules.genre_tags.items():
        for tag in tags & genre_tags:
            if tag in rules.tags_78_only and kind != "78rpm":
                continue
            return genre
    return None


def is_generic_folk(tags: set[str], rules: Rules) -> bool:
    if tags & rules.generic_folk_tags:
        return True
    return any(has_word(t, "folk") or has_word(t, "traditional") for t in tags)


def looks_like_lp(meta: dict) -> bool:
    ident = str(meta.get("identifier", ""))
    colls = [c.lower() for c in as_list(meta.get("collection"))]
    text = (as_text(meta.get("title")) + " " + as_text(meta.get("subject"))).lower()
    return (ident.startswith("lp_") or "album_recordings" in colls
            or any(c.startswith("vinyl") for c in colls)
            or bool(re.search(r"\blp\b|33 ?1/3|vinyl", text)))


# --------------------------------------------------------------------------- main entry

def classify_item(ident: str, kind: str, data: dict, rules: Rules) -> ItemResult:
    meta = data.get("metadata", {})
    files = data.get("files", [])
    title = strip_html(as_text(meta.get("title"))).strip() or ident
    artist = as_text(meta.get("creator")) or None
    res = ItemResult(id=ident, status=INCLUDE, title=title, artist=artist)
    flags: list[str] = []

    def exclude(reason: str) -> ItemResult:
        res.status = EXCLUDE
        res.reasons = [reason]
        return res

    ov = rules.overrides or {}
    if ident in set(ov.get("exclude", []) or []):
        return exclude("excluded in overrides.yaml")
    force_include = ident in set(ov.get("include", []) or [])

    desc = strip_html(as_text(meta.get("description")))
    notes = strip_html(as_text(meta.get("notes")))
    rights = as_text(meta.get("rights"))
    tags = extract_tags(meta)
    blob = " | ".join([title, desc, notes, rights, " | ".join(sorted(tags))]).lower()

    condition = str(meta.get("condition") or "").strip().lower()
    if condition in rules.exclude_conditions:
        return exclude(f"disc condition: {condition}")

    # 1. Restricted / unavailable material.
    if str(meta.get("access-restricted-item", "")).lower() == "true":
        return exclude("access-restricted item (lending only)")
    restricted = find_words(blob, rules.restricted_phrases)
    if restricted:
        return exclude(f"restricted/ceremonial wording: {', '.join(restricted)}")
    colls = {c.lower() for c in as_list(meta.get("collection"))}
    bad_colls = colls & rules.exclude_collections
    if bad_colls:
        return exclude(f"broadcast/spoken collection: {', '.join(sorted(bad_colls))}")

    # 2. Genre.
    synthetic = tags & rules.synthetic_tags
    if synthetic:
        return exclude(f"synthesized, not a recording: {', '.join(sorted(synthetic))}")
    spoken = tags & rules.spoken_tags
    if spoken:
        return exclude(f"spoken word / non-music: {', '.join(sorted(spoken))}")
    bands = sorted(tags & rules.band_tags) + find_words(f"{title} | {artist or ''}".lower(), rules.band_words)
    if bands:
        return exclude(f"military/regimental band: {', '.join(bands[:3])}")
    genre = specific_genre(tags, kind, rules)
    excluded = tags & rules.exclude_tags
    generic = is_generic_folk(tags - excluded, rules)
    if excluded and not genre:
        if generic:
            flags.append(f"folk tag mixed with non-folk tags: {', '.join(sorted(excluded))}")
        else:
            return exclude(f"not folk: {', '.join(sorted(excluded))}")
    elif excluded:
        flags.append(f"{genre} mixed with non-folk tags: {', '.join(sorted(excluded))}")
    if not genre and not generic:
        return exclude("no folk/traditional tag")

    # 3. Date / rights.
    year, year_note = parse_year(meta)
    res.year, res.year_note = year, year_note
    if year is not None and year > rules.year_max:
        return exclude(f"dated {year} (after {rules.year_max})")
    if kind == "78rpm":
        if year is None:
            res.year_note = "undated 78rpm disc (format obsolete by mid-1960s)"
    else:
        if year is None:
            return exclude("no usable date")
        if year_note and year_note.startswith("unparsed"):
            flags.append(year_note)
        later = sorted({int(y) for y in YEAR_RE.findall(blob) if int(y) > rules.year_max})
        if later:
            flags.append(f"text mentions later year(s): {', '.join(map(str, later[:5]))}")
        reissue = find_words(blob, rules.reissue_words)
        if reissue:
            flags.append(f"possible compilation/reissue: {', '.join(reissue[:4])}")
        if year_note and year_note.startswith("range"):
            flags.append(f"date is a {year_note} (compilation?)")
        if looks_like_lp(meta) and year < LP_FIRST_YEAR:
            flags.append(f"LP dated {year}, before LPs existed (later reissue of older recordings)")
    if year is not None and year < FIRST_SOUND_RECORDING_YEAR:
        flags.append(f"dated {year}, before sound recording (probably the song's date)")

    # 4. Ceremonial content without explicit restriction.
    ceremonial = find_words(blob, rules.ceremonial_words)
    if ceremonial:
        flags.append(f"ceremonial content, check restrictions: {', '.join(ceremonial[:3])}")

    indigenous = find_words(blob, rules.indigenous_words)
    if indigenous:
        flags.append(f"Indigenous recording, check community protocols: {', '.join(indigenous[:3])}")

    # 5. Tracks.
    choices = pick_files(files)
    if not choices:
        return exclude("no playable MP3")
    writers = extract_writers(meta, choices, rules)
    approved = {w.lower() for w in (ov.get("approved_writers") or [])}
    unapproved = [w for w in writers if w.lower() not in approved]
    res.writers = writers
    if unapproved and rules.flag_named_writers:
        flags.append(f"named writer (check composition rights): {'; '.join(unapproved[:4])}")

    region, region_src = guess_region(meta, tags, title, desc, genre, rules)
    genre_from_region = genre is None
    if genre_from_region:
        genre = rules.region_genre.get(region, "world")
    res.genre, res.region = genre, region
    various = (artist or "").strip().lower() in ("various", "various artists")
    picture = pick_image(ident, files)
    multi = len(choices) > 1

    for c in choices:
        if c.seconds is not None and c.seconds < rules.min_seconds:
            continue
        if c.seconds is not None and c.seconds > rules.max_seconds:
            continue
        t_title = clean_title(c.title or title)
        t_region, t_genre = region, genre
        if multi:
            # Compilations: a track title like "Music Of Panama" beats the item's region.
            own = region_from_text(t_title, rules) if c.title else None
            if own:
                t_region = own
            elif various and region_src == "description":
                t_region = "Various"
            if genre_from_region:
                t_genre = rules.region_genre.get(t_region, "world")
        res.tracks.append(Track(
            id=ident, file=c.name, title=t_title,
            artist=(c.artist or artist),
            year=year, region=t_region, genre=t_genre, seconds=c.seconds, picture=picture,
        ))
    if not res.tracks:
        return exclude("no MP3 of usable length")
    if not indigenous and any(t.region == "Indigenous North America" for t in res.tracks):
        flags.append("Indigenous recording, check community protocols: region")

    if force_include:
        res.status, res.reasons = INCLUDE, ["included in overrides.yaml"] + flags
    elif flags:
        res.status, res.reasons = FLAG, flags
    return res
