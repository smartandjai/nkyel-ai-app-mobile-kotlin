// presentation/components/GabomaCodeBlock.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMA CODE BLOCK - Markdown code block with copy button and haptic feedback
 *  Header: language label + copy button (SurfaceDepth4 bg, GoldGaboma text)
 *  Body: HorizontalScrollableCode (SurfaceDepth3 bg, TurquoiseIA text for code)
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@Composable
fun GabomaCodeBlock(
    code: String,
    language: String = "code",
    modifier: Modifier = Modifier,
    onCopyClick: (() -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            kotlinx.coroutines.delay(2000)
            isCopied = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = GabomaColors.SurfaceDepth3,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
    ) {
        // ─────────────────────────────────────────────────────────────────────
        // HEADER - Language label + Copy button
        // ─────────────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GabomaColors.ElevatedBlackPanther)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language label
            Text(
                text = language,
                style = MaterialTheme.typography.labelMedium,
                color = GabomaColors.AccentBlackPanther
            )

            // Copy button with ripple
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                    isCopied = true
                    onCopyClick?.invoke()
                },
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = GabomaColors.TextSecondary
                ),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "Copy code",
                    modifier = Modifier.size(18.dp),
                    tint = animateColorAsState(
                        if (isCopied) GabomaColors.SuccessGreen else GabomaColors.AccentBlackPanther,
                        label = "copy_icon_color"
                    ).value
                )
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // CODE BODY - Horizontally scrollable
        // ─────────────────────────────────────────────────────────────────────
        HorizontalScrollableCode(
            code = code,
            modifier = Modifier
                .fillMaxWidth()
                .background(GabomaColors.SurfaceDepth3)
                .padding(12.dp)
        )
    }
}

/**
 * Horizontally scrollable code container for long lines
 */
@Composable
private fun HorizontalScrollableCode(
    code: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .horizontalScroll(scrollState)
    ) {
        Text(
            text = code,
            style = CodeBlockStyle,
            color = GabomaColors.AccentBlackPanther,
            modifier = Modifier
                .padding(end = 8.dp)  // Icons.Filled.Add padding for scrollbar visual balance
        )
    }
}

/**
 * Simple inline code span for markdown rendering
 */
@Composable
fun InlineCodeSpan(
    code: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = GabomaColors.ElevatedBlackPanther,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = code,
            style = CodeBlockStyle.copy(fontSize = CodeBlockStyle.fontSize * 0.9f),
            color = GabomaColors.AccentBlackPanther
        )
    }
}

/**
 * Code language badge (e.g., "Python", "Kotlin", "Bash")
 */
@Composable
fun CodeLanguageBadge(
    language: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = GabomaColors.AccentBlackPanther.copy(alpha = 0.12f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    ) {
        Text(
            text = language,
            style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
            color = GabomaColors.AccentBlackPanther,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
