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
    background = Color(0xFF0D1B0D),        // Sangat gelap kehijauan
    surface = Color(0xFF152415),           // Permukaan card gelap kehijauan
    surfaceVariant = Color(0xFF1E3020),    // Input field background gelap
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFECF5EC),      // Teks utama di background — sangat terang
    onSurface = Color(0xFFE8F0E8),         // Teks di dalam card — terang
    onSurfaceVariant = Color(0xFFB8D0B8),  // Teks sekunder / placeholder
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF3D5C3D),           // Border terlihat di dark
    outlineVariant = Color(0xFF253825)     // Border tipis
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
    onSurfaceVariant = Color(0xFF757575),
    outline = Color(0xFFCCCCCC),
    outlineVariant = Color(0xFFF0F0F0)
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