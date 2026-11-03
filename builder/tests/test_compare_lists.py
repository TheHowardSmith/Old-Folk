import csv
import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "scripts"))
import compare_lists  # noqa: E402


def _list(d: Path, tracks: list[dict], ext: list[dict] = ()):
    d.mkdir()
    (d / "s-a-1.json").write_text(json.dumps({"t": tracks}))
    idx = {"genres": [{"k": "blues", "name": "Country Blues"}], "shards": [{"f": "s-a-1.json"}], "ext": []}
    if ext:
        (d / "x-a-1.json").write_text(json.dumps({"t": list(ext)}))
        idx["ext"] = [{"f": "x-a-1.json"}]
    (d / "index.json").write_text(json.dumps(idx))


def test_removed_tracks_get_their_reasons(tmp_path):
    t = lambda i, f="a.mp3": {"i": i, "f": f, "t": i.upper(), "g": "blues"}
    _list(tmp_path / "live", [t("keep"), t("sale"), t("excl"), t("flag"), t("gone"), t("multi", "b.mp3")])
    _list(tmp_path / "new", [t("keep"), t("multi", "c.mp3")], ext=[t("loc:1")])
    with open(tmp_path / "new" / "excluded.csv", "w", newline="") as fh:
        csv.writer(fh).writerows([["id", "reason", "title", "artist"], ["excl", "credited writer died after 1955: X", "", ""]])
    with open(tmp_path / "new" / "flagged.csv", "w", newline="") as fh:
        csv.writer(fh).writerows([["id", "reasons"], ["flag", "other tracks claimed in the US: Y || more"]])
    av = tmp_path / "availability.jsonl"
    av.write_text(json.dumps({"k": "sale/a.mp3", "found": True}) + "\n" + json.dumps({"k": "keep/a.mp3", "found": False}) + "\n")

    assert compare_lists.main([str(tmp_path / "live"), str(tmp_path / "new"), "--availability", str(av)]) == 0
    rows = {r["item"]: r["reason"] for r in csv.DictReader(open(tmp_path / "new" / "removed.csv"))}
    assert rows == {
        "sale": "on sale or streaming today (iTunes)",
        "excl": "credited writer died after 1955",
        "flag": "moved to review",
        "gone": "item no longer in the archive search",
        "multi": "track no longer picked from this item",
    }
    md = (tmp_path / "new" / "review.md").read_text()
    assert "Removed: **5**" in md and "Added: **2**" in md and "Country Blues" in md
