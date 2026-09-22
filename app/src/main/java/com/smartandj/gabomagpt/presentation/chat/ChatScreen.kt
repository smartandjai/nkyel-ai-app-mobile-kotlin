// presentation/chat/ChatScreen.kt
package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole
import com.smartandj.gabomagpt.presentation.components.GabomaIlluminatedBackground
import com.smartandj.gabomagpt.presentation.theme.GabomaIcons
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeColors
import com.smartandj.gabomagpt.presentation.theme.LocalGabomaColors
import com.smartandj.gabomagpt.presentation.theme.LocalGabomaTheme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat

@Composable
fun ChatScreen(
    onOpenSidebar : () -> Unit,
    onOpenAntre   : () -> Unit,
    onOpenProfil  : () -> Unit,
    onOpenAgent   : () -> Unit = {},
    viewModel     : ChatViewModel = hiltViewModel()
) {
    val gabomaColors  = LocalGabomaColors.current
    val gabomaTheme   = LocalGabomaTheme.current  // NEW: Get new 6-theme system
    val listState     = rememberLazyListState()


    val uiState by viewModel.uiState.collectAsState()
    val messages = uiState.messages
    val isStreaming = uiState.isStreaming
    val loxoActive = uiState.isLoxoActive

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // NEW: Wrap entire screen with Illuminate background + Haze state
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gabomaTheme.backgroundColor)

    ) {
        // NEW: Glow animation when AI is typing
        GabomaIlluminatedBackground(
            isAITyping = isStreaming,
            theme      = gabomaTheme,
            modifier   = Modifier.fillMaxSize()
        ) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gabomaTheme.backgroundColor)
        ) {
            ChatTopBar(
                onOpenSidebar = onOpenSidebar,
                onNewChat     = { viewModel.clearMessages() },
                onOpenRendu = { /* open antre */ }
            )

            Box(modifier = Modifier.weight(1f)) {
                if (messages.isEmpty()) {
                    com.smartandj.gabomagpt.presentation.home.GabomaEmptyHomeScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        state          = listState,
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages, key = { it.id }) { message ->
                            MessageBubbleLuxe(
                                message = message,
                                onCopy = { text ->
                                    // clipboard removed
                                    // TODO: Implement clipboard copy
                                },
                                onShare = { text ->
                                    // TODO: Implement share intent
                                },
                                onThumbUp = { text ->
                                    // TODO: Send positive feedback
                                },
                                onThumbDown = { text ->
                                    // TODO: Show feedback dialog
                                },
                                onRegenerate = {
                                    viewModel.retryLastMessage()
                                },
                                isStreaming = isStreaming && message.id == messages.lastOrNull()?.id && message.role != com.smartandj.gabomagpt.domain.model.ChatRole.USER,
                                accentColor = gabomaColors.primary,
                                isDarkTheme = true
                            )
                        }
                        if (isStreaming) {
                            item { StreamingIndicator(gabomaColors) }
                        }
                    }
                }
            }

            // Bouton ACTIVER L'AGENT ONYX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onOpenAgent,
                    colors = ButtonDefaults.buttonColors(containerColor = gabomaColors.primary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ACTIVER L'AGENT ONYX GRIS ▶",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = gabomaColors.primary
                    )
                }
            }

            // NEW: ChatMessageInputCompose with Haze blur effect + Model selector
            ChatMessageInputCompose(
                onSend       = { text ->
                    viewModel.onInputChange(text)
                    viewModel.sendMessage()
                },
                isLoading    = isStreaming,
                isLoxoActive = loxoActive,
                onToggleLoxo = viewModel::toggleLoxo,
                currentModel = uiState.selectedModel,
                onModelChange = viewModel::selectModel,

                blurRadius   = 24.dp
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message      : ChatMessage,
    gabomaColors : GabomaThemeColors
) {
    val isUser = message.role == ChatRole.USER
    val timestamp = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(message.createdAtMillis))

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment     = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(gabomaColors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Face,
                    contentDescription = "Agent",
                    tint               = gabomaColors.primary,
                    modifier           = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier            = Modifier.widthIn(max = 300.dp)
        ) {
            if (!isUser) {
                Text(
                    text = message.modelDisplayName ?: "OnyxGris",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    letterSpacing = 1.5.sp,
                    color = gabomaColors.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart    = if (isUser) 16.dp else 4.dp,
                    topEnd      = if (isUser) 4.dp  else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd   = 16.dp
                ),
                color = if (isUser)
                    gabomaColors.primary.copy(alpha = 0.15f)
                else
                    gabomaColors.elevated,
                tonalElevation = 0.dp
            ) {
                com.smartandj.gabomagpt.presentation.components.MarkdownText(
                    markdown   = message.content,
                    modifier   = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color      = gabomaColors.textPrimary
                )
            }
            Text(
                text = timestamp,
                fontSize = 10.sp,
                color = gabomaColors.textMuted,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(gabomaColors.elevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AJ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = gabomaColors.textMuted
                )
            }
        }
    }
}

@Composable
private fun StreamingIndicator(gabomaColors: GabomaThemeColors) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_pulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Recherche en cours",
            tint = gabomaColors.primary.copy(alpha = alpha),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "En piste...",
            color = gabomaColors.primary.copy(alpha = alpha),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}
