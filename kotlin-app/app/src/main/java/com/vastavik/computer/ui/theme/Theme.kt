package com.vastavik.computer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = VastavikColors.LightPrimary,
    primaryContainer = VastavikColors.LightPrimary.copy(alpha = 0.1f),
    secondary = VastavikColors.LightAccent,
    secondaryContainer = VastavikColors.LightAccent.copy(alpha = 0.1f),
    tertiary = VastavikColors.LightSuccess,
    tertiaryContainer = VastavikColors.LightSuccessContainer,
    error = VastavikColors.LightError,
    errorContainer = VastavikColors.LightErrorContainer,
    background = VastavikColors.LightBackground,
    surface = VastavikColors.LightSurface,
    surfaceVariant = VastavikColors.LightSurfaceVariant,
    onPrimary = Color.White,
    onPrimaryContainer = VastavikColors.LightPrimary,
    onSecondary = Color.White,
    onSecondaryContainer = VastavikColors.LightAccent,
    onTertiary = Color.White,
    onTertiaryContainer = VastavikColors.LightSuccess,
    onError = Color.White,
    onErrorContainer = VastavikColors.LightError,
    onBackground = VastavikColors.LightTextPrimary,
    onSurface = VastavikColors.LightTextPrimary,
    onSurfaceVariant = VastavikColors.LightTextSecondary,
    outline = VastavikColors.LightOutline,
    outlineVariant = VastavikColors.LightOutline.copy(alpha = 0.5f),
    inverseSurface = VastavikColors.DarkSurface,
    inverseOnSurface = VastavikColors.DarkTextPrimary,
    inversePrimary = VastavikColors.DarkPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = VastavikColors.DarkPrimary,
    primaryContainer = VastavikColors.DarkPrimary.copy(alpha = 0.2f),
    secondary = VastavikColors.DarkAccent,
    secondaryContainer = VastavikColors.DarkAccent.copy(alpha = 0.2f),
    tertiary = VastavikColors.DarkSuccess,
    tertiaryContainer = VastavikColors.DarkSuccessContainer,
    error = VastavikColors.DarkError,
    errorContainer = VastavikColors.DarkErrorContainer,
    background = VastavikColors.DarkBackground,
    surface = VastavikColors.DarkSurface,
    surfaceVariant = VastavikColors.DarkSurfaceVariant,
    onPrimary = Color.White,
    onPrimaryContainer = VastavikColors.DarkPrimary,
    onSecondary = Color.White,
    onSecondaryContainer = VastavikColors.DarkAccent,
    onTertiary = Color.White,
    onTertiaryContainer = VastavikColors.DarkSuccess,
    onError = Color.White,
    onErrorContainer = VastavikColors.DarkError,
    onBackground = VastavikColors.DarkTextPrimary,
    onSurface = VastavikColors.DarkTextPrimary,
    onSurfaceVariant = VastavikColors.DarkTextSecondary,
    outline = VastavikColors.DarkOutline,
    outlineVariant = VastavikColors.DarkOutline.copy(alpha = 0.5f),
    inverseSurface = VastavikColors.LightSurface,
    inverseOnSurface = VastavikColors.LightTextPrimary,
    inversePrimary = VastavikColors.LightPrimary
)

@Composable
fun VastavikTheme(
    darkTheme: Boolean = false,
    neoBrutalish: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = VastavikTypography,
        shapes = if (neoBrutalish) Shapes(
            extraSmall = RoundedCornerShape(2.dp),
            small = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(2.dp),
            large = RoundedCornerShape(2.dp),
            extraLarge = RoundedCornerShape(2.dp)
        ) else Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
            extraLarge = RoundedCornerShape(24.dp)
        ),
        content = content
    )
}

val androidx.compose.material3.ColorScheme.appPrimary: Color
    get() = primary
val androidx.compose.material3.ColorScheme.appAccent: Color
    get() = secondary
val androidx.compose.material3.ColorScheme.appBackground: Color
    get() = background
val androidx.compose.material3.ColorScheme.appSurface: Color
    get() = surface
val androidx.compose.material3.ColorScheme.appTextPrimary: Color
    get() = onBackground
val androidx.compose.material3.ColorScheme.appTextSecondary: Color
    get() = onSurfaceVariant