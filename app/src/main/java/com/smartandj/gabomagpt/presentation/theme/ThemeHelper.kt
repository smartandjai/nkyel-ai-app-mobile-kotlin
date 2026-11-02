package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.ui.graphics.Color
import com.smartandj.gabomagpt.presentation.settings.AccentColor

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  THEME HELPER — Resolves colors from GabomaThemeType (single source of truth)
 * ═══════════════════════════════════════════════════════════════════════════════
 */

data class ThemeColors(
    val background: Color,
    val elevated: Color,
    val accent: Color,
    val accentLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color
)

fun getThemeColors(theme: GabomaThemeType, accent: AccentColor): ThemeColors {
    val def = GabomaThemeDefinitions.getTheme(theme)
    val accentColor = accent.color
    val accentLightColor = accentColor.copy(alpha = 0.6f)

    return ThemeColors(
        background = def.backgroundColor,
        elevated = def.cardColor,
        accent = accentColor,
        accentLight = accentLightColor,
        textPrimary = def.textPrimary,
        textSecondary = def.textSecondary,
        textMuted = def.textTertiary
    )
}
