package nz.oldfolk.playback

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin

/**
 * Heavy surface noise detector for old disc transfers.
 *
 * On the first [SECONDS] of a track (mono), short-time spectra are compared:
 *  - ratio: median of (energy 4-10 kHz) / (energy 150-3000 Hz) over the
 *    louder frames, in dB. Hiss and crackle live above 4 kHz, while most of
 *    the music on a 78 sits below 3 kHz.
 *  - flatness: median spectral flatness in 4-10 kHz (1 = white noise). Bright
 *    music (fiddles, banjos, castanets) also has energy up there, but it is
 *    tonal, so its flatness is lower.
 * A track is noisy when both exceed the thresholds. Calibrated with this code
 * on Internet Archive 78s (decoded at 44.1 kHz): clean electrical-era sides
 * score about -30 dB; the two reported "hard to listen to" examples scored
 * -14.5 dB (Chaliapin, "Dubinoushka") and -9.8 dB ("Frohe Genossen", 1923),
 * flatness 0.56-0.57; a bright but clean band (Jimmy Shand) scored -11.7 dB
 * with flatness 0.22 and is kept. On a random sample these thresholds skip
 * about 15% of tracks.
 */
object NoiseCheck {
    const val SECONDS = 30
    const val RATIO_THRESHOLD_DB = -15.5
    const val FLATNESS_THRESHOLD = 0.5
    private const val N = 2048
    private const val HOP = 1024

    data class Result(val ratioDb: Double, val flatness: Double) {
        val noisy: Boolean get() = ratioDb >= RATIO_THRESHOLD_DB && flatness >= FLATNESS_THRESHOLD
    }

    fun analyze(x: FloatArray, length: Int, rate: Int): Result? {
        if (length < N * 4) return null
        val window = DoubleArray(N) { 0.5 - 0.5 * cos(2 * PI * it / (N - 1)) }
        val binHz = rate.toDouble() / N
        fun bin(hz: Double) = (hz / binHz).toInt().coerceIn(0, N / 2)
        val m0 = bin(150.0); val m1 = bin(3000.0)
        val n0 = bin(4000.0); val n1 = bin(minOf(10000.0, rate / 2.0 - binHz))
        if (n1 - n0 < 8) return null
        val music = ArrayList<Double>()
        val ratios = ArrayList<Double>()
        val flats = ArrayList<Double>()
        val re = DoubleArray(N)
        val im = DoubleArray(N)
        var start = 0
        while (start + N <= length) {
            for (i in 0 until N) { re[i] = x[start + i] * window[i]; im[i] = 0.0 }
            fft(re, im)
            var mu = 0.0
            for (k in m0 until m1) mu += re[k] * re[k] + im[k] * im[k]
            var no = 0.0
            var logSum = 0.0
            for (k in n0 until n1) {
                val p = re[k] * re[k] + im[k] * im[k] + 1e-12
                no += p
                logSum += ln(p)
            }
            val count = n1 - n0
            music += mu
            ratios += (no + 1e-12) / (mu + 1e-12)
            flats += exp(logSum / count) / (no / count)
            start += HOP
        }
        // Ignore the quietest 30% of frames (gaps, run-in and run-out grooves).
        val gate = music.sorted()[(music.size * 0.3).toInt()]
        val keep = music.indices.filter { music[it] > gate }.ifEmpty { music.indices.toList() }
        return Result(10 * log10(median(keep.map { ratios[it] })), median(flats))
    }

    private fun median(v: List<Double>): Double {
        val s = v.sorted()
        return if (s.size % 2 == 1) s[s.size / 2] else (s[s.size / 2 - 1] + s[s.size / 2]) / 2
    }

    /** In-place iterative radix-2 FFT. */
    fun fft(re: DoubleArray, im: DoubleArray) {
        val n = re.size
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) { j = j xor bit; bit = bit shr 1 }
            j = j xor bit
            if (i < j) {
                var t = re[i]; re[i] = re[j]; re[j] = t
                t = im[i]; im[i] = im[j]; im[j] = t
            }
        }
        var len = 2
        while (len <= n) {
            val ang = -2 * PI / len
            val wr = cos(ang); val wi = sin(ang)
            var i = 0
            while (i < n) {
                var cr = 1.0; var ci = 0.0
                for (k in 0 until len / 2) {
                    val ur = re[i + k]; val ui = im[i + k]
                    val vr = re[i + k + len / 2] * cr - im[i + k + len / 2] * ci
                    val vi = re[i + k + len / 2] * ci + im[i + k + len / 2] * cr
                    re[i + k] = ur + vr; im[i + k] = ui + vi
                    re[i + k + len / 2] = ur - vr; im[i + k + len / 2] = ui - vi
                    val nr = cr * wr - ci * wi
                    ci = cr * wi + ci * wr; cr = nr
                }
                i += len
            }
            len = len shl 1
        }
    }
}
