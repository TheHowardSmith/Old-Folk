package nz.oldfolk.playback

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class NoiseCheckTest {
    private val rate = 22050

    private fun tones(noiseLevel: Float): FloatArray {
        val rnd = Random(1)
        return FloatArray(rate * 20) { i ->
            val t = i.toDouble() / rate
            // a simple "tune": a few harmonics wandering in the 200-1500 Hz range
            val f = 220.0 * (1 + (i / (rate / 2)) % 4)
            (0.3 * sin(2 * PI * f * t) + 0.15 * sin(2 * PI * 2 * f * t) + 0.08 * sin(2 * PI * 3 * f * t)).toFloat() +
                noiseLevel * (rnd.nextFloat() * 2 - 1)
        }
    }

    @Test
    fun cleanMusicIsNotNoisy() {
        val r = NoiseCheck.analyze(tones(0.003f), rate * 20, rate)!!
        assertFalse("ratio ${r.ratioDb} flat ${r.flatness}", r.noisy)
    }

    @Test
    fun musicBuriedInHissIsNoisy() {
        val r = NoiseCheck.analyze(tones(0.25f), rate * 20, rate)!!
        assertTrue("ratio ${r.ratioDb} flat ${r.flatness}", r.noisy)
    }

    @Test
    fun brightTonalMusicIsNotNoisy() {
        // strong 5-8 kHz partials (like a bright fiddle) but tonal, little noise
        val x = FloatArray(rate * 20) { i ->
            val t = i.toDouble() / rate
            (0.2 * sin(2 * PI * 440 * t) + 0.2 * sin(2 * PI * 5500 * t) + 0.2 * sin(2 * PI * 7300 * t)).toFloat()
        }
        val r = NoiseCheck.analyze(x, x.size, rate)!!
        assertFalse("ratio ${r.ratioDb} flat ${r.flatness}", r.noisy)
    }

    @Test
    fun fftOfImpulseIsFlat() {
        val re = DoubleArray(8).also { it[0] = 1.0 }
        val im = DoubleArray(8)
        NoiseCheck.fft(re, im)
        assertTrue(re.all { kotlin.math.abs(it - 1.0) < 1e-9 } && im.all { kotlin.math.abs(it) < 1e-9 })
    }
}
