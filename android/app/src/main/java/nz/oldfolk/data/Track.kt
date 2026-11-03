package nz.oldfolk.data

import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

/** One playable recording, as listed in a track-list shard. */
data class Track(
    val id: String,          // Internet Archive item identifier (or "loc:<id>" etc. for other archives)
    val file: String,        // file name inside the item
    val title: String,
    val artist: String?,
    val year: Int?,
    val region: String,
    val genre: String,
    /** Label/cover image: "1" = <id>_itemimage.jpg, "2" = <id>_itemimage.png, else a file name, null = none. */
    val picture: String? = null,
    /** Full audio URL, for recordings hosted outside the Internet Archive. */
    val url: String? = null,
    /** The recording's web page, likewise. */
    val page: String? = null,
    /** Holding institution to credit, for recordings outside the Internet Archive. */
    val source: String? = null,
) {
    /** Stable identity across list versions: used for "never repeat" and downloads. */
    val key: Long = keyOf(id, file)

    val streamUrl: String
        get() = url ?: ("https://archive.org/download/" + Uri.encode(id) + "/" +
            file.split('/').joinToString("/") { Uri.encode(it) })

    val detailsUrl: String get() = page ?: ("https://archive.org/details/" + Uri.encode(id))

    /** True for recordings on the Internet Archive; others come from smaller archives. */
    val onArchiveOrg: Boolean get() = url == null

    /** Where the recording is hosted, for display. */
    val sourceName: String
        get() = when {
            url == null -> "Internet Archive"
            source != null -> source
            url.contains("loc.gov") -> "Library of Congress"
            else -> Uri.parse(url).host ?: "web"
        }

    /** Disc label / album cover (falls back to the Archive's item thumbnail); null if none. */
    val artUrl: String?
        get() = if (url != null) picture?.takeIf { it.startsWith("https://") } else when (picture) {
            null -> "https://archive.org/services/img/" + Uri.encode(id)
            "0" -> null      // no cover or label scan (the thumbnail would only be a waveform): the drawn 78
            "1" -> "https://archive.org/download/" + Uri.encode(id) + "/" + Uri.encode(id + "_itemimage.jpg")
            "2" -> "https://archive.org/download/" + Uri.encode(id) + "/" + Uri.encode(id + "_itemimage.png")
            else -> "https://archive.org/download/" + Uri.encode(id) + "/" +
                picture.split('/').joinToString("/") { Uri.encode(it) }
        }

    /** "1928" or "78 rpm era" for undated discs. */
    val yearLabel: String get() = year?.toString() ?: "78 rpm era"

    fun toJson(): JSONObject = JSONObject().apply {
        put("i", id); put("f", file); put("t", title)
        artist?.let { put("a", it) }
        year?.let { put("y", it) }
        put("r", region); put("g", genre)
        picture?.let { put("p", it.toIntOrNull() ?: it) }
        url?.let { put("u", it) }
        page?.let { put("w", it) }
        source?.let { put("s", it) }
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
            picture = o.opt("p")?.toString()?.ifBlank { null },
            url = o.optString("u").ifBlank { null },
            page = o.optString("w").ifBlank { null },
            source = o.optString("s").ifBlank { null },
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
            // "ext" lists shards of recordings hosted outside the Internet Archive
            // (kept separate so older app versions, which can't play them, skip them).
            val s = o.getJSONArray("shards")
            val ext = o.optJSONArray("ext") ?: JSONArray()
            return TrackIndex(
                version = o.getLong("v"),
                genres = List(g.length()) {
                    val x = g.getJSONObject(it)
                    Genre(x.getString("k"), x.getString("name"), x.optInt("n"))
                },
                shards = (List(s.length()) { s.getJSONObject(it) } + List(ext.length()) { ext.getJSONObject(it) })
                    .map { x -> ShardInfo(x.getString("f"), x.getString("g"), x.optInt("n"), x.getString("sha256")) },
            )
        }
    }
}
