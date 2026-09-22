package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SandboxViewer — Native Kotlin equivalent of the Brick 9 Web Visualizer.
 *
 * Displays the persistent agent workspace inside Android using Jetpack Compose:
 * - Live Terminal (stdout/stderr)
 * - File Explorer (Persistent workspace)
 */
@Composable
fun SandboxViewer(
    provider: String = "E2B Cloud Firecracker",
    isRunning: Boolean = true,
    terminalOutput: List<String> = listOf(
        "gabo-agent@sandbox:~$ source .venv/bin/activate",
        "(venv) gabo-agent@sandbox:~$ python src/main.py",
        "[INFO] Loading Vector vector database...",
        "[INFO] Initializing browser-use for Vision tasks...",
        "     > Processing page screenshot...",
        "     > Extracted 14 nodes."
    ),
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(SandboxTab.TERMINAL) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0D12)), // Deep OLED background
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ─── Header ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16161E)) // Slightly lighter header
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Dns,
                        contentDescription = "Sandbox",
                        tint = Color(0xFFC5A059),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SANDBOX",
                        color = Color(0xFFEDECE6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        color = Color(0xFFC5A059).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = provider,
                            color = Color(0xFFC5A059),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (isRunning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Dot pulse indicator
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF00D4AA))
                        )
                        Text(
                            text = "Actif",
                            color = Color(0xFF00D4AA),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ─── Tabs ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16161E))
                    .padding(horizontal = 8.dp)
            ) {
                TabButton(
                    title = "Terminal",
                    icon = Icons.Filled.Terminal,
                    isActive = activeTab == SandboxTab.TERMINAL,
                    onClick = { activeTab = SandboxTab.TERMINAL }
                )
                TabButton(
                    title = "Fichiers",
                    icon = Icons.Filled.Folder,
                    isActive = activeTab == SandboxTab.FILES,
                    onClick = { activeTab = SandboxTab.FILES }
                )
            }

            Divider(color = Color(0xFF2A2A35), thickness = 1.dp)

            // ─── Content ───
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    label = "SandboxContentTransition"
                ) { tab ->
                    when (tab) {
                        SandboxTab.TERMINAL -> TerminalView(terminalOutput, isRunning)
                        SandboxTab.FILES -> FileExplorerView()
                    }
                }
            }
        }
    }
}

enum class SandboxTab { TERMINAL, FILES }

@Composable
private fun TabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isActive) Color(0xFFC5A059) else Color(0xFF888680)
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = title,
            color = tint,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun TerminalView(lines: List<String>, isRunning: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(lines) { line ->
            Text(
                text = line,
                color = if (line.contains("[INFO]")) Color(0xFF5B8DEF) else Color(0xFFA0A0A0),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
        if (isRunning) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "gabo-agent@sandbox:~$",
                        color = Color(0xFF00D4AA),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(14.dp)
                            .background(Color(0xFFC5A059))
                    )
                }
            }
        }
    }
}

@Composable
private fun FileExplorerView() {
    // Mock files for Jetpack Compose UI
    val files = listOf(
        Pair(0, "src/"),
        Pair(1, "main.py"),
        Pair(1, "utils.py"),
        Pair(0, "data/"),
        Pair(1, "gabonese_corpus.csv"),
        Pair(0, "requirements.txt")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(files) { (depth, name) ->
            val isFolder = name.endsWith("/")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = (depth * 16).dp, top = 2.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isFolder) Icons.Filled.Folder else if (name.endsWith(".py")) Icons.Filled.Code else Icons.Filled.InsertDriveFile,
                    contentDescription = null,
                    tint = if (isFolder) Color(0xFFC5A059) else Color(0xFF888680),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = name,
                    color = Color(0xFFEDECE6),
                    fontSize = 12.sp
                )
            }
        }
    }
}
