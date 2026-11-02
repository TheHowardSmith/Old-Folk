from oldfolk_builder import loc
from oldfolk_builder.classify import EXCLUDE, FLAG, INCLUDE
from oldfolk_builder.config import load_rules
from oldfolk_builder.output import track_json

RULES = load_rules()
RULES.schedules_path = None
CFG = loc.load_config()
COLL = "florida-folklife-from-the-works-progress-administration"
MP3 = "https://tile.loc.gov/storage-services/media/ingested/api/loc.afc.afcflwpa.3135a1.mp3"


def record(**item):
    base = {
        "title": "Gonna See My Long-Haired Babe",
        "date": "19390618",
        "genre": ["Songs"],
        "subjects": ["Work songs", "Songs", "United States -- Florida -- Duval County -- Jacksonville"],
        "contributors": ["Kennedy, Stetson (Collector)", "Hurston, Zora Neale (Performer)"],
        "notes": ["A railroad work (spiking) song."],
        "language": ["eng"],
        "location": ["United States -- Florida -- Duval County -- Jacksonville"],
        "rights": "No known restrictions on use or reproduction.",
    }
    base.update(item)
    return {"id": "http://www.loc.gov/item/flwpa000003/", "date": "1939-06-18",
            "item": base, "partof": ["american folklife center"],
            "resources": [{"audio": MP3, "files": 1}]}


def classify(rec, detail=None, coll=COLL):
    return loc.classify_record(loc.item_id(rec), coll, rec, detail, RULES, CFG)


def test_field_recording_with_open_rights_is_included():
    r = classify(record())
    assert r.status == INCLUDE, r.reasons
    t = r.tracks[0]
    assert t.id == "loc:flwpa000003"
    assert t.artist == "Zora Neale Hurston"
    assert t.year == 1939
    assert t.genre == "field"     # no style tag: the field-recordings fallback
    j = track_json(t)
    assert j["u"] == MP3 and j["w"] == "https://www.loc.gov/item/flwpa000003/"
    assert "p" not in j


def test_unclear_rights_go_to_review():
    r = classify(record(rights="See copyright restriction statement."))
    assert r.status == FLAG
    assert any("rights statement" in x for x in r.reasons)


def test_spoken_word_excluded_but_song_with_introduction_kept():
    assert classify(record(genre=["Interviews", "Spoken word"])).status == EXCLUDE
    assert classify(record(genre=["Songs", "Spoken word"])).status == INCLUDE


def test_poor_transfers_excluded():
    r = classify(record(notes=["The sound quality of this recording is extremely poor."]))
    assert r.status == EXCLUDE and r.reasons[0].startswith("poor transfer")


def test_county_names_are_not_indigenous_content():
    rec = record(title="Ej lÃºcka, lÃºcka sirokÃ¡", genre=["Songs"],
                 subjects=["Slovak Americans", "United States -- Florida -- Seminole County -- Slavia"],
                 language=["slo"])
    r = classify(rec)
    assert r.status == INCLUDE, r.reasons
    assert r.tracks[0].title == "Ej lúcka, lúcka siroká"     # loc.gov mojibake repaired
    assert r.tracks[0].genre == "european"


def test_item_json_segments_become_tracks():
    rec = record()
    rec["resources"] = [{"url": "https://www.loc.gov/resource/x/"}]
    detail = {"resources": [{"files": [[
        {"download": "https://tile.loc.gov/a/one.mp3", "shortName": "Part 1"},
        {"download": "https://tile.loc.gov/a/two.mp3", "shortName": "Part 2"},
    ]]}]}
    r = classify(rec, detail)
    assert [t.file for t in r.tracks] == ["one.mp3", "two.mp3"]
    assert classify(rec).reasons == ["no playable MP3"]


def test_songs_of_america_only_takes_field_recordings():
    r = classify(record(genre=["Popular music"]), coll="songs-of-america")
    assert r.status == EXCLUDE
