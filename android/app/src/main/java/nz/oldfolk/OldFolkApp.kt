package nz.oldfolk

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import nz.oldfolk.data.FailureLog
import nz.oldfolk.data.PlayHistory
import nz.oldfolk.data.Playlists
import nz.oldfolk.data.Settings
import nz.oldfolk.data.StreamHistory
import nz.oldfolk.data.TrackListRepository
import nz.oldfolk.playback.Levels
import nz.oldfolk.playback.MediaCache
import nz.oldfolk.playback.RadioEngine
import java.io.File

class OldFolkApp : Application() {
    lateinit var graph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        graph = AppGraph(this)
        graph.trackList.start { graph.settings.genres.value }
    }
}

/** App-wide singletons (manual dependency injection). Created on the main thread. */
class AppGraph(app: Application) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val settings = Settings(app)
    val failures = FailureLog(app)
    val history = PlayHistory(File(app.filesDir, "played.bin"))
    val trackList = TrackListRepository(app, scope)
    val levels = Levels(File(app.filesDir, "levels.json"))
    val playlists = Playlists(app)
    val mediaCache = MediaCache(app)
    val streamHistory = StreamHistory(File(app.filesDir, "stream_history.jsonl"))
    val engine = RadioEngine(app, scope, trackList, history, settings, playlists, failures, mediaCache, streamHistory, levels)
}
