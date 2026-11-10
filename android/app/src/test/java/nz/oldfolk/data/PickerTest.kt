package nz.oldfolk.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PickerTest {
    private fun t(n: Int, genre: String) = Track("item$n", "f$n.mp3", "Song $n", null, 1930, "USA", genre)
    private val tracks = (1..10).map { t(it, if (it <= 6) "oldtime" else "blues") }

    @Test
    fun picksOnlyUnplayedTracksOfSelectedGenres() {
        val played = mutableSetOf<Long>()
        val seen = mutableSetOf<String>()
        repeat(4) {
            val r = Picker.pick(tracks, Selection(setOf("blues")), { it in played }, emptySet(), Random(it))
            r as Picker.Result.Picked
            assertEquals("blues", r.track.genre)
            assertTrue(seen.add(r.track.id))       // never repeats
            played += r.track.key
        }
        assertEquals(Picker.Result.Exhausted, Picker.pick(tracks, Selection(setOf("blues")), { it in played }, emptySet()))
    }

    @Test
    fun excludesQueuedTracks() {
        val queued = tracks.filter { it.genre == "blues" }.drop(1).map { it.key }.toSet()
        val r = Picker.pick(tracks, Selection(setOf("blues")), { false }, queued) as Picker.Result.Picked
        assertEquals("item7", r.track.id)
    }

    @Test
    fun emptyGenreSetMeansAll_andUnknownGenreMeansNoTracks() {
        assertTrue(Picker.pick(tracks, Selection(), { false }, emptySet()) is Picker.Result.Picked)
        assertEquals(Picker.Result.NoTracks, Picker.pick(tracks, Selection(setOf("jazz")), { false }, emptySet()))
    }

    @Test
    fun regionsFilterThroughTheIndexAndCombineWithStyles() {
        val mixed = listOf(
            Track("us1", "a.mp3", "A", null, 1930, "USA", "oldtime"),
            Track("ca1", "b.mp3", "B", null, 1930, "Canada", "songs"),
            Track("ie1", "c.mp3", "C", null, 1930, "British Isles", "songs"),
            Track("ie2", "d.mp3", "D", null, 1930, "British Isles", "dance"),
        )
        val regionOf = mapOf("USA" to "north_america", "Canada" to "north_america", "British Isles" to "british_isles")
        val na = Selection(regions = setOf("north_america"), regionOf = regionOf)
        assertEquals(setOf("us1", "ca1"), mixed.filter(na::matches).map { it.id }.toSet())
        val songsInIreland = Selection(setOf("songs"), setOf("british_isles"), regionOf)
        assertEquals(listOf(mixed[2].key), Picker.matchingKeys(mixed, songsInIreland))
        // A list without map regions (an older index) ignores a saved region choice.
        assertEquals(4, mixed.count(Selection(regions = setOf("north_america"))::matches))
    }

    @Test
    fun keysAreStableAndDistinct() {
        assertEquals(Track.keyOf("a", "b.mp3"), Track.keyOf("a", "b.mp3"))
        assertTrue(Track.keyOf("a", "b.mp3") != Track.keyOf("a", "c.mp3"))
        assertEquals(tracks.size, tracks.map { it.key }.toSet().size)
    }

    @Test
    fun aBigAlbumDoesNotCrowdOutSingleDiscs() {
        // one 400-track compilation and 100 single 78s
        val big = (1..400).map { Track("compilation", "c$it.mp3", "Song $it", null, 1930, "USA", "oldtime") }
        val singles = (1..100).map { t(100 + it, "oldtime") }
        val rnd = Random(1)
        val fromBig = (1..1000).count {
            (Picker.pick(big + singles, Selection(), { false }, emptySet(), rnd) as Picker.Result.Picked).track.id == "compilation"
        }
        assertTrue("picked the compilation $fromBig times in 1000", fromBig in 100..300)   // ~ 20/(20+100), not 400/500
    }

    @Test
    fun skipsAlbumsHeardRecentlyOrQueued() {
        val albumA = (1..20).map { Track("A", "a$it.mp3", "A $it", null, 1930, "USA", "oldtime") }
        val albumB = (1..20).map { Track("B", "b$it.mp3", "B $it", null, 1930, "USA", "oldtime") }
        val single = t(99, "oldtime")
        val all = albumA + albumB + single
        repeat(50) {
            val r = Picker.pick(all, Selection(), { false }, setOf(albumB[0].key), Random(it), recentItems = listOf("A"))
            assertEquals("item99", (r as Picker.Result.Picked).track.id)
        }
        // when every album was heard recently, it still plays something
        val r = Picker.pick(albumA, Selection(), { false }, emptySet(), recentItems = listOf("A"))
        assertTrue(r is Picker.Result.Picked)
    }
}
