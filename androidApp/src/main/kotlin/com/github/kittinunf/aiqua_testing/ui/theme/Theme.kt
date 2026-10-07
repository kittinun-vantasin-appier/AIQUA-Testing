package com.github.kittinunf.aiqua_testing.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colors come from the Accessible Palette ramps: green (brand), slate, yellow, red and gray.
private val LightColors = lightColorScheme(
    primary = Color(0xFF3D863E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC2EEBC),
    onPrimaryContainer = Color(0xFF1E361D),
    inversePrimary = Color(0xFF7EDB7B),
    secondary = Color(0xFF545F71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0E3E8),
    onSecondaryContainer = Color(0xFF2C3139),
    tertiary = Color(0xFF91732F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDF89),
    onTertiaryContainer = Color(0xFF3B2E18),
    error = Color(0xFFC05648),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD2),
    onErrorContainer = Color(0xFF4B2620),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1B1B1B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1B),
    surfaceVariant = Color(0xFFE2E2E2),
    onSurfaceVariant = Color(0xFF5E5E5E),
    surfaceTint = Color(0xFF3D863E),
    inverseSurface = Color(0xFF303030),
    inverseOnSurface = Color(0xFFF1F1F1),
    outline = Color(0xFF919191),
    outlineVariant = Color(0xFFC6C6C6),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFE2E2E2),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = Color(0xFFF1F1F1),
    surfaceContainerHigh = Color(0xFFEAEAEA),
    surfaceContainerHighest = Color(0xFFE2E2E2),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7EDB7B),
    onPrimary = Color(0xFF1E361D),
    primaryContainer = Color(0xFF336A33),
    onPrimaryContainer = Color(0xFFC2EEBC),
    inversePrimary = Color(0xFF3D863E),
    secondary = Color(0xFFC1C7D1),
    onSecondary = Color(0xFF2C3139),
    secondaryContainer = Color(0xFF3F4754),
    onSecondaryContainer = Color(0xFFE0E3E8),
    tertiary = Color(0xFFEDC144),
    onTertiary = Color(0xFF3B2E18),
    tertiaryContainer = Color(0xFF735B28),
    onTertiaryContainer = Color(0xFFFFDF89),
    error = Color(0xFFFFB4A5),
    onError = Color(0xFF4B2620),
    errorContainer = Color(0xFF97463A),
    onErrorContainer = Color(0xFFFFDAD2),
    background = Color(0xFF1B1B1B),
    onBackground = Color(0xFFE2E2E2),
    surface = Color(0xFF1B1B1B),
    onSurface = Color(0xFFE2E2E2),
    surfaceVariant = Color(0xFF474747),
    onSurfaceVariant = Color(0xFFC6C6C6),
    surfaceTint = Color(0xFF7EDB7B),
    inverseSurface = Color(0xFFE2E2E2),
    inverseOnSurface = Color(0xFF303030),
    outline = Color(0xFF919191),
    outlineVariant = Color(0xFF474747),
    surfaceBright = Color(0xFF474747),
    surfaceDim = Color(0xFF1B1B1B),
    surfaceContainerLowest = Color(0xFF141414),
    surfaceContainerLow = Color(0xFF222222),
    surfaceContainer = Color(0xFF282828),
    surfaceContainerHigh = Color(0xFF303030),
    surfaceContainerHighest = Color(0xFF3A3A3A),
)

@Composable
fun GroceryTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
