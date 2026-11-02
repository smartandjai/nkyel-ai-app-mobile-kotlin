package com.gabomagpt.mobile

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageBubble(
    theme: GabomaThemePreset,
    message: ChatMessage,
    onOpenArtifact: (ArtifactCard) -> Unit,
    onCopy: () -> Unit,
    onRegenerate: () -> Unit
) {
    val isUser = message.role == "user"
    val bubbleColor = if (isUser) theme.primary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f)
    val clipboard = LocalClipboardManager.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 22.dp,
                bottomStart = if (isUser) 22.dp else 8.dp,
                bottomEnd = if (isUser) 8.dp else 22.dp
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (message.streaming) {
                    StreamingText(message.content, theme)
                } else {
                    Text(
                        message.content,
                        color = if (isUser) lerp(theme.text, theme.primary, 0.35f) else theme.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (!isUser && !message.streaming) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            clipboard.setText(AnnotatedString(message.content))
                            onCopy()
                        }) {
                            Text("Capturer", color = theme.primary)
                        }
                        TextButton(onClick = onRegenerate) {
                            Text("Relancer la Chasse", color = theme.text.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
        if (message.sources.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                message.sources.forEach { source ->
                    Surface(color = theme.accent.copy(alpha = 0.15f), shape = RoundedCornerShape(999.dp)) {
                        Text(
                            source,
                            color = theme.text,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
        if (message.artifacts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Le Rendu (💎)", color = theme.primary, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                message.artifacts.forEach { artifact ->
                    ArtifactPreviewCard(theme, artifact) { onOpenArtifact(artifact) }
                }
            }
        }
    }
}

@Composable
fun ArtifactPreviewCard(theme: GabomaThemePreset, artifact: ArtifactCard, onOpen: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        color = Color.White.copy(alpha = 0.04f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(artifact.kind.icon(), contentDescription = null, tint = theme.primary)
                Column(modifier = Modifier.weight(1f)) {
                    Text(artifact.title, color = theme.text, style = MaterialTheme.typography.bodyLarge)
                    Text(artifact.kind.label, color = theme.text.copy(alpha = 0.58f), style = MaterialTheme.typography.labelMedium)
                    artifact.premiumHint?.let {
                        Text(it, color = theme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Text("Ouvrir", color = theme.primary, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun StreamingText(text: String, theme: GabomaThemePreset) {
    val infinite = rememberInfiniteTransition(label = "cursor")
    val alpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "cursorAlpha"
    )
    Text(
        text = "$text ▋",
        color = theme.text.copy(alpha = 0.95f),
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .width(14.dp)
            .height(2.dp)
            .background(theme.primary.copy(alpha = alpha), RoundedCornerShape(999.dp))
    )
}

@Composable
fun ArtifactBottomSheet(artifact: ArtifactCard, theme: GabomaThemePreset, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Le Rendu (💎)", color = theme.primary, style = MaterialTheme.typography.titleMedium)
                Text(artifact.title, color = theme.text, style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = { }) { Icon(Icons.Outlined.Share, contentDescription = null, tint = theme.text) }
                IconButton(onClick = { }) { Icon(Icons.Outlined.Download, contentDescription = null, tint = theme.text) }
                IconButton(onClick = onClose) { Icon(Icons.Outlined.Close, contentDescription = null, tint = theme.text) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            ArtifactKind.entries.forEach { kind ->
                FilterChip(selected = kind == artifact.kind, onClick = { }, label = { Text(kind.label) })
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White.copy(alpha = 0.04f),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            when (artifact.kind) {
                ArtifactKind.HTML -> HtmlPreview(theme, artifact.content)
                ArtifactKind.CODE -> CodePreview(theme, artifact.content)
                ArtifactKind.MARKDOWN -> MarkdownPreview(theme, artifact.content)
                ArtifactKind.TEXT -> TextPreview(theme, artifact.content)
                ArtifactKind.PDF, ArtifactKind.WORD, ArtifactKind.EXCEL, ArtifactKind.POWERPOINT ->
                    FilePreview(theme, artifact)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = theme.primary, contentColor = theme.bg),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Activer ton Génie PREMIUM")
        }
    }
}

@Composable
fun HtmlPreview(theme: GabomaThemePreset, content: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("HTML preview", color = theme.primary, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            content,
            color = theme.text,
            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CodePreview(theme: GabomaThemePreset, content: String) {
    val clipboard = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Code", color = theme.primary, style = MaterialTheme.typography.labelLarge)
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(content))
                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
            }) {
                Icon(Icons.Outlined.ContentCopy, contentDescription = null, tint = theme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copier", color = theme.primary)
            }
        }
        Text(
            content,
            color = theme.text,
            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MarkdownPreview(theme: GabomaThemePreset, content: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Markdown", color = theme.primary, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(content, color = theme.text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun TextPreview(theme: GabomaThemePreset, content: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Texte", color = theme.primary, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(content, color = theme.text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun FilePreview(theme: GabomaThemePreset, artifact: ArtifactCard) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(artifact.kind.icon(), contentDescription = null, tint = theme.primary, modifier = Modifier.size(42.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(artifact.title, color = theme.text, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            artifact.content,
            color = theme.text.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
