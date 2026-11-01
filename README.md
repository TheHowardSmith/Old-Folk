# Old Folk

## [⬇ Download the Android app (old-folk.apk)](https://github.com/TheHowardSmith/Old-Folk/releases/latest/download/old-folk.apk)

Open that link on your Android phone (Android 8 or newer), then open the downloaded file to install it. Android may ask you to allow installing apps from your browser. No account or sign-up is needed. All versions are on the [Releases page](https://github.com/TheHowardSmith/Old-Folk/releases).

---

Old Folk plays a never-ending random stream of public-domain folk and field recordings from around the world, streamed directly from the [Internet Archive](https://archive.org). There is no streaming server: a vetted track list is published as small JSON files, and the app picks random tracks from it and plays them straight from archive.org.

## What the app does

- **Endless radio:** random tracks, and your phone never repeats one until you've heard everything in your selection.
- **Genres:** pick one or more (old-time, blues, Celtic, klezmer, calypso, Hawaiian, field recordings, and optional pre-1970 world popular music such as tango and bolero).
- **Background playback:** keeps playing with the screen off, with notification and lock-screen controls.
- **Smooth playback, even when skipping a lot:** 5 tracks are fetched ahead at first, then 20 ahead once you're streaming. Your last 20 tracks are kept too, so the previous button works instantly. Everything goes in a capped 600 MB cache, and tracks that won't play are skipped automatically. Fetching 20 ahead uses more mobile data.
- **History tab:** scroll up through everything you've ever streamed on this phone, and down through the tracks coming next. Older tracks aren't stored on the phone; tapping one streams it again.
- **Track details:** title, performer, year, region and genre, with a link to the recording's Internet Archive page.
- **Playlists:** save tracks you like; they're downloaded to your phone so they play offline (no storage cap).
- **Report this track:** opens a pre-filled GitHub issue (rights concerns, wrong details, not folk...).
- **Failure reports:** tracks that fail to play or download are logged with the reason and whether the phone was online. From the About screen you can send them to this repository as a pre-filled issue, so we can check whether the file is really gone or it was a connection or app problem.

How it works: on first launch the app downloads `index.json` and one random part ("shard") of the track list, starts playing, and fetches the remaining shards in the background. Everything is cached; it only downloads again when the list version changes. Playback URLs are `https://archive.org/download/{item}/{file}`.

## Repository layout

| Path | What it is |
|---|---|
| `builder/` | Python tool that searches the Internet Archive and builds the track list |
| `builder/rules/` | Editable search queries, genre/region keywords and exclusion rules |
| `builder/overrides.yaml` | Manual review decisions (include/exclude items, approved writers) |
| `android/` | The Android app (Kotlin, Jetpack Compose, Media3/ExoPlayer, MediaSessionService) |
| `.github/workflows/` | CI, APK release, and track-list publishing |

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

## Publishing

- **App:** every push to `main` that changes `android/` runs **Release APK**, which builds a signed APK and publishes it as the latest release (`old-folk.apk`). Signing uses the repository secrets `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` and `ANDROID_KEY_PASSWORD`.
- **Track list:** run the **Build track list** workflow (Actions tab → Build track list → Run workflow). `sample` builds a quick test list; `full` builds everything, resuming across runs (each run fetches for about 5 hours). The files are published to the `tracklist` release, which the app reads from `https://github.com/TheHowardSmith/Old-Folk/releases/download/tracklist/`.

To build the app yourself: install Android Studio (or JDK 17 plus the Android SDK), then run `cd android && ./gradlew assembleDebug`.

## Selection rules

- Only recordings believed to have been **recorded or released in 1970 or earlier** (New Zealand protects sound recordings for 50 years; 1970 leaves a safety margin).
- Undated 78 rpm discs are treated as pre-1970, because the format was obsolete by the mid-1960s.
- Items whose date looks unreliable (compilations, reissues, LPs dated before LPs existed, text mentioning later years, modern recordings of old songs) are **flagged for review**, not included.
- Songs credited to a named (non-traditional) writer are flagged so the **composition** copyright can be checked as well.
- Recordings whose descriptions say they are **restricted, sacred/secret or culturally sensitive are excluded**. Indigenous and ceremonial recordings are flagged for a human check.
- Jazz, Western pop, classical, opera, spoken word, and military/regimental bands are excluded.
- Pre-1970 **world popular** 78s (non-English popular song and Latin dance music from outside the Anglo, Western-European and Nordic mainstream) are included as two separate, optional genres.
- No audio is ever stored in this repository.

## Rights notice

The recordings are hosted by the **Internet Archive**, not by this project, and are **not covered by this repository's licenses**. Tracks were selected because they are believed to be in the public domain in **New Zealand**. Their status may be different in other countries; check your local law before reusing them.

Rights holders who want a recording removed from the list can email **thehowardsmith@gmail.com**, or open an issue on this repository. Removed items are added to `builder/overrides.yaml` so they never return.

## Licenses

- Code: [Apache License 2.0](LICENSE)
- Track list (the generated JSON metadata): [CC0 1.0](LICENSE-DATA)
