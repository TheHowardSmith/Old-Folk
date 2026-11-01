# Old Folk

**Download the app:** the Android APK will be published under [Releases](https://github.com/thehowardsmith/old-folk/releases/latest) (coming soon).

Old Folk plays a never-ending random stream of public-domain folk and field recordings from around the world, streamed directly from the [Internet Archive](https://archive.org). There is no streaming server: a vetted track list is published as small JSON files, and the app picks random tracks from it and plays them straight from archive.org.

> This README will be expanded as the app is built. Status: **stage 1 (track list builder)**.

## Repository layout

| Path | What it is |
|---|---|
| `builder/` | Python tool that searches the Internet Archive and builds the track list |
| `builder/rules/` | Editable search queries, genre/region keywords and exclusion rules |
| `builder/overrides.yaml` | Manual review decisions (include/exclude items, approved writers) |
| `android/` | The Android app (coming in stage 3) |

## Building the track list

```bash
cd builder
python3 -m venv .venv && . .venv/bin/activate
pip install -e '.[test]'
oldfolk-build search            # find candidate items (fast)
oldfolk-build sample --n 200    # optional: pick a test sample
oldfolk-build fetch --sample    # fetch metadata (drop --sample for the full run)
oldfolk-build build --sample    # classify and write out-sample/ (or out/)
```

Fetching is polite (at most 2 requests at a time, retries with backoff) and resumable: everything is cached in `builder/cache/oldfolk.sqlite`, so it can be stopped and restarted at any time. `build` works only from the cache, so rules can be tuned and re-run offline.

Output (not committed to git):

- `index.json`: list version, genres and shard list
- `s-<genre>-<n>.json`: the track list, about 5,000 tracks per shard
- `r-<genre>-<n>.json`: **review set**, flagged tracks that are kept out of the app until checked
- `flagged.csv`, `writers.csv`, `excluded.csv`, `report.md`: for manual review

Shard format (short keys): `{"v":1,"g":"oldtime","t":[{"i":"<item id>","f":"<file name>","t":"<title>","a":"<artist>","y":1928,"r":"<region>","g":"<genre>"}]}`. Playback URL: `https://archive.org/download/{i}/{f}`.

## Selection rules

- Only recordings believed to have been **recorded or released in 1970 or earlier** (New Zealand protects sound recordings for 50 years; 1970 leaves a safety margin).
- Undated 78 rpm discs are treated as pre-1970, because the format was obsolete by the mid-1960s.
- Items whose date looks unreliable (compilations, reissues, LPs dated before LPs existed, text mentioning later years, modern recordings of old songs) are **flagged for review**, not included.
- Songs credited to a named (non-traditional) writer are flagged so the **composition** copyright can be checked as well.
- Recordings whose descriptions say they are **restricted, sacred/secret or culturally sensitive are excluded**. Indigenous and ceremonial recordings are flagged for a human check.
- Jazz, pop, classical, opera and spoken word are excluded.
- No audio is ever stored in this repository.

## Rights notice

The recordings are hosted by the **Internet Archive**, not by this project, and are **not covered by this repository's licenses**. Tracks were selected because they are believed to be in the public domain in **New Zealand**. Their status may be different in other countries; check your local law before reusing them.

Rights holders who want a recording removed from the list can email **thehowardsmith@gmail.com**, or open an issue on this repository. Removed items are added to `builder/overrides.yaml` so they never return.

## Licenses

- Code: [Apache License 2.0](LICENSE)
- Track list (the generated JSON metadata): [CC0 1.0](LICENSE-DATA)
