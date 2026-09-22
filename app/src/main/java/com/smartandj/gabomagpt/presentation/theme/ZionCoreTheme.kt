// presentation/theme/ZionCoreTheme.kt
package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smartandj.gabomagpt.presentation.settings.AccentColor

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMAGPT MATERIAL3 THEME - Design System V3
 *  Uses GabomaThemeType as single source of truth (6 themes).
 * ═══════════════════════════════════════════════════════════════════════════════
 */

val LocalGabomaColors = staticCompositionLocalOf<GabomaThemeColors> {
    resolveGabomaColors(GabomaThemeType.BLACK_PANTHER)
}

val GabomaDarkColorScheme = darkColorScheme(
    primary = GabomaColors.Primary,
    onPrimary = GabomaColors.BgBlackPanther,
    primaryContainer = GabomaColors.Primary.copy(alpha = 0.2f),
    onPrimaryContainer = GabomaColors.TextPrimary,
    
    secondary = GabomaColors.Secondary,
    onSecondary = GabomaColors.BgBlackPanther,
    secondaryContainer = GabomaColors.Secondary.copy(alpha = 0.2f),
    onSecondaryContainer = GabomaColors.Secondary,
    
    tertiary = GabomaColors.Tertiary,
    onTertiary = GabomaColors.BgBlackPanther,
    tertiaryContainer = GabomaColors.Tertiary.copy(alpha = 0.2f),
    onTertiaryContainer = GabomaColors.Tertiary,
    
    background = GabomaColors.Background,
    onBackground = GabomaColors.OnBackground,
    
    surface = GabomaColors.SurfaceDepth1,
    onSurface = GabomaColors.OnSurface,
    surfaceVariant = GabomaColors.SurfaceDepth3,
    onSurfaceVariant = GabomaColors.OnSurfaceVariant,
    
    surfaceTint = GabomaColors.Primary,
    inverseSurface = GabomaColors.TextPrimary,
    inverseOnSurface = GabomaColors.BgBlackPanther,
    inversePrimary = GabomaColors.BgBlackPanther,
    
    error = GabomaColors.Error,
    onError = GabomaColors.BgBlackPanther,
    errorContainer = GabomaColors.Error.copy(alpha = 0.1f),
    onErrorContainer = GabomaColors.Error,
    
    outline = GabomaColors.Divider,
    outlineVariant = GabomaColors.GlassSurface
)

@Composable
fun GabomaAppTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalGabomaColors provides resolveGabomaColors(GabomaThemeType.BLACK_PANTHER)
    ) {
        MaterialTheme(
            colorScheme = GabomaDarkColorScheme,
            typography = GabomaTypography,
            shapes = RoundedCornerShapes,
            content = content
        )
    }
}

val LocalNkyelColors = LocalGabomaColors

typealias NkyelThemeColors = GabomaThemeColors

object NkyelTheme {
    val colors: GabomaThemeColors
        @Composable get() = LocalGabomaColors.current
}

/**
 * Singleton accessor for theme colors.
 * Usage: GabomaTheme.colors.primary, GabomaTheme.colors.background, etc.
 */
object GabomaTheme {
    val colors: GabomaThemeColors
        @Composable get() = LocalGabomaColors.current
}

/**
 * Shape system with rounded corners progression (V3 scale)
 */
val RoundedCornerShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),  // radius-sm
    small = RoundedCornerShape(10.dp),      // radius-md
    medium = RoundedCornerShape(14.dp),     // radius-lg
    large = RoundedCornerShape(20.dp),      // radius-xl
    extraLarge = RoundedCornerShape(32.dp)
)

// ─────────────────────────────────────────────────────────────────────────────
// THEME COLORS — Resolved from GabomaThemeType (single source of truth)
// ─────────────────────────────────────────────────────────────────────────────

data class GabomaThemeColors(
    val background: Color,
    val elevated: Color,
    val card: Color,
    val primary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
    val error: Color = GabomaColors.ErrorRed,
    val success: Color = GabomaColors.SuccessGreen,
    val isLight: Boolean = false
)

fun resolveGabomaColors(theme: GabomaThemeType): GabomaThemeColors {
    val def = GabomaThemeDefinitions.getTheme(theme)
    return GabomaThemeColors(
        background = def.backgroundColor,
        elevated = def.cardColor,
        card = def.surfaceColor,
        primary = def.accentPrimary,
        textPrimary = def.textPrimary,
        textSecondary = def.textSecondary,
        textMuted = def.textTertiary,
        border = def.accentPrimary.copy(alpha = 0.08f),
        success = GabomaColors.SuccessGreen,
        isLight = theme.isLight
    )
}

fun resolveColorScheme(theme: GabomaThemeType, accent: AccentColor): ColorScheme {
    val g = resolveGabomaColors(theme)
    val accentColor = accent.color
    
    return if (g.isLight) {
        lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            background = g.background,
            onBackground = g.textPrimary,
            surface = g.elevated,
            onSurface = g.textPrimary,
            surfaceVariant = g.card,
            onSurfaceVariant = g.textSecondary,
            secondary = accentColor,
            onSecondary = Color.White,
            outline = g.border
        )
    } else {
        darkColorScheme(
            primary = accentColor,
            onPrimary = g.background,
            background = g.background,
            onBackground = g.textPrimary,
            surface = g.elevated,
            onSurface = g.textPrimary,
            surfaceVariant = g.card,
            onSurfaceVariant = g.textSecondary,
            secondary = accentColor,
            onSecondary = g.background,
            outline = g.border
        )
    }
}

@Composable
fun ZionCoreTheme(
    theme: NkyelThemeType? = null,
    accent: AccentColor = AccentColor.FORET,
    gabomaTheme: NkyelThemeDefinition? = null,
    nkyelTheme: NkyelThemeDefinition? = null,
    content: @Composable () -> Unit
) {
    val effectiveTheme = nkyelTheme ?: gabomaTheme
    if (effectiveTheme != null) {
        // V3 path: full NkyelThemeDefinition available
        val isLight = effectiveTheme.type.isLight
        val colorScheme = if (isLight) {
            lightColorScheme(
                primary = effectiveTheme.accentPrimary,
                onPrimary = Color.White,
                background = effectiveTheme.backgroundColor,
                onBackground = effectiveTheme.textPrimary,
                surface = effectiveTheme.surfaceColor,
                onSurface = effectiveTheme.textPrimary,
                surfaceVariant = effectiveTheme.cardColor,
                onSurfaceVariant = effectiveTheme.textSecondary,
                outline = effectiveTheme.uiTokens.sidebarDivider
            )
        } else {
            darkColorScheme(
                primary = effectiveTheme.accentPrimary,
                onPrimary = effectiveTheme.backgroundColor,
                background = effectiveTheme.backgroundColor,
                onBackground = effectiveTheme.textPrimary,
                surface = effectiveTheme.surfaceColor,
                onSurface = effectiveTheme.textPrimary,
                surfaceVariant = effectiveTheme.cardColor,
                onSurfaceVariant = effectiveTheme.textSecondary,
                outline = effectiveTheme.uiTokens.sidebarDivider
            )
        }

        CompositionLocalProvider(
            LocalNkyelTheme provides effectiveTheme,
            LocalNkyelMarkdownColors provides effectiveTheme.markdownColors,
            LocalNkyelUITokens provides effectiveTheme.uiTokens,
            LocalNkyelColors provides resolveGabomaColors(effectiveTheme.type)
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = NkyelTypography,
                shapes = RoundedCornerShapes,
                content = content
            )
        }
    } else {
        // Legacy path: resolve from NkyelThemeType
        val currentTheme = theme ?: NkyelThemeType.BLACK_PANTHER
        val gabomaColors = resolveGabomaColors(currentTheme)
        val colorScheme = resolveColorScheme(currentTheme, accent)

        CompositionLocalProvider(
            LocalNkyelColors provides gabomaColors
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = NkyelTypography,
                shapes = RoundedCornerShapes,
                content = content
            )
        }
    }
}

