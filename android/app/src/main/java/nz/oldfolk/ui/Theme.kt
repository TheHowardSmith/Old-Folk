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
    val woodHi: Color,        // cabinet wood, top to bottom
    val wood: Color,
    val woodLo: Color,
    val band: Color,          // header / bottom bar: oxblood bakelite
    val bandLo: Color,
    val onBand: Color,
    val bandLine: Color,      // deco scallop lines
    val voxBase: Color,       // grille cloth
    val voxFawn: Color,
    val voxMid: Color,
    val voxHi: Color,
    val knobFluteHi: Color,   // fluted bakelite knobs
    val knobFluteLo: Color,
    val knobCap: Color,
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
    dialFace = Color(0xFF2A1B10),          // smoked dial glass
    dialFaceBottom = Color(0xFF170E07),
    dialInk = Color(0xFFF3E6CB),
    needle = Color(0xFFFF7A4A),
    glow = Color(0xFFC8892E),
    liveText = Color(0xFF8A5410),
    eye = Color(0xFF8FDB5E),
    brass = Color(0xFF7A5A1E),          // ~5.5:1 on the cream background
    cloth = Color(0xFFE6D5B6),
    grille = Color(0x99C2AB84),
    knob = Color(0xFF6B4A30),           // brown bakelite
    knobIcon = Color(0xFFF6EBD5),
    ivory = Color(0xFFF2E8D3),
    walnut = Color(0xFF5A3A24),
    onWalnut = Color(0xFFF3E6CB),
    card = Color(0xFFF9F2E4),
    woodHi = Color(0xFF8A5A33),
    wood = Color(0xFF6E4426),
    woodLo = Color(0xFF4E2E17),
    band = Color(0xFF5C1E16),
    bandLo = Color(0xFF48160F),
    onBand = Color(0xFFF6E7C8),
    bandLine = Color(0x33E3C27A),
    voxBase = Color(0xFF5A3D25),
    voxFawn = Color(0xFFD3B07C),
    voxMid = Color(0xFF7B5735),
    voxHi = Color(0xFF9C774C),
    knobFluteHi = Color(0xFF7A5536),
    knobFluteLo = Color(0xFF3E2615),
    knobCap = Color(0xFF5E3E25),
)

// Evening: dark walnut with the valves glowing amber.
private val DarkScheme = darkColorScheme(
    primary = Color(0xFFF0B565),
    onPrimary = Color(0xFF3D2300),
    primaryContainer = Color(0xFF4A3216),
    onPrimaryContainer = Color(0xFFFFDDB0),
    secondary = Color(0xFFCDAE6E),
    onSecondary = Color(0xFF392A05),
    secondaryContainer = Color(0xFF433322),
    onSecondaryContainer = Color(0xFFEFDDB8),
    tertiary = Color(0xFFFFB45C),
    background = Color(0xFF1F160F),
    onBackground = Color(0xFFEDE0C8),
    surface = Color(0xFF1F160F),
    onSurface = Color(0xFFEDE0C8),
    surfaceVariant = Color(0xFF35271C),
    onSurfaceVariant = Color(0xFFC9B79B),
    surfaceContainer = Color(0xFF2A1E15),
    surfaceContainerLow = Color(0xFF251B13),
    surfaceContainerHigh = Color(0xFF33261B),
    outline = Color(0xFF6E5A43),
    outlineVariant = Color(0xFF3E3125),
    error = Color(0xFFFFB4A6),
)
private val DarkRadio = RadioColors(
    dialFace = Color(0xFF1C120A),
    dialFaceBottom = Color(0xFF0E0804),
    dialInk = Color(0xFFF0DDB8),
    needle = Color(0xFFFF7A4A),
    glow = Color(0xFFFFA53A),
    liveText = Color(0xFFFFB45C),
    eye = Color(0xFF8FDB5E),
    brass = Color(0xFFCDAE6E),
    cloth = Color(0xFF33251A),
    grille = Color(0x26E8C98A),
    knob = Color(0xFF4E3624),
    knobIcon = Color(0xFFF0E2C8),
    ivory = Color(0xFFE6DAC0),
    walnut = Color(0xFF3B281A),
    onWalnut = Color(0xFFF0E2C8),
    card = Color(0xFF2E2117),
    woodHi = Color(0xFF6A4428),
    wood = Color(0xFF52331C),
    woodLo = Color(0xFF3A2312),
    band = Color(0xFF3C130E),
    bandLo = Color(0xFF2C0D09),
    onBand = Color(0xFFF6E7C8),
    bandLine = Color(0x24E3C27A),
    voxBase = Color(0xFF3A2716),
    voxFawn = Color(0xFFA98A5C),
    voxMid = Color(0xFF5A3F25),
    voxHi = Color(0xFF6F5333),
    knobFluteHi = Color(0xFF644530),
    knobFluteLo = Color(0xFF2C1A0D),
    knobCap = Color(0xFF4A301C),
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
