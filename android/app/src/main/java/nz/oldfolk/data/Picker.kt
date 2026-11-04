package nz.oldfolk.data

import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Chooses the next radio track: random, matching the genre filter, not yet played.
 *
 * It picks an album (archive item) first and then a song on it, so a 300-track
 * compilation doesn't crowd out single 78s: an album's chance grows only with
 * the square root of its unplayed tracks. Albums heard recently or already
 * queued are skipped while there are others to choose from.
 */
object Picker {
    /** How many recently played tracks' albums to avoid. */
    const val RECENT_ALBUMS = 30

    sealed interface Result {
        data class Picked(val track: Track) : Result
        /** Every matching track has been played. */
        data object Exhausted : Result
        /** Nothing matches the filter (or the list is empty). */
        data object NoTracks : Result
    }

    fun pick(
        tracks: List<Track>,
        genres: Set<String>,               // empty = all genres
        isPlayed: (Long) -> Boolean,
        exclude: Set<Long>,                // already queued
        random: Random = Random.Default,
        recentItems: Collection<String> = emptyList(),   // albums of the last tracks played
    ): Result {
        var matching = 0
        val avoid = HashSet<String>(recentItems)
        val byItem = LinkedHashMap<String, MutableList<Track>>()
        for (t in tracks) {
            if (genres.isNotEmpty() && t.genre !in genres) continue
            matching++
            if (t.key in exclude) { avoid += t.id; continue }
            if (isPlayed(t.key)) continue
            byItem.getOrPut(t.id) { ArrayList() } += t
        }
        if (byItem.isEmpty()) return if (matching == 0) Result.NoTracks else Result.Exhausted
        val fresh = byItem.filterKeys { it !in avoid }.values.ifEmpty { byItem.values }.toList()
        val weights = fresh.map { sqrt(it.size.toDouble()) }
        var r = random.nextDouble() * weights.sum()
        var album = fresh.last()
        for (i in fresh.indices) { r -= weights[i]; if (r <= 0) { album = fresh[i]; break } }
        return Result.Picked(album[random.nextInt(album.size)])
    }

    fun matchingKeys(tracks: List<Track>, genres: Set<String>): List<Long> =
        tracks.filter { genres.isEmpty() || it.genre in genres }.map { it.key }
}
