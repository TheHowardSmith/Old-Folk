import json

from oldfolk_builder.classify import EXCLUDE, INCLUDE, classify_item
from oldfolk_builder.config import load_rules
from oldfolk_builder.llm_checks import LlmChecks, load


def write(path, rows):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("".join(json.dumps(r) + "\n" for r in rows), encoding="utf-8")


def test_only_a_confirmed_second_pass_removes(tmp_path):
    write(tmp_path / "sonnet" / "w00.jsonl", [
        {"id": "a", "verdict": "protected"}, {"id": "b", "verdict": "protected"},
        {"id": "c", "verdict": "clear"}, {"id": "d", "verdict": "unknown"},
        {"id": "e", "verdict": "protected"}])
    write(tmp_path / "sonnet" / "c00.jsonl", [{"id": "cover:white christmas", "verdict": "pop"},
                                              {"id": "cover:clementine", "verdict": "pop"}])
    write(tmp_path / "inputs" / "c00.jsonl", [{"id": "cover:white christmas", "items": ["x1", "x2"]},
                                              {"id": "cover:clementine", "items": ["x3"]}])
    write(tmp_path / "opus" / "v0.jsonl", [
        {"id": "a", "check": "confirmed", "writer": "Fred Kirby", "died": 1996},
        {"id": "b", "check": "unsure"},
        {"id": "e", "check": "rejected", "writer": "B. G. DeSylva", "died": 1950},
        {"id": "cover:white christmas", "check": "confirmed", "writer": "Irving Berlin", "died": 1989},
        {"id": "cover:clementine", "check": "rejected"}])
    r = load(tmp_path)
    assert r.protected == {"a": "Fred Kirby (d. 1996)"}
    assert r.cleared == {"c", "e"}
    assert r.covers == {("x1", "white christmas"): "Irving Berlin (d. 1989)",
                        ("x2", "white christmas"): "Irving Berlin (d. 1989)"}


def test_confirmed_covers_drop_only_that_track():
    rules = load_rules()
    rules.schedules_path = None
    rules.writer_search, rules.writer_deaths = {}, {}
    rules.llm_checks = LlmChecks(covers={("lp", "white christmas"): "Irving Berlin (d. 1989)"})

    def mp3(name, title):
        return {"name": name, "format": "VBR MP3", "length": "180.0", "source": "original", "title": title}
    data = {"metadata": {"identifier": "lp", "title": "Carols", "creator": "Choir", "subject": ["Folk"],
                         "date": "1958"},
            "files": [mp3("1.mp3", "White Christmas"), mp3("2.mp3", "The Holly and the Ivy")]}
    r = classify_item("lp", "lp", data, rules)
    assert r.status == INCLUDE and [t.title for t in r.tracks] == ["The Holly and the Ivy"]
    data["files"] = data["files"][:1]
    r = classify_item("lp", "lp", data, rules)
    assert r.status == EXCLUDE and "cover of a protected song" in r.reasons[0]


def test_covers_match_songs_inside_medleys(tmp_path):
    import json
    from oldfolk_builder import llm_checks
    for d in ("sonnet", "opus", "inputs"):
        (tmp_path / d).mkdir()
    (tmp_path / "inputs" / "c00.jsonl").write_text(
        json.dumps({"id": "cover:white christmas", "items": ["a"]}) + "\n" +
        json.dumps({"id": "cover:white christmas#medleys", "items": ["b"]}) + "\n")
    (tmp_path / "sonnet" / "c00.jsonl").write_text(
        json.dumps({"id": "cover:white christmas", "verdict": "pop"}) + "\n" +
        json.dumps({"id": "cover:white christmas#medleys", "verdict": "pop"}) + "\n")
    (tmp_path / "opus" / "v0.jsonl").write_text(
        json.dumps({"id": "cover:white christmas", "check": "confirmed", "writer": "Irving Berlin", "died": 1989}) + "\n" +
        json.dumps({"id": "cover:white christmas#medleys", "check": "confirmed", "writer": "Irving Berlin", "died": 1989}) + "\n")
    c = llm_checks.load(tmp_path)
    assert ("a", "white christmas") in c.covers and ("b", "white christmas") in c.covers
    assert "white christmas" in llm_checks.title_parts("1. RUDOLPH THE RED NOSED REINDEER; 2. WHITE CHRISTMAS")
    assert "jalousie" in llm_checks.title_parts("Tango Jalousie - Argentina - Xavier Cugat Orch")
