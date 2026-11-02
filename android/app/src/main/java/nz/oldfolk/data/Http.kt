package nz.oldfolk.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import nz.oldfolk.BuildConfig
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

val USER_AGENT = "OldFolkAir/${BuildConfig.VERSION_NAME} (Android; +https://github.com/${BuildConfig.REPO})"

class HttpStatusException(val code: Int, url: String) : IOException("HTTP $code for $url")

/** Small blocking HTTP helpers (call from Dispatchers.IO). Follows https redirects. */
object Http {
    private fun open(url: String, timeoutMs: Int): HttpURLConnection {
        var current = url
        repeat(8) {
            val c = URL(current).openConnection() as HttpURLConnection
            c.instanceFollowRedirects = false
            c.connectTimeout = timeoutMs
            c.readTimeout = timeoutMs
            c.setRequestProperty("User-Agent", USER_AGENT)
            val code = c.responseCode
            if (code in 300..399) {
                val loc = c.getHeaderField("Location") ?: throw HttpStatusException(code, current)
                c.disconnect()
                current = URL(URL(current), loc).toString()
            } else if (code !in 200..299) {
                c.disconnect()
                throw HttpStatusException(code, current)
            } else {
                return c
            }
        }
        throw IOException("too many redirects for $url")
    }

    fun getText(url: String, timeoutMs: Int = 20_000): String {
        val c = open(url, timeoutMs)
        try {
            return c.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            c.disconnect()
        }
    }

    /** Download to [dest] via a temp file; [progress] gets 0..1 when the size is known. */
    fun download(url: String, dest: File, progress: (Float) -> Unit = {}) {
        val c = open(url, 30_000)
        val tmp = File(dest.parentFile, dest.name + ".part")
        try {
            val total = c.contentLengthLong
            var done = 0L
            c.inputStream.use { input ->
                tmp.outputStream().use { out ->
                    val buf = ByteArray(64 * 1024)
                    while (true) {
                        val n = input.read(buf)
                        if (n < 0) break
                        out.write(buf, 0, n)
                        done += n
                        if (total > 0) progress(done.toFloat() / total)
                    }
                }
            }
            if (total > 0 && done != total) throw IOException("incomplete download ($done of $total bytes)")
            if (!tmp.renameTo(dest)) throw IOException("could not save ${dest.name}")
        } finally {
            c.disconnect()
            tmp.delete()
        }
    }
}

fun Context.isOnline(): Boolean {
    val cm = getSystemService(ConnectivityManager::class.java) ?: return true
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

fun Context.networkType(): String {
    val cm = getSystemService(ConnectivityManager::class.java) ?: return "unknown"
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return "none"
    return when {
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
        else -> "other"
    }
}
