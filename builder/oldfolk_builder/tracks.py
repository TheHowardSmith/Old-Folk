"""Pick one playable MP3 per track from an item's file list."""
from __future__ import annotations

import re
from dataclasses import dataclass

# Lower number = preferred.
FORMAT_RANK = {
    "vbr mp3": 0,
    "128kbps mp3": 1,
    "160kbps mp3": 2,
    "192kbps mp3": 2,
    "256kbps mp3": 3,
    "320kbps mp3": 4,
    "mp3": 5,
    "96kbps mp3": 6,
    "64kbps mp3": 7,
    "56kbps mp3": 8,
    "32kbps mp3": 9,
}
SKIP_NAME = re.compile(r"(_sample|_spectrogram|_thumb|\bpreview\b)", re.I)


@dataclass
class FileChoice:
    name: str
    title: str | None
    artist: str | None
    seconds: float | None
    track: str | None
    composer: str | None


def parse_length(value) -> float | None:
    """IA lengths are '147.46' or '03:04' or '1:02:03'."""
    if value is None:
        return None
    s = str(value).strip()
    try:
        if ":" in s:
            secs = 0.0
            for part in s.split(":"):
                secs = secs * 60 + float(part)
            return secs
        return float(s)
    except ValueError:
        return None


def _as_text(value) -> str | None:
    if value is None:
        return None
    if isinstance(value, list):
        value = ", ".join(str(v) for v in value if v)
    value = str(value).strip()
    return value or None


NO_PICTURE = "0"     # "p": "0" = no cover or label image
IMAGE_RANK = {"item image": 0, "jpeg": 1, "png": 3, "item tile": 4, "jpeg thumb": 5, "thumbnail": 6}


def pick_image(ident: str, files: list[dict]) -> str | int | None:
    """Best cover/label image for an item.

    Returns 1 / 2 for the standard "<id>_itemimage.jpg" / ".png" scans (stored
    as one-character flags to keep shards small), another file name, or
    NO_PICTURE when the item has no cover or label image: the Archive's
    thumbnail service would then only serve a picture of the waveform, so the
    apps draw a 78 instead.
    """
    best = None
    for f in files:
        name = f.get("name", "")
        fmt = str(f.get("format", "")).lower()
        if not name.lower().endswith((".jpg", ".jpeg", ".png")) or fmt == "spectrogram" or "spectrogram" in name:
            continue
        if fmt == "png" and f.get("source") == "derivative":
            continue   # waveform/spectrogram-style renders of the audio
        rank = IMAGE_RANK.get(fmt, 7)
        if best is None or rank < best[0]:
            best = (rank, name)
    if best is None or best[0] >= 4:
        return NO_PICTURE    # nothing, or only the tile generated from the audio
    if best[1] == f"{ident}_itemimage.jpg":
        return 1
    if best[1] == f"{ident}_itemimage.png":
        return 2
    return best[1]


def pick_files(files: list[dict]) -> list[FileChoice]:
    """One MP3 per underlying recording.

    Derivative MP3s are grouped by their `original` (the source FLAC/WAV, or the
    segments JSON for LP sides); original MP3s stand alone. In each group the
    best-ranked format wins.
    """
    groups: dict[str, tuple[int, dict]] = {}
    for f in files:
        name = f.get("name", "")
        if not name.lower().endswith(".mp3") or SKIP_NAME.search(name):
            continue
        if str(f.get("private", "")).lower() == "true":
            continue
        rank = FORMAT_RANK.get(str(f.get("format", "")).lower(), 10)
        if f.get("source") == "derivative" and f.get("original"):
            orig = f["original"]
            # LP sides share one segments JSON; each side is its own track.
            key = name if orig.endswith(".json") else orig
        else:
            key = name
        best = groups.get(key)
        if best is None or rank < best[0]:
            groups[key] = (rank, f)

    # If an original MP3 also has derivative MP3s grouped under it, drop the
    # original's own entry so the recording appears once.
    derived_from = {k for k, (_, f) in groups.items() if f.get("source") == "derivative"}
    out = []
    for key, (_, f) in groups.items():
        if f.get("source") != "derivative" and f["name"] in derived_from:
            continue
        out.append(FileChoice(
            name=f["name"],
            title=_as_text(f.get("title")),
            artist=_as_text(f.get("creator")),
            seconds=parse_length(f.get("length")),
            track=_as_text(f.get("track")),
            composer=_as_text(f.get("composer")),
        ))

    def sort_key(c: FileChoice):
        num = re.match(r"\d+", c.track or "")
        return (int(num.group()) if num else 10**6, c.name)

    return sorted(out, key=sort_key)
