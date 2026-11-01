package nz.oldfolk.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Extra colours for the radio-cabinet details, after a 1950s valve set:
 * dark glass dial with a pale scale and red needle, cloth grille, bakelite
 * knobs with brass rings, ivory push-key, green "magic eye", walnut cabinet.
 */
@Immutable
data class RadioColors(
    val dialFace: Color,
    val dialFaceBottom: Color,
    val dialInk: Color,
    val needle: Color,
    val glow: Color,          // amber: "live" accents (current row, selection)
    val liveText: Color,      // amber text on highlighted rows (contrast-safe)
    val eye: Color,           // magic-eye green
    val brass: Color,         // section labels, trim
    val cloth: Color,         // grille panel
    val grille: Color,        // grille lattice lines
    val knob: Color,          // bakelite
    val knobIcon: Color,
    val ivory: Color,         // play key
    val walnut: Color,        // cabinet band (bottom bar)
    val onWalnut: Color,
    val card: Color,
)

// Daylight: ivory bakelite, walnut and brass.
private val LightScheme = lightColorScheme(
    primary = Color(0xFF6B3E1F),
    onPrimary = Color(0xFFFFF8EC),
    primaryContainer = Color(0xFFEAD7BC),
    onPrimaryContainer = Color(0xFF2B1A0C),
    secondary = Color(0xFF8C6B2F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFE2C4),
    onSecondaryContainer = Color(0xFF2E2208),
    tertiary = Color(0xFFB8641A),
    background = Color(0xFFF4ECDC),
    onBackground = Color(0xFF2B1D12),
    surface = Color(0xFFF4ECDC),
    onSurface = Color(0xFF2B1D12),
    surfaceVariant = Color(0xFFE7DBC3),
    onSurfaceVariant = Color(0xFF5A4733),
    surfaceContainer = Color(0xFFEDE2CD),
    surfaceContainerLow = Color(0xFFF8F1E3),
    surfaceContainerHigh = Color(0xFFE9DDC6),
    outline = Color(0xFFB9A684),
    outlineVariant = Color(0xFFD8C9AB),
    error = Color(0xFFA2382B),
)
private val LightRadio = RadioColors(
    dialFace = Color(0xFF2E251B),
    dialFaceBottom = Color(0xFF1C1610),
    dialInk = Color(0xFFDCE4C4),
    needle = Color(0xFFE0482C),
    glow = Color(0xFFC8892E),
    liveText = Color(0xFF8A5410),
    eye = Color(0xFF8FDB5E),
    brass = Color(0xFF7A5A1E),          // ~5.5:1 on the cream background
    cloth = Color(0xFFE6D5B6),
    grille = Color(0x99C2AB84),
    knob = Color(0xFF2E241B),
    knobIcon = Color(0xFFF1E6D0),
    ivory = Color(0xFFF5EDDC),
    walnut = Color(0xFF4A2F1E),
    onWalnut = Color(0xFFF1E3C8),
    card = Color(0xFFF9F2E4),
)

// Evening: dark walnut with the valves glowing amber.
private val DarkScheme = darkColorScheme(
    primary = Color(0xFFF0B565),
    onPrimary = Color(0xFF3D2300),
    primaryContainer = Color(0xFF4A3216),
    onPrimaryContainer = Color(0xFFFFDDB0),
    secondary = Color(0xFFCDAE6E),
    onSecondary = Color(0xFF392A05),
    secondaryContainer = Color(0xFF3A2E1C),
    onSecondaryContainer = Color(0xFFEFDDB8),
    tertiary = Color(0xFFFFB45C),
    background = Color(0xFF16100B),
    onBackground = Color(0xFFEDE0C8),
    surface = Color(0xFF16100B),
    onSurface = Color(0xFFEDE0C8),
    surfaceVariant = Color(0xFF2D2219),
    onSurfaceVariant = Color(0xFFC9B79B),
    surfaceContainer = Color(0xFF211810),
    surfaceContainerLow = Color(0xFF1B140E),
    surfaceContainerHigh = Color(0xFF2A1F16),
    outline = Color(0xFF6E5A43),
    outlineVariant = Color(0xFF3E3125),
    error = Color(0xFFFFB4A6),
)
private val DarkRadio = RadioColors(
    dialFace = Color(0xFF261E16),
    dialFaceBottom = Color(0xFF130E09),
    dialInk = Color(0xFFD6DFBC),
    needle = Color(0xFFFF6A3D),
    glow = Color(0xFFFFA53A),
    liveText = Color(0xFFFFB45C),
    eye = Color(0xFF8FDB5E),
    brass = Color(0xFFC9A867),
    cloth = Color(0xFF221A12),
    grille = Color(0x1FE8C98A),
    knob = Color(0xFF15100B),
    knobIcon = Color(0xFFEDE0C8),
    ivory = Color(0xFFE9DFC8),
    walnut = Color(0xFF24180F),
    onWalnut = Color(0xFFEDE0C8),
    card = Color(0xFF241B12),
)

val LocalRadioColors = staticCompositionLocalOf { LightRadio }

private val base = Typography()
private val Serif = FontFamily.Serif
private val OldFolkTypography = base.copy(
    headlineMedium = base.headlineMedium.copy(fontFamily = Serif, fontWeight = FontWeight.Medium),
    headlineSmall = base.headlineSmall.copy(fontFamily = Serif, fontWeight = FontWeight.Medium, lineHeight = 30.sp),
    titleLarge = base.titleLarge.copy(fontFamily = Serif, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp),
    titleMedium = base.titleMedium.copy(fontFamily = Serif),
    labelSmall = base.labelSmall.copy(letterSpacing = 1.2.sp),
)

/** Small-caps style label used on the "station plate" and section headings. */
val PlateLabel = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)

/** Track and playlist titles in lists. */
val TrackTitle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 17.sp, lineHeight = 22.sp)

fun plural(n: Int, one: String, many: String = one + "s") = "%,d %s".format(n, if (n == 1) one else many)

@Composable
fun OldFolkTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalRadioColors provides if (dark) DarkRadio else LightRadio) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = OldFolkTypography,
            content = content,
        )
    }
}
