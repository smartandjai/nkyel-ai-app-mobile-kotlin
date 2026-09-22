// presentation/chat/ChatScreenLuxe.kt
package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole
import kotlinx.coroutines.launch

@Composable
fun ChatScreenLuxe(
    messages: List<ChatMessage>,
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
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF020304),      // Noir Panther top
                        Color(0xFF050709),      // Noir Panther bottom
                        Color(0xFF020304)       // Noir Panther very bottom
                    )
                )
            )
    ) {
        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            reverseLayout = false,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 16.dp,
                bottom = 120.dp
            )
        ) {
            items(
                items = messages,
                key = { it.id ?: messages.indexOf(it) }
            ) { message ->
                MessageBubbleLuxe(
                    message = message,
                    onCopy = onCopy,
                    onShare = onShare,
                    onThumbUp = onThumbUp,
                    onThumbDown = onThumbDown,
                    onRegenerate = onRegenerate,
                    isStreaming = isStreaming,
                    accentColor = accentColor,
                    isDarkTheme = isDarkTheme
                )
            }

            // Empty state
            if (messages.isEmpty()) {
                item {
                    EmptyStateChat(accentColor = accentColor)
                }
            }
        }

        // Sticky footer notice
        if (messages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF050709).copy(alpha = 0.8f),
                                Color(0xFF050709)
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Gaboma AI est une IA et peut faire des erreurs",
                    style = TextStyle(
                        color = Color(0xFF9B8BB3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateChat(accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Logo luxe
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(0.2f),
                            accentColor.copy(0.05f)
                        ),
                        radius = 150f
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✨",
                style = TextStyle(fontSize = 48.sp),
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Gaboma AI",
            style = TextStyle(
                color = accentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Bienvenue. Comment puis-je t'aider?",
            style = TextStyle(
                color = Color(0xFF9B8BB3),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
