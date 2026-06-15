package com.example.plantify.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PlantifyLightGreen,
    secondary = PlantifyMediumGreen,
    tertiary = PlantifyDarkGreen,
    background = Color(0xFF121212),        // Pure dark background
    surface = Color(0xFF1E1E1E),           // Card surface dark
    surfaceVariant = Color(0xFF2C2C2C),    // Input field background dark
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE0E0E0),      // Teks utama terang
    onSurface = Color(0xFFE0E0E0),         // Teks di dalam card terang
    onSurfaceVariant = Color(0xFFA0A0A0),  // Teks sekunder / placeholder
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF424242),           // Border terlihat di dark
    outlineVariant = Color(0xFF2C2C2C)     // Border tipis
)

private val LightColorScheme = lightColorScheme(
    primary = PlantifyMediumGreen,
    secondary = PlantifyDarkGreen,
    tertiary = PlantifyLightGreen,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F4F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    outline = PlantifyMediumGreen // Added for clearer borders
)

@Composable
fun PlantifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}