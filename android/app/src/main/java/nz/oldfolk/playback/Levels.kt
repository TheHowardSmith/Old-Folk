@file:androidx.annotation.OptIn(markerClass = [androidx.media3.common.util.UnstableApi::class])

package nz.oldfolk.playback

import android.media.MediaCodec
import android.media.MediaDataSource
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.util.Log
import androidx.media3.common.C
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.ContentMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.File
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log10
import kotlin.math.pow

/**
 * Loudness levelling and noise check. When a track has been downloaded
 * (prefetched into the cache, or saved for a playlist) its first [MAX_SECONDS]
 * are decoded once:
 *  - a gated RMS loudness is measured (a simplified EBU R128-style reading);
 *    the playback gain that brings it to [TARGET_DB] is applied by
 *    [RadioEngine] at the start of the track;
 *  - the first [NoiseCheck.SECONDS] are checked for heavy surface noise
 *    ([NoiseCheck]); the radio skips tracks flagged as noisy.
 */
class Levels(private val file: File) {
    private val gains = ConcurrentHashMap<Long, Float>()
    private val noise = ConcurrentHashMap<Long, NoiseCheck.Result>()
    private val noiseFile = File(file.parentFile, "noise.json")
    private val _version = MutableStateFlow(0)
    /** Bumps whenever a new measurement is stored. */
    val version: StateFlow<Int> = _version.asStateFlow()

    init {
        runCatching {
            val o = JSONObject(file.readText())
            o.keys().forEach { k -> gains[k.toLong(16)] = o.getDouble(k).toFloat() }
        }
        runCatching {
            val o = JSONObject(noiseFile.readText())
            o.keys().forEach { k ->
                val a = o.getJSONArray(k)
                noise[k.toLong(16)] = NoiseCheck.Result(a.getDouble(0), a.getDouble(1))
            }
        }
    }

    /** True if the track was measured and has heavy surface noise; null if not measured. */
    fun isNoisy(key: Long): Boolean? = noise[key]?.noisy

    fun noisyCount(): Int = noise.values.count { it.noisy }

    /** Gain in dB for a track, or null if it hasn't been measured yet. */
    fun gainDb(key: Long): Float? = gains[key]

    fun has(key: Long) = gains.containsKey(key)

    fun analyzeCached(key: Long, cache: Cache, url: String) {
        if (has(key)) return
        val size = ContentMetadata.getContentLength(cache.getContentMetadata(url))
        if (size <= 0) return
        CacheMediaDataSource(cache, url, size).use { src -> store(key, read { it.setDataSource(src) }) }
    }

    fun analyzeFile(key: Long, f: File) {
        if (has(key) || !f.exists()) return
        store(key, read { it.setDataSource(f.absolutePath) })
    }

    private fun store(key: Long, reading: Reading?) {
        val loudness = reading?.loudness ?: return
        val gain = (TARGET_DB - loudness).coerceIn(MIN_GAIN_DB, MAX_GAIN_DB).toFloat()
        gains[key] = gain
        reading.noise?.let { noise[key] = it }
        save()
        _version.value++
    }

    @Synchronized
    private fun save() {
        val o = JSONObject()
        gains.forEach { (k, v) -> o.put(java.lang.Long.toHexString(k), ((v * 10).toInt() / 10.0)) }
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(o.toString())
        tmp.renameTo(file)
        val n = JSONObject()
        noise.forEach { (k, v) ->
            n.put(java.lang.Long.toHexString(k), org.json.JSONArray().put((v.ratioDb * 10).toInt() / 10.0)
                .put((v.flatness * 100).toInt() / 100.0))
        }
        val tmpN = File(noiseFile.parentFile, noiseFile.name + ".tmp")
        tmpN.writeText(n.toString())
        tmpN.renameTo(noiseFile)
    }

    data class Reading(val loudness: Double?, val noise: NoiseCheck.Result?)

    companion object {
        private const val TAG = "Levels"
        const val TARGET_DB = -20.0     // gated RMS, dBFS
        const val MIN_GAIN_DB = -15.0
        const val MAX_GAIN_DB = 10.0    // boosting old discs further mostly raises the noise
        private const val MAX_SECONDS = 90
        private const val WINDOW_S = 0.4

        /** Gated RMS loudness of the first [MAX_SECONDS] in dBFS, or null if undecodable. */
        fun measure(setSource: (MediaExtractor) -> Unit): Double? = read(setSource)?.loudness

        /** One decode pass: loudness of the first [MAX_SECONDS] plus the noise check. */
        fun read(setSource: (MediaExtractor) -> Unit): Reading? {
            val ex = MediaExtractor()
            var codec: MediaCodec? = null
            try {
                setSource(ex)
                val track = (0 until ex.trackCount).firstOrNull {
                    ex.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
                } ?: return null
                ex.selectTrack(track)
                val format = ex.getTrackFormat(track)
                var rate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                var channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                val c = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME)!!)
                codec = c
                c.configure(format, null, null, 0)
                c.start()

                val windows = ArrayList<Double>()   // mean square per window
                // Per-channel mean squares (stereo only), for one-sided transfers that
                // ChannelFixer plays on both speakers: those are measured on the loud channel.
                val windowsL = ArrayList<Double>()
                val windowsR = ArrayList<Double>()
                var winL = 0.0
                var winR = 0.0
                var totalL = 0.0
                var totalR = 0.0
                var mono = FloatArray(rate * NoiseCheck.SECONDS)  // first seconds, channels averaged
                var monoLen = 0
                var frameSum = 0f
                var chan = 0
                var winSum = 0.0
                var winCount = 0
                var total = 0L
                var winSize = (rate * channels * WINDOW_S).toInt()
                val maxSamples = rate.toLong() * channels * MAX_SECONDS
                val info = MediaCodec.BufferInfo()
                var inputDone = false
                var outputDone = false
                while (!outputDone && total < maxSamples) {
                    if (!inputDone) {
                        val i = c.dequeueInputBuffer(10_000)
                        if (i >= 0) {
                            val buf = c.getInputBuffer(i)!!
                            val n = ex.readSampleData(buf, 0)
                            if (n < 0) {
                                c.queueInputBuffer(i, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                                inputDone = true
                            } else {
                                c.queueInputBuffer(i, 0, n, ex.sampleTime, 0)
                                ex.advance()
                            }
                        }
                    }
                    val o = c.dequeueOutputBuffer(info, 10_000)
                    if (o == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        rate = c.outputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                        channels = c.outputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                        winSize = (rate * channels * WINDOW_S).toInt()
                        if (monoLen == 0) mono = FloatArray(rate * NoiseCheck.SECONDS)
                    } else if (o >= 0) {
                        val out = c.getOutputBuffer(o)!!
                        out.position(info.offset)
                        out.limit(info.offset + info.size)
                        val shorts = out.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                        while (shorts.hasRemaining()) {
                            val v = shorts.get() / 32768.0
                            winSum += v * v
                            if (channels == 2) { if (chan == 0) winL += v * v else winR += v * v }
                            frameSum += v.toFloat()
                            if (++chan >= channels) {
                                if (monoLen < mono.size) mono[monoLen++] = frameSum / channels
                                frameSum = 0f; chan = 0
                            }
                            if (++winCount >= winSize) {
                                windows += winSum / winCount
                                if (channels == 2) {
                                    windowsL += winL / (winCount / 2.0); windowsR += winR / (winCount / 2.0)
                                    totalL += winL; totalR += winR
                                }
                                winSum = 0.0; winCount = 0; winL = 0.0; winR = 0.0
                            }
                            total++
                        }
                        c.releaseOutputBuffer(o, false)
                        if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) outputDone = true
                    }
                }
                val noiseResult = if (monoLen >= rate * 8) NoiseCheck.analyze(mono, monoLen, rate) else null
                val measured = when (oneSided(totalL, totalR)) {
                    1 -> windowsL
                    2 -> windowsR
                    else -> windows
                }
                return Reading(gatedLoudness(measured), noiseResult)
            } catch (e: Exception) {
                Log.i(TAG, "level read failed: ${e.message}")
                return null
            } finally {
                runCatching { codec?.stop() }
                runCatching { codec?.release() }
                ex.release()
            }
        }

        /**
         * 1 if only the left channel carries sound, 2 if only the right, else 0
         * (same -30 dB rule as [ChannelFixer]).
         */
        fun oneSided(energyL: Double, energyR: Double): Int = when {
            energyL <= 0.0 && energyR <= 0.0 -> 0
            energyR < energyL * 1e-3 -> 1
            energyL < energyR * 1e-3 -> 2
            else -> 0
        }

        /** Mean loudness of windows above an absolute (-60 dB) and relative (-15 dB) gate. */
        fun gatedLoudness(meanSquares: List<Double>): Double? {
            fun db(ms: Double) = 10 * log10(ms.coerceAtLeast(1e-12))
            val abs = meanSquares.filter { db(it) > -60.0 }
            if (abs.isEmpty()) return null
            val relGate = db(abs.average()) - 15.0
            val gated = abs.filter { db(it) > relGate }
            return db(gated.ifEmpty { abs }.average())
        }

        fun dbToLinear(db: Float): Float = 10.0.pow(db / 20.0).toFloat()
    }
}

/** Lets MediaExtractor read a fully cached stream straight from the media cache. */
private class CacheMediaDataSource(cache: Cache, private val key: String, private val length: Long) : MediaDataSource() {
    private val factory = CacheDataSource.Factory().setCache(cache)   // no upstream: cache only
    private var ds: DataSource? = null
    private var pos = -1L

    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position >= length) return -1
        if (ds == null || position != pos) {
            ds?.close()
            ds = factory.createDataSource().also {
                it.open(DataSpec.Builder().setUri(Uri.parse(key)).setKey(key).setPosition(position).build())
            }
            pos = position
        }
        val n = ds!!.read(buffer, offset, size)
        if (n == C.RESULT_END_OF_INPUT) return -1
        pos += n
        return n
    }

    override fun getSize(): Long = length

    override fun close() {
        runCatching { ds?.close() }
        ds = null
    }
}
