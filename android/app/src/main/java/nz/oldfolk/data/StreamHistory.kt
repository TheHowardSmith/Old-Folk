package nz.oldfolk.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.File

/**
 * Everything this phone has streamed, oldest first, kept forever (one JSON
 * line per play, ~200 bytes). Only the most recent tracks are kept in the
 * audio cache; older entries are metadata only and stream again when played.
 */
class StreamHistory(private val file: File) {
    data class Entry(val track: Track, val time: Long)

    private val _entries = MutableStateFlow(load())
    val entries: StateFlow<List<Entry>> = _entries.asStateFlow()

    private fun load(): List<Entry> {
        if (!file.exists()) return emptyList()
        return file.readLines().mapNotNull { line ->
            runCatching {
                val o = JSONObject(line)
                Entry(Track.fromJson(o), o.optLong("ts"))
            }.getOrNull()
        }
    }

    @Synchronized
    fun record(track: Track) {
        val list = _entries.value
        if (list.lastOrNull()?.track?.key == track.key) return
        val e = Entry(track, System.currentTimeMillis())
        file.appendText(track.toJson().put("ts", e.time).toString() + "\n")
        _entries.value = list + e
    }

    @Synchronized
    fun clear() {
        file.delete()
        _entries.value = emptyList()
    }
}
