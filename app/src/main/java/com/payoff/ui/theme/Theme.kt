package com.payoff.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// UPI Colors (PRD UX Principles)
val UpiGreen = Color(0xFF00B050)
val UpiGreenDark = Color(0xFF00893F)
val DeepIndigo = Color(0xFF1E1A3C)
val DeepIndigoLight = Color(0xFF2A2454)
val WarmAmber = Color(0xFFF5A623)
val ErrorRed = Color(0xFFD32F2F)

private val LightColorScheme = lightColorScheme(
    primary = UpiGreen,
    onPrimary = Color.White,
    secondary = DeepIndigo,
    onSecondary = Color.White,
    tertiary = WarmAmber,
    error = ErrorRed,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = UpiGreen,
    onPrimary = Color.White,
    secondary = DeepIndigoLight,
    onSecondary = Color.White,
    tertiary = WarmAmber,
    error = Color(0xFFEF5350),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
)

@Composable
fun PayOffTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // We want strict brand colors, so disable dynamic color by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
