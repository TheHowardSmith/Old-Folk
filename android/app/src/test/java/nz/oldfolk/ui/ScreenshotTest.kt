package nz.oldfolk.ui

import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import nz.oldfolk.data.Genre
import nz.oldfolk.data.MapRegion
import nz.oldfolk.data.Playlist
import nz.oldfolk.data.Track
import org.junit.Rule
import org.junit.Test

/** Renders each screen with sample data, light and dark (./gradlew recordPaparazziDebug). */
class ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5, maxPercentDifference = 0.1)

    private val track = Track("78_x", "x.mp3", "The Ballad of the Boll Weevil", "Fiddlin' John Carson and his Virginia Reelers",
        1927, "USA", "oldtime")
    private val undated = Track("78_y", "y.mp3", "Halay", "Sam Haidostian", null, "Middle East & Caucasus", "asia")
    private val upNext = listOf(
        Track("a", "a.mp3", "Sally Goodin", "Eck Robertson", 1922, "USA", "oldtime", seconds = 166),
        Track("b", "b.mp3", "Kerry Polka", "Patrick Killoran", 1937, "British Isles", "british", seconds = 194),
        Track("c", "c.mp3", "Mamele", "Abe Schwartz Orchestra", null, "Central & Eastern Europe", "jewish"),
    )
    private val genres = listOf(
        Genre("songs", "Traditional Songs & Ballads", 11240), Genre("composed", "Composed & Popular Songs", 3980),
        Genre("dance", "Dance Tunes", 4120), Genre("oldtime", "Old-time & Hillbilly", 4767),
        Genre("country", "Country & Western", 2079), Genre("blues", "Country Blues", 1213),
        Genre("gospel", "Gospel & Spirituals", 1624), Genre("polka", "Polka", 5560),
        Genre("jewish", "Klezmer & Jewish", 1009), Genre("field", "Field Recordings & Work Songs", 1933),
    )
    private val regions = listOf(
        MapRegion("north_america", "North America", listOf("USA"), 14800), MapRegion("latin_america", "Latin America", listOf(), 2400),
        MapRegion("caribbean", "Caribbean", listOf(), 560), MapRegion("british_isles", "British Isles", listOf(), 1540),
        MapRegion("western_europe", "Western Europe", listOf(), 1500), MapRegion("nordic", "Nordic", listOf(), 1900),
        MapRegion("eastern_europe", "Eastern Europe", listOf(), 4400), MapRegion("southern_europe", "Southern Europe", listOf(), 1960),
        MapRegion("middle_east", "Middle East", listOf(), 540), MapRegion("africa", "Africa", listOf(), 370),
        MapRegion("central_asia", "Central Asia & Siberia", listOf(), 60), MapRegion("south_asia", "South Asia", listOf(), 160),
        MapRegion("east_asia", "East Asia", listOf(), 300), MapRegion("southeast_asia", "Southeast Asia", listOf(), 150),
        MapRegion("pacific", "Hawaii & Pacific", listOf(), 1640),
    )

    private fun radio(t: Track?, playing: Boolean, msg: String? = null) = RadioUi(positionMs = 74_000, durationMs = 185_000,
        station = "Radio · all genres", track = t, genreName = t?.let { "Old-time & Hillbilly" },
        isPlaying = playing, isBuffering = false, message = msg,
        inPlaylist = false, listLoading = false, listProgress = null, showRetry = false,
    )

    private fun shot(dark: Boolean, tab: Tab, title: String = "Old Folk Air", content: @Composable () -> Unit) =
        paparazzi.snapshot {
            OldFolkTheme(dark = dark) {
                AppFrame(tab, title, showBack = false, showAboutButton = true, onBack = {}, onAbout = {}, onTab = {}, content = content)
            }
        }

    @Test fun radioPlayingLight() = shot(false, Tab.RADIO) { RadioContent(radio(track, true), RadioActions()) }
    @Test fun radioPlayingDark() = shot(true, Tab.RADIO) { RadioContent(radio(track, true), RadioActions()) }
    @Test fun radioUndatedPausedDark() = shot(true, Tab.RADIO) { RadioContent(radio(undated, false), RadioActions()) }
    @Test fun radioEmptyLight() = shot(false, Tab.RADIO) { RadioContent(radio(null, false), RadioActions()) }
    @Test fun genresLight() = shot(false, Tab.GENRES, "Genres") {
        GenresContent(genres, setOf("oldtime", "blues"), {}, {}, regions, emptySet(), atlas = rememberAtlas())
    }
    @Test fun genresDark() = shot(true, Tab.GENRES, "Genres") {
        GenresContent(genres, emptySet(), {}, {}, regions, setOf("nordic", "british_isles"), atlas = rememberAtlas())
    }
    @Test fun historyDark() = shot(true, Tab.HISTORY, "History") {
        HistoryContent(
            listOf(HistoryRow.Past(upNext[1], "9:12 pm"), HistoryRow.Past(upNext[0], "9:15 pm"),
                HistoryRow.Now(track)) + upNext.mapIndexed { i, t -> HistoryRow.Ahead(t, i + 3) },
            isPlaying = true, isSaved = { it.year != null }, onRow = {},
        )
    }
    @Test fun historyLight() = shot(false, Tab.HISTORY, "History") {
        HistoryContent(
            listOf(HistoryRow.Past(upNext[1], "9:12 pm"), HistoryRow.Now(track)) +
                upNext.mapIndexed { i, t -> HistoryRow.Ahead(t, i + 2) },
            isPlaying = false, isSaved = { it.year != null }, onRow = {},
        )
    }
    @Test fun playlistsLight() = shot(false, Tab.PLAYLISTS, "Playlists") {
        PlaylistsContent(listOf(Playlist("1", "Kitchen dances", upNext), Playlist("2", "Sunday blues", listOf(track))), {}, {})
    }
    @Test fun aboutDark() = shot(true, Tab.RADIO, "About") {
        AboutContent(AboutInfo("202611011030", 24870, "9/9", 312, 2, "210 MB of 600 MB", "0.1.9",
            update = UpdateUi.Available("0.2.0"), theme = "dark"), {}, {}, {}, {})
    }
}
