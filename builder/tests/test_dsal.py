from oldfolk_builder import dsal
from oldfolk_builder.classify import EXCLUDE, FLAG, INCLUDE
from oldfolk_builder.config import load_rules
from oldfolk_builder.output import track_json

RULES = load_rules()
RULES.schedules_path = None
CFG = dsal.load_config()


def rec(**kw):
    r = {"recording_file": "113AK.mp3", "recording_title": "Hōli song", "language": "Maithilī",
         "narrator": "Ramanand Prasad Varma, Son of X", "gramophone_records_volume": "Bihar and Orissa"}
    r.update(kw)
    return r


def test_song_included_as_indian_with_credit():
    r = dsal.classify_record("113AK.mp3", rec(), RULES, CFG)
    assert r.status == INCLUDE, r.reasons
    j = track_json(r.tracks[0])
    assert j["u"] == "https://dsal.uchicago.edu/images/reference/lsi/audio/113AK.mp3"
    assert j["s"].startswith("Digital South Asia Library")
    assert j["t"] == "Hōli song (Maithilī)" and j["a"] == "Ramanand Prasad Varma"
    assert j["g"] == "southasia" and "y" not in j


def test_mp4_files_keep_their_real_url():
    r = dsal.classify_record("123AK.mp4", rec(recording_file="123AK.mp4"), RULES, CFG)
    assert r.tracks[0].url.endswith("/123AK.mp4")


def test_speech_samples_excluded():
    r = dsal.classify_record("1.mp3", rec(recording_title="Parable of the prodigal son"), RULES, CFG)
    assert r.status == EXCLUDE


def test_burmese_languages_and_funeral_songs():
    r = dsal.classify_record("2.mp3", rec(recording_title="A love song", language="Danaw"), RULES, CFG)
    assert r.tracks[0].region == "Southeast Asia" and r.tracks[0].genre == "asia"
    r = dsal.classify_record("3.mp3", rec(recording_title="Cremation song", language="Karen"), RULES, CFG)
    assert r.status == FLAG
