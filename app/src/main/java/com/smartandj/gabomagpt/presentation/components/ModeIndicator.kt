package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * ModeIndicator — Shows the current agent execution mode.
 *
 * Three modes:
 *   - Planning (purple) → Research & plan creation
 *   - Standard (gold)   → Full execution with all tools
 *   - Edit (blue)       → Artifact modification
 *
 * Compact chip design that fits in the chat header.
 */
@Composable
fun ModeIndicator(
    mode: String,
    modifier: Modifier = Modifier
) {
    val (icon, label, color) = getModeConfig(mode)

    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "modeColor"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = animatedColor.copy(alpha = 0.12f),
        contentColor = animatedColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(14.dp),
                tint = animatedColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = animatedColor
            )
        }
    }
}

/**
 * TakeoverControl — "Step in" button for human-in-the-loop.
 *
 * Allows the user to pause the agent and take manual control.
 */
@Composable
fun TakeoverControl(
    isAgentRunning: Boolean,
    onTakeover: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isAgentRunning) return

    FilledTonalButton(
        onClick = onTakeover,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.AutoFixHigh,
            contentDescription = "Take over",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Step in",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────

private data class ModeConfig(
    val icon: ImageVector,
    val label: String,
    val color: Color
)

private fun getModeConfig(mode: String): ModeConfig {
    return when (mode.lowercase()) {
        "planning" -> ModeConfig(
            icon = Icons.Filled.Psychology,
            label = "Planning",
            color = Color(0xFF9C27B0) // Purple
        )
        "standard" -> ModeConfig(
            icon = Icons.Filled.AutoFixHigh,
            label = "Standard",
            color = Color(0xFFC5A059) // Gold (Gaboma brand)
        )
        "edit" -> ModeConfig(
            icon = Icons.Filled.Edit,
            label = "Edit",
            color = Color(0xFF2196F3) // Blue
        )
        else -> ModeConfig(
            icon = Icons.Filled.AutoFixHigh,
            label = mode.replaceFirstChar { it.uppercase() },
            color = Color(0xFFC5A059)
        )
    }
}
