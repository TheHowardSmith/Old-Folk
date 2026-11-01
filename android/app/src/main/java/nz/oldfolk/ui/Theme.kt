package nz.oldfolk.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Warm shellac/paper palette, used when dynamic colour isn't available.
private val Light = lightColorScheme(
    primary = Color(0xFF7A4E1D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDDB8),
    onPrimaryContainer = Color(0xFF2A1700),
    secondary = Color(0xFF6E5B48),
    background = Color(0xFFFFF8F3),
    surface = Color(0xFFFFF8F3),
)
private val Dark = darkColorScheme(
    primary = Color(0xFFF2BC80),
    onPrimary = Color(0xFF462A00),
    primaryContainer = Color(0xFF603F0E),
    onPrimaryContainer = Color(0xFFFFDDB8),
    secondary = Color(0xFFDCC2A9),
    background = Color(0xFF19120C),
    surface = Color(0xFF19120C),
)

@Composable
fun OldFolkTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> Dark
        else -> Light
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
