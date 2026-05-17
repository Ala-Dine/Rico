package com.univeloued.rico.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Dark Theme Layout Palette
private val DarkColorScheme = darkColorScheme(
    primary = SeafoamTeal,
    secondary = SoftMint,
    tertiary = MutedTeal,
    background = Color(0xFF121212),      // Dark material surface background
    surface = Color(0xFF1E1E1E),         // Slightly lighter dark surface for cards
    onPrimary = Color(0xFF00201A),       // Dark text inside primary elements
    onSecondary = Color(0xFF002118),     // Dark text inside secondary elements
    onBackground = LightMint,            // Crisp soft-white/mint text on dark mode
    onSurface = LightMint
)

// Light Theme Layout Palette
private val LightColorScheme = lightColorScheme(
    primary = DeepTeal,
    secondary = DarkSage,
    tertiary = SlateGreen,
    background = LightMint,              // Uses your ultra-light color as the background canvas
    surface = Color.White,               // White containers/cards
    onPrimary = Color.White,             // White text inside deep teal buttons
    onSecondary = Color.White,
    onBackground = Color(0xFF00201A),    // Dark green/black text for legibility
    onSurface = Color(0xFF00201A)
)

@Composable
fun RicoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false to force your custom palette over Android's dynamic engine
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Points to your package's Typography.kt file
        content = content
    )
}
