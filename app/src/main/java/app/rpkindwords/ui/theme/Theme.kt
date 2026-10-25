package app.rpkindwords.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Markenfarben passend zum App-Icon (Rose/Orange); bewusst kein Dynamic Color.
private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFFD6245F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDDE4),
    onPrimaryContainer = Color(0xFF5C0A25),
    secondary = Color(0xFFE8590C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE2D0),
    onSecondaryContainer = Color(0xFF4A1A00),
    tertiary = Color(0xFF8B4A62),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF2B1A17),
    surface = Color(0xFFFFF8F5),
    onSurface = Color(0xFF2B1A17),
    surfaceVariant = Color(0xFFF8E1DA),
    onSurfaceVariant = Color(0xFF58413C),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF1EB),
    surfaceContainer = Color(0xFFFFEAE2),
    surfaceContainerHigh = Color(0xFFFCE3DA),
    surfaceContainerHighest = Color(0xFFF8DDD3),
    outline = Color(0xFF8C706A),
    outlineVariant = Color(0xFFDDBFB8),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFFFB1C4),
    onPrimary = Color(0xFF5C0A25),
    primaryContainer = Color(0xFF8C1442),
    onPrimaryContainer = Color(0xFFFFD9E1),
    secondary = Color(0xFFFFB68F),
    onSecondary = Color(0xFF532200),
    secondaryContainer = Color(0xFF773300),
    onSecondaryContainer = Color(0xFFFFDBC9),
    tertiary = Color(0xFFE8B0C4),
    background = Color(0xFF1E1412),
    onBackground = Color(0xFFF5DDD8),
    surface = Color(0xFF1E1412),
    onSurface = Color(0xFFF5DDD8),
    surfaceVariant = Color(0xFF58413C),
    onSurfaceVariant = Color(0xFFDDBFB8),
    surfaceContainerLowest = Color(0xFF190F0D),
    surfaceContainerLow = Color(0xFF271917),
    surfaceContainer = Color(0xFF2C1D1A),
    surfaceContainerHigh = Color(0xFF372724),
    surfaceContainerHighest = Color(0xFF42322F),
    outline = Color(0xFFA68A84),
    outlineVariant = Color(0xFF58413C),
)

@Composable
fun KindWordsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}
