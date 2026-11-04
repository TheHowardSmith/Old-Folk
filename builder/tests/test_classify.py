from oldfolk_builder.classify import (EXCLUDE, FLAG, INCLUDE, classify_item, extract_tags,
                                     parse_year)
from oldfolk_builder.config import load_rules
from oldfolk_builder.tracks import parse_length, pick_files

RULES = load_rules()
RULES.schedules_path = None   # tests never use the real US schedules index
RULES.writer_deaths = {}      # ... nor real writer death lookups
RULES.writer_search = {}
from oldfolk_builder.llm_checks import LlmChecks
RULES.llm_checks = LlmChecks()


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
    assert r.genre == "mideast"
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


def test_writer_rule_keeps_unless_a_late_death_is_confirmed():
    meta = {"genre": "Hillbilly", "description": "<p><b>Writer:</b> Jimmie Rodgers; Clayton McMichen</p>"}
    rules = load_rules()
    rules.schedules_path = None
    rules.writer_deaths = {}                      # nothing known: kept, on the check list
    r = classify_item("w", "78rpm", item(meta), rules)
    assert r.status == INCLUDE and r.writers == ["Jimmie Rodgers", "Clayton McMichen"]
    assert any("not confirmed" in c for c in r.checks)
    rules.writer_deaths = {"Jimmie Rodgers": {"status": "dead", "died": 1933},
                           "Clayton McMichen": {"status": "dead", "died": 1970}}
    r = classify_item("w", "78rpm", item(meta), rules)
    assert r.status == EXCLUDE and "McMichen (d. 1970)" in r.reasons[0]       # confirmed too recent
    rules.writer_deaths["Clayton McMichen"] = {"status": "dead", "died": 1950}
    r = classify_item("w", "78rpm", item(meta), rules)
    assert r.status == INCLUDE and not r.checks                               # both died 70+ years ago
    # A surname or initial alone isn't trusted to be the same person: kept and checked.
    short = {"genre": "Polka", "description": "<p><b>Writer:</b> F. Wojnarowski</p>"}
    rules.writer_deaths = {"F. Wojnarowski": {"status": "dead", "died": 1973}}
    r = classify_item("p", "78rpm", item(short), rules)
    assert r.status == INCLUDE and r.checks
    rules.writer_deaths = {}
    rules.overrides = {"approved_writers": ["jimmie rodgers", "Clayton McMichen"]}
    r = classify_item("w", "78rpm", item(meta), rules)
    assert r.status == INCLUDE and not r.checks                               # approved by hand


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


def test_pick_image_prefers_label_scan():
    from oldfolk_builder.tracks import pick_image
    files = [{"name": "x_spectrogram.png", "format": "Spectrogram", "source": "derivative"},
             {"name": "Song - Band.png", "format": "PNG", "source": "derivative"},
             {"name": "__ia_thumb.jpg", "format": "Item Tile", "source": "original"},
             {"name": "x.jpg", "format": "JPEG", "source": "derivative"},
             {"name": "x_itemimage.jpg", "format": "Item Image", "source": "original"}]
    assert pick_image("x", files) == 1
    assert pick_image("x", files[:4]) == "x.jpg"
    assert pick_image("x", files[:3]) == "0"       # generic tile only (a waveform): no picture
    assert pick_image("x", []) == "0"


def test_polka_is_its_own_genre():
    assert classify_item("p", "78rpm", item({"genre": "Polka", "language": "Polish"}), RULES).genre == "polka"
    # untagged polka by title, generic folk tag
    r = classify_item("q", "78rpm", item({"genre": "Ethnic", "title": "Clarinet Polka", "language": "Polish"}), RULES)
    assert r.genre == "polka"
    # oberek stays Eastern European; a hillbilly "polka" stays old-time
    assert classify_item("o", "78rpm", item({"genre": "Oberek", "language": "Polish"}), RULES).genre == "eastern_europe"
    assert classify_item("h", "78rpm", item({"genre": "Hillbilly", "title": "Hillbilly Polka"}), RULES).genre == "oldtime"


def test_folk_dance_instruction_records_excluded():
    for creator in ("Michael Herman's Folk Orchestra", "FOLKRAFT INTERNATIONAL ORCHESTRA"):
        r = classify_item("i", "78rpm", item({"genre": "Folk Dance", "creator": creator}), RULES)
        assert r.status == EXCLUDE and "instruction" in r.reasons[0]
    r = classify_item("j", "78rpm", item({"genre": "Folk Dance", "publisher": "Folk Dancer"}), RULES)
    assert r.status == EXCLUDE


def test_us_claimed_recordings_excluded_unless_us_public_domain():
    from oldfolk_builder.schedules import ScheduleIndex
    rules = load_rules()
    rules.exclude_us_claimed = True
    rules.schedules = ScheduleIndex({"sally goodin": [(frozenset({"robertson"}), "Sony Music Entertainment")]})
    meta = {"genre": "Old Time", "title": "Sally Goodin", "creator": "Eck Robertson"}
    r = classify_item("s", "78rpm", item(meta), rules)
    assert r.status == EXCLUDE and "Sony" in r.reasons[0]
    # recorded 1922: US protection has expired, so the claim doesn't matter
    assert classify_item("s", "78rpm", item(dict(meta, date="1922")), rules).status == INCLUDE
    # same title, different performer: kept
    assert classify_item("t", "78rpm", item(dict(meta, creator="Other Fiddler")), rules).status == INCLUDE


def lp(ident, title, genres, creator="Various", description="Tracklist: 1. A  2. B", date="1962"):
    return classify_item(ident, "other",
                         item({"identifier": ident, "title": title, "creator": creator,
                               "genre": genres, "date": date, "description": description,
                               "collection": ["album_recordings", "audio_music"]}), RULES)


def test_non_western_classical_is_traditional():
    r = lp("lp_raga-todi", "Raga Todi / Mishra Thumri",
           ["Classical", "Folk, World, & Country", "Indian Classical"], creator="Bismillah Khan")
    assert r.status == INCLUDE, r.reasons
    r = lp("lp_hundred-feet", "Hundred Feet Above The Pole - Chinese Classical Music",
           ["Classical", "Folk, World, & Country"])
    assert r.status == INCLUDE, r.reasons
    r = lp("lp_nagauta", "Nagauta Music And Arrangements From Older Classics",
           ["Classical", "Folk, World, & Country"], creator="The Azuma Kabuki Musicians")
    assert r.status == INCLUDE, r.reasons
    r = lp("lp_dragon-robe", "Beating The Dragon Robe, A Traditional Peking Opera",
           ["Folk, World, & Country", "Opera", "Chinese Classical"])
    assert r.status == INCLUDE, r.reasons


def test_western_forms_and_early_music_keep_classical_flag():
    r = lp("lp_chinese-violin", "The Chinese Violin",
           ["Classical", "Folk, World, & Country", "Chinese Classical"],
           description="Tracklist: 1. Butterfly Lovers (A Violin Concerto): First Movement")
    assert r.status == FLAG
    r = lp("lp_andalusia", "Medieval Courtly Monody - Arabian Andalusia",
           ["Classical", "Folk, World, & Country", "Medieval", "Andalusian Classical"])
    assert r.status == FLAG and "medieval" in r.reasons[0]
    r = lp("lp_bartok", "44 Violin Duets", ["Classical", "Folk, World, & Country", "Folk"])
    assert r.status == FLAG


def test_umbrella_genre_with_pop_tags_is_excluded():
    r = lp("lp_holiday-in-japan", "Holiday In Japan",
           ["Pop", "Folk, World, & Country", "Easy Listening"],
           creator="Ricardo Santos And His Orchestra", date="1957")
    assert r.status == EXCLUDE and "Folk, World, & Country" in r.reasons[0]
    r = lp("lp_east-of-india", "East Of India: Exotic Melodies",
           ["Folk, World, & Country", "Stage & Screen"], date="1959")
    assert r.status == EXCLUDE
    # a real folk tag alongside keeps it in review
    r = lp("lp_mantovani", "Folk Songs Around The World",
           ["Jazz", "Folk, World, & Country", "Folk", "Easy Listening"], date="1964")
    assert r.status == FLAG


def test_victor_ethnic_disc_popular_music_tag_ignored():
    r = classify_item("loc-jukebox-20824-loussine-ga", "78rpm",
                      item({"identifier": "loc-jukebox-20824-loussine-ga", "title": "Loussine ga",
                            "date": "1916-12-08", "collection": ["78rpm", "audio_music"],
                            "subject": ["ethnic music", "vocal", "victor", "armenian",
                                        "popular music", "78rpm"]}), RULES)
    assert r.status == INCLUDE, r.reasons
    assert r.region == "Middle East & Caucasus"


def test_vintagesense_series_excluded():
    r = classify_item("www.VintageSense.com-Vintage-Indian-Music-80n", "78rpm",
                      item({"identifier": "www.VintageSense.com-Vintage-Indian-Music-80n",
                            "subject": "indian; folk; film; classical; hindi",
                            "collection": ["78rpm", "audio_music"]}), RULES)
    assert r.status == EXCLUDE and "VintageSense" in r.reasons[0]


def test_central_asian_tracks_are_not_african_despite_bad_language_tag():
    files = [mp3("01.mp3", title="Amu (Tajik Folk Song)"), mp3("02.mp3", title="Lezginka (Daghestan Dance)"),
             mp3("03.mp3", title="Dombara (Kazakh Folk Song)")]
    r = classify_item("x", "other", item({"title": "Folk Music Of The S.S.R. Middle East",
                                          "subject": ["Folk"], "date": "1956",
                                          "language": ["eng", "uzb", "hau"]}, files), RULES)
    regions = {t.title: t.region for t in r.tracks}
    assert regions["Amu (Tajik Folk Song)"] == "Central Asia"
    assert regions["Dombara (Kazakh Folk Song)"] == "Central Asia"
    assert regions["Lezginka (Daghestan Dance)"] == "Middle East & Caucasus"
    genres = {t.title: t.genre for t in r.tracks}
    assert genres["Amu (Tajik Folk Song)"] == "mideast"     # Central Asia goes with the Middle East
    assert genres["Lezginka (Daghestan Dance)"] == "mideast"


def test_indian_music_has_its_own_genre():
    r = classify_item("x", "78rpm", item({"subject": ["Folk", "Hindustani"], "title": "Raga Bhairavi"}), RULES)
    assert r.genre == "southasia"
    r = classify_item("x", "78rpm", item({"subject": ["Folk"], "language": "hin"}), RULES)
    assert r.genre == "southasia" and r.region == "South Asia"


def test_russian_is_european_and_siberian_is_asian():
    r = classify_item("x", "78rpm", item({"subject": ["Folk"], "title": "Don Cossack Song"}), RULES)
    assert (r.region, r.genre) == ("Central & Eastern Europe", "eastern_europe")
    r = classify_item("x", "78rpm", item({"subject": ["Folk"], "title": "Russian Folk Song"}), RULES)
    assert r.genre == "eastern_europe"
    r = classify_item("x", "other", item({"subject": ["Folk"], "title": "Tuvan Throat Singing", "date": "1960"}), RULES)
    assert (r.region, r.genre) == ("Siberia", "asia")
    r = classify_item("x", "other", item({"subject": ["Folk"], "title": "Chukchi Songs", "date": "1960"}), RULES)
    assert r.status == FLAG


def test_source_level_subjects_bring_in_bolivian_78s():
    from oldfolk_builder.cli import with_subjects
    data = item({"subject": ["78rpm", "bolivia"], "title": "Chayanteña", "date": "1917"})
    assert classify_item("x", "78rpm", data, RULES).status == EXCLUDE
    r = classify_item("x", "78rpm", with_subjects(data, ["folk music", "bolivian"]), RULES)
    assert r.status == INCLUDE and r.genre == "latin" and r.region == "Latin America"


GD_TAGS = ["the grateful dead", "psychedelic rock", "acid rock", "folk", "blues rock", "blues", "usa",
           "country", "country rock", "vinyl rip"]


def test_rock_styles_are_not_folk():
    r = classify_item("x", "other", item({"subject": GD_TAGS, "title": "Aoxomoxoa", "date": "1969"}), RULES)
    assert r.status != INCLUDE


def test_user_rips_are_kept_but_checked_and_tv_excluded():
    r = classify_item("x", "other", item({"subject": ["folk"], "date": "1969",
                                          "collection": ["hifidelity_potpourri", "hifidelity"]}), RULES)
    assert r.status == INCLUDE and any("hifidelity" in c for c in r.checks), (r.reasons, r.checks)
    tv = classify_item("tv", "other", item({"subject": ["folk"], "date": "1966", "collection": ["television"]}), RULES)
    assert tv.status == EXCLUDE and "collection" in tv.reasons[0]


def test_claimed_songs_on_an_album_are_dropped(tmp_path):
    import json
    from dataclasses import replace
    p = tmp_path / "sched.jsonl"
    p.write_text(json.dumps({"title": ["St. Stephen"], "artist": ["Grateful Dead"],
                             "rights_owner": ["Warner Records Inc."]}) + "\n")
    rules = replace(RULES, schedules=None, schedules_path=p, exclude_us_claimed=True)
    files = [mp3("a1.mp3", title="St. Stephen", creator="Grateful Dead"),
             mp3("a2.mp3", title="Old Song", creator="Grateful Dead")]
    r = classify_item("x", "other", item({"subject": ["folk"], "title": "An Album", "date": "1960",
                                          "creator": "Grateful Dead"}, files), rules)
    assert [t.title for t in r.tracks] == ["Old Song"]
    assert r.status == INCLUDE and any("claimed" in c for c in r.checks)   # unclaimed songs kept, checked
    only = classify_item("x", "other", item({"subject": ["folk"], "title": "An Album", "date": "1960",
                                             "creator": "Grateful Dead"}, files[:1]), rules)
    assert only.status == EXCLUDE


def test_availability_matching_and_filter(tmp_path):
    import json
    from oldfolk_builder import availability as av
    from oldfolk_builder.cli import apply_availability
    hit = {"trackName": "Good Old Fashioned Love", "artistName": "Wynn Stewart"}
    assert av.is_match("Good Old Fashioned Love", "Wynn Stewart", hit)
    assert not av.is_match("Good Old Fashioned Love", "Various", hit)          # title alone is too weak
    assert not av.is_match("Old Love", "Wynn Stewart", {"trackName": "Love Me Do", "artistName": "Wynn Stewart"})
    files = [mp3("a.mp3", title="Good Old Fashioned Love"), mp3("b.mp3", title="Other")]
    r = classify_item("x", "other", item({"subject": ["folk"], "date": "1960", "creator": "Wynn Stewart"}, files), RULES)
    p = tmp_path / "availability.jsonl"
    p.write_text(json.dumps({"k": "x/a.mp3", "found": True}) + "\n" + json.dumps({"k": "x/b.mp3", "found": False}) + "\n")
    assert apply_availability([r], p) == 1
    assert [t.file for t in r.tracks] == ["b.mp3"]


def test_jewish_recordings_move_out_of_place_based_genres():
    from oldfolk_builder.classify import is_jewish
    assert is_jewish("Songs Of Israel")
    assert is_jewish("Favorite Yiddish Songs")
    assert is_jewish("Nigun Rikud")
    assert is_jewish("Shuvi Nafshi", "Cantor Leibele Waldman")
    assert is_jewish("Finjan", 'Yaffa Yarkoni, "Kol Israel" Orch.')
    # place or surname alone is not enough
    assert not is_jewish("Recitation from Firdausi's Shahname", "Ishak Ramani and Israel Kashi")
    assert not is_jewish("Degirmenci", "Victoria Hazan")
    assert not is_jewish("Donde Estan Los Carboneros", "Coros Cantores de Madrid")
    assert not is_jewish("Hora Staccato")


def test_user_uploads_after_1925_are_kept_and_checked():
    r = classify_item("some-1970-lp-rip", "other",
                      item({"subject": "folk", "date": "1970-06-10", "collection": "opensource_audio",
                            "language": "Japanese"}), RULES)
    assert r.status == INCLUDE and any("user upload" in x for x in r.checks), (r.reasons, r.checks)
    old = classify_item("old-upload", "other",
                        item({"subject": "folk", "date": "1912", "collection": "opensource_audio"}), RULES)
    assert not old.checks, old.checks


def test_approved_upload_lifts_only_the_upload_hold():
    from oldfolk_builder.config import load_rules
    rules = load_rules()
    rules.schedules_path = None
    rules.writer_deaths = {}
    rules.overrides = {"approved_uploads": ["home-tape-1965"]}
    r = classify_item("home-tape-1965", "other",
                      item({"subject": "folk", "date": "1965", "collection": "opensource_audio"}), rules)
    assert not any("user upload" in x for x in r.reasons + r.checks), (r.reasons, r.checks)


def test_song_specific_writer_results_come_first():
    meta = {"genre": "Polka", "description": "<p><b>Writer:</b> Brown; Vejvoda</p>"}
    rules = load_rules()
    rules.schedules_path = None
    rules.writer_deaths = {}
    rules.writer_search = {"bb": {"writers": {
        "Brown": {"status": "dead_recent", "died": 1958, "mb_name": "Lew Brown"},
        "Vejvoda": {"status": "dead_recent", "died": 1988, "mb_name": "Jaromír Vejvoda"}}}}
    r = classify_item("bb", "78rpm", item(meta), rules)
    assert r.status == EXCLUDE and "Lew Brown (d. 1958)" in r.reasons[0]
    rules.writer_search = {"hc": {"writers": {"Adams": {"status": "dead_old", "died": 1913}}}}
    r = classify_item("hc", "78rpm", item({"genre": "Folk", "description": "<p><b>Writer:</b> Adams</p>"}), rules)
    assert r.status == INCLUDE and not r.checks


def test_european_split_four_ways_by_region():
    def g(meta):
        return classify_item("e", "78rpm", item(dict({"subject": ["Folk"]}, **meta)), RULES).genre
    assert g({"title": "Swedish Hambo"}) == "nordic"
    assert g({"title": "Greek Dance"}) == "mediterranean"
    assert g({"title": "Tyrolean Yodel"}) == "european"
    assert g({"title": "Ukrainian Song"}) == "eastern_europe"
    # style tags alone
    assert g({"genre": "Schottis"}) == "nordic"
    assert g({"genre": "Gypsy"}) == "eastern_europe"


def test_choirs_get_their_own_genre():
    def g(creator, **meta):
        return classify_item("c", "78rpm", item(dict({"subject": ["Folk"], "creator": creator}, **meta)), RULES).genre
    assert g("Ukrainian Bandurist Chorus") == "choral"
    assert g("Хор села Варзуги") == "choral"
    assert g("Roger Wagner Chorale") == "choral"
    # backed by a choir is not a choir record
    assert g("Will Glahé Und Sein Orchester Mit Chor", title="Swedish Song") == "nordic"
    assert g("Buddy Lucas Orchestra & Chorus", title="Swedish Song") == "nordic"
    assert g("Klaus Alzner's orkester og kor", title="Swedish Song") == "nordic"
    # gospel and polka keep theirs
    assert g("Tuskegee Institute Choir", genre="Spirituals") == "gospel"
    assert g("Chor Dana", genre="Polka") == "polka"
    # choirs from outside Europe and North America keep their region's genre
    assert g("The De Paur Chorus", title="Nigeria - Ake (Traditional Work Chant)") == "african"
    assert g("Korean Folk Choir", title="Korean Folk Song") == "asia"


def test_other_traditional_is_resorted():
    def files(*titles):
        return [mp3(f"{i}.mp3", f"{i}.flac") | {"title": t} for i, t in enumerate(titles)]
    def g(meta, *titles):
        r = classify_item("w", "lp", item(dict({"subject": ["Folk"], "date": "1958"}, **meta), files(*titles)), RULES)
        return r.genre, {t.genre for t in r.tracks}
    assert g({}, "The Ash Grove", "Down In The Valley", "Kisses Sweeter Than Wine") == ("american", {"american"})
    assert g({}, "Rock Of Ages", "Silent Night", "Holy, Holy, Holy") == ("gospel", {"gospel"})
    assert g({"creator": "Σούλα Μπιρμπίλη"}, "Ta Dilina", "Epipoleos") == ("mediterranean", {"mediterranean"})
    assert g({}, "Hora Mamtera", "Kizba", "Avelina") == ("world", {"world"})


def test_punk_and_creative_commons_uploads():
    r = classify_item("p", "lp", item({"subject": ["folk", "punk"], "date": "1913"}), RULES)
    assert r.status == FLAG and "punk" in r.reasons[0]
    r = classify_item("q", "lp", item({"subject": ["folk"], "date": "1958",
                                       "licenseurl": "http://creativecommons.org/licenses/by-nc-nd/3.0/"}), RULES)
    assert any("Creative Commons" in c for c in r.checks)



def test_album_tracks_keep_the_items_jewish_genre():
    files = [mp3("01.mp3", title="Ma Avarech"), mp3("02.mp3", title="Debka Skayo")]
    r = classify_item("lp_new-folk-dances-of-israel", "lp",
                      item({"title": "New Folk Dances Of Israel", "subject": ["Folk, World, & Country"],
                            "genre": "Folk, World, & Country", "date": "1970", "creator": "Uri Hodorov"},
                           files), RULES)
    assert r.genre == "jewish" and {t.genre for t in r.tracks} == {"jewish"}


def test_middle_eastern_and_asian_are_separate():
    def g(meta):
        return classify_item("m", "78rpm", item(dict({"subject": ["Folk"]}, **meta)), RULES).genre
    assert g({"genre": "Turkish"}) == "mideast"
    assert g({"genre": "Arabic"}) == "mideast"
    assert g({"genre": "Japanese"}) == "asia"
    assert g({"title": "Korean Folk Song"}) == "asia"
