package com.example.fitnfocus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun FitNFocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Kein Dynamisches Design
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> FitNFocusDarkColorScheme
        else -> FitNFocusLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FitNFocusTypography,
        shapes = FitNFocusShapes,
        content = content
    )
}