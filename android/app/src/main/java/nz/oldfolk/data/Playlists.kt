package nz.oldfolk.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class Playlist(val id: String, val name: String, val tracks: List<Track>)

sealed interface DownloadStatus {
    data object Queued : DownloadStatus
    data class Downloading(val progress: Float) : DownloadStatus
    data object Done : DownloadStatus
    data class Failed(val reason: String) : DownloadStatus
}

/**
 * User playlists. Every track in a playlist is downloaded into app storage so
 * it plays offline. No storage cap. Downloads run one at a time; anything not
 * finished is resumed on the next app start.
 */
class Playlists(
    private val context: Context,
    private val scope: CoroutineScope,
    private val failures: FailureLog,
) {
    private val file = File(context.filesDir, "playlists.json")
    private val downloadDir = File(context.filesDir, "downloads").apply { mkdirs() }

    private val _playlists = MutableStateFlow(load())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _downloads = MutableStateFlow<Map<Long, DownloadStatus>>(emptyMap())
    val downloads: StateFlow<Map<Long, DownloadStatus>> = _downloads.asStateFlow()

    private var worker: Job? = null

    init {
        refreshStatuses()
        startDownloads()
    }

    fun localFile(track: Track): File = File(downloadDir, java.lang.Long.toHexString(track.key) + ".mp3")

    fun isDownloaded(track: Track): Boolean = localFile(track).exists()

    fun get(id: String): Playlist? = _playlists.value.firstOrNull { it.id == id }

    fun create(name: String): Playlist {
        val p = Playlist(UUID.randomUUID().toString(), name.trim().ifBlank { "Playlist" }, emptyList())
        save(_playlists.value + p)
        return p
    }

    fun rename(id: String, name: String) = update(id) { it.copy(name = name.trim().ifBlank { it.name }) }

    fun delete(id: String) {
        save(_playlists.value.filterNot { it.id == id })
        deleteOrphanFiles()
    }

    fun add(id: String, track: Track) {
        update(id) { p -> if (p.tracks.any { it.key == track.key }) p else p.copy(tracks = p.tracks + track) }
        refreshStatuses()
        startDownloads()
    }

    fun remove(id: String, track: Track) {
        update(id) { p -> p.copy(tracks = p.tracks.filterNot { it.key == track.key }) }
        deleteOrphanFiles()
        refreshStatuses()
    }

    fun retryFailed() {
        _downloads.update { m -> m.mapValues { (_, v) -> if (v is DownloadStatus.Failed) DownloadStatus.Queued else v } }
        startDownloads()
    }

    fun storageBytes(): Long = downloadDir.listFiles()?.sumOf { it.length() } ?: 0L

    private fun update(id: String, f: (Playlist) -> Playlist) =
        save(_playlists.value.map { if (it.id == id) f(it) else it })

    private fun allTracks(): List<Track> =
        _playlists.value.flatMap { it.tracks }.distinctBy { it.key }

    private fun refreshStatuses() {
        val current = _downloads.value
        _downloads.value = allTracks().associate { t ->
            t.key to when {
                isDownloaded(t) -> DownloadStatus.Done
                else -> current[t.key]?.takeIf { it !is DownloadStatus.Done } ?: DownloadStatus.Queued
            }
        }
    }

    private fun startDownloads() {
        if (worker?.isActive == true) return
        worker = scope.launch(Dispatchers.IO) {
            while (true) {
                val next = allTracks().firstOrNull { _downloads.value[it.key] == DownloadStatus.Queued } ?: break
                setStatus(next.key, DownloadStatus.Downloading(0f))
                try {
                    Http.download(next.streamUrl, localFile(next)) { p -> setStatus(next.key, DownloadStatus.Downloading(p)) }
                    setStatus(next.key, DownloadStatus.Done)
                } catch (e: Exception) {
                    Log.w("Playlists", "download failed: ${next.id}/${next.file}", e)
                    val reason = describe(e)
                    failures.record(next, "download", reason)
                    setStatus(next.key, DownloadStatus.Failed(reason))
                }
            }
        }
    }

    private fun setStatus(key: Long, s: DownloadStatus) = _downloads.update { it + (key to s) }

    private fun deleteOrphanFiles() {
        val keep = allTracks().map { localFile(it).name }.toSet()
        downloadDir.listFiles()?.filter { it.name !in keep && !it.name.endsWith(".part") }?.forEach { it.delete() }
    }

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
