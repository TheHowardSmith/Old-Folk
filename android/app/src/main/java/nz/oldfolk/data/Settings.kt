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

    var askedNotificationPermission: Boolean
        get() = prefs.getBoolean("asked_notifications", false)
        set(v) = prefs.edit().putBoolean("asked_notifications", v).apply()
}
