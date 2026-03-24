package com.example.chatapp.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme

// ============================================================
// Terra – Organic Design System  (imported from Stitch)
// North Star: "Rooted Warmth"
// Seed color: #4a7c59  |  Variant: TONAL_SPOT  |  Saturation: 2
// ============================================================

// ----- Primary palette (Forest Green) -----
val TerraGreen          = Color(0xFF4A7C59)   // P-40  – primary (light)
val TerraGreenLight     = Color(0xFF8ECF9E)   // P-80  – primary (dark)
val TerraGreenDim       = Color(0xFF2A6038)   // P-30  – primary container (dark)
val TerraGreenContainer = Color(0xFFD8F0DE)   // P-90  – primary container (light)
val TerraGreenFixed     = Color(0xFFC8E8D0)   // P-90  – primary fixed
val TerraGreenFixedDim  = Color(0xFF8ECF9E)   // P-80  – primary fixed dim
val TerraGreenDeep      = Color(0xFF002110)   // P-10  – on primary fixed

// ----- Secondary palette (Warm Stone) -----
val TerraStone          = Color(0xFF6B6358)   // S-40  – secondary (light)
val TerraStoneLight     = Color(0xFFD4CCBF)   // S-80  – secondary (dark)
val TerraStoneContainer = Color(0xFFF0E8DB)   // S-90  – secondary container (light)
val TerraStoneFixed     = Color(0xFFF0E8DB)   // secondary fixed
val TerraStoneFixedDim  = Color(0xFFD4CCBF)
val TerraStoneDeep      = Color(0xFF1E1A13)   // S-10  – on secondary fixed
val TerraStoneVariant   = Color(0xFF4A4538)   // on secondary fixed variant

// ----- Tertiary palette (Warm Amber) -----
val TerraAmber          = Color(0xFF705C30)   // T-40  – tertiary (light)
val TerraAmberLight     = Color(0xFFDCC48E)   // T-80  – tertiary (dark)
val TerraAmberContainer = Color(0xFFC4A66A)   // T-60  – tertiary container (light)
val TerraAmberFixed     = Color(0xFFF8E0A8)   // T-90  – tertiary fixed
val TerraAmberFixedDim  = Color(0xFFDCC48E)
val TerraAmberDeep      = Color(0xFF221A05)   // T-10
val TerraAmberVariant   = Color(0xFF554020)   // on tertiary container

// ----- Background / Surface (Warm Cream) -----
val TerraCream          = Color(0xFFFAF6F0)   // N-98  – background + surface (light)
val TerraCreamBright    = Color(0xFFFAF6F0)   // surface bright
val TerraCreamLow       = Color(0xFFF5F1EA)   // surface container low
val TerraCreamContainer = Color(0xFFF0ECE4)   // surface container
val TerraCreamHigh      = Color(0xFFEAE6DE)   // surface container high
val TerraCreamHighest   = Color(0xFFE4E0D8)   // surface container highest
val TerraCreamDim       = Color(0xFFDBD7CF)   // surface dim
val TerraCreamPure      = Color(0xFFFFFFFF)   // surface container lowest

// ----- Dark background / surface -----
val TerraDarkBg         = Color(0xFF101510)   // N-6   – background (dark)
val TerraDarkSurface    = Color(0xFF101510)   // N-6   – surface (dark)
val TerraDarkContainer  = Color(0xFF1C211E)   // surface container (dark)
val TerraDarkHigh       = Color(0xFF262B28)   // surface container high (dark)
val TerraDarkHighest    = Color(0xFF303530)   // surface container highest (dark)

// ----- Content on surface -----
val TerraOnBgLight      = Color(0xFF2E3230)   // N-10  – on background (light)
val TerraOnBgDark       = Color(0xFFE0EBE4)   // N-90  – on background (dark)
val TerraOnSurfaceVarL  = Color(0xFF4A4E4A)   // NV-40 – on surface variant (light)
val TerraOnSurfaceVarD  = Color(0xFFC4C8BC)   // NV-80 – on surface variant (dark)

// ----- Outline -----
val TerraOutlineLight   = Color(0xFF74796E)   // NV-50
val TerraOutlineVarL    = Color(0xFFC4C8BC)   // NV-80
val TerraOutlineDark    = Color(0xFF8E9388)   // NV-60
val TerraOutlineVarD    = Color(0xFF4A4E4A)   // NV-40

// ----- Inverse -----
val TerraInverseSurface = Color(0xFF2E3230)   // N-10
val TerraInverseOnSurfL = Color(0xFFF5F0E8)   // N-95
val TerraInverseOnSurfD = Color(0xFF101510)   // N-6

// ----- Error -----
val TerraErrorLight     = Color(0xFFB83230)
val TerraErrorDark      = Color(0xFFFFB4AB)
val TerraErrorContainer = Color(0xFFFFDAD8)
val TerraOnErrorLight   = Color(0xFFFFFFFF)
val TerraOnErrorDark    = Color(0xFF690005)
val TerraOnErrContLight = Color(0xFF690005)
val TerraOnErrContDark  = Color(0xFFFFDAD8)
val TerraErrContDark    = Color(0xFF93000A)

// ----- Chat bubble semantic colors -----
// Own bubble = primary in each theme mode
val BubbleOwnLight      = TerraGreen           // Forest green bubble for sender
val BubbleOwnDark       = TerraGreenLight      // Lighter green in dark mode
val BubbleOtherLight    = TerraCreamContainer  // Warm cream bubble for receiver
val BubbleOtherDark     = TerraDarkContainer   // Dark warm surface

// ============================================================
// Light color scheme
// ============================================================
val LightColors = lightColorScheme(
    primary              = TerraGreen,
    onPrimary            = Color(0xFFFFFFFF),
    primaryContainer     = TerraGreenContainer,
    onPrimaryContainer   = TerraGreenDeep,
    inversePrimary       = TerraGreenLight,
    secondary            = TerraStone,
    onSecondary          = Color(0xFFFFFFFF),
    secondaryContainer   = TerraStoneContainer,
    onSecondaryContainer = TerraStoneDeep,
    tertiary             = TerraAmber,
    onTertiary           = Color(0xFFFFFFFF),
    tertiaryContainer    = TerraAmberFixed,
    onTertiaryContainer  = TerraAmberVariant,
    background           = TerraCream,
    onBackground         = TerraOnBgLight,
    surface              = TerraCream,
    onSurface            = TerraOnBgLight,
    surfaceVariant       = TerraCreamHighest,
    onSurfaceVariant     = TerraOnSurfaceVarL,
    surfaceTint          = TerraGreen,
    inverseSurface       = TerraInverseSurface,
    inverseOnSurface     = TerraInverseOnSurfL,
    outline              = TerraOutlineLight,
    outlineVariant       = TerraOutlineVarL,
    error                = TerraErrorLight,
    onError              = TerraOnErrorLight,
    errorContainer       = TerraErrorContainer,
    onErrorContainer     = TerraOnErrContLight,
    scrim                = Color(0xFF000000)
)

// ============================================================
// Dark color scheme  (M3 inverse tones from Terra seed)
// ============================================================
val DarkColors = darkColorScheme(
    primary              = TerraGreenLight,
    onPrimary            = TerraGreenDeep,
    primaryContainer     = TerraGreenDim,
    onPrimaryContainer   = TerraGreenFixed,
    inversePrimary       = TerraGreen,
    secondary            = TerraStoneLight,
    onSecondary          = TerraStoneDeep,
    secondaryContainer   = TerraStoneVariant,
    onSecondaryContainer = TerraStoneFixed,
    tertiary             = TerraAmberLight,
    onTertiary           = TerraAmberDeep,
    tertiaryContainer    = TerraAmberVariant,
    onTertiaryContainer  = TerraAmberFixed,
    background           = TerraDarkBg,
    onBackground         = TerraOnBgDark,
    surface              = TerraDarkSurface,
    onSurface            = TerraOnBgDark,
    surfaceVariant       = TerraDarkHighest,
    onSurfaceVariant     = TerraOnSurfaceVarD,
    surfaceTint          = TerraGreenLight,
    inverseSurface       = TerraInverseOnSurfL,
    inverseOnSurface     = TerraInverseOnSurfD,
    outline              = TerraOutlineDark,
    outlineVariant       = TerraOutlineVarD,
    error                = TerraErrorDark,
    onError              = TerraOnErrorDark,
    errorContainer       = TerraErrContDark,
    onErrorContainer     = TerraOnErrContDark,
    scrim                = Color(0xFF000000)
)
