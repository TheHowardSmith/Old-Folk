package nz.oldfolk.ui

import androidx.compose.runtime.Composable
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import nz.oldfolk.data.Genre
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
        Genre("oldtime", "Old-time & Hillbilly", 6211), Genre("blues", "Country Blues", 2480),
        Genre("british", "British Isles & Celtic", 812), Genre("european", "European Folk & Dance", 9470),
        Genre("jewish", "Klezmer & Jewish", 560), Genre("pacific", "Hawaiian & Pacific", 1490),
        Genre("caribbean", "Calypso & Caribbean", 190), Genre("asia", "Asian & Middle Eastern", 870),
        Genre("field", "Field Recordings & Work Songs", 120),
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
    @Test fun genresLight() = shot(false, Tab.GENRES, "Genres") { GenresContent(genres, setOf("oldtime", "blues"), {}, {}) }
    @Test fun genresDark() = shot(true, Tab.GENRES, "Genres") { GenresContent(genres, emptySet(), {}, {}) }
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
