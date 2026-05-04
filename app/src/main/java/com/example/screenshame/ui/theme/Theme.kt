package com.example.screenshame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

// colours
val black = Color(0xFF0A0A0A)
val white = Color(0xFFFFFFFF)
val surface = Color(0xFFF7F7F7)
val border = Color(0xFFE5E5E5)
val textSecondary = Color(0xFF6B6B6B)
val red = Color(0xFFE5352A)

private val colorScheme = lightColorScheme(
    primary = black,
    onPrimary = white,
    background = white,
    onBackground = black,
    surface = surface,
    onSurface = black,
    outline = border,
)

// inter as default sans-serif (system default is fine, so use default for body)
val interFamily = FontFamily.Default

// typography
val appTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif, // georgia fallback
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)

@Composable
fun ScreenShameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography,
        content = content
    )
}