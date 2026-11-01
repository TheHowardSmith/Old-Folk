import pytest

import oldfolk_builder.config as config


@pytest.fixture(autouse=True, scope="session")
def _no_real_schedules_index():
    """Keep tests hermetic: never load the local US schedules cache."""
    original = config._schedules_path
    config._schedules_path = lambda cls: None
    yield
    config._schedules_path = original
