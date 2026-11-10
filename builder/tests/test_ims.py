import json

from oldfolk_builder import ims
from oldfolk_builder.classify import EXCLUDE, INCLUDE
from oldfolk_builder.config import load_rules
from oldfolk_builder.output import track_json

RULES = load_rules()
RULES.schedules_path = None
RULES.writer_deaths = {}
CFG = ims.load_config()
MP3 = "https://discografia-brasileira.s3.sa-east-1.amazonaws.com/466d4d240a8fbe0b33a1e1939348d047.mp3"


def rec(**kw):
    d = {"@type": "MusicRecording", "publicDomainYear": "1999", "name": "CABOCLO DO MATO",
         "inAlbum": {"@type": "MusicAlbum", "dateBegin": "1927", "name": "Odeon 10020"},
         "creator": {"@type": "Person", "name": "Américo Jacomino \"Canhoto\""},
         "genre": {"name": "Toada"}, "audio": {"@type": "AudioObject", "contentUrl": MP3},
         "pageUrl": "https://discografiabrasileira.com.br/fonograma/19508/x"}
    d.update(kw)
    return d


def test_parse_page_reads_json_ld():
    page = ('<html><script type="application/ld+json">' + json.dumps(rec()) + '</script></html>')
    assert ims.parse_page(page)["publicDomainYear"] == "1999"
    assert ims.parse_page("<html></html>") is None


def test_public_domain_folk_recording_included():
    r = ims.classify_record("19508", rec(), RULES, CFG, this_year=2026)
    assert r.status == INCLUDE, r.reasons
    j = track_json(r.tracks[0])
    assert j["u"] == MP3 and j["s"] == "Instituto Moreira Salles"
    assert j["t"] == "Caboclo Do Mato" and j["y"] == 1927 and j["g"] == "latin"


def test_not_yet_public_domain_or_pop_excluded():
    assert ims.classify_record("1", rec(publicDomainYear="2027"), RULES, CFG, this_year=2026).status == EXCLUDE
    assert ims.classify_record("1", rec(publicDomainYear=None), RULES, CFG, this_year=2026).status == EXCLUDE
    r = ims.classify_record("1", rec(genre={"name": "Canção"}), RULES, CFG, this_year=2026)
    assert r.status == EXCLUDE and "popular" in r.reasons[0]


def test_samba_and_other_dances_get_their_own_style():
    for g in ("Samba", "Choro", "Marcha", "Tango brasileiro", "Valsa"):
        r = ims.classify_record("1", rec(genre={"name": g}), RULES, CFG, this_year=2026)
        assert r.status == INCLUDE and r.tracks[0].genre == "latindance", (g, r.reasons)
    # ... but only once they are in the public domain in Brazil.
    assert ims.classify_record("1", rec(genre={"name": "Samba"}, publicDomainYear="2034"), RULES, CFG,
                               this_year=2026).status == EXCLUDE


def test_brazilian_polcas_go_to_polka():
    r = ims.classify_record("1", rec(genre={"name": "Polca"}), RULES, CFG, this_year=2026)
    assert r.status == INCLUDE and r.tracks[0].genre == "polka"


def test_performer_with_stage_and_real_name_reads_as_text():
    assert ims._name({"@type": "Person", "name": ["Bahiano", "Manuel Pedro dos Santos"]}) == "Bahiano (Manuel Pedro dos Santos)"
    assert ims._name({"name": ["Cadete", "KDT", "Manoel Evêncio da Costa Moreira"]}) == "Cadete (Manoel Evêncio da Costa Moreira)"
    assert ims._name([{"name": "Pixinguinha"}, {"name": ["Donga"]}]) == "Pixinguinha, Donga"
