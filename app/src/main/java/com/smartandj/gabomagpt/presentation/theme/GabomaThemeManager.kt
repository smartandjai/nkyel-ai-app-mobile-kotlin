// presentation/theme/GabomaThemeManager.kt
package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  COMPOSITION LOCALS - Theme injection into composable tree
 *  Access theme colors anywhere via LocalGabomaTheme.current
 * ═══════════════════════════════════════════════════════════════════════════════
 */

val LocalGabomaTheme = staticCompositionLocalOf<GabomaThemeDefinition> {
    error("GabomaTheme not provided!")
}

val LocalGabomaMarkdownColors = staticCompositionLocalOf<GabomaMarkdownColors> {
    error("GabomaMarkdownColors not provided!")
}

val LocalGabomaUITokens = staticCompositionLocalOf<GabomaUITokens> {
    error("GabomaUITokens not provided!")
}

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  HELPER FUNCTIONS - Get theme properties in composables
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@Composable
fun getGabomaTheme(): GabomaThemeDefinition = LocalGabomaTheme.current

@Composable
fun getMarkdownColors(): GabomaMarkdownColors = LocalGabomaMarkdownColors.current

@Composable
fun getUITokens(): GabomaUITokens = LocalGabomaUITokens.current
