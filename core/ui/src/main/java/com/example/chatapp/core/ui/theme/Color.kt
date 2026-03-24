package com.example.chatapp.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme

// ============================================================
// Terra – Organic Design System  (imported from Figma)
// North Star: "Rooted Warmth"
// Seed color: #4A7C59
// ============================================================

// ----- Primary palette (Forest Green) -----
val TerraGreen          = Color(0xFF4A7C59)   // primary (light)
val TerraGreenTint      = Color(0xFF78A886)   // lighter tint for icons/secondary accents
val TerraGreenNeon      = Color(0xFF86FEA7)   // primary (dark) – vibrant neon
val TerraGreenGradEnd   = Color(0xFF3BB668)   // gradient end for dark active elements
val TerraGreenDim       = Color(0xFF2A6038)   // primary container (dark)
val TerraGreenContainer = Color(0xFFD8F0DE)   // primary container (light)
val TerraGreenFixed     = Color(0xFFC8E8D0)   // primary fixed
val TerraGreenDeep      = Color(0xFF002910)   // on primary (dark) / on primary fixed

// ----- Secondary palette (Warm Stone) -----
val TerraStone          = Color(0xFF6B6358)   // secondary (light)
val TerraStoneLight     = Color(0xFFD4CCBF)   // secondary (dark)
val TerraStoneContainer = Color(0xFFF0E8DB)   // secondary container (light)
val TerraStoneFixed     = Color(0xFFF0E8DB)   // secondary fixed
val TerraStoneFixedDim  = Color(0xFFD4CCBF)
val TerraStoneDeep      = Color(0xFF1E1A13)   // on secondary fixed
val TerraStoneVariant   = Color(0xFF4A4538)   // on secondary fixed variant

// ----- Tertiary palette (Warm Amber) -----
val TerraAmber          = Color(0xFF705C30)   // tertiary (light)
val TerraAmberLight     = Color(0xFFDCC48E)   // tertiary (dark)
val TerraAmberContainer = Color(0xFFC4A66A)   // tertiary container (light)
val TerraAmberFixed     = Color(0xFFF8E0A8)   // tertiary fixed
val TerraAmberFixedDim  = Color(0xFFDCC48E)
val TerraAmberDeep      = Color(0xFF221A05)   // T-10
val TerraAmberVariant   = Color(0xFF554020)   // on tertiary container

// ----- Light Background / Surface (Warm Cream) -----
val TerraCream          = Color(0xFFFAF6F0)   // background + surface (light)
val TerraCreamBright    = Color(0xFFFAF6F0)   // surface bright
val TerraCreamLow       = Color(0xFFF5F1EA)   // surface container low / search bar / input
val TerraCreamContainer = Color(0xFFF0ECE4)   // surface container / received bubbles / cards
val TerraCreamHigh      = Color(0xFFEAE6DE)   // surface container high
val TerraCreamHighest   = Color(0xFFE4E0D8)   // surface container highest
val TerraCreamDim       = Color(0xFFDBD7CF)   // surface dim
val TerraCreamPure      = Color(0xFFFFFFFF)   // surface container lowest

// ----- Dark background / surface (from Figma dark frames) -----
val TerraDarkBg         = Color(0xFF0D0F0D)   // background (dark)
val TerraDarkSurface    = Color(0xFF0D0F0D)   // surface (dark)
val TerraDarkLow        = Color(0xFF121412)   // surface container low (dark)
val TerraDarkContainer  = Color(0xFF181A18)   // surface container (dark) / dark nav bar
val TerraDarkHigh       = Color(0xFF1E201E)   // surface container high (dark) / received bubble
val TerraDarkHighest    = Color(0xFF242624)   // surface container highest (dark)

// ----- Content on surface -----
val TerraOnBgLight      = Color(0xFF2E3230)   // on background (light)
val TerraOnBgDark       = Color(0xFFE0EBE4)   // on background (dark)
val TerraOnSurfaceVarL  = Color(0xFF4A4E4A)   // on surface variant (light)
val TerraOnSurfaceVarD  = Color(0xFFABABA8)   // on surface variant (dark) / inactive icons

// ----- Outline -----
val TerraOutlineLight   = Color(0xFF74796E)   // outline (light) / placeholder/icon
val TerraOutlineVarL    = Color(0xFFC4C8BC)   // outline variant (light)
val TerraOutlineDark    = Color(0xFF8E9388)   // outline (dark)
val TerraOutlineVarD    = Color(0xFF4A4E4A)   // outline variant (dark)

// ----- Inverse -----
val TerraInverseSurface = Color(0xFF2E3230)
val TerraInverseOnSurfL = Color(0xFFF5F0E8)
val TerraInverseOnSurfD = Color(0xFF0D0F0D)

// ----- Error -----
val TerraErrorLight     = Color(0xFFB83230)
val TerraErrorDark      = Color(0xFFFF7351)   // warm orange-red per Figma dark
val TerraErrorContainer = Color(0xFFFFDAD8)
val TerraOnErrorLight   = Color(0xFFFFFFFF)
val TerraOnErrorDark    = Color(0xFF450900)
val TerraOnErrContLight = Color(0xFF690005)
val TerraOnErrContDark  = Color(0xFFFFDAD8)
val TerraErrContDark    = Color(0xFF93000A)

// ----- Bottom Navigation -----
val NavInactiveLight    = Color(0xFF78716C)   // inactive icon/text (light)
val NavInactiveDark     = Color(0xFFABABA8)   // inactive icon/text (dark)
val NavBorderLight      = Color(0xFFE7E5E4)   // nav top border (light)

// ----- Chat bubble semantic colors -----
val BubbleOwnLight      = TerraGreen           // Forest green bubble for sender (light)
val BubbleOwnDark       = TerraGreenNeon       // Neon green bubble for sender (dark)
val BubbleOtherLight    = TerraCreamContainer  // Warm cream bubble for receiver (light)
val BubbleOtherDark     = TerraDarkHigh        // #1E201E bubble for receiver (dark)

// ============================================================
// Light color scheme
// ============================================================
val LightColors = lightColorScheme(
    primary              = TerraGreen,
    onPrimary            = Color(0xFFFFFFFF),
    primaryContainer     = TerraGreenContainer,
    onPrimaryContainer   = TerraGreenDeep,
    inversePrimary       = TerraGreenNeon,
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
// Dark color scheme  (Figma dark frames – vibrant neon accent)
// ============================================================
val DarkColors = darkColorScheme(
    primary              = TerraGreenNeon,
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
    surfaceTint          = TerraGreenNeon,
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
