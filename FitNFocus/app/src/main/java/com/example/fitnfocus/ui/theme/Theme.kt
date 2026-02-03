package com.example.fitnfocus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun FitNFocusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        FitNFocusDarkColorScheme
    } else {
        FitNFocusLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FitNFocusTypography,
        shapes = FitNFocusShapes,
        content = content
    )
}