package nz.oldfolk.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Small user preferences, kept in SharedPreferences. */
class Settings(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _genres = MutableStateFlow(splitEuropean(prefs.getStringSet("genres", emptySet())!!.toSet()))

    /** Genres that were split (Nov 2026): a saved choice of the old one keeps the new ones. */
    private fun splitEuropean(keys: Set<String>): Set<String> {
        var out = keys
        val edit = prefs.edit()
        for ((flag, from, to) in listOf(
            Triple("euro_split", "european", setOf("nordic", "eastern_europe", "mediterranean")),
            Triple("asia_split", "asia", setOf("mideast")),
            Triple("country_split", "oldtime", setOf("country")),
            Triple("country_split2", "american", setOf("country")),
            Triple("classical_split", "southasia", setOf("classical")),
            Triple("classical_split2", "asia", setOf("classical")),
            Triple("classical_split3", "mideast", setOf("classical")),
        )) {
            if (!prefs.getBoolean(flag, false) && from in out) out = out + to
            edit.putBoolean(flag, true)
        }
        edit.putStringSet("genres", out).apply()
        return out
    }

    private val _regions = MutableStateFlow(prefs.getStringSet("regions", emptySet())!!.toSet())

    init {
        // Genres became styles and places became the map (Nov 2026): a saved choice of regional
        // genres becomes those regions on the map. A mix of styles and regions can't be kept
        // exactly, so it starts again from everything.
        if (!prefs.getBoolean("styles_split", false)) {
            val old = _genres.value
            val regional = old.filter { it in OLD_REGIONAL }
            val styles = old - OLD_REGIONAL.keys
            var genres = old
            var regions = _regions.value
            if (regional.isNotEmpty() && styles.isNotEmpty()) { genres = emptySet(); regions = emptySet() }
            else if (regional.isNotEmpty()) { genres = emptySet(); regions = regional.flatMap { OLD_REGIONAL.getValue(it) }.toSet() }
            prefs.edit().putStringSet("genres", genres).putStringSet("regions", regions).putBoolean("styles_split", true).apply()
            _genres.value = genres
            _regions.value = regions
        }
    }

    /** Selected style (genre) keys; empty means all styles. */
    val genres: StateFlow<Set<String>> = _genres.asStateFlow()

    fun setGenres(keys: Set<String>) {
        prefs.edit().putStringSet("genres", keys).apply()
        _genres.value = keys
    }

    /** Selected map regions (keys of the index's "regions"); empty means everywhere. */
    val regions: StateFlow<Set<String>> = _regions.asStateFlow()

    fun setRegions(keys: Set<String>) {
        prefs.edit().putStringSet("regions", keys).apply()
        _regions.value = keys
    }

    private val _skipNoisy = MutableStateFlow(prefs.getBoolean("skip_noisy", true))
    /** Radio skips tracks the noise check flags as having heavy surface noise. */
    val skipNoisy: StateFlow<Boolean> = _skipNoisy.asStateFlow()

    fun setSkipNoisy(on: Boolean) {
        prefs.edit().putBoolean("skip_noisy", on).apply()
        _skipNoisy.value = on
    }

    private val _theme = MutableStateFlow(prefs.getString("theme", "system") ?: "system")
    /** "system" (follow the phone), "light" or "dark". */
    val theme: StateFlow<String> = _theme.asStateFlow()

    fun setTheme(mode: String) {
        prefs.edit().putString("theme", mode).apply()
        _theme.value = mode
    }

    var askedNotificationPermission: Boolean
        get() = prefs.getBoolean("asked_notifications", false)
        set(v) = prefs.edit().putBoolean("asked_notifications", v).apply()

    private companion object {
        /** The old regional genres and the map regions they became. */
        val OLD_REGIONAL = mapOf(
            "american" to listOf("north_america"), "british" to listOf("british_isles"),
            "european" to listOf("western_europe"), "nordic" to listOf("nordic"),
            "eastern_europe" to listOf("eastern_europe"), "mediterranean" to listOf("southern_europe"),
            "latin" to listOf("latin_america"), "african" to listOf("africa"), "mideast" to listOf("middle_east"),
            "asia" to listOf("east_asia", "southeast_asia", "central_asia"), "southasia" to listOf("south_asia"),
            "pacific" to listOf("pacific"), "world" to emptyList(),
        )
    }
}
