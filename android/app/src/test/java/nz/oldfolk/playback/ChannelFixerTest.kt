package nz.oldfolk.playback

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

class ChannelFixerTest {
    private val rate = 44100

    private fun tone(i: Int, amp: Double = 0.3, f: Double = 440.0) = (amp * 32767 * sin(2 * PI * f * i / rate)).toInt().toShort()

    /** Interleaved stereo in, interleaved stereo out. */
    private fun run(fixer: ChannelFixer, frames: Int, left: (Int) -> Short, right: (Int) -> Short): ShortArray {
        fixer.configure(AudioProcessor.AudioFormat(rate, 2, C.ENCODING_PCM_16BIT))
        fixer.flush()
        val inBuf = ByteBuffer.allocateDirect(frames * 4).order(ByteOrder.nativeOrder())
        for (i in 0 until frames) { inBuf.putShort(left(i)); inBuf.putShort(right(i)) }
        inBuf.flip()
        val out = ShortArray(frames * 2)
        var n = 0
        while (inBuf.hasRemaining()) {
            // Feed in player-sized chunks.
            val chunk = inBuf.slice().order(ByteOrder.nativeOrder())
            chunk.limit(minOf(chunk.remaining(), 4096))
            fixer.queueInput(chunk)
            inBuf.position(inBuf.position() + chunk.position())
            val o = fixer.output
            while (o.hasRemaining()) out[n++] = o.short
        }
        return out
    }

    @Test
    fun rightOnlyRecordingPlaysOnBothSpeakers() {
        val out = run(ChannelFixer(), rate * 2, { 0 }, { tone(it) })
        // After detection (first 10 ms block) and the 60 ms crossfade, left == right.
        for (i in rate / 10 until rate * 2) assertEquals(out[2 * i + 1].toDouble(), out[2 * i].toDouble(), 1.0)
    }

    @Test
    fun leftOnlyWithFaintHissIsAlsoFixed() {
        val out = run(ChannelFixer(), rate, { tone(it) }, { tone(it, amp = 0.0003, f = 7000.0) })
        for (i in rate / 10 until rate) assertEquals(out[2 * i].toDouble(), out[2 * i + 1].toDouble(), 1.0)
    }

    @Test
    fun normalStereoUntouched() {
        val out = run(ChannelFixer(), rate, { tone(it) }, { tone(it, amp = 0.1, f = 330.0) })
        for (i in 0 until rate) {
            assertEquals(tone(i).toDouble(), out[2 * i].toDouble(), 1.0)
            assertEquals(tone(i, amp = 0.1, f = 330.0).toDouble(), out[2 * i + 1].toDouble(), 1.0)
        }
    }

    @Test
    fun silenceIsLeftAlone() {
        val fixer = ChannelFixer()
        run(fixer, rate, { 0 }, { 0 })
        assertEquals(0, fixer.mode)
    }

    @Test
    fun switchesBackForTheNextNormalTrack() {
        val fixer = ChannelFixer()
        // 1 s one-sided, then 1 s normal stereo (a gapless track change).
        val out = run(fixer, rate * 2, { if (it < rate) 0 else tone(it, f = 330.0) }, { tone(it) })
        assertEquals(0, fixer.mode)
        val start = rate + rate / 5
        for (i in start until start + 2000) assertEquals(tone(i, f = 330.0).toDouble(), out[2 * i].toDouble(), 1.0)
    }

    @Test
    fun monoIsNotProcessed() {
        val fixer = ChannelFixer()
        fixer.configure(AudioProcessor.AudioFormat(rate, 1, C.ENCODING_PCM_16BIT))
        assertFalse(fixer.isActive)
        val stereo = ChannelFixer()
        stereo.configure(AudioProcessor.AudioFormat(rate, 2, C.ENCODING_PCM_16BIT))
        assertTrue(stereo.isActive)
    }
}
