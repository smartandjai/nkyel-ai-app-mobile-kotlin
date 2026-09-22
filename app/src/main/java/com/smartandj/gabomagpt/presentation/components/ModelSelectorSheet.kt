// presentation/components/ModelSelectorSheet.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.*
import androidx.compose.material.icons.filled.*

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  MODEL SELECTOR SHEET - Bottom sheet for 6 AI model tier selection
 *  Features: Gradient badges per tier, haptic feedback on selection, smooth animations
 * ═══════════════════════════════════════════════════════════════════════════════
 */

data class GabomaModel(
    val id: String,
    val displayName: String,
    val description: String,
    val tier: ModelTier,
    val accentColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val isFree: Boolean = false
)

enum class ModelTier {
    AURATA, WANDANA, ONYX_GRIS, BLACK_PANTHER, NYEL
}

val DEFAULT_MODELS = listOf(
    GabomaModel(
        id = "aurata",
        displayName = "AURATA",
        description = "Free tier - Fast responses for quick tasks",
        tier = ModelTier.AURATA,
        accentColor = GabomaColors.AccentBlackPanther,
        gradientStart = GabomaColors.AccentBlackPanther,
        gradientEnd = GabomaColors.AccentBlackPanther,
        isFree = true
    ),
    GabomaModel(
        id = "wandana",
        displayName = "WANDANA",
        description = "Mode recherche et deep recherche",
        tier = ModelTier.WANDANA,
        accentColor = GabomaColors.AccentBlackPanther,
        gradientStart = GabomaColors.AccentBlackPanther,
        gradientEnd = GabomaColors.LoxoEmeraldEnd
    ),
    GabomaModel(
        id = "onyxgris",
        displayName = "ONYX GRIS",
        description = "Max tier - Premium reasoning and creativity",
        tier = ModelTier.ONYX_GRIS,
        accentColor = GabomaColors.AccentBlackPanther,
        gradientStart = GabomaColors.AccentBlackPanther,
        gradientEnd = GabomaColors.OnyxVioletEnd
    ),
    GabomaModel(
        id = "black_panther",
        displayName = "BLACK PANTHER",
        description = "Agent tier - Advanced orchestration and tool use",
        tier = ModelTier.BLACK_PANTHER,
        accentColor = GabomaColors.AccentBlackPanther,
        gradientStart = GabomaColors.AccentBlackPanther,
        gradientEnd = GabomaColors.AccentBlackPanther
    ),
    GabomaModel(
        id = "nyel",
        displayName = "ÑKYEL",
        description = "Modèle intelligent",
        tier = ModelTier.NYEL,
        accentColor = GabomaColors.AccentBlackPanther,
        gradientStart = GabomaColors.AccentBlackPanther,
        gradientEnd = GabomaColors.NkyelPearlEnd
    )
)

@Composable
fun ModelSelectorSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    selectedModel: String? = null,
    onModelSelected: (GabomaModel) -> Unit,
    models: List<GabomaModel> = DEFAULT_MODELS,
    hapticManager: HapticFeedbackManager? = null,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        scrimColor = GabomaColors.AbyssBlack.copy(alpha = 0.32f),
        containerColor = GabomaColors.SurfaceDepth2,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ─────────────────────────────────────────────────────────────────────
            // HEADER
            // ─────────────────────────────────────────────────────────────────────
            Text(
                text = "Select AI Model",
                style = MaterialTheme.typography.headlineSmall,
                color = GabomaColors.TextPrimary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // ─────────────────────────────────────────────────────────────────────
            // MODEL CARDS
            // ─────────────────────────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(models) { model ->
                    ModelCard(
                        model = model,
                        isSelected = model.id == selectedModel,
                        onClick = {
                            onModelSelected(model)
                            hapticManager?.selectModel()
                            onDismiss()
                        }
                    )
                }
            }

            // Spacer at bottom for scroll
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ModelCard(
    model: GabomaModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        color = GabomaColors.SurfaceDepth3,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                // Model name with gradient effect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = model.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = GabomaColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Tier badge
                    TierBadge(
                        tier = model.tier,
                        accentColor = model.accentColor,
                        isFree = model.isFree
                    )
                }

                // Icons.Filled.Description
                Text(
                    text = model.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GabomaColors.TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Selection indicator (animated checkmark)
            AnimatedContent(
                targetState = isSelected,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                modifier = Modifier.size(32.dp)
            ) { selected ->
                if (selected) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = model.accentColor,
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = model.accentColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(4.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = GabomaColors.SurfaceDepth2,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun TierBadge(
    tier: ModelTier,
    accentColor: Color,
    isFree: Boolean = false
) {
    val badgeText = if (isFree) "FREE" else tier.name
    val badgeColor = if (isFree) GabomaColors.TextTertiary else accentColor

    Surface(
        modifier = Modifier,
        color = badgeColor.copy(alpha = 0.12f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = badgeText,
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

/**
 * Gravity model selector button for chat screen (trigger for bottom sheet)
 */
@Composable
fun ModelSelectorButton(
    selectedModel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val model = DEFAULT_MODELS.find { it.id == selectedModel }
    
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = GabomaColors.SurfaceDepth3
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (model != null) {
            Text(
                text = model.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = model.accentColor,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Text(
                text = "Select Model",
                style = MaterialTheme.typography.labelSmall,
                color = GabomaColors.TextSecondary
            )
        }
    }
}
