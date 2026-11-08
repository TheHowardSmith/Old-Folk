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
        )) {
            if (!prefs.getBoolean(flag, false) && from in out) out = out + to
            edit.putBoolean(flag, true)
        }
        edit.putStringSet("genres", out).apply()
        return out
    }
    /** Selected genre keys; empty means all genres. */
    val genres: StateFlow<Set<String>> = _genres.asStateFlow()

    fun setGenres(keys: Set<String>) {
        prefs.edit().putStringSet("genres", keys).apply()
        _genres.value = keys
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
}
