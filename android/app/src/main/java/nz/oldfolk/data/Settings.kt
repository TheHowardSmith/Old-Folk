package nz.oldfolk.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Small user preferences, kept in SharedPreferences. */
class Settings(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _genres = MutableStateFlow(prefs.getStringSet("genres", emptySet())!!.toSet())
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

    var askedNotificationPermission: Boolean
        get() = prefs.getBoolean("asked_notifications", false)
        set(v) = prefs.edit().putBoolean("asked_notifications", v).apply()
}
