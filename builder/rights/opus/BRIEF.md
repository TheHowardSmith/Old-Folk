# Verify removal candidates (Old Folk Air)

Old Folk Air streams old public-domain folk recordings. Policy: keep unless **confirmed**
protected. A composed song is protected if any composer or lyricist died after 1955 or is
living. A first pass (Sonnet, low effort, no web) marked each song below as protected (writer
songs) or pop (possible covers of a protected composed song). Your job is to check each one
before anything is removed. Many first-pass calls guessed a famous person from a bare surname.

Each input line has the song, performer(s), year, genre, the credited writers (or, for covers,
musicbrainz_writers), and `sonnet` = the first-pass verdict, writer, died, note.

For each song decide:
- "confirmed": you are confident the credited writer of THIS song is that person, and they
  died after 1955 or are living (give the death year or "living"). For covers: the recording
  really is that composed song (not a traditional song with the same title, and not an
  arrangement of a public-domain tune).
- "rejected": the first pass was wrong (wrong person, writer died 1955 or earlier, song is
  traditional / public domain, or a different song with the same title).
- "unsure": you can't confirm either way. Unsure tracks stay in the app.

Use your knowledge first; use WebSearch when a death year or the identity of a credited name
is in doubt and one search would settle it. Decide a writer once and apply it to their songs,
but check each song fits.

Write one JSON object per line to your output file, appending with Bash every ~40 songs so
progress is saved (if the file already has lines, skip those ids — you are resuming):
{"id": "...", "check": "confirmed" | "rejected" | "unsure", "writer": "full name or null", "died": 1964 | "living" | null, "basis": "knowledge" | "web", "note": "short specific reason"}

Every input id must get exactly one line, ids copied exactly. Reply at the end with only the
counts per check and the output path.
