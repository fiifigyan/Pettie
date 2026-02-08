package com.pettie.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PetGreen = Color(0xFF4CAF50)
private val PetGreenDark = Color(0xFF388E3C)
private val PetGreenLight = Color(0xFF81C784)
private val PetTeal = Color(0xFF009688)
private val Cream = Color(0xFFF5F5DC)
private val WarmGray = Color(0xFFEEEEEE)

private val LightColorScheme = lightColorScheme(
    primary = PetGreen,
    onPrimary = Color.White,
    primaryContainer = PetGreenLight,
    onPrimaryContainer = PetGreenDark,
    secondary = PetTeal,
    onSecondary = Color.White,
    background = WarmGray,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Cream,
    onSurfaceVariant = Color.DarkGray,
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PetGreenLight,
    onPrimary = Color.Black,
    primaryContainer = PetGreenDark,
    onPrimaryContainer = PetGreenLight,
    secondary = PetTeal,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color.LightGray,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun PettieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PettieTypography,
        content = content
    )
}
