// presentation/components/MemoVoiceComposables.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeColors
import androidx.compose.material.icons.filled.*

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  MEMO VOCAL COMPOSABLES — Press-and-hold recording system (WhatsApp/Telegram style)
 * ═══════════════════════════════════════════════════════════════════════════════
 */

// ── Pulsing red dot indicator ──────────────────────────────────────────────
@Composable
fun PulsingDot(
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_dot")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

// ── Mini waveform animation ───────────────────────────────────────────────
@Composable
fun MiniWaveform(
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val wave1 by infiniteTransition.animateFloat(
        0.2f, 1f, infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "wave1"
    )
    val wave2 by infiniteTransition.animateFloat(
        0.3f, 1f, infiniteRepeatable(tween(500, 100), RepeatMode.Reverse),
        label = "wave2"
    )
    val wave3 by infiniteTransition.animateFloat(
        0.2f, 1f, infiniteRepeatable(tween(400, 200), RepeatMode.Reverse),
        label = "wave3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(wave1, wave2, wave3).forEach { waveHeight ->
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(12.dp * waveHeight)
                    .background(color, shape = CircleShape)
            )
        }
    }
}

// ── Memo voice button with press-and-hold detection ──────────────────────
@Composable
fun MemoVoiceButton(
    theme: GabomaThemeColors,
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (pressing) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "memo_scale"
    )

    Box(
        modifier = modifier
            .size(36.dp)
            .scale(scale)
            .background(
                if (pressing) theme.primary.copy(0.25f) else Color.Transparent,
                CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressing = true
                        onStart()
                        // Attendre relâchement
                        tryAwaitRelease()
                        pressing = false
                        onStop()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Mic,
            "Mémo vocal",
            tint = if (pressing) theme.primary else theme.textSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Recording indicator with timer and waveform ────────────────────────────
@Composable
fun MemoRecordingIndicator(
    seconds: Int,
    theme: GabomaThemeColors,
    onCancel: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mm = seconds / 60
    val ss = seconds % 60
    val timeStr = "%d:%02d".format(mm, ss)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Annuler (bouton ×)
        IconButton(
            onClick = onCancel,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Close,
                "Annuler",
                tint = theme.textMuted,
                modifier = Modifier.size(16.dp)
            )
        }

        // Dot rouge pulsant + timer + waveform
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            PulsingDot(color = Color(0xFFFF3B30), size = 8.dp)
            Spacer(Modifier.width(6.dp))
            Text(
                timeStr,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = theme.textPrimary
            )
            Spacer(Modifier.width(12.dp))
            // Mini waveform
            MiniWaveform(
                color = theme.primary,
                modifier = Modifier
                    .width(48.dp)
                    .height(20.dp)
            )
        }

        // Envoyer le mémo (bouton ▲)
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(34.dp)
                .background(theme.primary, CircleShape)
        ) {
            Icon(
                Icons.Default.ArrowUpward,
                "Envoyer mémo",
                tint = theme.background,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ── Live mode indicator button ─────────────────────────────────────────────
@Composable
fun LiveMicButton(
    theme: GabomaThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        0.5f, 1f,
        infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "live_alpha"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(36.dp)
            .background(
                theme.primary.copy(alpha = 0.1f),
                CircleShape
            )
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    Color(0xFFFF3B30).copy(alpha = pulseAlpha),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "●",
                color = Color(0xFFFF3B30),
                fontSize = 6.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
