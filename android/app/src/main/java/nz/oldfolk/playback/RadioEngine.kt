@file:androidx.annotation.OptIn(markerClass = [androidx.media3.common.util.UnstableApi::class])

package nz.oldfolk.playback

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import nz.oldfolk.data.FailureLog
import nz.oldfolk.data.Picker
import nz.oldfolk.data.PlayHistory
import nz.oldfolk.data.Playlist
import nz.oldfolk.data.Playlists
import nz.oldfolk.data.Settings
import nz.oldfolk.data.Track
import nz.oldfolk.data.TrackListRepository
import nz.oldfolk.data.isOnline

/**
 * Owns the ExoPlayer and decides what plays next.
 *
 * Radio mode keeps [AHEAD] random, never-played tracks queued after the
 * current one (so notification/lock-screen "next" always works) and
 * prefetches the next [PREFETCH] into the disk cache. A track that fails is
 * logged, skipped and replaced. Playlist mode plays a saved playlist (from
 * local downloads where available) on repeat.
 *
 * All methods must be called on the main thread.
 */
class RadioEngine(
    private val context: Context,
    private val scope: CoroutineScope,
    private val repo: TrackListRepository,
    private val history: PlayHistory,
    private val settings: Settings,
    private val playlists: Playlists,
    private val failures: FailureLog,
    private val mediaCache: MediaCache,
) {
    enum class Mode { RADIO, PLAYLIST }

    data class UiState(
        val current: Track? = null,
        val upNext: List<Track> = emptyList(),
        val isPlaying: Boolean = false,
        val isBuffering: Boolean = false,
        val mode: Mode = Mode.RADIO,
        val playlistName: String? = null,
        val message: String? = null,
        val playedCount: Int = 0,
    )

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(mediaCache.dataSourceFactory))
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(),
            /* handleAudioFocus = */ true,
        )
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_NETWORK)
        .build()

    private val queued = HashMap<String, Track>()   // mediaId -> track
    private var mode = Mode.RADIO
    private var playlistName: String? = null
    private var message: String? = null
    private var consecutiveErrors = 0
    private var waitingForNetwork = false
    private val main = Handler(Looper.getMainLooper())

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (player.isPlaying) markPlayed()
            if (mode == Mode.RADIO) {
                trimHistory()
                topUp()
            }
            prefetchUpcoming()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                consecutiveErrors = 0
                if (message != null && !waitingForNetwork) message = null
                markPlayed()
            }
        }

        override fun onPlayerError(error: PlaybackException) = handleError(error)

        override fun onEvents(player: Player, events: Player.Events) = publish()
    }

    init {
        player.addListener(listener)
        scope.launch(Dispatchers.Main) {
            repo.state.collect { onListChanged(it) }
        }
        scope.launch(Dispatchers.Main) {
            settings.genres.drop(1).collect { onGenresChanged() }
        }
    }

    // ------------------------------------------------------------------ controls

    fun play() {
        if (mode == Mode.RADIO && player.mediaItemCount == 0) topUp()
        if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
            player.prepare()
        }
        player.play()
    }

    fun pause() = player.pause()

    fun togglePlay() = if (player.playWhenReady) pause() else play()

    fun skip() {
        if (player.hasNextMediaItem()) player.seekToNextMediaItem()
        else if (mode == Mode.RADIO) {
            topUp()
            if (player.hasNextMediaItem()) player.seekToNextMediaItem()
        }
        play()
    }

    fun startRadio() {
        mode = Mode.RADIO
        playlistName = null
        player.shuffleModeEnabled = false
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.clearMediaItems()
        queued.clear()
        topUp()
        play()
    }

    fun playPlaylist(p: Playlist, startIndex: Int = 0, shuffle: Boolean = false) {
        if (p.tracks.isEmpty()) return
        mode = Mode.PLAYLIST
        playlistName = p.name
        queued.clear()
        val items = p.tracks.map { t ->
            val local = playlists.localFile(t).takeIf { it.exists() }
            mediaItem(t, local?.let { Uri.fromFile(it) })
        }
        player.setMediaItems(items, startIndex.coerceIn(0, items.size - 1), 0L)
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.shuffleModeEnabled = shuffle
        player.prepare()
        player.play()
    }

    // ------------------------------------------------------------------ queue

    private fun mediaItem(t: Track, localUri: Uri? = null): MediaItem {
        queued[t.key.toString()] = t
        val genreName = repo.state.value.index?.genreName(t.genre) ?: t.genre
        return MediaItem.Builder()
            .setMediaId(t.key.toString())
            .setUri(localUri ?: Uri.parse(t.streamUrl))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(t.title)
                    .setArtist(t.artist ?: "Unknown performer")
                    .setAlbumTitle("${t.yearLabel} · ${t.region}")
                    .setGenre(genreName)
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .build()
            )
            .build()
    }

    private fun aheadCount(): Int =
        if (player.mediaItemCount == 0) 0 else player.mediaItemCount - player.currentMediaItemIndex - 1

    /** Keep [AHEAD] tracks queued after the current one (radio mode). */
    private fun topUp() {
        if (mode != Mode.RADIO) return
        val list = repo.state.value
        val genres = settings.genres.value
        var resets = 0
        var added = false
        val needed = { if (player.mediaItemCount == 0) AHEAD + 1 else AHEAD - aheadCount() }
        while (needed() > 0) {
            val exclude = queued.values.map { it.key }.toSet()
            when (val r = Picker.pick(list.tracks, genres, history::contains, exclude)) {
                is Picker.Result.Picked -> {
                    player.addMediaItem(mediaItem(r.track))
                    added = true
                }
                Picker.Result.Exhausted -> {
                    if (!list.complete || resets > 0) break
                    // Everything in this selection has been heard: start the cycle again.
                    history.forget(Picker.matchingKeys(list.tracks, genres))
                    message = "You've heard every track in this selection. Starting over."
                    resets++
                }
                Picker.Result.NoTracks -> break
            }
        }
        if (added && player.playbackState == Player.STATE_IDLE && !waitingForNetwork) player.prepare()
        publish()
    }

    /** Drop played items, keeping one behind the current track. */
    private fun trimHistory() {
        val idx = player.currentMediaItemIndex
        if (idx > 1) {
            for (i in 0 until idx - 1) queued.remove(player.getMediaItemAt(i).mediaId)
            player.removeMediaItems(0, idx - 1)
        }
    }

    private fun markPlayed() {
        if (mode != Mode.RADIO) return
        val t = currentTrack() ?: return
        history.add(t.key)
    }

    private fun currentTrack(): Track? = player.currentMediaItem?.let { queued[it.mediaId] }

    private fun prefetchUpcoming() {
        val urls = ArrayList<String>()
        // The player buffers the current item itself; prefetch the ones after it.
        val start = player.currentMediaItemIndex + 1
        for (i in start until minOf(player.mediaItemCount, start + PREFETCH)) {
            val uri = player.getMediaItemAt(i).localConfiguration?.uri ?: continue
            if (uri.scheme == "https") urls += uri.toString()
        }
        mediaCache.prefetch(scope, urls)
    }

    private fun onListChanged(s: TrackListRepository.State) {
        if (mode == Mode.RADIO) {
            val wasEmpty = player.mediaItemCount == 0
            topUp()
            if (wasEmpty && player.mediaItemCount > 0) prefetchUpcoming()
        }
        publish()
    }

    private fun onGenresChanged() {
        if (mode != Mode.RADIO) return
        val count = player.mediaItemCount
        if (count == 0) {
            topUp()
            return
        }
        val idx = player.currentMediaItemIndex
        for (i in idx + 1 until count) queued.remove(player.getMediaItemAt(i).mediaId)
        if (idx + 1 < count) player.removeMediaItems(idx + 1, count)
        topUp()
        val genres = settings.genres.value
        val cur = currentTrack()
        if (cur != null && genres.isNotEmpty() && cur.genre !in genres && player.hasNextMediaItem()) {
            val wasPlaying = player.playWhenReady
            player.seekToNextMediaItem()
            if (!wasPlaying) player.pause()
        }
        prefetchUpcoming()
    }

    // ------------------------------------------------------------------ errors

    private fun handleError(error: PlaybackException) {
        val track = currentTrack()
        val networkError = error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
            error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT
        if (networkError && !context.isOnline()) {
            // The phone is offline: don't blame the track. Resume when back online.
            message = "No internet connection. Playback will resume when you're back online."
            waitForNetwork()
            publish()
            return
        }
        if (track != null) {
            failures.record(track, "play", describe(error))
            if (mode == Mode.RADIO) history.add(track.key)
        }
        consecutiveErrors++
        if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
            message = "Several tracks in a row failed to play. Check your connection, then press play."
            consecutiveErrors = 0
            publish()
            return
        }
        message = "Skipped a track that wouldn't play."
        val idx = player.currentMediaItemIndex
        if (mode == Mode.RADIO && idx in 0 until player.mediaItemCount) {
            queued.remove(player.getMediaItemAt(idx).mediaId)
            player.removeMediaItem(idx)
            topUp()
        } else if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
        if (player.mediaItemCount > 0) {
            player.prepare()
            player.play()
        }
        publish()
    }

    private fun waitForNetwork() {
        if (waitingForNetwork) return
        waitingForNetwork = true
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return
        cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                main.postDelayed({
                    waitingForNetwork = false
                    runCatching { cm.unregisterNetworkCallback(this) }
                    message = null
                    if (player.mediaItemCount > 0) {
                        player.prepare()
                        player.play()
                    }
                    publish()
                }, 1500)
            }
        })
    }

    private fun describe(e: PlaybackException): String {
        var c: Throwable? = e
        while (c != null) {
            if (c is HttpDataSource.InvalidResponseCodeException) return "HTTP ${c.responseCode}"
            c = c.cause
        }
        return e.errorCodeName
    }

    // ------------------------------------------------------------------ state

    private fun publish() {
        val upNext = ArrayList<Track>()
        val idx = player.currentMediaItemIndex
        for (i in idx + 1 until minOf(player.mediaItemCount, idx + 4)) {
            queued[player.getMediaItemAt(i).mediaId]?.let { upNext += it }
        }
        val list = repo.state.value
        val msg = message ?: when {
            list.error != null && list.tracks.isEmpty() -> list.error
            list.tracks.isEmpty() && list.loading -> "Downloading the track list…"
            list.tracks.isNotEmpty() && player.mediaItemCount == 0 && mode == Mode.RADIO ->
                if (list.loading) "Loading more tracks…" else "No tracks match the selected genres."
            else -> null
        }
        _state.value = UiState(
            current = currentTrack(),
            upNext = upNext,
            isPlaying = player.isPlaying,
            isBuffering = player.playbackState == Player.STATE_BUFFERING && player.playWhenReady,
            mode = mode,
            playlistName = playlistName,
            message = msg,
            playedCount = history.size(),
        )
    }

    companion object {
        const val AHEAD = 4
        const val PREFETCH = 2
        const val MAX_CONSECUTIVE_ERRORS = 6
    }
}
