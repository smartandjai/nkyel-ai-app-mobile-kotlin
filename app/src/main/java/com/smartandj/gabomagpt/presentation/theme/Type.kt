// presentation/theme/Type.kt
package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMAGPT TYPOGRAPHY - Design System V3 (OLED-first)
 *  Scale: Major Third 1.25 (base 16sp)
 *  Fonts: Sora (Body) & Outfit (Display)
 * ═══════════════════════════════════════════════════════════════════════════════
 */

// Placeholder FontFamilies (You need to add actual .ttf files to res/font and replace these definitions)
val SoraFamily = FontFamily.SansSerif // Replace with FontFamily(Font(R.font.sora_regular, FontWeight.Normal), Font(R.font.sora_medium, FontWeight.Medium), Font(R.font.sora_semibold, FontWeight.SemiBold))
val OutfitFamily = FontFamily.SansSerif // Replace with FontFamily(Font(R.font.outfit_semibold, FontWeight.SemiBold))
val JetBrainsMonoFamily = FontFamily.Monospace // Replace with actual JetBrains Mono

val GabomaTypography = Typography(
    // ─────────────────────────────────────────────────────────────────────
    // DISPLAY LEVEL - Outfit SemiBold
    // ─────────────────────────────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,       // H1
        lineHeight = 48.sp,
        letterSpacing = (-0.025).sp
    ),
    displayMedium = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,       // H2
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    displaySmall = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,       // H3
        lineHeight = 32.sp,
        letterSpacing = (-0.015).sp
    ),

    // ─────────────────────────────────────────────────────────────────────
    // HEADLINE LEVEL - Outfit SemiBold
    // ─────────────────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,       // Label / H4
        lineHeight = 26.sp,
        letterSpacing = (-0.01).sp
    ),

    // ─────────────────────────────────────────────────────────────────────
    // BODY LEVEL - Sora
    // ─────────────────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.Normal, // Body Large
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.Medium, // Body (texte de chat)
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.Medium, // Small / Meta
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.02.sp
    ),

    // ─────────────────────────────────────────────────────────────────────
    // LABEL LEVEL - Sora
    // ─────────────────────────────────────────────────────────────────────
    labelSmall = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.Normal, // Caption
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// CUSTOM TEXT STYLES - Specialized for GabomaGPT V3 UI/UX
// ─────────────────────────────────────────────────────────────────────────────

val UserMessageStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.SemiBold, // User text is SemiBold
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

val AiMessageStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.Medium, // AI text is Medium
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp
)

val CodeInlineStyle = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp
)

val CodeBlockStyle = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp, // Mobile will override to 13.sp
    lineHeight = 22.sp,
    letterSpacing = 0.sp
)

val EyebrowLabelStyle = TextStyle(
    fontFamily = SoraFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.07.sp // Caps labels have larger tracking
)
