// presentation/theme/NkyelThemeManager.kt
package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  COMPOSITION LOCALS - Theme injection into composable tree
 *  Access theme colors anywhere via LocalNkyelTheme.current
 * ═══════════════════════════════════════════════════════════════════════════════
 */

val LocalNkyelTheme = staticCompositionLocalOf<NkyelThemeDefinition> {
    error("NkyelTheme not provided!")
}

val LocalNkyelMarkdownColors = staticCompositionLocalOf<NkyelMarkdownColors> {
    error("NkyelMarkdownColors not provided!")
}

val LocalNkyelUITokens = staticCompositionLocalOf<NkyelUITokens> {
    error("NkyelUITokens not provided!")
}

// Backward compatibility
val LocalGabomaTheme = LocalNkyelTheme
val LocalGabomaMarkdownColors = LocalNkyelMarkdownColors
val LocalGabomaUITokens = LocalNkyelUITokens

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  HELPER FUNCTIONS - Get theme properties in composables
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@Composable
fun getNkyelTheme(): NkyelThemeDefinition = LocalNkyelTheme.current

@Composable
fun getMarkdownColors(): NkyelMarkdownColors = LocalNkyelMarkdownColors.current

@Composable
fun getUITokens(): NkyelUITokens = LocalNkyelUITokens.current

@Composable
fun getGabomaTheme(): NkyelThemeDefinition = getNkyelTheme()
