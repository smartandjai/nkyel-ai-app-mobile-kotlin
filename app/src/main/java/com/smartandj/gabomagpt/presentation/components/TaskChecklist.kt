package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartandj.gabomagpt.stream.GabomaStreamEvent

/**
 * TaskChecklist — Manus-style step checklist.
 *
 * Shows the agent's plan steps in real-time:
 *   - [ ] Pending step (outline circle)
 *   - [/] In-progress step (play icon, highlighted)
 *   - [x] Completed step (checkmark, strikethrough)
 *
 * Animates smoothly as steps complete.
 */
@Composable
fun TaskChecklist(
    todos: List<GabomaStreamEvent.TodoItem>,
    modifier: Modifier = Modifier
) {
    if (todos.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Plan",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Progress counter
                val completed = todos.count { it.done }
                Text(
                    text = "$completed/${todos.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // Progress bar
                Spacer(modifier = Modifier.weight(1f))
                LinearProgressIndicator(
                    progress = { if (todos.isNotEmpty()) completed.toFloat() / todos.size else 0f },
                    modifier = Modifier
                        .width(60.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Steps
            todos.forEachIndexed { index, todo ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(
                        spring(stiffness = Spring.StiffnessMedium),
                        initialOffsetY = { it / 2 }
                    )
                ) {
                    TaskChecklistItem(todo = todo)
                }
            }
        }
    }
}

@Composable
private fun TaskChecklistItem(
    todo: GabomaStreamEvent.TodoItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Status icon
        when {
            todo.done -> Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Done",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            todo.inProgress -> Icon(
                imageVector = Icons.Outlined.PlayCircle,
                contentDescription = "In Progress",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp)
            )
            else -> Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = "Pending",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }

        // Step text
        Text(
            text = todo.text,
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = if (todo.done) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = when {
                todo.done -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                todo.inProgress -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
