package nz.oldfolk.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class Playlist(val id: String, val name: String, val tracks: List<Track>)

/**
 * User playlists: lists of tracks that stream like the radio does (through the
 * encrypted playback cache). Recordings are never saved to the phone as files,
 * so they can't be copied off it and passed around.
 */
class Playlists(context: Context) {
    private val file = File(context.filesDir, "playlists.json")

    init {
        // Earlier versions downloaded playlist tracks as MP3 files: remove them.
        File(context.filesDir, "downloads").deleteRecursively()
    }

    private val _playlists = MutableStateFlow(load())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    fun get(id: String): Playlist? = _playlists.value.firstOrNull { it.id == id }

    fun create(name: String): Playlist {
        val p = Playlist(UUID.randomUUID().toString(), name.trim().ifBlank { "Playlist" }, emptyList())
        save(_playlists.value + p)
        return p
    }

    fun rename(id: String, name: String) = update(id) { it.copy(name = name.trim().ifBlank { it.name }) }

    fun delete(id: String) = save(_playlists.value.filterNot { it.id == id })

    fun add(id: String, track: Track) =
        update(id) { p -> if (p.tracks.any { it.key == track.key }) p else p.copy(tracks = p.tracks + track) }

    fun remove(id: String, track: Track) =
        update(id) { p -> p.copy(tracks = p.tracks.filterNot { it.key == track.key }) }

    private fun update(id: String, f: (Playlist) -> Playlist) =
        save(_playlists.value.map { if (it.id == id) f(it) else it })

    private fun load(): List<Playlist> = runCatching {
        val arr = JSONArray(file.readText())
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            val t = o.getJSONArray("tracks")
            Playlist(o.getString("id"), o.getString("name"), List(t.length()) { Track.fromJson(t.getJSONObject(it)) })
        }
    }.getOrDefault(emptyList())

    private fun save(list: List<Playlist>) {
        val arr = JSONArray()
        list.forEach { p ->
            arr.put(JSONObject().put("id", p.id).put("name", p.name)
                .put("tracks", JSONArray().apply { p.tracks.forEach { put(it.toJson()) } }))
        }
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(arr.toString())
        tmp.renameTo(file)
        _playlists.value = list
    }

    companion object {
        fun describe(e: Throwable): String = when (e) {
            is HttpStatusException -> "HTTP ${e.code}"
            is java.net.SocketTimeoutException -> "network timeout"
            is java.net.UnknownHostException -> "no connection (DNS)"
            is java.net.ConnectException -> "connection failed"
            is javax.net.ssl.SSLException -> "TLS error"
            else -> e.javaClass.simpleName + (e.message?.let { ": $it" } ?: "")
        }
    }
}
