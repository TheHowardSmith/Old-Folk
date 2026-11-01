package nz.oldfolk.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Tracks that failed to play or download on this phone. The user can send
 * them to the repository as a pre-filled GitHub issue; a maintainer script
 * then re-checks each URL to tell dead files from phone-side problems.
 */
class FailureLog(private val context: Context) {
    data class Failure(
        val id: String,
        val file: String,
        val kind: String,        // "play" or "download"
        val reason: String,      // e.g. "HTTP 404", "network timeout", "decoder error"
        val online: Boolean,     // did the phone think it had internet?
        val network: String,     // wifi / cellular / none
        val time: Long,
    )

    private val file = File(context.filesDir, "failures.json")
    private val _items = MutableStateFlow(load())
    val items: StateFlow<List<Failure>> = _items.asStateFlow()

    private fun load(): List<Failure> = runCatching {
        val arr = JSONArray(file.readText())
        List(arr.length()) {
            val o = arr.getJSONObject(it)
            Failure(o.getString("i"), o.getString("f"), o.getString("k"), o.getString("r"),
                o.optBoolean("o", true), o.optString("n"), o.optLong("t"))
        }
    }.getOrDefault(emptyList())

    @Synchronized
    fun record(track: Track, kind: String, reason: String) {
        val f = Failure(track.id, track.file, kind, reason.take(200), context.isOnline(),
            context.networkType(), System.currentTimeMillis())
        // Keep one entry per track+kind, most recent last; cap the log size.
        val list = _items.value.filterNot { it.id == f.id && it.file == f.file && it.kind == f.kind } + f
        save(list.takeLast(200))
    }

    @Synchronized
    fun clear() = save(emptyList())

    private fun save(list: List<Failure>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().put("i", it.id).put("f", it.file).put("k", it.kind).put("r", it.reason)
                .put("o", it.online).put("n", it.network).put("t", it.time))
        }
        file.writeText(arr.toString())
        _items.value = list
    }

    /** Markdown-ish lines for the issue body; the checker script parses the `item|file` part. */
    fun reportBody(versionName: String, max: Int = 40): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        val lines = _items.value.takeLast(max).joinToString("\n") {
            "- `${it.id}|${it.file}` ${it.kind}: ${it.reason} (online=${it.online}, ${it.network}, ${fmt.format(Date(it.time))} UTC)"
        }
        return "App version: $versionName\nAndroid: ${android.os.Build.VERSION.RELEASE}\n\n$lines"
    }
}
