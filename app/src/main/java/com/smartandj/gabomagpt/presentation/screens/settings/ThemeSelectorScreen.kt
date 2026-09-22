// presentation/screens/settings/ThemeSelectorScreen.kt
package com.smartandj.gabomagpt.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.settings.ThemePreferencesManager
import com.smartandj.gabomagpt.presentation.theme.*
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.*

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  THEME SELECTOR SCREEN - Displays 6 themes with live preview
 *  User selects one theme → persisted to DataStore → applied app-wide
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@Composable
fun ThemeSelectorScreen(
    themeManager: ThemePreferencesManager,
    modifier: Modifier = Modifier
) {
    val currentTheme by themeManager.themeFlow.collectAsState(initial = GabomaThemeType.NUIT_LOPE)
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GabomaThemeDefinitions.NuitLope.backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ─────────────────────────────────────────────────────────────────────────
        // HEADER
        // ─────────────────────────────────────────────────────────────────────────
        item {
            Text(
                text = "Choisissez votre thème",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GabomaThemeDefinitions.NuitLope.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // ─────────────────────────────────────────────────────────────────────────
        // THEME CARDS (6 themes)
        // ─────────────────────────────────────────────────────────────────────────
        items(GabomaThemeType.values().toList()) { themeType ->
            val theme = GabomaThemeDefinitions.getTheme(themeType)
            val isSelected = currentTheme == themeType

            ThemeCard(
                theme = theme,
                isSelected = isSelected,
                onClick = {
                    scope.launch {
                        themeManager.setTheme(themeType)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Individual theme preview card
 */
@Composable
fun ThemeCard(
    theme: GabomaThemeDefinition,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(theme.cardColor)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) theme.accentPrimary else theme.textTertiary,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ─────────────────────────────────────────────────────────────────────
            // HEADER: Theme name + selection indicator
            // ─────────────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = theme.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.accentPrimary
                    )
                    Text(
                        text = theme.description,
                        fontSize = 12.sp,
                        color = theme.textSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = theme.accentPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────────────
            // COLOR PREVIEW - Show 5 key colors
            // ─────────────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Background color
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.backgroundColor)
                        .border(1.dp, theme.textTertiary, RoundedCornerShape(6.dp))
                )

                // Surface color
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.surfaceColor)
                        .border(1.dp, theme.textTertiary, RoundedCornerShape(6.dp))
                )

                // Accent primary
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.accentPrimary)
                )

                // Glow color
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.glowColor1)
                )

                // Text color
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(theme.textPrimary)
                )
            }
        }
    }
}

@Composable
fun Modifier.border(
    width: androidx.compose.ui.unit.Dp,
    color: Color,
    shape: RoundedCornerShape
) = this
    .clip(shape)
    .then(
        Modifier.background(color).padding(width)
    )
