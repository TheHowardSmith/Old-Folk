package nz.oldfolk.data

import nz.oldfolk.BuildConfig
import org.json.JSONObject

/** Checks GitHub for a newer release of the app (tags look like "app-v0.1.9"). */
object Updates {
    val apkUrl = "https://github.com/${BuildConfig.REPO}/releases/latest/download/old-folk-air.apk"
    private val latestUrl = "https://api.github.com/repos/${BuildConfig.REPO}/releases/latest"

    sealed interface Result {
        data class Available(val version: String) : Result
        data object UpToDate : Result
        data class Failed(val reason: String) : Result
    }

    /** Blocking: call from Dispatchers.IO. */
    fun check(current: String = BuildConfig.VERSION_NAME): Result = try {
        val tag = JSONObject(Http.getText(latestUrl, 15_000)).optString("tag_name")
        val latest = tag.removePrefix("app-").removePrefix("v")
        if (latest.isBlank()) Result.Failed("no release found")
        else if (isNewer(latest, current)) Result.Available(latest)
        else Result.UpToDate
    } catch (e: Exception) {
        Result.Failed(if (e is java.net.UnknownHostException) "no connection" else (e.message ?: "couldn't reach GitHub"))
    }

    /** "0.1.10" > "0.1.9"; extra suffixes ("-debug") are ignored. */
    fun isNewer(latest: String, current: String): Boolean {
        fun parts(v: String) = v.substringBefore('-').split('.').map { it.toIntOrNull() ?: 0 }
        val a = parts(latest); val b = parts(current)
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }; val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }
}
