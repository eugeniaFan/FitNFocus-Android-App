package com.example.fitnfocus.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme


private val PrimaryLight = Color(0xFF4F46E5)
private val PrimaryDark  = Color(0xFFB8B5FF)

private val SecondaryLight = Color(0xFF14B8A6)
private val SecondaryDark  = Color(0xFF5EEAD4)

private val TertiaryLight = Color(0xFFF59E0B)
private val TertiaryDark  = Color(0xFFFBBF24)

private val BackgroundLight = Color(0xFFF7F7FB)
private val SurfaceLight    = Color(0xFFFFFFFF)
private val SurfaceVariantLight = Color(0xFFEDEDF7)

private val BackgroundDark = Color(0xFF0B0B12)
private val SurfaceDark    = Color(0xFF11111A)
private val SurfaceVariantDark = Color(0xFF1B1B2A)
private val OnLight = Color(0xFF0F172A)
private val OnDark  = Color(0xFFEAEAF2)



// Legacy purple tones
val PurplePrimary = Color(0xFF6750A4)
val PurpleTintBg = Color(0xFFF5F3FA)
val PurpleContainer = Color(0xFFE9E0FC)

val OrangeAccent = Color(0xFFFF9933)
val OrangeSoft = Color(0xFFFFE8D0)

val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceVariantSoft = Color(0xFFF2F0F7)
val OutlineVariantSoft = Color(0xFFDAD6E6)

val TextPrimary = Color(0xFF18181C)
val TextSecondary = Color(0xFF62606E)


val FitNFocusLightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,

    secondary = SecondaryLight,
    onSecondary = Color(0xFF06201D),

    tertiary = TertiaryLight,
    onTertiary = Color(0xFF2A1600),

    background = BackgroundLight,
    onBackground = OnLight,

    surface = SurfaceLight,
    onSurface = OnLight,

    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),

    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),

    error = Color(0xFFB00020),
    onError = Color.White
)

val FitNFocusDarkColorScheme = darkColorScheme(

    primary = PrimaryDark,
    onPrimary = Color(0xFF14142B),

    secondary = SecondaryDark,
    onSecondary = Color(0xFF06201D),

    tertiary = TertiaryDark,
    onTertiary = Color(0xFF2A1600),

    background = BackgroundDark,
    onBackground = OnDark,

    surface = SurfaceDark,
    onSurface = OnDark,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB6B6C6),

    outline = Color(0xFF3A3A52),
    outlineVariant = Color(0xFF2A2A3D),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)