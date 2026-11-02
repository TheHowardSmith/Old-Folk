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
import nz.oldfolk.data.StreamHistory
import nz.oldfolk.data.Track
import nz.oldfolk.data.TrackListRepository
import nz.oldfolk.data.isOnline

/**
 * Owns the ExoPlayer and decides what plays next.
 *
 * Radio mode queues random, never-played tracks after the current one:
 * [START_AHEAD] before playback starts, then [STREAM_AHEAD] once streaming,
 * and prefetches all of them into the disk cache in order (so skipping
 * through tracks quickly stays smooth). The last [KEEP_BEHIND] played tracks
 * stay in the queue (and cache) so "previous" works. A track that fails is
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
    private val streamHistory: StreamHistory,
    private val levels: Levels,
) {
    enum class Mode { RADIO, PLAYLIST }

    data class UiState(
        val current: Track? = null,
        val upNext: List<Track> = emptyList(),
        val queue: List<Track> = emptyList(),   // whole player queue (behind + current + ahead)
        val currentIndex: Int = -1,
        val isPlaying: Boolean = false,
        val isBuffering: Boolean = false,
        val mode: Mode = Mode.RADIO,
        val playlistName: String? = null,
        val message: String? = null,
        val playedCount: Int = 0,
    )

    /** Audio chain: one-sided transfers go to both speakers, then the spike limiter, on every track. */
    private val renderers = object : androidx.media3.exoplayer.DefaultRenderersFactory(context) {
        override fun buildAudioSink(
            context: Context, enableFloatOutput: Boolean, enableAudioTrackPlaybackParams: Boolean,
        ): androidx.media3.exoplayer.audio.AudioSink =
            androidx.media3.exoplayer.audio.DefaultAudioSink.Builder(context)
                .setEnableFloatOutput(enableFloatOutput)
                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                .setAudioProcessors(arrayOf(ChannelFixer(), SpikeLimiter()))
                .build()
    }

    val player: ExoPlayer = ExoPlayer.Builder(context, renderers)
        .setMediaSourceFactory(DefaultMediaSourceFactory(mediaCache.dataSourceFactory))
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(),
            /* handleAudioFocus = */ true,
        )
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_NETWORK)
        .build()

    private val queued = HashMap<String, Track>()   // mediaId -> track (metadata for queue items)
    private var streaming = false                   // has playback started this session?
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
            applyLevel()
            skipIfNoisy()
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
                if (!streaming) {
                    // First playback: grow the queue from START_AHEAD to STREAM_AHEAD.
                    streaming = true
                    topUp()
                    prefetchUpcoming()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) = handleError(error)

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            releaseEnhancer()
            applyLevel()
        }

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
        scope.launch(Dispatchers.Main) {
            // A measurement arrived: apply it if the current track has only just started.
            levels.version.drop(1).collect {
                if (player.currentPosition < 3000) applyLevel()
                dropNoisyAhead()
            }
        }
    }

    // ------------------------------------------------------------------ levelling

    private var enhancer: android.media.audiofx.LoudnessEnhancer? = null

    /**
     * Bring the current track to the common level: loud tracks are turned down
     * with the player volume, quiet ones boosted with LoudnessEnhancer (which
     * limits peaks). Unmeasured tracks play unchanged until measured.
     */
    private fun applyLevel() {
        val t = currentTrack()
        val gain = t?.let { levels.gainDb(it.key) }
        if (t != null && gain == null) measureInBackground(t)
        val g = gain ?: 0f
        player.volume = if (g < 0) Levels.dbToLinear(g) else 1f
        val boostMb = if (g > 0) (g * 100).toInt() else 0
        try {
            if (boostMb > 0) {
                val e = enhancer ?: android.media.audiofx.LoudnessEnhancer(player.audioSessionId).also { enhancer = it }
                e.setTargetGain(boostMb)
                e.enabled = true
            } else {
                enhancer?.enabled = false
            }
        } catch (e: Exception) {
            // Some devices don't offer the effect; levelling then only turns loud tracks down.
            releaseEnhancer()
        }
    }

    private fun skipNoisyOn() = mode == Mode.RADIO && settings.skipNoisy.value

    /** Remove queued radio tracks that the noise check has flagged (they count as heard). */
    private fun dropNoisyAhead() {
        if (!skipNoisyOn()) return
        var removed = false
        for (i in player.mediaItemCount - 1 downTo player.currentMediaItemIndex + 1) {
            val key = player.getMediaItemAt(i).mediaId.toLongOrNull() ?: continue
            if (levels.isNoisy(key) == true) {
                history.add(key)
                player.removeMediaItem(i)
                removed = true
            }
        }
        if (removed) {
            topUp()
            prefetchUpcoming()
        }
    }

    /** If the track that just started is known to be noisy, move on. */
    private fun skipIfNoisy() {
        if (!skipNoisyOn()) return
        val t = currentTrack() ?: return
        if (levels.isNoisy(t.key) == true && player.hasNextMediaItem()) {
            main.post {
                if (currentTrack()?.key == t.key) {
                    history.add(t.key)
                    player.seekToNextMediaItem()
                }
            }
        }
    }

    private fun releaseEnhancer() {
        runCatching { enhancer?.release() }
        enhancer = null
    }

    /** Measure a track that is already on the phone (cached or downloaded) but has no reading yet. */
    private fun measureInBackground(t: Track) {
        val local = playlists.localFile(t)
        scope.launch(Dispatchers.IO) {
            when {
                local.exists() -> levels.analyzeFile(t.key, local)
                mediaCache.isFullyCached(t.streamUrl) -> levels.analyzeCached(t.key, mediaCache.cache, t.streamUrl)
            }
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

    /** Previous track in the queue, or restart the current one if there is none. */
    fun previous() {
        if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem() else player.seekTo(0)
        play()
    }

    /** Play the queue item at [index] next (now), keeping the rest of the queue in order. */
    fun playQueued(index: Int) {
        val cur = player.currentMediaItemIndex
        if (index !in 0 until player.mediaItemCount) return
        if (index == cur) {
            play(); return
        }
        val target = if (index < cur) cur else cur + 1
        player.moveMediaItem(index, target)
        player.seekToNextMediaItem()
        play()
    }

    /** Play a track from the listening history: from the queue if it's still there, else stream it now. */
    fun playTrack(t: Track) {
        val id = t.key.toString()
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == id) {
                playQueued(i); return
            }
        }
        val local = playlists.localFile(t).takeIf { it.exists() }
        val item = mediaItem(t, local?.let { Uri.fromFile(it) })
        if (player.mediaItemCount == 0) {
            player.setMediaItem(item)
        } else {
            player.addMediaItem(player.currentMediaItemIndex + 1, item)
            player.seekToNextMediaItem()
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
                    .setArtworkUri(t.artUrl?.let { Uri.parse(it) })
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .build()
            )
            .build()
    }

    private fun aheadCount(): Int =
        if (player.mediaItemCount == 0) 0 else player.mediaItemCount - player.currentMediaItemIndex - 1

    private fun targetAhead() = if (streaming) STREAM_AHEAD else START_AHEAD

    private fun queuedKeys(): Set<Long> =
        (0 until player.mediaItemCount).mapNotNull { player.getMediaItemAt(it).mediaId.toLongOrNull() }.toSet()

    /** Keep [targetAhead] tracks queued after the current one (radio mode). */
    private fun topUp() {
        if (mode != Mode.RADIO) return
        val list = repo.state.value
        val genres = settings.genres.value
        var resets = 0
        var added = false
        val needed = { if (player.mediaItemCount == 0) targetAhead() + 1 else targetAhead() - aheadCount() }
        while (needed() > 0) {
            val exclude = queuedKeys()
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

    /** Drop old items, keeping [KEEP_BEHIND] played tracks behind the current one. */
    private fun trimHistory() {
        val idx = player.currentMediaItemIndex
        if (idx > KEEP_BEHIND) player.removeMediaItems(0, idx - KEEP_BEHIND)
    }

    private fun markPlayed() {
        val t = currentTrack() ?: return
        streamHistory.record(t)
        if (mode == Mode.RADIO) history.add(t.key)
    }

    private fun currentTrack(): Track? = player.currentMediaItem?.let { queued[it.mediaId] }

    private fun prefetchUpcoming() {
        val urls = ArrayList<String>()
        // The player buffers the current item itself; prefetch the ones after it.
        // In queue order, so the next few are ready first and the rest follow.
        // Smaller archives than archive.org only get their next EXT_AHEAD tracks
        // fetched early; the rest are fetched when they come up, so skipping
        // through the queue doesn't download files nobody hears from them.
        val start = player.currentMediaItemIndex + 1
        var ext = 0
        for (i in start until minOf(player.mediaItemCount, start + STREAM_AHEAD)) {
            val uri = player.getMediaItemAt(i).localConfiguration?.uri ?: continue
            if (uri.scheme != "https") continue
            if (uri.host?.endsWith("archive.org") != true && ++ext > EXT_AHEAD) continue
            urls += uri.toString()
        }
        val keys = HashMap<String, Long>()
        for (i in start until minOf(player.mediaItemCount, start + STREAM_AHEAD)) {
            val item = player.getMediaItemAt(i)
            val uri = item.localConfiguration?.uri ?: continue
            item.mediaId.toLongOrNull()?.let { keys[uri.toString()] = it }
        }
        mediaCache.prefetch(scope, urls) { url ->
            keys[url]?.let { key -> levels.analyzeCached(key, mediaCache.cache, url) }
        }
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
        val queue = ArrayList<Track>()
        var idx = player.currentMediaItemIndex
        for (i in 0 until player.mediaItemCount) {
            val t = queued[player.getMediaItemAt(i).mediaId]
            if (t != null) queue += t else if (i < idx) idx--
        }
        if (player.mediaItemCount == 0) idx = -1
        val upNext = if (idx >= 0) queue.drop(idx + 1).take(5) else emptyList()
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
            queue = queue,
            currentIndex = idx,
            isPlaying = player.isPlaying,
            isBuffering = player.playbackState == Player.STATE_BUFFERING && player.playWhenReady,
            mode = mode,
            playlistName = playlistName,
            message = msg,
            playedCount = history.size(),
        )
    }

    companion object {
        const val START_AHEAD = 5
        const val STREAM_AHEAD = 20
        /** At most this many upcoming tracks from archives other than archive.org are prefetched. */
        const val EXT_AHEAD = 2
        const val KEEP_BEHIND = 20
        const val MAX_CONSECUTIVE_ERRORS = 6
    }
}
