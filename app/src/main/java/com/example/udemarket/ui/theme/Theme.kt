package com.example.udemarket.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UdeMarketColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.Black,
    secondary = AccentPurple,
    onSecondary = Color.White,
    background = Color.Black,
    surface = SurfaceColor,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun UdeMarketTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = UdeMarketColorScheme,
        typography = Typography,
        content = content
    )
}
