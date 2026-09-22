package com.smartandj.gabomagpt.presentation.chat.tier

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeDefinition

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  TIER CHIP — Inline badge shown in the input bar
 *  Tappable → opens TierPickerSheet
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@Composable
fun TierChip(
    selectedTier: GabomaTier,
    theme: GabomaThemeDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(theme.accentPrimary.copy(alpha = 0.06f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Tier dot
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(selectedTier.accentColor)
        )

        // Tier label
        Text(
            text = selectedTier.badgeLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = theme.accentPrimary,
            letterSpacing = 0.4.sp
        )
    }
}
