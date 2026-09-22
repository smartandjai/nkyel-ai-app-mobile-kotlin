// presentation/chat/MessageBubbleLuxe.kt
package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole
import androidx.compose.material.icons.filled.*

@Composable
fun MessageBubbleLuxe(
    message: ChatMessage,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onThumbUp: (String) -> Unit,
    onThumbDown: (String) -> Unit,
    onRegenerate: () -> Unit,
    isStreaming: Boolean = false,
    accentColor: Color = Color(0xFFC5A059),
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(0.95f) }
    
    val isUser = message.role == ChatRole.USER
    val bubbleBackgroundColor = if (isUser) {
        Color(0xFF3B533E) // Vert Forêt mat
    } else {
        Color(0x0AFFFFFF) // Glassmorphism 4%
    }
    
    val bubbleBorderColor = if (isUser) {
        Color(0x66C5A059) // Or 40% pour user
    } else {
        Color(0x1AC5A059) // Or 10% pour IA
    }
    
    val textColor = if (isUser) Color(0xFFE2E8F0) else Color(0xFFE2E8F0)
    val scaleAnimation by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 500f)
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Message bubble avec animation d'entrée
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(600, easing = EaseInOutCubic)
            ) + fadeIn(animationSpec = tween(400))
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .shadow(
                        elevation = if (isUser) 12.dp else 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = if (isUser) accentColor.copy(0.2f) else accentColor.copy(0.1f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                bubbleBackgroundColor.copy(1f),
                                bubbleBackgroundColor.copy(0.95f)
                            ),
                            radius = 200f
                        )
                    )
                    .graphicsLayer(scaleX = scaleAnimation, scaleY = scaleAnimation)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { scale = 0.95f }
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                scale = 0.92f
                                tryAwaitRelease()
                                scale = 0.95f
                            }
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                com.smartandj.gabomagpt.presentation.components.GabomaMarkdownRenderer(
                    markdown = message.content,
                    isStreaming = isStreaming,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Actions toolbar (IA uniquement) — MessageActionBar
        if (!isUser) {
            MessageActionBar(
                messageId = message.id ?: "",
                content = message.content,
                isStreaming = isStreaming,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Thinking indicator (optionnel)
        if (!isUser && message.content.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            ThinkingIndicator(accentColor = accentColor)
        }
    }
}


@Composable
fun ActionIconLuxe(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    accentColor: Color
) {
    var hovered by remember { mutableStateOf(false) }
    val colorAnim by animateColorAsState(
        targetValue = if (hovered) accentColor else Color(0xFF9B8BB3),
        animationSpec = tween(200)
    )
    
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (hovered) Color(0x0AFFFFFF) else Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { 
                        hovered = true
                        tryAwaitRelease()
                        hovered = false
                    }
                )
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.width(16.dp),
            tint = colorAnim
        )
    }
}

@Composable
fun ThinkingIndicator(accentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        repeat(3) { index ->
            val opacity by animateFloatAsState(
                targetValue = if (index % 2 == 0) 0.3f else 1f,
                animationSpec = tween(600 + (index * 200), easing = EaseInOutCubic)
            )
            
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accentColor.copy(alpha = opacity))
            )
        }
        Text(
            text = "Thinking...",
            style = TextStyle(
                color = Color(0xFF9B8BB3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
