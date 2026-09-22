package com.smartandj.gabomagpt.presentation.settings

import com.smartandj.gabomagpt.presentation.theme.GabomaThemeType

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  APP THEME — Single source of truth redirect
 *  AppTheme is a typealias for GabomaThemeType (6 themes).
 *  Legacy code can keep using AppTheme without changes.
 * ═══════════════════════════════════════════════════════════════════════════════
 */
typealias AppTheme = GabomaThemeType

/**
 * 5 Accent colors (pétales du logo Gaboma AI)
 */
enum class AccentColor(
    val displayName: String,
    val color: androidx.compose.ui.graphics.Color
) {
    FORET("Forêt Gabonaise", androidx.compose.ui.graphics.Color(0xFF22C55E)),
    OCEAN("Océan Atlantique", androidx.compose.ui.graphics.Color(0xFF38BDF8)),
    SOLEIL("Soleil d'Afrique", androidx.compose.ui.graphics.Color(0xFFFACC15)),
    FLAMME("Flamme Équatoriale", androidx.compose.ui.graphics.Color(0xFFEF4444)),
    NUIT("Nuit Tropicale", androidx.compose.ui.graphics.Color(0xFF7C3AED));

    companion object {
        val default: AccentColor = FORET
    }
}
