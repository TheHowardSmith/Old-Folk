@file:androidx.annotation.OptIn(markerClass = [androidx.media3.common.util.UnstableApi::class])

package nz.oldfolk.playback

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.TransferListener
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
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
 */
class MediaCache(context: Context, maxBytes: Long = 300L * 1024 * 1024) {
    val cache = SimpleCache(
        File(context.cacheDir, "media"),
        LeastRecentlyUsedCacheEvictor(maxBytes),
        StandaloneDatabaseProvider(context),
    )

    private val http = DefaultHttpDataSource.Factory()
        .setUserAgent(USER_AGENT)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15_000)
        .setReadTimeoutMs(20_000)

    val cachedHttp: CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(http)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    /** Local playlist downloads are read directly; everything else goes through the cache. */
    val dataSourceFactory = DataSource.Factory {
        RoutingDataSource(FileDataSource(), cachedHttp.createDataSource())
    }

    fun usedBytes(): Long = cache.cacheSpace

    private var prefetchJob: Job? = null

    /** Replace the prefetch set; earlier unfinished prefetches are cancelled. */
    fun prefetch(scope: CoroutineScope, urls: List<String>) {
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
                } catch (e: Exception) {
                    if (isActive) Log.i("MediaCache", "prefetch failed for $url: ${e.message}")
                }
            }
        }
    }
}

private class RoutingDataSource(
    private val file: DataSource,
    private val net: DataSource,
) : DataSource {
    private var current: DataSource? = null

    override fun addTransferListener(transferListener: TransferListener) {
        file.addTransferListener(transferListener)
        net.addTransferListener(transferListener)
    }

    override fun open(dataSpec: DataSpec): Long {
        val scheme = dataSpec.uri.scheme
        val ds = if (scheme == null || scheme == "file") file else net
        current = ds
        return ds.open(dataSpec)
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
        current?.read(buffer, offset, length) ?: throw IllegalStateException("not opened")

    override fun getUri(): Uri? = current?.uri

    override fun getResponseHeaders(): Map<String, List<String>> = current?.responseHeaders ?: emptyMap()

    override fun close() {
        try {
            current?.close()
        } finally {
            current = null
        }
    }
}
