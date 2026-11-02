@file:androidx.annotation.OptIn(markerClass = [androidx.media3.common.util.UnstableApi::class])

package nz.oldfolk.playback

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.AesCipherDataSink
import androidx.media3.datasource.AesCipherDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.datasource.cache.ContentMetadata
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import nz.oldfolk.data.USER_AGENT
import java.io.File

/**
 * Streaming cache: a capped LRU disk cache in cacheDir. Streams are read
 * through it, and the next few radio tracks are prefetched into it so that
 * playback continues smoothly (and survives short connection drops).
 *
 * Everything is stored encrypted (AES-CTR, keyed per file and position) with a
 * key held by the Android Keystore ([CacheKey]), so cached recordings can only
 * be played by this app, not copied off the phone and shared as files.
 */
class MediaCache(context: Context, maxBytes: Long = MAX_BYTES) {
    private val secret = CacheKey.load(context)

    val cache: SimpleCache = run {
        File(context.cacheDir, "media").deleteRecursively()      // older, unencrypted cache
        val dir = File(context.cacheDir, "media-enc")
        if (secret.fresh) dir.deleteRecursively()                 // written with a key we no longer have
        SimpleCache(dir, LeastRecentlyUsedCacheEvictor(maxBytes), StandaloneDatabaseProvider(context))
    }

    private val http = DefaultHttpDataSource.Factory()
        .setUserAgent(USER_AGENT)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15_000)
        .setReadTimeoutMs(20_000)

    private val decrypting = DataSource.Factory { AesCipherDataSource(secret.key, FileDataSource()) }

    val cachedHttp: CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(http)
        .setCacheReadDataSourceFactory(decrypting)
        .setCacheWriteDataSinkFactory {
            // The scratch buffer keeps the player's copy of the data unencrypted.
            AesCipherDataSink(secret.key, CacheDataSink(cache, CacheDataSink.DEFAULT_FRAGMENT_SIZE), ByteArray(SCRATCH))
        }
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    /** Reads only what is already cached (for the level and noise check). */
    val cacheOnly: CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(cache)
        .setCacheReadDataSourceFactory(decrypting)

    val dataSourceFactory: DataSource.Factory = cachedHttp

    fun usedBytes(): Long = cache.cacheSpace

    /** True when the whole file for [url] is in the cache. */
    fun isFullyCached(url: String): Boolean = try {
        val len = ContentMetadata.getContentLength(cache.getContentMetadata(url))
        len > 0 && cache.isCached(url, 0, len)
    } catch (_: Exception) {
        false
    }

    companion object {
        const val MAX_BYTES = 600L * 1024 * 1024
        private const val SCRATCH = 16 * 1024
    }

    private var prefetchJob: Job? = null

    /**
     * Replace the prefetch set; earlier unfinished prefetches are cancelled.
     * [afterCached] runs (on the IO thread) for each URL once it is fully cached.
     */
    fun prefetch(scope: CoroutineScope, urls: List<String>, afterCached: (String) -> Unit = {}) {
        prefetchJob?.cancel()
        prefetchJob = scope.launch(Dispatchers.IO) {
            for (url in urls) {
                if (!isActive) break
                try {
                    val writer = CacheWriter(cachedHttp.createDataSource(), DataSpec(Uri.parse(url)), null, null)
                    ensureActive()
                    val job = coroutineContext[Job]
                    job?.invokeOnCompletion { writer.cancel() }
                    writer.cache()
                    ensureActive()
                    afterCached(url)
                } catch (e: Exception) {
                    if (isActive) Log.i("MediaCache", "prefetch failed for $url: ${e.message}")
                }
            }
        }
    }
}
