package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartandj.gabomagpt.stream.GabomaStreamEvent

/**
 * LiveAgentPanel — Real-time agent execution monitor.
 *
 * Shows in a sliding panel on the right side:
 *   - Current mode (planning/standard/edit)
 *   - Task checklist (Manus-style)
 *   - Active tool calls with live output
 *   - Verification badge for Gabonese languages
 *   - Takeover button
 *
 * Consumes a list of GabomaStreamEvents from the SSE client.
 */
@Composable
fun LiveAgentPanel(
    events: List<GabomaStreamEvent>,
    isAgentRunning: Boolean,
    onTakeover: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Derive state from events
    val currentMode = remember(events) {
        events.filterIsInstance<GabomaStreamEvent.ModeChange>()
            .lastOrNull()?.to ?: "standard"
    }

    val todos = remember(events) {
        events.filterIsInstance<GabomaStreamEvent.TodoUpdate>()
            .lastOrNull()?.todos ?: emptyList()
    }

    val toolStarts = remember(events) {
        events.filterIsInstance<GabomaStreamEvent.ToolStart>()
    }

    val toolEnds = remember(events) {
        events.filterIsInstance<GabomaStreamEvent.ToolEnd>()
    }

    val verification = remember(events) {
        events.filterIsInstance<GabomaStreamEvent.Verification>().lastOrNull()
    }

    val activeToolIds = remember(toolStarts, toolEnds) {
        val endedIds = toolEnds.map { it.toolCallId }.toSet()
        toolStarts.filter { it.toolCallId !in endedIds }
    }

    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(topStart = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ─── Header ───
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Agent View",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Agent Live",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        ModeIndicator(mode = currentMode)
                    }

                    TakeoverControl(
                        isAgentRunning = isAgentRunning,
                        onTakeover = onTakeover
                    )
                }
            }

            // ─── Content ───
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Task Checklist
                if (todos.isNotEmpty()) {
                    item {
                        TaskChecklist(todos = todos)
                    }
                }

                // Workspace Sandbox Viewer (Brick 9)
                item {
                    Text(
                        text = "Workspace Sandbox",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                    SandboxViewer(isRunning = isAgentRunning)
                }

                // Active Tool Calls
                if (activeToolIds.isNotEmpty()) {
                    item {
                        Text(
                            text = "Executing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }
                    items(activeToolIds, key = { it.toolCallId }) { toolStart ->
                        val toolEnd = toolEnds.find { it.toolCallId == toolStart.toolCallId }
                        val toolProgress = events
                            .filterIsInstance<GabomaStreamEvent.ToolProgress>()
                            .lastOrNull { it.toolCallId == toolStart.toolCallId }

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(spring(stiffness = Spring.StiffnessMedium)),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            ToolCallCard(
                                toolStart = toolStart,
                                toolEnd = toolEnd,
                                toolProgress = toolProgress
                            )
                        }
                    }
                }

                // Completed Tool Calls (collapsed)
                val completedTools = toolStarts.filter { start ->
                    toolEnds.any { it.toolCallId == start.toolCallId }
                }
                if (completedTools.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completed (${completedTools.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                        )
                    }
                    items(
                        completedTools.takeLast(5),
                        key = { "done-${it.toolCallId}" }
                    ) { toolStart ->
                        val toolEnd = toolEnds.find { it.toolCallId == toolStart.toolCallId }
                        ToolCallCard(
                            toolStart = toolStart,
                            toolEnd = toolEnd,
                        )
                    }
                }

                // Verification Badge
                verification?.let { v ->
                    item {
                        VerificationBadge(verification = v)
                    }
                }

                // Idle state
                if (todos.isEmpty() && activeToolIds.isEmpty() && !isAgentRunning) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Agent is idle.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Verification Badge — Shows Gabonese language verification status.
 */
@Composable
private fun VerificationBadge(
    verification: GabomaStreamEvent.Verification,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (verification.verified)
            Color(0xFF1F9D6B).copy(alpha = 0.08f)
        else
            Color(0xFFD98E3B).copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (verification.verified) "✓" else "⚠",
                style = MaterialTheme.typography.titleSmall
            )
            Column {
                Text(
                    text = if (verification.verified)
                        "Vérifié · ${verification.languageDisplay ?: verification.language}"
                    else
                        verification.tag ?: "Non vérifié",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (verification.verified)
                        Color(0xFF1F9D6B)
                    else
                        Color(0xFFD98E3B)
                )
            }
        }
    }
}
