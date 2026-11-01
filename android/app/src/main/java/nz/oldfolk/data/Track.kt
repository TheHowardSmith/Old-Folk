package nz.oldfolk.data

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

/** One playable recording, as listed in a track-list shard. */
data class Track(
    val id: String,          // Internet Archive item identifier
    val file: String,        // file name inside the item
    val title: String,
    val artist: String?,
    val year: Int?,
    val region: String,
    val genre: String,
) {
    /** Stable identity across list versions: used for "never repeat" and downloads. */
    val key: Long = keyOf(id, file)

    val streamUrl: String
        get() = "https://archive.org/download/" + Uri.encode(id) + "/" +
            file.split('/').joinToString("/") { Uri.encode(it) }

    val detailsUrl: String get() = "https://archive.org/details/" + Uri.encode(id)

    /** "1928" or "78 rpm era" for undated discs. */
    val yearLabel: String get() = year?.toString() ?: "78 rpm era"

    fun toJson(): JSONObject = JSONObject().apply {
        put("i", id); put("f", file); put("t", title)
        artist?.let { put("a", it) }
        year?.let { put("y", it) }
        put("r", region); put("g", genre)
    }

    companion object {
        /** 64-bit FNV-1a of "id/file". */
        fun keyOf(id: String, file: String): Long {
            var h = -0x340d631b7bdddcdbL // 0xcbf29ce484222325
            for (b in "$id/$file".toByteArray(Charsets.UTF_8)) {
                h = h xor (b.toLong() and 0xff)
                h *= 0x100000001b3L
            }
            return h
        }

        fun fromJson(o: JSONObject): Track = Track(
            id = o.getString("i"),
            file = o.getString("f"),
            title = o.optString("t").ifBlank { "Untitled" },
            artist = o.optString("a").ifBlank { null },
            year = if (o.has("y")) o.optInt("y").takeIf { it > 0 } else null,
            region = o.optString("r").ifBlank { "Unknown" },
            genre = o.optString("g").ifBlank { "world" },
        )

        fun parseShard(json: String): List<Track> {
            val arr: JSONArray = JSONObject(json).getJSONArray("t")
            return List(arr.length()) { fromJson(arr.getJSONObject(it)) }
        }
    }
}

data class Genre(val key: String, val name: String, val count: Int)

data class ShardInfo(val file: String, val genre: String, val count: Int, val sha256: String)

data class TrackIndex(
    val version: Long,
    val genres: List<Genre>,
    val shards: List<ShardInfo>,
) {
    fun genreName(key: String): String = genres.firstOrNull { it.key == key }?.name ?: key

    companion object {
        fun parse(json: String): TrackIndex {
            val o = JSONObject(json)
            val g = o.getJSONArray("genres")
            val s = o.getJSONArray("shards")
            return TrackIndex(
                version = o.getLong("v"),
                genres = List(g.length()) {
                    val x = g.getJSONObject(it)
                    Genre(x.getString("k"), x.getString("name"), x.optInt("n"))
                },
                shards = List(s.length()) {
                    val x = s.getJSONObject(it)
                    ShardInfo(x.getString("f"), x.getString("g"), x.optInt("n"), x.getString("sha256"))
                },
            )
        }
    }
}
