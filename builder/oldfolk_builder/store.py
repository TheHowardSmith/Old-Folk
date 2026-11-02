"""SQLite cache of search hits and item metadata.

Everything the builder downloads lands here, so any step can be interrupted
and re-run: searching skips nothing (it is cheap), fetching skips items that
are already cached, and building reads only from the cache.
"""
from __future__ import annotations

import json
import sqlite3
import time
from pathlib import Path
from typing import Iterator

SCHEMA = """
CREATE TABLE IF NOT EXISTS items (
    id          TEXT PRIMARY KEY,
    source      TEXT NOT NULL,          -- name of the sources.yaml entry that found it
    kind        TEXT NOT NULL,          -- 78rpm | other
    sample      INTEGER DEFAULT 0,      -- 1 if chosen for the sample run
    meta        TEXT,                   -- JSON {metadata, files} once fetched
    fetched_at  REAL,
    error       TEXT,                   -- last fetch error, if any
    attempts    INTEGER DEFAULT 0
);
CREATE INDEX IF NOT EXISTS items_unfetched ON items(meta) WHERE meta IS NULL;

-- Recordings from other archives (e.g. source = 'loc' for loc.gov).
CREATE TABLE IF NOT EXISTS ext_items (
    source      TEXT NOT NULL,
    id          TEXT NOT NULL,
    coll        TEXT NOT NULL,          -- collection it was first found in
    record      TEXT NOT NULL,          -- JSON search record
    detail      TEXT,                   -- JSON extra item data, when the record wasn't enough
    fetched_at  REAL,
    error       TEXT,
    attempts    INTEGER DEFAULT 0,
    PRIMARY KEY (source, id)
);
-- Search pages already read (resume), and each collection's last page.
CREATE TABLE IF NOT EXISTS ext_pages (
    source      TEXT NOT NULL,
    key         TEXT NOT NULL,
    value       INTEGER,
    PRIMARY KEY (source, key)
);
"""


class Store:
    def __init__(self, path: Path):
        path.parent.mkdir(parents=True, exist_ok=True)
        self.db = sqlite3.connect(str(path), check_same_thread=False)
        self.db.executescript(SCHEMA)
        self.db.commit()

    def add_hits(self, source: str, kind: str, ids: list[str]) -> int:
        """Insert newly found identifiers; existing ones keep their first source."""
        before = self.count()
        self.db.executemany(
            "INSERT OR IGNORE INTO items(id, source, kind) VALUES (?, ?, ?)",
            [(i, source, kind) for i in ids],
        )
        self.db.commit()
        return self.count() - before

    def mark_sample(self, ids: list[str]) -> None:
        self.db.execute("UPDATE items SET sample = 0")
        self.db.executemany("UPDATE items SET sample = 1 WHERE id = ?", [(i,) for i in ids])
        self.db.commit()

    def ids_by_source(self) -> dict[str, list[str]]:
        out: dict[str, list[str]] = {}
        for ident, source in self.db.execute("SELECT id, source FROM items ORDER BY id"):
            out.setdefault(source, []).append(ident)
        return out

    def unfetched(self, sample_only: bool, max_attempts: int = 5) -> list[str]:
        q = "SELECT id FROM items WHERE meta IS NULL AND attempts < ?"
        if sample_only:
            q += " AND sample = 1"
        return [r[0] for r in self.db.execute(q + " ORDER BY id", (max_attempts,))]

    def save_meta(self, ident: str, meta: dict) -> None:
        self.db.execute(
            "UPDATE items SET meta = ?, fetched_at = ?, error = NULL, attempts = attempts + 1 WHERE id = ?",
            (json.dumps(meta, separators=(",", ":")), time.time(), ident),
        )
        self.db.commit()

    def save_error(self, ident: str, error: str) -> None:
        self.db.execute(
            "UPDATE items SET error = ?, attempts = attempts + 1 WHERE id = ?", (error[:500], ident)
        )
        self.db.commit()

    def fetched(self, sample_only: bool) -> Iterator[tuple[str, str, str, dict]]:
        q = "SELECT id, source, kind, meta FROM items WHERE meta IS NOT NULL"
        if sample_only:
            q += " AND sample = 1"
        for ident, source, kind, meta in self.db.execute(q + " ORDER BY id"):
            yield ident, source, kind, json.loads(meta)

    # ---------------------------------------------------------------- other archives

    def save_ext_records(self, source: str, coll: str, records: list[tuple[str, dict]]) -> None:
        self.db.executemany(
            "INSERT INTO ext_items(source, id, coll, record, fetched_at) VALUES (?, ?, ?, ?, ?) "
            "ON CONFLICT(source, id) DO UPDATE SET record = excluded.record, fetched_at = excluded.fetched_at",
            [(source, i, coll, json.dumps(r, separators=(",", ":")), time.time()) for i, r in records],
        )
        self.db.commit()

    def ext_page_done(self, source: str, key: str) -> bool:
        return self.db.execute("SELECT 1 FROM ext_pages WHERE source = ? AND key = ?",
                               (source, key)).fetchone() is not None

    def mark_ext_page(self, source: str, key: str) -> None:
        self.db.execute("INSERT OR IGNORE INTO ext_pages(source, key) VALUES (?, ?)", (source, key))
        self.db.commit()

    def ext_last_page(self, source: str, coll: str) -> int | None:
        row = self.db.execute("SELECT value FROM ext_pages WHERE source = ? AND key = ?",
                              (source, f"{coll}#last")).fetchone()
        return row[0] if row else None

    def set_ext_last_page(self, source: str, coll: str, page: int) -> None:
        self.db.execute("INSERT OR REPLACE INTO ext_pages(source, key, value) VALUES (?, ?, ?)",
                        (source, f"{coll}#last", page))
        self.db.commit()

    def ext_needing_detail(self, source: str, max_attempts: int = 3) -> list[tuple[str, dict]]:
        q = ("SELECT id, record FROM ext_items WHERE source = ? AND detail IS NULL AND attempts < ? "
             "ORDER BY id")
        return [(i, json.loads(r)) for i, r in self.db.execute(q, (source, max_attempts))]

    def save_ext_detail(self, source: str, ident: str, detail: dict) -> None:
        self.db.execute("UPDATE ext_items SET detail = ?, error = NULL, attempts = attempts + 1 "
                        "WHERE source = ? AND id = ?",
                        (json.dumps(detail, separators=(",", ":")), source, ident))
        self.db.commit()

    def save_ext_error(self, source: str, ident: str, error: str) -> None:
        self.db.execute("UPDATE ext_items SET error = ?, attempts = attempts + 1 "
                        "WHERE source = ? AND id = ?", (error[:500], source, ident))
        self.db.commit()

    def ext_items(self, source: str) -> Iterator[tuple[str, str, dict, dict | None]]:
        q = "SELECT id, coll, record, detail FROM ext_items WHERE source = ? ORDER BY id"
        for ident, coll, record, detail in self.db.execute(q, (source,)):
            yield ident, coll, json.loads(record), json.loads(detail) if detail else None

    def ext_stats(self, source: str) -> dict:
        one = lambda where: self.db.execute(  # noqa: E731
            f"SELECT COUNT(*) FROM ext_items WHERE source = ? AND {where}", (source,)).fetchone()[0]
        return {"items": one("1=1"), "with_detail": one("detail IS NOT NULL"),
                "failed": one("detail IS NULL AND error IS NOT NULL")}

    def count(self, where: str = "1=1") -> int:
        return self.db.execute(f"SELECT COUNT(*) FROM items WHERE {where}").fetchone()[0]

    def stats(self) -> dict:
        return {
            "found": self.count(),
            "fetched": self.count("meta IS NOT NULL"),
            "failed": self.count("meta IS NULL AND error IS NOT NULL"),
            "sample": self.count("sample = 1"),
            "sample_fetched": self.count("sample = 1 AND meta IS NOT NULL"),
        }
