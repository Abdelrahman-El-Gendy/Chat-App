package com.example.chatapp.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme

// Primary colors (Purple theme)
val PrimaryLight = Color(0xFF6750A4)
val PrimaryMain = Color(0xFF6200EE)
val PrimaryDark = Color(0xFF3700B3)

// Secondary colors (Teal accent)
val SecondaryLight = Color(0xFF03DAC6)
val SecondaryMain = Color(0xFF018786)
val SecondaryDark = Color(0xFF005F5E)

// Surface colors for light theme
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF3EDF7)
val BackgroundLight = Color(0xFFFFFBFE)

// Surface colors for dark theme
val SurfaceDark = Color(0xFF1C1B1F)
val SurfaceVariantDark = Color(0xFF49454F)
val BackgroundDark = Color(0xFF1C1B1F)

// Message bubble colors
val BubbleOwnLight = Color(0xFF6750A4)
val BubbleOwnDark = Color(0xFFD0BCFF)
val BubbleOtherLight = Color(0xFFF3EDF7)
val BubbleOtherDark = Color(0xFF49454F)

// Semantic colors
val ErrorLight = Color(0xFFB3261E)
val ErrorDark = Color(0xFFF2B8B5)
val SuccessLight = Color(0xFF4CAF50)
val SuccessDark = Color(0xFF81C784)
val WarningLight = Color(0xFFFF9800)
val WarningDark = Color(0xFFFFB74D)

// Text colors
val TextPrimaryLight = Color(0xFF1C1B1F)
val TextSecondaryLight = Color(0xFF49454F)
val TextPrimaryDark = Color(0xFFE6E1E5)
val TextSecondaryDark = Color(0xFFCAC4D0)

// Light color palette
val LightColors = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = PrimaryMain,
    onPrimaryContainer = Color(0xFFFFFFFF),
    inversePrimary = PrimaryDark,
    secondary = SecondaryLight,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = SecondaryMain,
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFF6750A4),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF9381CC),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceTint = PrimaryLight,
    inverseSurface = Color(0xFF313034),
    inverseOnSurface = Color(0xFFF4EFF4),
    outline = TextSecondaryLight,
    outlineVariant = SurfaceVariantLight,
    scrim = Color(0xFF000000)
)

// Dark color palette
val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFFEADDFF),
    primaryContainer = Color(0xFF4A3498),
    onPrimaryContainer = Color(0xFFEADDFF),
    inversePrimary = PrimaryLight,
    secondary = SecondaryDark,
    onSecondary = Color(0xFFC3ECEA),
    secondaryContainer = Color(0xFF295D5C),
    onSecondaryContainer = Color(0xFFC3ECEA),
    tertiary = Color(0xFF7D66CC),
    onTertiary = Color(0xFFEADDFF),
    tertiaryContainer = Color(0xFF331C85),
    onTertiaryContainer = Color(0xFFEADDFF),
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    surfaceTint = PrimaryDark,
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313034),
    outline = TextSecondaryDark,
    outlineVariant = SurfaceVariantDark,
    scrim = Color(0xFF000000)
)
