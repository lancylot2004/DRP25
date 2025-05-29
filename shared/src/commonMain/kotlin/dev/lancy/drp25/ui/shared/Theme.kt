package dev.lancy.drp25.ui.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

internal val lightColourScheme = lightColorScheme(
    primary = Color(0xFF3B6755),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF96C5B0),
    onPrimaryContainer = Color(0xFF265342),
    secondary = Color(0xFF755757),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDFB9B9),
    onSecondaryContainer = Color(0xFF654849),
    tertiary = Color(0xFF6E595D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEFD3D7),
    onTertiaryContainer = Color(0xFF6F5A5D),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    background = Color(0xFFF9FAF7),
    onBackground = Color(0xFF1A1C1B),
    surface = Color(0xFFF9FAF7),
    onSurface = Color(0xFF1A1C1B),
    surfaceVariant = Color(0xFFDCE4DE),
    onSurfaceVariant = Color(0xFF414944),
    outline = Color(0xFF717974),
    outlineVariant = Color(0xFFC0C8C3),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2E312F),
    inverseOnSurface = Color(0xFFF0F1EE),
    inversePrimary = Color(0xFFA1D1BB),
    surfaceDim = Color(0xFFD9DAD7),
    surfaceBright = Color(0xFFF9FAF7),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF3F4F1),
    surfaceContainer = Color(0xFFEDEEEB),
    surfaceContainerHigh = Color(0xFFE8E8E5),
    surfaceContainerHighest = Color(0xFFE2E3E0),
)

internal val darkColourScheme = darkColorScheme(
    primary = Color(0xFFB1E1CB),
    onPrimary = Color(0xFF063829),
    primaryContainer = Color(0xFF96C5B0),
    onPrimaryContainer = Color(0xFF265342),
    secondary = Color(0xFFFCD5D4),
    onSecondary = Color(0xFF422A2A),
    secondaryContainer = Color(0xFFDFB9B9),
    onSecondaryContainer = Color(0xFF654849),
    tertiary = Color(0xFFFFF4F4),
    onTertiary = Color(0xFF3D2C2F),
    tertiaryContainer = Color(0xFFEFD3D7),
    onTertiaryContainer = Color(0xFF6F5A5D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF111412),
    onBackground = Color(0xFFE2E3E0),
    surface = Color(0xFF111412),
    onSurface = Color(0xFFE2E3E0),
    surfaceVariant = Color(0xFF414944),
    onSurfaceVariant = Color(0xFFC0C8C3),
    outline = Color(0xFF8A938D),
    outlineVariant = Color(0xFF414944),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE2E3E0),
    inverseOnSurface = Color(0xFF2E312F),
    inversePrimary = Color(0xFF3B6755),
    surfaceDim = Color(0xFF111412),
    surfaceBright = Color(0xFF373A38),
    surfaceContainerLowest = Color(0xFF0C0F0D),
    surfaceContainerLow = Color(0xFF1A1C1B),
    surfaceContainer = Color(0xFF1E201F),
    surfaceContainerHigh = Color(0xFF282B29),
    surfaceContainerHighest = Color(0xFF333534),
)

@Composable
private fun appTypography(): Typography = Typography().run {
    val fontFamily = FontFamily.Default
    val styleFactory = { spSize: Float, weight: FontWeight ->
        TextStyle(
            color = Color.White,
            fontFamily = fontFamily,
            fontSize = TextUnit(spSize, TextUnitType.Sp),
            fontWeight = weight
        )
    }

    copy(
        titleLarge = styleFactory(40f, FontWeight.Bold),
        titleMedium = styleFactory(32f, FontWeight.Bold),
        titleSmall = styleFactory(22f, FontWeight.Bold),
        labelLarge = styleFactory(20f, FontWeight.SemiBold),
        labelMedium = styleFactory(16f, FontWeight.SemiBold),
        labelSmall = styleFactory(14f, FontWeight.SemiBold),
        bodyLarge = styleFactory(20f, FontWeight.Normal),
        bodyMedium = styleFactory(16f, FontWeight.Normal),
        bodySmall = styleFactory(14f, FontWeight.Normal),
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) = MaterialTheme(
    colorScheme = if (darkTheme) { darkColourScheme } else { lightColourScheme },
    typography = appTypography(),
    content = content
)
