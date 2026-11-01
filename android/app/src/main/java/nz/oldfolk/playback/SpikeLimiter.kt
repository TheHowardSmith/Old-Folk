package nz.oldfolk.playback

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.AudioProcessor.UnhandledAudioFormatException
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Dips serious spikes (thumps, pops, overloads) in unmastered transfers.
 *
 * An adaptive look-ahead peak limiter: it follows the track's typical peak
 * level (the average of 10 ms block peaks; rises over ~0.3 s, falls over ~3 s)
 * and only acts on peaks more than [thresholdDb] above it. Gain drops smoothly
 * during the [lookaheadMs] look-ahead, so the spike is already turned down
 * when it reaches the output, then recovers over ~60 ms. Peaks quieter than
 * [floorDb] are never touched. Needs no per-track settings, so it works
 * across gapless track changes. Cost: a few operations per sample.
 */
@UnstableApi
class SpikeLimiter(
    private val thresholdDb: Float = 12f,
    private val lookaheadMs: Float = 3f,
    private val floorDb: Float = -20f,
    private val maxReductionDb: Float = 18f,
) : BaseAudioProcessor() {
    private var channels = 0
    private var isFloat = false
    private var look = 0                         // look-ahead in frames
    private var delay = FloatArray(0)            // ring of look * channels samples
    private var delayPos = 0
    private var window = FloatArray(0)           // required gains of the last look+1 frames
    private var dq = IntArray(0)                 // monotonic deque (indices into window)
    private var dqHead = 0
    private var dqSize = 0
    private var frameNo = 0L
    private var blockSize = 0
    private var blockPeak = 0f
    private var blockCount = 0
    private var env = -1f
    private var gain = 1f
    private var attack = 0f
    private var release = 0f
    private val ratio = 10f.pow(thresholdDb / 20f)
    private val floor = 10f.pow(floorDb / 20f)
    private val minGain = 10f.pow(-maxReductionDb / 20f)
    private val envUp = (1 - exp(-10.0 / 300.0)).toFloat()
    private val envDown = (1 - exp(-10.0 / 3000.0)).toFloat()

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        val enc = inputAudioFormat.encoding
        if (enc != C.ENCODING_PCM_16BIT && enc != C.ENCODING_PCM_FLOAT) throw UnhandledAudioFormatException(inputAudioFormat)
        channels = inputAudioFormat.channelCount
        isFloat = enc == C.ENCODING_PCM_FLOAT
        val rate = inputAudioFormat.sampleRate
        look = max(1, (rate * lookaheadMs / 1000f).toInt())
        blockSize = max(1, rate / 100)
        attack = (1 - exp(-4.0 / look)).toFloat()            // ~98% of the way within the look-ahead
        release = (1 - exp(-1.0 / (0.06 * rate))).toFloat()
        return inputAudioFormat
    }

    override fun onFlush() = resetState()

    override fun onReset() = resetState()

    private fun resetState() {
        delay = FloatArray(look * channels)
        delayPos = 0
        window = FloatArray(look + 1)
        dq = IntArray(look + 1)
        dqHead = 0; dqSize = 0
        frameNo = 0
        blockPeak = 0f; blockCount = 0
        env = -1f
        gain = 1f
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (delay.size != look * channels) resetState()
        val bytesPerSample = if (isFloat) 4 else 2
        val frames = inputBuffer.remaining() / (bytesPerSample * channels)
        if (frames == 0) return
        val out = replaceOutputBuffer(frames * bytesPerSample * channels)
        val input = inputBuffer.order(ByteOrder.nativeOrder())
        val frame = FloatArray(channels)
        repeat(frames) {
            var peak = 0f
            for (c in 0 until channels) {
                val v = if (isFloat) input.float else input.short / 32768f
                frame[c] = v
                peak = max(peak, abs(v))
            }
            // Typical peak level, from 10 ms block peaks.
            blockPeak = max(blockPeak, peak)
            if (++blockCount >= blockSize) {
                env = if (env < 0) blockPeak else env + (if (blockPeak > env) envUp else envDown) * (blockPeak - env)
                blockPeak = 0f; blockCount = 0
            }
            val threshold = max(floor, (if (env < 0) 1f else env) * ratio)
            val required = if (peak > threshold) max(minGain, threshold / peak) else 1f

            // Minimum required gain over the look-ahead window (sliding-window minimum).
            val slot = (frameNo % window.size).toInt()
            window[slot] = required
            while (dqSize > 0 && window[dq[(dqHead + dqSize - 1) % dq.size]] >= required) dqSize--
            dq[(dqHead + dqSize) % dq.size] = slot
            dqSize++
            val oldest = frameNo - look
            while (dqSize > 1 && frameAge(dq[dqHead]) < oldest) { dqHead = (dqHead + 1) % dq.size; dqSize-- }
            val target = window[dq[dqHead]]
            gain += (target - gain) * (if (target < gain) attack else release)

            // Output the delayed frame with the current gain; store the new one.
            for (c in 0 until channels) {
                val i = delayPos * channels + c
                val y = delay[i] * gain
                delay[i] = frame[c]
                if (isFloat) out.putFloat(y)
                else out.putShort((y * 32768f).toInt().coerceIn(-32768, 32767).toShort())
            }
            delayPos = (delayPos + 1) % look
            frameNo++
        }
        out.flip()
    }

    /** Absolute frame number of a window slot that is within the last window.size frames. */
    private fun frameAge(slot: Int): Long {
        val size = window.size
        val curSlot = (frameNo % size).toInt()
        val back = ((curSlot - slot) % size + size) % size
        return frameNo - back
    }
}
