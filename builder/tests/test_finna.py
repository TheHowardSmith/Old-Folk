from oldfolk_builder import finna
from oldfolk_builder.classify import EXCLUDE, FLAG, INCLUDE
from oldfolk_builder.config import load_rules
from oldfolk_builder.output import track_json

RULES = load_rules()
RULES.schedules_path = None
RULES.writer_deaths = {}
CFG = finna.load_config()


def sks(**kw):
    rec = {
        "id": "sks.1_2", "title": "Paraske Mitrune. Itkuvirsiä. Suojärvi. 1917.", "year": "1917",
        "nonPresenterAuthors": [{"role": "esittäjä", "name": "Mitrune, Paraske"},
                                {"role": "äänittäjä", "name": "Väisänen, Armas Otto"}],
        "subjects": [["itkuvirret"], ["aineistokokoelma"]],
        "urls": [
            {"url": "https://yksa.disec.fi/Yksa4/download/1/file/a", "desc": "fonokop055_01.mp3"},
            {"url": "https://yksa.disec.fi/Yksa4/download/1/file/b", "desc": "fonokop055_01.wav"},
            {"url": "https://yksa.disec.fi/Yksa4/download/2/file/c", "desc": "a494a_01.mp3"},
        ],
        "buildings": [{"value": "0/FINLIT/"}, {"value": "1/FINLIT/Arkisto/"}],
    }
    rec.update(kw)
    return rec


def classify(rec):
    return finna.classify_record(rec["id"], rec, RULES, CFG)


def test_field_recording_included_once_with_credit():
    r = classify(sks())
    assert r.status == INCLUDE, r.reasons
    assert len(r.tracks) == 1          # two transfers of part _01 count once
    t = r.tracks[0]
    j = track_json(t)
    assert j["i"] == "finna:sks.1_2"
    assert j["u"] == "https://yksa.disec.fi/Yksa4/download/1/file/a"
    assert j["w"] == "https://www.finna.fi/Record/sks.1_2"
    assert j["s"] == "Finnish Literature Society"
    assert t.artist == "Paraske Mitrune"
    assert t.region == "Nordic" and t.genre == "nordic"


def test_speech_and_unlisted_holders_excluded():
    assert classify(sks(subjects=[["uskomustarinat"], ["aineistokokoelma"]])).status == EXCLUDE
    assert classify(sks(buildings=[{"value": "0/MPKK/"}])).status == EXCLUDE


def test_joik_flagged_for_a_human_check():
    r = classify(sks(subjects=[["joiut"], ["kansanlaulut"]]))
    assert r.status == FLAG


def test_after_1970_excluded():
    assert classify(sks(year="2023")).status == EXCLUDE
