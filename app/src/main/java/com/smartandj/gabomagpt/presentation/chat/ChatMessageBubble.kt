package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onCopy: ((String) -> Unit)? = null,
    onShare: ((String) -> Unit)? = null,
    onThumbUp: ((String) -> Unit)? = null,
    onThumbDown: ((String) -> Unit)? = null,
    onRegenerate: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (message.role == ChatRole.USER)
            androidx.compose.ui.Alignment.End
        else
            androidx.compose.ui.Alignment.Start
    ) {
        Row(
            horizontalArrangement = if (message.role == ChatRole.USER)
                Arrangement.End
            else
                Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .background(
                        color = if (message.role == ChatRole.USER)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.role == ChatRole.USER) 16.dp else 0.dp,
                            bottomEnd = if (message.role == ChatRole.USER) 0.dp else 16.dp
                        )
                    )
                    .border(
                        width = if (message.role == ChatRole.ASSISTANT) 1.dp else 0.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.role == ChatRole.USER) 16.dp else 0.dp,
                            bottomEnd = if (message.role == ChatRole.USER) 0.dp else 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                if (message.sources.isNotEmpty() && message.role == ChatRole.ASSISTANT) {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        items(message.sources) { source ->
                            androidx.compose.material3.Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .widthIn(min = 120.dp, max = 200.dp)
                                    .clickable { /* TODO: Open URL */ }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = source.host,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = source.title,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = message.content,
                    color = if (message.role == ChatRole.USER)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Actions sous messages IA
        if (message.role == ChatRole.ASSISTANT && onCopy != null && onShare != null && onThumbUp != null && onThumbDown != null && onRegenerate != null) {
            Spacer(modifier = Modifier.height(8.dp))
            MessageActions(
                messageText = message.content,
                onCopy = { onCopy(message.content) },
                onShare = { onShare(message.content) },
                onThumbUp = { onThumbUp(message.content) },
                onThumbDown = { onThumbDown(message.content) },
                onRegenerate = onRegenerate,
                backgroundColor = androidx.compose.ui.graphics.Color(0x0AFFFFFF),
                iconTint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
