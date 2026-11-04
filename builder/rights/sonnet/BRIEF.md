# Rights check brief (Old Folk Air)

Old Folk Air is a free radio app that streams old public-domain folk recordings from
archive.org. A track stays in the app unless we **confirm** it is still protected. The rule
for songs: a composed song is protected if any of its writers (composer or lyricist) died
after 1955 or is still living. Traditional songs, and songs whose writers all died in 1955
or earlier, are fine.

Policy: **keep unless confirmed.** Only say "protected" when you are confident. When the
credit is too thin to identify the person (a surname alone, initials, an obscure name you
don't recognise), answer "unknown". Do not guess. "Unknown" tracks stay in the app.

## How to work

- Use your own knowledge first. Use WebSearch only when a credit looks like a specific,
  findable person and one search would settle their death year (for example "A. Barosso"
  on "Brazil" is Ary Barroso; "A. E. Brumley" is Albert E. Brumley). Keep searches to roughly
  one per distinct writer at most, and skip searches for obscure names on ethnic 78s.
- The input is sorted by writer. Decide each writer once and apply it to all their songs,
  but check the song too: the same surname can be a different person.
- Read your input file with the Read tool. Write results by appending to your output file
  with Bash (a heredoc with `cat >> FILE <<'EOF'`), **every 25 songs or so**, so progress is
  saved if you are stopped. Before starting, check whether the output file already has
  lines; if it does, skip the ids already written (you are resuming).
- One JSON object per line, ids copied exactly from the input. Every input id must get a
  line. Keep "note" short (under 120 characters).

## Writer batches (files named w*.jsonl)

Each input line: id, song, performer, year (may be null), genre, writers (the credited
names we could not identify), sometimes already_cleared (writers already known to have died
by 1955).

Output line:
{"id": "...", "verdict": "protected" | "clear" | "unknown", "writer": "full name you identified or null", "died": 1964 | "living" | null, "basis": "knowledge" | "web", "note": "..."}

- protected: you are confident who the writer is and that they died after 1955 or are living.
- clear: you are confident every credited writer died in 1955 or earlier, or the credit is
  really a performer, arranger of a traditional tune, or not a person (place, label, "trad.").
- unknown: anything else.

## Cover batches (files named c*.jsonl)

Each input line is a song title in the track list that MusicBrainz matched to a composed
song whose writers died after 1955 (musicbrainz_writers). The match was by title only, so
it is often wrong: the record may be a traditional song with the same title, a different
song, or an arrangement of a public-domain tune. Judge from the title, performers, year and
genre whether the record is really that protected composed song.

Output line:
{"id": "...", "verdict": "pop" | "folk" | "unknown", "writer": "the protected writer if pop, else null", "died": year | "living" | null, "basis": "knowledge" | "web", "note": "..."}

- pop: the recording is that protected composed song (commercial pop, country, show tune,
  folk-revival song with a known modern writer such as Woody Guthrie or Bob Dylan).
- folk: it is traditional, public domain, or a different song with the same title.
- unknown: you can't tell.

When you finish, reply with only the counts per verdict and the output file path.
