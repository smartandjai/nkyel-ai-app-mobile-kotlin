package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smartandj.gabomagpt.presentation.components.LiveAgentPanel
import com.smartandj.gabomagpt.stream.GabomaStreamEvent
import kotlinx.coroutines.launch

/**
 * Wide Search / Agent Screen (Mobile implementation)
 *
 * On mobile, since we don't have the width for a desktop "Split-View",
 * the LiveAgentPanel is implemented as a BottomSheetScaffold.
 * When the agent starts thinking/executing (Wide Search), the user can
 * drag up the bottom sheet to see the Live Agent Panel (Checklist, Sandbox, Terminal).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GabomaAgentScreen(
    events: List<GabomaStreamEvent>,
    isAgentRunning: Boolean,
    onTakeover: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-expand the bottom sheet slightly when the agent starts doing complex tasks
    LaunchedEffect(isAgentRunning) {
        if (isAgentRunning) {
            // Optional: auto-peek when agent starts
            // scaffoldState.bottomSheetState.expand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            // ─── LIVE AGENT PANEL (Wide Search Mobile) ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f) // Takes up to 85% of screen when fully expanded
            ) {
                LiveAgentPanel(
                    events = events,
                    isAgentRunning = isAgentRunning,
                    onTakeover = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.partialExpand()
                        }
                        onTakeover()
                    }
                )
            }
        },
        sheetPeekHeight = if (isAgentRunning) 64.dp else 0.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color(0xFF0D0D12),
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color(0xFFC5A059).copy(alpha = 0.5f)
            )
        }
    ) { innerPadding ->
        // ─── MAIN CHAT INTERFACE ───
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF050507)) // Nuit Lopé theme background
        ) {
            // The normal chat messages go here
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Mock Chat Content
                Text(
                    text = "Conversation avec Gaboma...",
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
