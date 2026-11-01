package nz.oldfolk.data

import kotlin.random.Random

/** Chooses the next radio track: random, matching the genre filter, not yet played. */
object Picker {
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
    ): Result {
        var matching = 0
        val candidates = ArrayList<Track>()
        for (t in tracks) {
            if (genres.isNotEmpty() && t.genre !in genres) continue
            matching++
            if (t.key in exclude || isPlayed(t.key)) continue
            candidates += t
        }
        return when {
            candidates.isNotEmpty() -> Result.Picked(candidates[random.nextInt(candidates.size)])
            matching == 0 -> Result.NoTracks
            else -> Result.Exhausted
        }
    }

    fun matchingKeys(tracks: List<Track>, genres: Set<String>): List<Long> =
        tracks.filter { genres.isEmpty() || it.genre in genres }.map { it.key }
}
