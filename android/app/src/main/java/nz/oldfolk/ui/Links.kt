package nz.oldfolk.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import nz.oldfolk.BuildConfig
import nz.oldfolk.data.Track

object Links {
    val repoUrl = "https://github.com/${BuildConfig.REPO}"

    fun open(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show()
        }
    }

    /** Pre-filled "report this track" issue (fields match .github/ISSUE_TEMPLATE/track-report.yml). */
    fun reportTrack(track: Track): String = Uri.parse("$repoUrl/issues/new").buildUpon()
        .appendQueryParameter("template", "track-report.yml")
        .appendQueryParameter("title", "Track report: ${track.title}".take(120))
        .appendQueryParameter("item", track.id)
        .appendQueryParameter("file", track.file)
        .appendQueryParameter("track", "${track.title} / ${track.artist ?: "unknown"} / ${track.yearLabel}")
        .build().toString()

    /** Pre-filled playback-failures issue (fields match playback-failures.yml). */
    fun reportFailures(body: String): String {
        var text = body
        while (true) {
            val url = Uri.parse("$repoUrl/issues/new").buildUpon()
                .appendQueryParameter("template", "playback-failures.yml")
                .appendQueryParameter("title", "Playback/download failures from the app")
                .appendQueryParameter("failures", text)
                .build().toString()
            // GitHub rejects very long URLs; drop the oldest lines until it fits.
            if (url.length < 7000 || !text.contains('\n')) return url
            val lines = text.lines()
            val header = lines.takeWhile { !it.startsWith("- ") }
            val items = lines.drop(header.size)
            if (items.size <= 1) return url
            text = (header + items.drop(1)).joinToString("\n")
        }
    }
}
