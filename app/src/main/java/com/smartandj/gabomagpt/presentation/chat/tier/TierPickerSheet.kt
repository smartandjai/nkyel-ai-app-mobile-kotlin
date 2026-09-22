package com.smartandj.gabomagpt.presentation.chat.tier

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeDefinition

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMA AI — TIER PICKER (Bottom Sheet / Modal)
 *  Sélecteur de Vecteur de Force — aligné sur le web TierPicker.tsx
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TierPickerSheet(
    isVisible: Boolean,
    selectedTier: GabomaTier,
    onSelect: (GabomaTier) -> Unit,
    onDismiss: () -> Unit,
    theme: GabomaThemeDefinition
) {
    if (!isVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = theme.cardColor,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(theme.textTertiary)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Text(
                text = "Vecteur de Force",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = theme.textPrimary,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            // Tier list
            GabomaTier.all.forEach { tier ->
                val isSelected = selectedTier == tier
                val isAvailable = tier.isAvailable

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isAvailable) {
                                Modifier.clickable {
                                    onSelect(tier)
                                    onDismiss()
                                }
                            } else Modifier
                        ),
                    color = when {
                        isSelected -> theme.accentPrimary.copy(alpha = 0.10f)
                        else -> Color.Transparent
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: dot + label + description
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (!isAvailable) Modifier else Modifier
                                )
                        ) {
                            // Tier dot
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isAvailable) tier.accentColor
                                        else tier.accentColor.copy(alpha = 0.4f)
                                    )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = tier.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isAvailable) theme.textPrimary
                                            else theme.textPrimary.copy(alpha = 0.4f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = tier.description,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = if (isAvailable) theme.textSecondary
                                            else theme.textSecondary.copy(alpha = 0.4f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Right: badge
                        if (isAvailable) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = theme.accentPrimary.copy(alpha = 0.06f)
                            ) {
                                Text(
                                    text = tier.badgeLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = theme.accentPrimary,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFE0584B).copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = "Verrouillé",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFE0584B).copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
