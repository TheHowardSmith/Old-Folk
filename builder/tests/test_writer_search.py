from oldfolk_builder.writer_search import (classify_result, clean_credit, is_traditional, names_match,
                                           split_titles)


def test_clean_credit():
    assert clean_credit("1 - C. Butler") == "C. Butler"
    assert clean_credit("– Hamlet Lima Quintana") == "Hamlet Lima Quintana"
    assert clean_credit("***") is None and clean_credit("jo") is None
    assert is_traditional(clean_credit("Trad."))


def test_names_match_needs_surname_and_fitting_initials():
    assert names_match("W. Obzut", "Walter Obzut")
    assert names_match("F. Wojnarowski", "", "Wojnarowski, Frank")
    assert names_match("Vejvoda", "Jaromír Vejvoda")
    assert not names_match("J. Smith", "Bob Smith")
    assert not names_match("Brown", "Brownlee Jones")


def test_split_titles():
    assert split_titles("BEER BARREL POLKA (ROLL OUT THE BARREL)") == [
        "BEER BARREL POLKA (ROLL OUT THE BARREL)", "BEER BARREL POLKA", "ROLL OUT THE BARREL"]
    assert split_titles("(1) Come o'er the stream Charlie; (2) Kelvin Grove") == [
        "Come o'er the stream Charlie", "Kelvin Grove"]


def test_classify_result():
    assert classify_result({"died": 1946}, 1955, 2026) == "dead_old"
    assert classify_result({"died": 1988}, 1955, 2026) == "dead_recent"
    assert classify_result({"born": 1940, "ended": False}, 1955, 2026) == "living"
    assert classify_result({"born": 1890, "ended": False}, 1955, 2026) == "unresolved"
    assert classify_result(None, 1955, 2026) == "unresolved"
