package nz.oldfolk.playback

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class SpikeLimiterTest {
    private val rate = 44100

    private fun run(samples: ShortArray): ShortArray {
        val lim = SpikeLimiter()
        lim.configure(AudioProcessor.AudioFormat(rate, 1, C.ENCODING_PCM_16BIT))
        lim.flush()
        val inBuf = ByteBuffer.allocateDirect(samples.size * 2).order(ByteOrder.nativeOrder())
        samples.forEach { inBuf.putShort(it) }
        inBuf.flip()
        val out = ShortArray(samples.size)
        var n = 0
        while (inBuf.hasRemaining()) {
            lim.queueInput(inBuf)
            val o = lim.output
            while (o.hasRemaining()) out[n++] = o.short
        }
        return out
    }

    /** 3 s of a sine at -20 dBFS peak with a 4 ms full-scale burst at 2 s. */
    private fun signal(): ShortArray {
        val s = ShortArray(rate * 3)
        for (i in s.indices) s[i] = (0.1 * 32767 * sin(2 * PI * 440 * i / rate)).toInt().toShort()
        val start = rate * 2
        for (i in start until start + rate * 4 / 1000) s[i] = (if (i % 2 == 0) 32000 else -32000).toShort()
        return s
    }

    @Test
    fun spikeIsDippedMusicUntouched() {
        val input = signal()
        val out = run(input)
        val look = rate * 3 / 1000
        // The burst (delayed by the look-ahead) is held near the threshold: 12 dB above the
        // music's typical peak (0.1 full scale), i.e. ~0.4 full scale instead of ~1.0.
        val start = rate * 2 + look
        val burstPeak = (start until start + rate * 4 / 1000).maxOf { abs(out[it].toInt()) }
        assertTrue("burst peak $burstPeak", burstPeak < 32767 * 0.398 * 1.08)
        // Music well away from the burst passes unchanged (just delayed).
        for (i in rate until rate + 2000) assertEquals(input[i - look].toDouble(), out[i].toDouble(), 2.0)
        for (i in (rate * 2.5).toInt() until (rate * 2.5).toInt() + 2000) assertEquals(input[i - look].toDouble(), out[i].toDouble(), 2.0)
    }

    @Test
    fun quietPassagesNeverLimited() {
        // A spike that only reaches -26 dBFS over a -46 dBFS signal stays untouched (below the floor).
        val s = ShortArray(rate * 2) { i -> (0.005 * 32767 * sin(2 * PI * 300 * i / rate)).toInt().toShort() }
        for (i in rate until rate + 100) s[i] = 1640
        val out = run(s)
        val look = rate * 3 / 1000
        for (i in rate until rate + 100) assertEquals(s[i].toDouble(), out[i + look].toDouble(), 2.0)
    }
}
