package nz.oldfolk.data

import nz.oldfolk.playback.Levels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.pow

class LevelsTest {
    private fun ms(db: Double) = 10.0.pow(db / 10.0)

    @Test
    fun gatingIgnoresSilenceAndQuietPassages() {
        // 10 windows at -20 dB, 10 of near silence, 2 quiet ones at -40 dB.
        val windows = List(10) { ms(-20.0) } + List(10) { ms(-80.0) } + List(2) { ms(-40.0) }
        assertEquals(-20.0, Levels.gatedLoudness(windows)!!, 0.01)
    }

    @Test
    fun silentTrackHasNoReading() {
        assertNull(Levels.gatedLoudness(List(5) { ms(-90.0) }))
    }

    @Test
    fun dbToLinear() {
        assertEquals(0.5f, Levels.dbToLinear(-6.0206f), 0.001f)
        assertEquals(1f, Levels.dbToLinear(0f), 0.0001f)
    }

    @Test
    fun oneSidedTransfersAreDetected() {
        assertEquals(2, nz.oldfolk.playback.Levels.oneSided(0.0, 5.0))
        assertEquals(1, nz.oldfolk.playback.Levels.oneSided(5.0, 0.001))
        assertEquals(0, nz.oldfolk.playback.Levels.oneSided(5.0, 0.5))    // just a quieter channel
        assertEquals(0, nz.oldfolk.playback.Levels.oneSided(0.0, 0.0))
    }
}
