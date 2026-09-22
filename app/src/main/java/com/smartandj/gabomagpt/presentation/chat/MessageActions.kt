// presentation/chat/MessageActions.kt
package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MessageActions(
    messageText: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onThumbUp: () -> Unit,
    onThumbDown: () -> Unit,
    onRegenerate: () -> Unit,
    backgroundColor: Color = Color(0x0AFFFFFF),
    iconTint: Color = Color(0xFF9B8BB3)
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Copier
        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copier",
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        }

        // Partager
        IconButton(
            onClick = onShare,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Partager",
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        }

        // Pouce haut
        IconButton(
            onClick = onThumbUp,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "C'est bien",
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        }

        // Pouce bas
        IconButton(
            onClick = onThumbDown,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ThumbDown,
                contentDescription = "Pas bien",
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        }

        // Régénérer
        IconButton(
            onClick = onRegenerate,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Régénérer",
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        }
    }
}
