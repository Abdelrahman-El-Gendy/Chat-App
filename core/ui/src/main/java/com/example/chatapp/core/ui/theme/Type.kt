package com.example.chatapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.chatapp.core.ui.R

// ============================================================
// Terra – Typography  (imported from Stitch)
//   Headlines : Literata  – warm serif with personality
//   Body/Label: Nunito Sans – friendly, rounded letterforms
// ============================================================

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

// Literata – headline / display font
private val LiterataFont = GoogleFont("Literata")
val LiterataFontFamily = FontFamily(
    Font(googleFont = LiterataFont, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = LiterataFont, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = LiterataFont, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = LiterataFont, fontProvider = googleFontProvider, weight = FontWeight.Bold),
)

// Nunito Sans – body / label font
private val NunitoSansFont = GoogleFont("Nunito Sans")
val NunitoSansFontFamily = FontFamily(
    Font(googleFont = NunitoSansFont, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = NunitoSansFont, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = NunitoSansFont, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = NunitoSansFont, fontProvider = googleFontProvider, weight = FontWeight.Bold),
)

val Typography = Typography(
    // ── Display ─────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily   = LiterataFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 57.sp,
        lineHeight   = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily   = LiterataFontFamily,
        fontWeight   = FontWeight.Bold,
        fontSize     = 45.sp,
        lineHeight   = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily   = LiterataFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 36.sp,
        lineHeight   = 44.sp,
        letterSpacing = 0.sp
    ),

    // ── Headline ─────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily   = LiterataFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 32.sp,
        lineHeight   = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily   = LiterataFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 28.sp,
        lineHeight   = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily   = LiterataFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 24.sp,
        lineHeight   = 32.sp,
        letterSpacing = 0.sp
    ),

    // ── Title  (Nunito Sans – friendly rounded) ──────────────
    titleLarge = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 22.sp,
        lineHeight   = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ── Body (generous line-height for breathable reading) ───
    bodyLarge = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 26.sp,   // 1.625 – "generous line-height" per design
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 22.sp,   // ≈ 1.57
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 12.sp,
        lineHeight   = 18.sp,
        letterSpacing = 0.4.sp
    ),

    // ── Label ────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily   = NunitoSansFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 11.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.5.sp
    )
)
