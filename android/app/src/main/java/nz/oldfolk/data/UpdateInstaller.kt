package nz.oldfolk.data

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Downloads the latest release APK inside the app and hands it to Android's package
 * installer, which asks the user to confirm (and, the first time, to allow installs
 * from Old Folk Air). Browsers were unreliable here: some hung at 100% on the APK.
 */
object UpdateInstaller {
    private val _problem = MutableStateFlow<String?>(null)
    /** Set when the installer reports a failure (null while all is well). */
    val problem: StateFlow<String?> = _problem.asStateFlow()

    /** Blocking: call from Dispatchers.IO. [progress] gets 0..1 while downloading. */
    fun downloadAndInstall(context: Context, progress: (Float) -> Unit) {
        _problem.value = null
        val dir = File(context.cacheDir, "update").apply { mkdirs() }
        dir.listFiles()?.forEach { it.delete() }
        val apk = File(dir, "old-folk-air.apk")
        Http.download(Updates.apkUrl, apk, progress)

        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        params.setAppPackageName(context.packageName)
        params.setSize(apk.length())
        val id = installer.createSession(params)
        installer.openSession(id).use { session ->
            apk.inputStream().use { input ->
                session.openWrite("base.apk", 0, apk.length()).use { out ->
                    input.copyTo(out)
                    session.fsync(out)
                }
            }
            val intent = Intent(context, UpdateReceiver::class.java).setPackage(context.packageName)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0)
            session.commit(PendingIntent.getBroadcast(context, id, intent, flags).intentSender)
        }
        apk.delete()
    }

    internal fun report(problem: String?) { _problem.value = problem }
}

/** Receives the package installer's answer: shows its confirmation screen, or reports a failure. */
class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                val confirm: Intent? = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION") intent.getParcelableExtra(Intent.EXTRA_INTENT)
                }
                if (confirm != null) {
                    confirm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try { context.startActivity(confirm) } catch (e: Exception) { UpdateInstaller.report(e.message ?: "couldn't open the installer") }
                }
            }
            PackageInstaller.STATUS_SUCCESS -> UpdateInstaller.report(null)   // Android restarts the app
            PackageInstaller.STATUS_FAILURE_ABORTED -> UpdateInstaller.report("installation cancelled")
            else -> UpdateInstaller.report(
                intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE) ?: "installer error $status")
        }
    }
}
