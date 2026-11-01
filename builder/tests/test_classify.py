from oldfolk_builder.classify import (EXCLUDE, FLAG, INCLUDE, classify_item, extract_tags,
                                     parse_year)
from oldfolk_builder.config import load_rules
from oldfolk_builder.tracks import parse_length, pick_files

RULES = load_rules()


def mp3(name, original=None, fmt="VBR MP3", length="180.0", **extra):
    f = {"name": name, "format": fmt, "length": length,
         "source": "derivative" if original else "original"}
    if original:
        f["original"] = original
    f.update(extra)
    return f


def item(meta, files=None):
    base = {"identifier": "x", "title": "A Song", "creator": "Somebody"}
    base.update(meta)
    return {"metadata": base,
            "files": files if files is not None else [mp3("a.mp3", "a.flac"),
                                                     {"name": "a.flac", "source": "original",
                                                      "format": "24bit Flac"}]}


def test_tags_keep_discogs_umbrella_genre_whole():
    tags = extract_tags({"genre": "Folk, World, & Country", "subject": ["Polka; Ethnic"]})
    assert "folk, world, & country" in tags
    assert "country" not in tags
    assert {"polka", "ethnic"} <= tags


def test_parse_year_variants():
    assert parse_year({"date": "1946-01-01T00:00:00Z"}) == (1946, None)
    assert parse_year({"date": "1920s"})[0] == 1929
    assert parse_year({"date": "192?"})[0] == 1929
    assert parse_year({"date": "1954-61"})[0] == 1961
    assert parse_year({"date": "1954-1961"})[0] == 1961
    assert parse_year({"date": "1950-11-22", "year": "1950"})[0] == 1950
    assert parse_year({})[0] is None


def test_parse_length():
    assert parse_length("03:04") == 184
    assert parse_length("147.46") == 147.46
    assert parse_length("1:00:00") == 3600
    assert parse_length(None) is None


def test_undated_78_folk_is_included_with_region_from_language():
    r = classify_item("78_halay", "78rpm",
                      item({"genre": "Folk", "language": "Armenian", "coverage": "USA",
                            "description": "<p><b>Performer:</b> Sam Haidostian</p>"}), RULES)
    assert r.status == INCLUDE, r.reasons
    assert r.region == "Middle East & Caucasus"
    assert r.genre == "asia"
    assert r.year is None and len(r.tracks) == 1


def test_jazz_excluded_and_spoken_word_excluded():
    assert classify_item("j", "78rpm", item({"genre": "Jazz"}), RULES).status == EXCLUDE
    r = classify_item("s", "78rpm", item({"genre": ["Folk", "Spoken Word"]}), RULES)
    assert r.status == EXCLUDE and "spoken" in r.reasons[0]


def test_after_cutoff_excluded():
    r = classify_item("late", "other", item({"subject": "folk", "date": "1972"}), RULES)
    assert r.status == EXCLUDE and "1972" in r.reasons[0]


def test_restricted_wording_excluded():
    r = classify_item("sacred", "other", item({
        "subject": "field recordings", "date": "1955",
        "description": "Culturally sensitive: not for public playback."}), RULES)
    assert r.status == EXCLUDE and "restricted" in r.reasons[0]


def test_ceremonial_flagged_not_excluded():
    r = classify_item("cer", "other", item({
        "subject": "field recordings", "date": "1955",
        "description": "A ceremonial song recorded in the village."}), RULES)
    assert r.status == FLAG and any("ceremonial" in x for x in r.reasons)


def test_lp_dated_before_lps_flagged_as_reissue():
    r = classify_item("lp_old", "other", item({
        "identifier": "lp_old", "subject": ["Folk Music", "Vinyl Recording"],
        "date": "1935", "title": "Leadbelly LP (1935)"}), RULES)
    assert r.status == FLAG
    assert any("before LPs existed" in x for x in r.reasons)


def test_compilation_and_later_year_flagged():
    r = classify_item("comp", "other", item({
        "subject": "folk", "date": "1930",
        "description": "Remastered compilation released on CD in 1994."}), RULES)
    assert r.status == FLAG
    joined = " ".join(r.reasons)
    assert "1994" in joined and "compilation" in joined


def test_writer_credit_flagged_and_approvable():
    meta = {"genre": "Hillbilly", "description": "<p><b>Writer:</b> Rodgers; McMichen</p>"}
    r = classify_item("w", "78rpm", item(meta), RULES)
    assert r.status == INCLUDE and r.writers == ["Rodgers", "McMichen"]   # kept by default
    rules = load_rules()
    rules.flag_named_writers = True
    assert classify_item("w", "78rpm", item(meta), rules).status == FLAG
    rules.overrides = {"approved_writers": ["Rodgers", "mcmichen"]}
    assert classify_item("w", "78rpm", item(meta), rules).status == INCLUDE


def test_traditional_writer_not_flagged():
    r = classify_item("t", "78rpm", item({"genre": "Irish",
                                          "description": "<b>Writer:</b> Traditional"}), RULES)
    assert r.status == INCLUDE and r.writers == []


def test_overrides_exclude_wins():
    rules = load_rules()
    rules.overrides = {"exclude": ["gone"]}
    assert classify_item("gone", "78rpm", item({"genre": "Folk"}), rules).status == EXCLUDE


def test_country_only_counts_on_78s():
    assert classify_item("c78", "78rpm", item({"genre": "Country"}), RULES).genre == "oldtime"
    r = classify_item("clp", "other", item({"subject": "Country", "date": "1965"}), RULES)
    assert r.status == EXCLUDE


def test_pick_files_one_mp3_per_recording():
    files = [
        {"name": "a.flac", "source": "original", "format": "Flac"},
        mp3("a.mp3", "a.flac"),
        mp3("a_64kb.mp3", "a.flac", fmt="64Kbps MP3"),
        mp3("b.mp3", None, fmt="VBR MP3"),               # original MP3 upload
        mp3("b_64kb.mp3", "b.mp3", fmt="64Kbps MP3"),     # its derivative
        mp3("side1.mp3", "x_segments.json", track="1"),
        mp3("side2.mp3", "x_segments.json", track="2"),
        mp3("c_sample.mp3", "c.flac"),
    ]
    names = [c.name for c in pick_files(files)]
    assert sorted(names) == ["a.mp3", "b.mp3", "side1.mp3", "side2.mp3"]


def test_oldtime_subject_alone_is_not_folk():
    # Uploaders use "Oldtime" to mean "old-fashioned"; it is not string-band music.
    r = classify_item("b1906", "78rpm", item({"subject": "Oldtime", "date": "1906"}), RULES)
    assert r.status == EXCLUDE


def test_psychedelic_folk_and_omr_excluded():
    r = classify_item("trex", "other", item({"subject": "psychedelic folk", "date": "1969"}), RULES)
    assert r.status == EXCLUDE
    r = classify_item("omr", "other", item({"subject": ["Lithuanian", "OMR", "Folk-Song"],
                                            "date": "1922"}), RULES)
    assert r.status == EXCLUDE and "synthesized" in r.reasons[0]


def test_writer_needs_label_not_prose():
    meta = {"genre": "Folk", "date": "1965",
            "description": "writer and curator of American folk songs Pete Seeger hosted"}
    assert classify_item("rq", "78rpm", item(meta), RULES).writers == []
    meta = {"genre": "Folk", "description": "<b>Writer:</b> None legible"}
    assert classify_item("nl", "78rpm", item(meta), RULES).writers == []


def test_region_from_script_and_compilation_tracks():
    r = classify_item("gr", "78rpm", item({"genre": "Folk", "title": "Το Τραγούδι"}), RULES)
    assert r.region == "Southern Europe"
    files = [mp3("1.mp3", "1.flac", title="Music Of Panama", track="1"),
             mp3("2.mp3", "2.flac", title="Pygmies Of The Ituri Forest", track="2"),
             mp3("3.mp3", "3.flac", title="Untitled chant", track="3")]
    r = classify_item("comp", "other", item({"genre": "Folk", "date": "1962", "creator": "Various",
                                             "description": "Includes music of Haiti."}, files),
                      RULES)
    regions = [t.region for t in r.tracks]
    assert regions == ["Latin America", "Africa", "Various"]
    assert [t.genre for t in r.tracks][:2] == ["latin", "african"]


def test_regimental_and_military_bands_excluded():
    r = classify_item("b", "78rpm", item({"genre": "Folk", "creator": "Band of the Coldstream Guards"}), RULES)
    assert r.status == EXCLUDE and "band" in r.reasons[0]
    r = classify_item("m", "78rpm", item({"genre": ["Folk", "March"]}), RULES)
    assert r.status == EXCLUDE
    r = classify_item("s", "78rpm", item({"genre": "Folk",
                                          "creator": "KUNGL. FLOTTANS MUSIKKÅR"}), RULES)
    assert r.status == EXCLUDE


def test_pop_and_latin_dance_not_included():
    assert classify_item("t", "78rpm", item({"genre": "Tango", "language": "Spanish"}), RULES).status == EXCLUDE
    assert classify_item("g", "78rpm", item({"genre": "Popular Music", "language": "Greek"}), RULES).status == EXCLUDE


def test_poor_and_worn_discs_excluded():
    for cond in ("Poor", "worn"):
        r = classify_item("p", "78rpm", item({"genre": "Folk", "condition": cond}), RULES)
        assert r.status == EXCLUDE and "condition" in r.reasons[0]
    assert classify_item("g", "78rpm", item({"genre": "Folk", "condition": "Fair"}), RULES).status == INCLUDE


def test_instrument_credits_are_not_writers():
    meta = {"genre": "Polka", "description": "Writer: Piano; Bass; Polka"}
    assert classify_item("p", "78rpm", item(meta), RULES).writers == []
