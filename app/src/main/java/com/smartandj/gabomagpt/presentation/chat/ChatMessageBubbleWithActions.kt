// presentation/chat/ChatMessageBubbleWithActions.kt
package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole

@Composable
fun ChatMessageBubbleWithActions(
    message: ChatMessage,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onThumbUp: (String) -> Unit,
    onThumbDown: (String) -> Unit,
    onRegenerate: () -> Unit,
    textColor: Color = Color(0xFFE2E8F0),
    aiBackgroundColor: Color = Color(0x05FFFFFF),
    userBackgroundColor: Color = Color(0xFF3B533E),
    accentColor: Color = Color(0xFFC5A059)
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = if (message.role == ChatRole.USER) 
            Alignment.End else Alignment.Start
    ) {
        // Message bubble
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (message.role == ChatRole.USER) 
                        userBackgroundColor else aiBackgroundColor
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                style = TextStyle(
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            )
        }

        // Actions (uniquement pour messages IA)
        if (message.role == ChatRole.ASSISTANT) {
            Spacer(modifier = Modifier.height(8.dp))
            MessageActions(
                messageText = message.content,
                onCopy = { onCopy(message.content) },
                onShare = { onShare(message.content) },
                onThumbUp = { onThumbUp(message.content) },
                onThumbDown = { onThumbDown(message.content) },
                onRegenerate = onRegenerate,
                backgroundColor = Color(0x0AFFFFFF),
                iconTint = accentColor
            )
        }
    }
}
