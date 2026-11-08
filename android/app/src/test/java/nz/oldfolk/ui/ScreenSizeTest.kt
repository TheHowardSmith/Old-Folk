package nz.oldfolk.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import nz.oldfolk.data.Track
import org.junit.Rule
import org.junit.Test

/** The radio screen on small, large-font and landscape phones: every button must be on screen. */
class ScreenSizeTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.NEXUS_5, maxPercentDifference = 0.1)

    private val track = Track("78_x", "x.mp3", "Will the Circle Be Unbroken (Can the Circle Be Unbroken by and by, Lord)",
        "The Carter Family with Sara, Maybelle and A. P. Carter", 1935, "USA", "oldtime")

    private fun radio(device: DeviceConfig, dark: Boolean = false) {
        paparazzi.unsafeUpdateConfig(deviceConfig = device)
        paparazzi.snapshot {
            OldFolkTheme(dark = dark) {
                AppFrame(Tab.RADIO, "Old Folk Air", showBack = false, showAboutButton = true,
                    onBack = {}, onAbout = {}, onTab = {}) {
                    RadioContent(RadioUi("Radio · all genres", track, "Old-time & Hillbilly", true, false, null,
                        false, false, null, false, positionMs = 74_000, durationMs = 185_000), RadioActions())
                }
            }
        }
    }

    @Test fun smallPhone() = radio(DeviceConfig.NEXUS_5)
    @Test fun smallPhoneLargeFont() = radio(DeviceConfig.NEXUS_5.copy(fontScale = 1.3f), dark = true)
    @Test fun tinyPhone() = radio(DeviceConfig.NEXUS_4.copy(screenHeight = 1066))   // ~ 384 x 533 dp
    @Test fun tallPhone() = radio(DeviceConfig.PIXEL_5)
    @Test fun landscapePhone() = radio(DeviceConfig.PIXEL_5.copy(
        screenWidth = DeviceConfig.PIXEL_5.screenHeight, screenHeight = DeviceConfig.PIXEL_5.screenWidth,
        orientation = ScreenOrientation.LANDSCAPE))
}
