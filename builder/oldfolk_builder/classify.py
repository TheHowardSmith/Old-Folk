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
from datetime import date
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
    url: str | None = None      # full audio URL, for recordings not on the Internet Archive
    page: str | None = None     # the recording's web page, likewise
    source: str | None = None   # holding institution shown in the app ("Library of Congress")


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
    # Open questions that don't keep the item out (unconfirmed writers, user
    # uploads...): the list for the slower, careful checks (checks.csv).
    checks: list[str] = field(default_factory=list)


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


# Jewish recordings are often tagged only by place (Israel, Palestine, Eastern
# Europe), which would file them under Asian & Middle Eastern or European. These
# words in the title, description or subjects move them to Klezmer & Jewish.
JEWISH_TEXT = re.compile(
    r"\b(jewish|yiddish(e)?|hebrew|klezmer|cantor(s|ial)?|chazz?an(ut|im)?|hazzan(ut|im)?|"
    r"c?hass?idi?(c|m)?|chassidim|hasidim|synagogue|sephardi(c|m)?|ladino|kol nidre|"
    r"niguni?m?|nign|lubavitch(er)?|rebbe|shabb?(at|os|es)|israeli?|palestine dances?|"
    r"yemenite|bukharian)\b", re.I)
# Performer credits are only trusted for unambiguous ensemble names: a person
# can be called Israel (e.g. Israel Kashi, a Persian singer) or Hazan (a
# common surname, e.g. Victoria Hazan, who sang Turkish songs).
JEWISH_PERFORMER = re.compile(
    r"\b(cantor|chazz?an|hazzan|chassidim|hasidim|kol israel|voice of israel|"
    r"israel(i)? (folk |philharmonic |symphony )?(orch|ensemble|choir|singers))\b", re.I)
JEWISH_MOVABLE = ("asia", "world", "european")


def is_jewish(text: str, performer: str = "") -> bool:
    return bool(JEWISH_TEXT.search(text or "") or JEWISH_PERFORMER.search(performer or ""))


def is_full_name(name: str) -> bool:
    """A forename and a surname ("Ray Henry"), not just initials or a surname
    ("F. Wojnarowski", "Smith"): only then is a matched death year trusted."""
    words = [w for w in re.split(r"[\s]+", name.strip()) if w]
    return len(words) >= 2 and len(words[0].rstrip(".")) > 1 and not words[0].endswith(".")


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


def traditional_art_waiver(tags: set[str], title: str, artist: str | None, desc: str,
                           rules: Rules) -> set[str]:
    """Exclude tags to ignore for non-Western classical and court traditions.

    Discogs files raga, gagaku, Cantonese music, Peking opera and the like under
    "Classical" (and "Opera"); the exclude list means Western art music. Returns
    the tags to waive: classical and opera when a tradition is named in the tags,
    title or performer; only classical when the item is merely from such a region.
    Nothing is waived when the title or description names Western forms
    (concerto, symphony...).
    """
    if find_words(f"{title} | {desc}".lower(), rules.western_art_words):
        return set()
    named = f"{title} | {artist or ''}".lower()
    if tags & rules.traditional_art_tags or find_words(named, rules.traditional_art_words):
        return {"classical", "opera"}
    region = region_from_text(" | ".join(sorted(tags)) + " | " + title, rules)
    if region in rules.traditional_art_regions:
        return {"classical"}
    return set()


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
    for pattern, reason in rules.exclude_id_patterns:
        if pattern.search(ident):
            return exclude(reason)

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
        return exclude(f"excluded collection: {', '.join(sorted(bad_colls))}")

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
    teaching = find_words(f"{title} | {artist or ''} | {as_text(meta.get('publisher'))}".lower(),
                          rules.instruction_ensembles)
    if teaching:
        return exclude(f"folk-dance instruction record: {teaching[0]}")
    genre = specific_genre(tags, kind, rules)
    # Any rock or psychedelic style ("psychedelic rock", "blues rock", "acid rock") is not folk.
    excluded = (tags & rules.exclude_tags) | {t for t in tags if has_word(t, "rock") or has_word(t, "psychedelic")}
    excluded -= traditional_art_waiver(tags, title, artist, desc, rules)
    if excluded and excluded <= rules.ethnic_waivable_tags and tags & rules.ethnic_record_tags:
        excluded = set()    # LoC/Victor "ethnic music" discs: "popular music" just means secular
    generic = is_generic_folk(tags - excluded, rules)
    if excluded and not genre:
        if generic and excluded & rules.pop_tags \
                and not is_generic_folk(tags - excluded - rules.weak_generic_tags, rules):
            return exclude(f"not folk: {', '.join(sorted(excluded))} "
                           "(only the broad 'Folk, World, & Country' label)")
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
    index = rules.load_schedules() if rules.exclude_us_claimed else None
    if index is not None and not (year and year <= rules.us_public_domain_year):
        claimed, owner = index.match(title, artist or "")
        if claimed == "yes":
            return exclude(f"recording claimed in the US (pre-1972 schedules): {owner}")
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

    # Anyone can upload to these collections, and some uploads are rips of
    # commercial albums (a 1970 Japanese pop-folk LP of Bacharach and Beatles
    # covers). They stay in but go on the check list, unless the recording is
    # old enough to be out of US copyright; confirmed ones go in overrides.yaml.
    community = colls & rules.review_collections
    approved = set((rules.overrides or {}).get("approved_uploads") or [])
    if community and ident not in approved and not (year and year <= rules.us_public_domain_year):
        res.checks.append(f"user upload, may be a commercial release: {', '.join(sorted(community))}")

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
    elif unapproved and rules.writer_rule:
        # Keep unless shown otherwise: a composed song is left out only when a
        # credited writer is confirmed to have died less than 70 years ago (a full
        # name matched to a death year). Writers we couldn't trace, or only know by
        # an initial or surname, stay in and go on the check list.
        cutoff = date.today().year - rules.writer_life_plus - 1
        deaths = rules.load_writer_deaths()
        late, unclear = [], []
        for w in unapproved:
            d = deaths.get(w) or {}
            if d.get("status") == "dead" and d.get("died") and d["died"] <= cutoff:
                continue
            if d.get("status") == "dead" and d.get("died") and is_full_name(w):
                late.append(f"{w} (d. {d['died']})")
            else:
                unclear.append(w)
        if late:
            return exclude(f"writer died less than 70 years ago: {'; '.join(late[:3])}")
        if unclear:
            res.checks.append(f"writer not confirmed died by {cutoff}: {'; '.join(unclear[:4])}")

    region, region_src = guess_region(meta, tags, title, desc, genre, rules)
    genre_from_region = genre is None
    if genre_from_region:
        genre = rules.region_genre.get(region, "world")
    # Polkas get their own genre (so listeners can leave them out), including
    # untagged discs with "polka" in the title.
    if genre in ("european", "world", "american") and re.search(r"\bpolka\b", title, re.I):
        genre = "polka"
    if genre in JEWISH_MOVABLE and is_jewish(" ".join([title, desc, " ".join(sorted(tags))]), artist or ""):
        genre = "jewish"
    res.genre, res.region = genre, region
    various = (artist or "").strip().lower() in ("various", "various artists")
    picture = pick_image(ident, files)
    multi = len(choices) > 1

    claimed_tracks = []
    for c in choices:
        if c.seconds is not None and c.seconds < rules.min_seconds:
            continue
        if c.seconds is not None and c.seconds > rules.max_seconds:
            continue
        t_title = clean_title(c.title or title)
        # US claims are also checked per track: an album's own title rarely
        # matches, but its songs do ("St. Stephen" by the Grateful Dead).
        if index is not None and c.title and not (year and year <= rules.us_public_domain_year):
            claimed, owner = index.match(t_title, c.artist or artist or "")
            if claimed == "yes":
                claimed_tracks.append(f"{t_title} ({owner})")
                continue
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
            # Mixed compilations: one track can be Jewish ("Hora - Israel").
            if t_genre in JEWISH_MOVABLE and is_jewish(t_title, c.artist or ""):
                t_genre = "jewish"
        res.tracks.append(Track(
            id=ident, file=c.name, title=t_title,
            artist=(c.artist or artist),
            year=year, region=t_region, genre=t_genre, seconds=c.seconds, picture=picture,
        ))
    if not res.tracks and claimed_tracks:
        return exclude(f"recording claimed in the US (pre-1972 schedules): {claimed_tracks[0]}")
    if not res.tracks:
        return exclude("no MP3 of usable length")
    if claimed_tracks:
        # Some songs on the album are claimed and dropped; the rest aren't, so they
        # stay in, but the label is active, so they go on the check list.
        res.checks.append(f"other tracks claimed in the US (pre-1972 schedules): {'; '.join(claimed_tracks[:3])}")
    if not indigenous and any(t.region == "Indigenous North America" for t in res.tracks):
        flags.append("Indigenous recording, check community protocols: region")

    if force_include:
        res.status, res.reasons = INCLUDE, ["included in overrides.yaml"] + flags
    elif flags:
        res.status, res.reasons = FLAG, flags
    return res
