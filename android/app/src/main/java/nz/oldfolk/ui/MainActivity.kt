package nz.oldfolk.ui

import android.Manifest
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import nz.oldfolk.OldFolkApp
import nz.oldfolk.playback.PlaybackService

class MainActivity : ComponentActivity() {
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val graph = (application as OldFolkApp).graph
        setContent {
            OldFolkTheme {
                OldFolkAppUi(graph, onBeforePlay = ::askNotificationPermissionOnce)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Binding a controller creates the playback service, which owns the
        // media notification and lock-screen controls.
        val token = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, token).buildAsync()
    }

    override fun onStop() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        super.onStop()
    }

    private fun askNotificationPermissionOnce() {
        val settings = (application as OldFolkApp).graph.settings
        if (Build.VERSION.SDK_INT >= 33 && !settings.askedNotificationPermission) {
            settings.askedNotificationPermission = true
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
