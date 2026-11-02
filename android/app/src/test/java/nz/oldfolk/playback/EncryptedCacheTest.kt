package nz.oldfolk.playback

import android.net.Uri
import androidx.media3.datasource.AesCipherDataSink
import androidx.media3.datasource.AesCipherDataSource
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import kotlin.random.Random

/** The same encrypted-cache wiring as [MediaCache], on a JVM cache (no database). */
@Suppress("DEPRECATION")
class EncryptedCacheTest {
    private val key = ByteArray(16) { it.toByte() }
    private val audio = ByteArray(300_000).also { Random(1).nextBytes(it) }
    private val url = "https://archive.org/download/x/a.mp3"

    private fun factory(cache: SimpleCache, upstream: DataSource.Factory?): CacheDataSource.Factory {
        val f = CacheDataSource.Factory().setCache(cache)
            .setCacheReadDataSourceFactory { AesCipherDataSource(key, FileDataSource()) }
            .setCacheWriteDataSinkFactory {
                AesCipherDataSink(key, CacheDataSink(cache, CacheDataSink.DEFAULT_FRAGMENT_SIZE), ByteArray(16 * 1024))
            }
        if (upstream != null) f.setUpstreamDataSourceFactory(upstream)
        return f
    }

    private fun readAll(ds: DataSource, spec: DataSpec): ByteArray {
        val out = ByteArrayOutputStream()
        ds.open(spec)
        val buf = ByteArray(8192)
        while (true) {
            val n = ds.read(buf, 0, buf.size)
            if (n < 0) break
            out.write(buf, 0, n)
        }
        ds.close()
        return out.toByteArray()
    }

    @Test
    fun streamsPlainAudioButStoresItEncrypted() {
        val dir = Files.createTempDirectory("cache").toFile()
        val cache = SimpleCache(dir, NoOpCacheEvictor())
        try {
            val spec = DataSpec(Uri.parse(url))
            // First play: from the network, written to the cache on the way.
            val first = readAll(factory(cache, { ByteArrayDataSource(audio) }).createDataSource(), spec)
            assertArrayEquals(audio, first)
            // Replay and a seek into the middle: from the cache only.
            val cached = factory(cache, null).createDataSource()
            assertArrayEquals(audio, readAll(cached, spec))
            val mid = readAll(cached, DataSpec.Builder().setUri(url).setPosition(123_457).build())
            assertArrayEquals(audio.copyOfRange(123_457, audio.size), mid)
            // On disk, no stretch of the audio appears in the clear.
            val probe = audio.copyOfRange(1000, 1032)
            dir.walk().filter { it.isFile && it.name.endsWith(".exo") }.forEach { f ->
                val bytes = f.readBytes()
                assertFalse("plaintext found in ${f.name}", bytes.indices.any { i ->
                    i + probe.size <= bytes.size && (probe.indices.all { bytes[i + it] == probe[it] })
                })
            }
        } finally {
            cache.release()
            dir.deleteRecursively()
        }
    }
}
