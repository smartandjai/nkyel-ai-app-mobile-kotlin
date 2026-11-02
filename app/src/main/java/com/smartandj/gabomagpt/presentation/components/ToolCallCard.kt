package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartandj.gabomagpt.stream.GabomaStreamEvent

/**
 * ToolCallCard — Repliable card showing a tool invocation.
 *
 * Displays:
 *   - Tool name with icon
 *   - Execution status (running/success/error)
 *   - Expandable args preview and result
 *   - Progress indicator when running
 *
 * Visual language matches the web version for cross-platform consistency.
 */
@Composable
fun ToolCallCard(
    toolStart: GabomaStreamEvent.ToolStart,
    toolEnd: GabomaStreamEvent.ToolEnd? = null,
    toolProgress: GabomaStreamEvent.ToolProgress? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isRunning = toolEnd == null
    val isSuccess = toolEnd?.success ?: true

    val statusColor by animateColorAsState(
        targetValue = when {
            isRunning -> MaterialTheme.colorScheme.tertiary
            isSuccess -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.error
        },
        label = "statusColor"
    )

    val progressAlpha by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progressAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // ─── Header Row ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tool icon
                Icon(
                    imageVector = getToolIcon(toolStart.toolName),
                    contentDescription = toolStart.toolName,
                    tint = statusColor,
                    modifier = Modifier.size(18.dp)
                )

                // Tool name
                Text(
                    text = toolStart.toolName,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Status indicator
                if (isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = statusColor
                    )
                } else {
                    Icon(
                        imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                        contentDescription = if (isSuccess) "Success" else "Error",
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Expand toggle
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Toggle details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            // ─── Progress bar ───
            if (isRunning && toolProgress?.progress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { toolProgress.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp)),
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }

            // ─── Expandable Details ───
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(spring(stiffness = Spring.StiffnessMedium)),
                exit = shrinkVertically(spring(stiffness = Spring.StiffnessMedium))
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // Args preview
                    toolStart.argsPreview?.let { args ->
                        Text(
                            text = "Arguments",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = args.entries.joinToString("\n") { "${it.key}: ${it.value}" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHighest,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(8.dp),
                            maxLines = 10,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Result or error
                    toolEnd?.let { end ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (end.success) "Result" else "Error",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (end.success) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = end.error ?: end.result ?: "No output",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (end.success) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHighest,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(8.dp),
                            maxLines = 20,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Live progress output
                    if (isRunning && toolProgress != null && toolProgress.output.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Live Output",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = toolProgress.output,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHighest,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(8.dp),
                            maxLines = 15,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Map tool names to Material Design icons.
 */
private fun getToolIcon(toolName: String): ImageVector {
    return when {
        toolName.contains("bash") || toolName.contains("terminal") -> Icons.Filled.Terminal
        toolName.contains("search") || toolName.contains("brave") || toolName.contains("grep") -> Icons.Filled.Search
        toolName.contains("read") || toolName.contains("write") || toolName.contains("file") -> Icons.Filled.Code
        toolName.contains("fetch") || toolName.contains("web") || toolName.contains("browse") -> Icons.Filled.Web
        toolName.contains("sql") || toolName.contains("postgres") || toolName.contains("database") -> Icons.Filled.Storage
        else -> Icons.Filled.Code
    }
}
