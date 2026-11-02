// presentation/components/StreamingMessageText.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import com.smartandj.gabomagpt.presentation.theme.*

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  STREAMING MESSAGE TEXT - Token-by-token animation with 18ms delays
 *  Performance: 55 tokens/sec, smooth alpha transitions (160ms), Animatable per word
 * ═══════════════════════════════════════════════════════════════════════════════
 */

data class AnimatedToken(
    val text: String,
    val alphaAnimatable: Animatable<Float, AnimationVector1D> = Animatable(0f)
)

@Composable
fun StreamingMessageText(
    text: String,
    isStreaming: Boolean = false,
    textStyle: TextStyle = androidx.compose.ui.text.TextStyle(color = GabomaColors.TextPrimary),
    color: Color = GabomaColors.TextPrimary,
    onCompleted: (() -> Unit)? = null
) {
    var displayedText by remember(text) { mutableStateOf("") }
    var tokens by remember(text) { mutableStateOf<List<AnimatedToken>>(emptyList()) }

    // Split text into words (tokens) and initialize animation states
    LaunchedEffect(text) {
        if (text.isEmpty()) {
            displayedText = ""
            tokens = emptyList()
            return@LaunchedEffect
        }

        val words = text.split(" ")
        tokens = words.map { AnimatedToken(it) }
        displayedText = ""

        // Animate each token with 18ms delay (~55 tokens/sec)
        words.forEachIndexed { index, word ->
            delay(18)  // 18ms per token = 55 tokens/sec
            displayedText = words.take(index + 1).joinToString(" ")

            // Smooth alpha animation (160ms, FastOutSlowInEasing)
            tokens.getOrNull(index)?.let { token ->
                launch {
                    token.alphaAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 160,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }

        if (!isStreaming) {
            onCompleted?.invoke()
        }
    }

    // Blinking cursor during streaming
    val infiniteTransition = rememberInfiniteTransition(label = "streaming_cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    val displayText = if (isStreaming) {
        "$displayedText▌".takeIf { displayedText.isNotEmpty() } ?: "▌"
    } else {
        displayedText
    }

    Text(
        text = displayText,
        style = textStyle,
        color = if (isStreaming && displayedText.isEmpty()) {
            GabomaColors.AccentBlackPanther.copy(alpha = cursorAlpha)
        } else {
            color
        }
    )
}

/**
 * Simpler version for non-streamed messages (instant display)
 */
@Composable
fun InfusingTokenText(
    text: String,
    textStyle: TextStyle = androidx.compose.ui.text.TextStyle(color = GabomaColors.TextPrimary),
    color: Color = GabomaColors.TextPrimary
) {
    Text(
        text = text,
        style = textStyle,
        color = color
    )
}

/**
 * Cursor component for streaming indicator
 */
@Composable
fun StreamingCursor(
    color: Color = GabomaColors.AccentBlackPanther,
    durationMillis: Int = 650
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    Text(
        text = "▌",
        color = color.copy(alpha = alpha),
        style = LocalTextStyle.current
    )
}
