package nz.oldfolk.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdatesTest {
    @Test fun comparesVersionsNumerically() {
        assertTrue(Updates.isNewer("0.1.10", "0.1.9"))
        assertTrue(Updates.isNewer("0.2.0", "0.1.9"))
        assertTrue(Updates.isNewer("1.0", "0.9.9"))
        assertFalse(Updates.isNewer("0.1.9", "0.1.9"))
        assertFalse(Updates.isNewer("0.1.8", "0.1.9"))
        assertFalse(Updates.isNewer("0.1.9", "0.1.9-debug"))
    }
}
