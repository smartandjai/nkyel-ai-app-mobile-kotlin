// presentation/theme/Color.kt
package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMAGPT COLOR TOKENS - Design System V3
 *  Palette gabonaise sémantique + Black Panther defaults.
 *  NOTE: GabomaThemeType enum lives in GabomaThemeSystem.kt (single source of truth)
 * ═══════════════════════════════════════════════════════════════════════════════
 */

object GabomaColors {
    // ─────────────────────────────────────────────────────────────────────
    // THEME DEFAULT V3: BLACK PANTHER (OLED/Dark)
    // ─────────────────────────────────────────────────────────────────────
    val BgBlackPanther = Color(0xFF020304)
    val SurfaceBlackPanther = Color(0xFF0A0908)
    val ElevatedBlackPanther = Color(0xFF14130F)
    val InputBlackPanther = Color(0xFF14130F)
    
    // ─────────────────────────────────────────────────────────────────────
    // TEXT HIERARCHY V3
    // ─────────────────────────────────────────────────────────────────────
    val TextPrimary = Color(0xFFEDEAE3)             // Blanc chaud
    val TextSecondary = Color(0xFF8A8378)           // Gris-or désaturé
    val TextTertiary = Color(0xFF5C5648)
    val TextMuted = Color(0xFF3D392E)

    // ─────────────────────────────────────────────────────────────────────
    // ACCENT V3 - Or sablé
    // ─────────────────────────────────────────────────────────────────────
    val AccentBlackPanther = Color(0xFFC5A059)
    val AccentBorder = Color(0x14C5A059) // ~8% opacity
    val AccentHover = Color(0x29C5A059)  // ~16% opacity
    val LoxoEmeraldEnd = Color(0xFF00D4AA)
    val OnyxVioletEnd = Color(0xFF9275FF)
    val NkyelPearlEnd = Color(0xFFF0E8D8)

    // ─────────────────────────────────────────────────────────────────────
    // STATUS COLORS - Palette gabonaise sémantique
    // ─────────────────────────────────────────────────────────────────────
    val ErrorRed = Color(0xFFE0584B)                // Latérite
    val SuccessGreen = Color(0xFF1F9D6B)            // Forêt / Okoumé
    val WarnAmber = Color(0xFFD98E3B)               // Ocre raphia
    val InfoBlue = Color(0xFF5B8DEF)                // Acier

    // ─────────────────────────────────────────────────────────────────────
    // UTILITY COLORS - Glassmorphism & UI Elements
    // ─────────────────────────────────────────────────────────────────────
    val GlassSurface = Color(0x1A020304)            // V3 glass base
    val Divider = Color(0x14C5A059)                 // Subtitle/border 8% accent

    // ─────────────────────────────────────────────────────────────────────
    // LEGACY COMPATIBILITY - Maps for existing code
    // ─────────────────────────────────────────────────────────────────────
    val Background = BgBlackPanther
    val AbyssBlack = BgBlackPanther
    val Primary = AccentBlackPanther
    val Secondary = InfoBlue
    val Tertiary = WarnAmber
    val SurfaceDepth1 = BgBlackPanther
    val SurfaceDepth2 = SurfaceBlackPanther
    val SurfaceDepth3 = ElevatedBlackPanther
    val OnBackground = TextPrimary
    val OnSurface = TextPrimary
    val OnSurfaceVariant = TextSecondary
    val Error = ErrorRed
}

// ─────────────────────────────────────────────────────────────────────────────
// LEGACY EXPORTS (used by older screens)
// ─────────────────────────────────────────────────────────────────────────────
val ThemeBlackPantherBg = GabomaColors.BgBlackPanther
val ThemeBlackPantherElevated = GabomaColors.ElevatedBlackPanther
val ThemeBlackPantherCard = GabomaColors.SurfaceBlackPanther
val PrimaryBlackPanther = GabomaColors.AccentBlackPanther
val TextPrimary = GabomaColors.TextPrimary
val TextSecondary = GabomaColors.TextSecondary
val ZionError = GabomaColors.ErrorRed
val ZionSuccess = GabomaColors.SuccessGreen
val ZionWarning = GabomaColors.WarnAmber

// ─────────────────────────────────────────────────────────────────────────────
// LEGACY THEME BG/ELEVATED (for ThemeHelper.kt compatibility)
// All now map through GabomaThemeDefinitions
// ─────────────────────────────────────────────────────────────────────────────
val ZionTextPrimary = GabomaColors.TextPrimary
val ZionTextSecondary = GabomaColors.TextSecondary
val ZionTextMuted = GabomaColors.TextMuted

val ThemeObsidianBg = Color(0xFF050507)        // Maps to Nuit Lopé bg
val ThemeObsidianElevated = Color(0xFF0D0D12)  // Maps to Nuit Lopé elevated
val ThemeOledBg = Color(0xFF020304)             // Maps to Black Panther bg
val ThemeOledElevated = Color(0xFF0A0908)       // Maps to Black Panther elevated
val ThemeBluNuitBg = Color(0xFF060A14)
val ThemeBluNuitElevated = Color(0xFF0A1020)
val ThemeBlancEmBg = Color(0xFFFAFAF8)         // Maps to Néo Blanc bg
val ThemeBlancEmElevated = Color(0xFFFFFFFF)    // Maps to Néo Blanc elevated

// ─────────────────────────────────────────────────────────────────────────────
// ACCENT COLORS for Models / Tiers (used by AntrScreen, ModelSelectorSheet)
// ─────────────────────────────────────────────────────────────────────────────
val AccentAurata = Color(0xFFC9A84C)           // Gold - Aurata model
val AccentForetBlackPanther = Color(0xFF1F9D6B) // Green - Black Panther / Forêt
val AccentGris = Color(0xFF8A8378)              // Gray - Neutral
val AccentLoxo = Color(0xFF19C37D)              // Emerald - Loxo model
val AccentSonar = Color(0xFF4A8DFF)             // Blue - Sonar model
val LoxoEmeraldEnd = Color(0xFF00D4AA)          // Loxo gradient end
val OnyxVioletEnd = Color(0xFF9275FF)           // Onyx gradient end
val NkyelPearlEnd = Color(0xFFF0E8D8)           // Nkyel gradient end
