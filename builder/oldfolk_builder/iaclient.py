"""Polite access to the Internet Archive through the official `internetarchive` package.

* At most `workers` (default 2) requests in flight.
* Retries with exponential backoff and jitter on network errors, 429 and 5xx.
* A small pause between requests from each worker.
"""
from __future__ import annotations

import logging
import random
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from typing import Callable, Iterable

import internetarchive as ia
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

log = logging.getLogger(__name__)

USER_AGENT_SUFFIX = " OldFolkListBuilder/1.0 (+https://github.com/thehowardsmith/old-folk)"

# Only these metadata keys are kept in the cache (keeps the DB small).
META_KEYS = (
    "identifier", "title", "creator", "date", "year", "genre", "subject", "description",
    "notes", "language", "coverage", "collection", "source", "publisher", "rights",
    "licenseurl", "composer", "lyricist", "condition", "access-restricted-item", "mediatype", "runtime",
)
FILE_KEYS = ("name", "source", "format", "title", "creator", "length", "original", "track",
             "private", "composer", "size")


def make_session() -> ia.ArchiveSession:
    session = ia.get_session()
    retry = Retry(
        total=5, backoff_factor=2.0, status_forcelist=(429, 500, 502, 503, 504),
        allowed_methods=frozenset({"GET", "HEAD"}), respect_retry_after_header=True,
    )
    adapter = HTTPAdapter(max_retries=retry, pool_connections=2, pool_maxsize=2)
    session.mount("https://", adapter)
    session.mount("http://", adapter)
    session.headers["User-Agent"] = session.headers.get("User-Agent", "") + USER_AGENT_SUFFIX
    return session


def with_backoff(fn: Callable, *args, attempts: int = 5, base: float = 2.0, **kwargs):
    """Call fn, retrying with exponential backoff + jitter on any exception."""
    for attempt in range(1, attempts + 1):
        try:
            return fn(*args, **kwargs)
        except Exception as exc:  # noqa: BLE001 - network stack raises many types
            if attempt == attempts:
                raise
            delay = base ** attempt + random.uniform(0, 1)
            log.warning("%s failed (%s); retry %d/%d in %.1fs", getattr(fn, "__name__", fn),
                        exc, attempt, attempts - 1, delay)
            time.sleep(delay)


def search_ids(session: ia.ArchiveSession, query: str) -> list[str]:
    """All identifiers matching an advanced-search query (uses the scrape API, paginated)."""
    def run():
        results = session.search_items(query, fields=["identifier"])
        return [r["identifier"] for r in results]
    return with_backoff(run)


def fetch_item(session: ia.ArchiveSession, ident: str) -> dict:
    """Metadata + file list for one item, trimmed to the keys we use."""
    item = with_backoff(session.get_item, ident)
    if not item.exists:
        raise LookupError(f"item {ident} does not exist or is dark")
    meta = {k: v for k, v in item.metadata.items() if k in META_KEYS}
    files = [{k: f[k] for k in FILE_KEYS if k in f} for f in item.files]
    return {"metadata": meta, "files": files}


def fetch_many(session, idents: Iterable[str], on_ok, on_err, workers: int = 2,
               pause: float = 0.5, progress_every: int = 50) -> None:
    """Fetch items with a small thread pool, calling on_ok/on_err from the main thread."""
    idents = list(idents)
    lock = threading.Lock()
    done = 0

    def task(ident):
        try:
            return ident, fetch_item(session, ident), None
        except Exception as exc:  # noqa: BLE001
            return ident, None, f"{type(exc).__name__}: {exc}"
        finally:
            time.sleep(pause)

    with ThreadPoolExecutor(max_workers=max(1, min(workers, 2))) as pool:
        futures = [pool.submit(task, i) for i in idents]
        for fut in as_completed(futures):
            ident, meta, err = fut.result()
            if err:
                on_err(ident, err)
            else:
                on_ok(ident, meta)
            with lock:
                done += 1
                if done % progress_every == 0 or done == len(idents):
                    log.info("fetched %d/%d", done, len(idents))
