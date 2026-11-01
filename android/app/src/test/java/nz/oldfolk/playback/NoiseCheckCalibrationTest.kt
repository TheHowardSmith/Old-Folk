package nz.oldfolk.playback

import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Opt-in calibration against real recordings: set NOISE_SAMPLES to a folder of
 * raw mono float32 files named <name>_<rate>.f32 (e.g. made with
 * `ffmpeg -i x.mp3 -t 30 -ac 1 -ar 44100 -f f32le name_44100.f32`).
 * Prints the measures and the verdict for each; skipped when unset.
 */
class NoiseCheckCalibrationTest {
    @Test
    fun printRealSamples() {
        val dir = System.getenv("NOISE_SAMPLES")?.let(::File)
        assumeTrue(dir != null && dir.isDirectory)
        dir!!.listFiles { f -> f.name.endsWith(".f32") }!!.sorted().forEach { f ->
            val rate = f.nameWithoutExtension.substringAfterLast('_').toInt()
            val bb = ByteBuffer.wrap(f.readBytes()).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()
            val x = FloatArray(bb.remaining()).also { bb.get(it) }
            val r = NoiseCheck.analyze(x, x.size, rate)
            println("NOISE ${f.nameWithoutExtension}: ratio=%.1f dB flatness=%.2f noisy=%s"
                .format(r?.ratioDb, r?.flatness, r?.noisy))
        }
    }
}
