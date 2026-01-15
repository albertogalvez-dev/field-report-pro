package com.fieldreportpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = PendingText,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextMain,
    surface = SurfaceLight,
    onSurface = TextMain,
    surfaceVariant = Color(0xFFF1F1F1),
    outline = SoftDivider
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = PendingText,
    onSecondary = Color.White,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceElevated,
    outline = DarkOutline
)

private val Shapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.Corner16),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.Corner20),
    large = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.Corner28)
)

@Composable
fun FieldReportTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = FieldReportTypography,
        shapes = Shapes,
        content = content
    )
}
