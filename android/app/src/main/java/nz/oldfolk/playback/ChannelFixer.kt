package nz.oldfolk.playback

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

/**
 * Plays one-sided stereo transfers (music on one channel, silence on the other)
 * on both speakers.
 *
 * It follows each channel's energy over 10 ms blocks (smoothed over
 * ~[smoothingS]). When one channel is more than [silentDb] below the other
 * while the other carries sound, the loud channel is copied to the silent
 * side, with a short crossfade. It switches back once the quiet channel comes
 * within [returnDb] (e.g. at the next, normal track), so it needs no per-track
 * settings and works across gapless track changes. Normal stereo and mono
 * files pass through untouched; [Levels] measures one-sided tracks on their
 * loud channel, so after copying they play at the same loudness as the rest.
 */
@UnstableApi
class ChannelFixer(
    private val silentDb: Float = 30f,
    private val returnDb: Float = 20f,
    private val signalFloorDb: Float = -50f,
    private val smoothingS: Float = 0.3f,
    private val fadeMs: Float = 60f,
) : BaseAudioProcessor() {
    private var isFloat = false
    private var blockSize = 0
    private var blockL = 0.0
    private var blockR = 0.0
    private var blockCount = 0
    private var envL = -1.0
    private var envR = -1.0
    private var smooth = 0.0
    /** 0 = normal, 1 = left copied to the (silent) right, 2 = right copied to the left. */
    var mode = 0
        private set
    private var mix = 0f            // 0 = original, 1 = fully copied
    private var fadeStep = 0f
    private val enterRatio = Math.pow(10.0, -silentDb / 10.0)
    private val exitRatio = Math.pow(10.0, -returnDb / 10.0)
    private val floor = Math.pow(10.0, signalFloorDb / 10.0)

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        val enc = inputAudioFormat.encoding
        if (inputAudioFormat.channelCount != 2 || (enc != C.ENCODING_PCM_16BIT && enc != C.ENCODING_PCM_FLOAT)) {
            return AudioFormat.NOT_SET      // mono already plays on both speakers
        }
        isFloat = enc == C.ENCODING_PCM_FLOAT
        val rate = inputAudioFormat.sampleRate
        blockSize = max(1, rate / 100)
        smooth = 1 - exp(-0.01 / smoothingS)
        fadeStep = 1f / max(1f, rate * fadeMs / 1000f)
        return inputAudioFormat
    }

    override fun onFlush() = resetState()

    override fun onReset() = resetState()

    private fun resetState() {
        blockL = 0.0; blockR = 0.0; blockCount = 0
        envL = -1.0; envR = -1.0
        mode = 0; mix = 0f
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val bytesPerSample = if (isFloat) 4 else 2
        val frames = inputBuffer.remaining() / (bytesPerSample * 2)
        if (frames == 0) return
        val out = replaceOutputBuffer(frames * bytesPerSample * 2)
        val input = inputBuffer.order(ByteOrder.nativeOrder())
        repeat(frames) {
            val l = if (isFloat) input.float else input.short / 32768f
            val r = if (isFloat) input.float else input.short / 32768f
            blockL += l * l; blockR += r * r
            if (++blockCount >= blockSize) {
                updateMode(blockL / blockCount, blockR / blockCount)
                blockL = 0.0; blockR = 0.0; blockCount = 0
            }
            mix = if (mode == 0) max(0f, mix - fadeStep) else min(1f, mix + fadeStep)
            var outL = l
            var outR = r
            if (mix > 0f) {
                // While fading back to normal the last copy direction still applies.
                if (copyLeft) outR = r + (l - r) * mix else outL = l + (r - l) * mix
            }
            if (isFloat) { out.putFloat(outL); out.putFloat(outR) }
            else {
                out.putShort((outL * 32768f).toInt().coerceIn(-32768, 32767).toShort())
                out.putShort((outR * 32768f).toInt().coerceIn(-32768, 32767).toShort())
            }
        }
        out.flip()
    }

    private var copyLeft = true

    private fun updateMode(msL: Double, msR: Double) {
        if (envL < 0) { envL = msL; envR = msR } else {
            envL += smooth * (msL - envL)
            envR += smooth * (msR - envR)
        }
        val loud = max(envL, envR)
        val quiet = min(envL, envR)
        when (mode) {
            0 -> if (loud > floor && quiet < loud * enterRatio) {
                mode = if (envL >= envR) 1 else 2
                copyLeft = mode == 1
            }
            else -> if (quiet > loud * exitRatio || (mode == 1) != (envL >= envR)) mode = 0
        }
    }
}
