# Sonnet rights checks

First-pass judgements by Claude Sonnet (low effort) on songs the MusicBrainz writer
search could not settle (`w*.jsonl`) and on titles that matched a protected composed
song (`c*.jsonl`, possible covers). One line per song: id, verdict, writer, died, basis,
note. Verdicts: protected / clear / unknown (writers) and pop / folk / unknown (covers).

These are candidates, not decisions. Protected and pop verdicts are checked again
before anything is removed; unknown stays in the list.
