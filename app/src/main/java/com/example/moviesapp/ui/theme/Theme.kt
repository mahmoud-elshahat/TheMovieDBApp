package com.example.moviesapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Amber80,
    onPrimary = Navy10,
    primaryContainer = Navy30,
    onPrimaryContainer = Amber80,
    secondary = Teal80,
    onSecondary = Navy10,
    background = Navy10,
    onBackground = White,
    surface = Navy20,
    onSurface = White,
    surfaceVariant = Navy30,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = Amber40,
    onPrimary = White,
    primaryContainer = Grey95,
    onPrimaryContainer = Grey10,
    secondary = Teal40,
    onSecondary = White,
    background = Grey95,
    onBackground = Grey10,
    surface = White,
    onSurface = Grey10,
    surfaceVariant = Grey95,
    error = ErrorRed
)

@Composable
fun MoviesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // disabled so our cinematic palette is always used
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
        typography = Typography,
        content = content
    )
}
