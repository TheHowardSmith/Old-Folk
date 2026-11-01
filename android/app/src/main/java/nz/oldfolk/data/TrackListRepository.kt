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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import nz.oldfolk.BuildConfig
import java.io.File
import java.security.MessageDigest
import kotlin.random.Random

/**
 * Downloads and caches the track list.
 *
 * Files live in filesDir/tracklist/: `index.json` plus one file per shard named
 * by its SHA-256, so a new list version only downloads shards that changed.
 * The index is re-checked on every app start; shards are fetched only when the
 * index version changes. On first launch one random shard (from the selected
 * genres) is fetched first so playback can start quickly; the rest follow in
 * the background so the whole list becomes available for "never repeat".
 */
class TrackListRepository(
    private val context: Context,
    private val scope: CoroutineScope,
    private val baseUrl: String = BuildConfig.TRACKLIST_BASE,
) {
    data class State(
        val index: TrackIndex? = null,
        val tracks: List<Track> = emptyList(),
        val shardsLoaded: Int = 0,
        val shardsTotal: Int = 0,
        val loading: Boolean = false,
        val error: String? = null,
    ) {
        val complete: Boolean get() = index != null && shardsLoaded >= shardsTotal
    }

    private val dir = File(context.filesDir, "tracklist").apply { mkdirs() }
    private val indexFile = File(dir, "index.json")
    private val mutex = Mutex()
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()
    private var job: Job? = null

    /** Load the cache, then check the network for a newer list. */
    fun start(preferredGenres: () -> Set<String>) {
        if (job?.isActive == true) return
        job = scope.launch(Dispatchers.IO) {
            mutex.withLock {
                loadCached()
                refresh(preferredGenres())
            }
        }
    }

    fun retry(preferredGenres: () -> Set<String>) {
        job = null
        start(preferredGenres)
    }

    private fun shardFile(s: ShardInfo) = File(dir, s.sha256 + ".json")

    private fun loadCached() {
        val index = runCatching { TrackIndex.parse(indexFile.readText()) }.getOrNull() ?: return
        publish(index)
    }

    /** Rebuild the in-memory track list from whichever shards are on disk. */
    private fun publish(index: TrackIndex, error: String? = null, loading: Boolean = false) {
        val tracks = ArrayList<Track>()
        var loaded = 0
        for (s in index.shards) {
            val f = shardFile(s)
            if (!f.exists()) continue
            runCatching { Track.parseShard(f.readText()) }
                .onSuccess { tracks += it; loaded++ }
                .onFailure { f.delete() }
        }
        _state.value = State(index, tracks, loaded, index.shards.size, loading, error)
    }

    private fun refresh(preferred: Set<String>) {
        _state.update { it.copy(loading = true, error = null) }
        val remote = try {
            val text = Http.getText(baseUrl + "index.json")
            TrackIndex.parse(text).also {
                if (it.version != _state.value.index?.version) indexFile.writeText(text)
            }
        } catch (e: Exception) {
            Log.w(TAG, "index fetch failed", e)
            val cached = _state.value.index
            if (cached == null) {
                _state.update { it.copy(loading = false, error = "Couldn't download the track list. Check your connection.") }
                return
            }
            cached
        }
        cleanUp(remote)
        publish(remote, loading = true)

        // Missing shards: one random shard of the preferred genres first, then the rest.
        val missing = remote.shards.filter { !shardFile(it).exists() }.toMutableList()
        if (missing.isEmpty()) {
            _state.update { it.copy(loading = false) }
            return
        }
        val haveForPreferred = _state.value.tracks.any { preferred.isEmpty() || it.genre in preferred }
        if (!haveForPreferred) {
            val candidates = missing.filter { preferred.isEmpty() || it.genre in preferred }.ifEmpty { missing }
            val first = weightedPick(candidates)
            missing.remove(first)
            missing.add(0, first)
        } else {
            missing.sortBy { if (preferred.isEmpty() || it.genre in preferred) 0 else 1 }
        }
        var failed = false
        for (s in missing) {
            try {
                val text = Http.getText(baseUrl + s.file, timeoutMs = 60_000)
                if (sha256(text.toByteArray(Charsets.UTF_8)) != s.sha256) {
                    Log.w(TAG, "checksum mismatch for ${s.file}")
                    failed = true
                    continue
                }
                shardFile(s).writeText(text)
                publish(remote, loading = true)
            } catch (e: Exception) {
                Log.w(TAG, "shard ${s.file} failed", e)
                failed = true
            }
        }
        publish(remote, loading = false,
            error = if (failed && _state.value.tracks.isEmpty()) "Couldn't download the track list. Check your connection." else null)
    }

    /** Delete shard files that the current index no longer lists. */
    private fun cleanUp(index: TrackIndex) {
        val keep = index.shards.map { it.sha256 + ".json" }.toSet() + "index.json"
        dir.listFiles()?.filter { it.name !in keep }?.forEach { it.delete() }
    }

    private fun weightedPick(shards: List<ShardInfo>): ShardInfo {
        val total = shards.sumOf { it.count.coerceAtLeast(1) }
        var r = Random.nextInt(total)
        for (s in shards) {
            r -= s.count.coerceAtLeast(1)
            if (r < 0) return s
        }
        return shards.last()
    }

    companion object {
        private const val TAG = "TrackList"
        fun sha256(bytes: ByteArray): String =
            MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
