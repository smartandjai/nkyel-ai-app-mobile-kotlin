// ============================================================
// GABOMAGPT — MODULE 2 : BARRE D'ACTIONS MESSAGE
// Style Illuminate 2026 · 100% fonctionnel
// Kotlin / Jetpack Compose — Standards 2026
// SMARTANDJ AI TECH · BY ANDJ
// ============================================================

package com.smartandj.gabomagpt.presentation.chat

import android.content.*
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.*
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────
// 1. DESIGN TOKENS BLACK-PANTHER (local)
// ─────────────────────────────────────────────────────────────
private object BP {
    val BgBase       = androidx.compose.ui.graphics.Color(0xFF020304)
    val Primary      = androidx.compose.ui.graphics.Color(0xFFC5A059)
    val TextMuted    = androidx.compose.ui.graphics.Color(0xFF9B8BB3)
    val Surface      = androidx.compose.ui.graphics.Color(0xFF0D0F14)
    val SurfaceHigh  = androidx.compose.ui.graphics.Color(0xFF1A1D26)
    val Border       = androidx.compose.ui.graphics.Color(0xFF1A1D26)
    val BorderLight  = androidx.compose.ui.graphics.Color(0xFF3A3D4A)
    val TextPrimary  = androidx.compose.ui.graphics.Color(0xFFF0EFE8)
    val TextSecondary= androidx.compose.ui.graphics.Color(0xFFB8B6A8)
    val ErrorRed     = androidx.compose.ui.graphics.Color(0xFFFF3B30)
}

// ─────────────────────────────────────────────────────────────
// 2. FEEDBACK REASONS
// ─────────────────────────────────────────────────────────────
val FEEDBACK_REASONS = listOf(
    "Réponse incorrecte",
    "Trop longue",
    "Hors sujet",
    "Répétitive",
    "Autre"
)

// ─────────────────────────────────────────────────────────────
// 3. COMPOSANT PRINCIPAL — MessageActionBar
// ─────────────────────────────────────────────────────────────
@Composable
fun MessageActionBar(
    messageId   : String,
    content     : String,
    isStreaming : Boolean,
    modifier    : Modifier = Modifier
) {
    // Ne rien afficher pendant le streaming
    if (isStreaming) return

    val haptic     = LocalHapticFeedback.current
    val clipboard  = LocalClipboardManager.current
    val context    = LocalContext.current
    val scope      = rememberCoroutineScope()

    // ── États ────────────────────────────────────────────
    var copied          by remember { mutableStateOf(false) }
    var speaking        by remember { mutableStateOf(false) }
    var liked           by remember { mutableStateOf(false) }
    var disliked        by remember { mutableStateOf(false) }
    var regenerating    by remember { mutableStateOf(false) }
    var showDropdown    by remember { mutableStateOf(false) }
    var showFeedback    by remember { mutableStateOf(false) }
    var showReport      by remember { mutableStateOf(false) }
    var selectedReasons by remember { mutableStateOf<List<String>>(emptyList()) }
    var reportText      by remember { mutableStateOf("") }

    // ── TTS lifecycle-aware ───────────────────────────────
    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.FRENCH
                engine?.setSpeechRate(0.95f)
                engine?.setPitch(1.0f)
            }
        }
        engine
    }
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // ── Animation d'entrée fade-in delay 200ms ───────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200L)
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(400),
        label         = "actionBarAlpha"
    )

    // ── Layout principal ─────────────────────────────────
    Column(
        modifier = modifier
            .alpha(alpha)
            .padding(start = 36.dp, top = 6.dp, end = 8.dp)
    ) {
        // ════════════════════════════════════════════════
        // BARRE PRINCIPALE — 6 boutons
        // ════════════════════════════════════════════════
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {

            // ── 1. CAPTURER ─────────────────────────────
            ActionIconBtn(
                icon    = if (copied) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                tooltip = if (copied) "Capturé !" else "Capturer",
                active  = copied,
                onClick = {
                    clipboard.setText(AnnotatedString(content))
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    copied = true
                    scope.launch { delay(2000L); copied = false }
                }
            )

            // ── 2. ÉCOUTER ──────────────────────────────
            ActionIconBtn(
                icon    = if (speaking) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                tooltip = if (speaking) "Arrêter" else "Écouter",
                active  = speaking,
                activeColor = BP.TextMuted,
                onClick = {
                    if (speaking) {
                        tts?.stop()
                        speaking = false
                    } else {
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) { }
                            override fun onDone(utteranceId: String?)  { speaking = false }
                            override fun onError(utteranceId: String?) { speaking = false }
                        })
                        tts?.speak(
                            content,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "gabomagpt_tts_$messageId"
                        )
                        speaking = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            )

            // ── 3. POUCE HAUT ───────────────────────────
            ActionIconBtn(
                icon    = if (liked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                tooltip = "Bonne réponse",
                active  = liked,
                onClick = {
                    liked    = true
                    disliked = false
                    showFeedback = false
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                }
            )

            // ── 4. POUCE BAS ────────────────────────────
            ActionIconBtn(
                icon        = if (disliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                tooltip     = "Mauvaise réponse",
                active      = disliked,
                activeColor = BP.TextMuted,
                onClick     = {
                    disliked     = !disliked
                    liked        = false
                    showFeedback = disliked
                    if (!disliked) selectedReasons = emptyList()
                    haptic.performHapticFeedback(HapticFeedbackType.Reject)
                }
            )

            // ── 5. RÉGÉNÉRER (Relancer la Chasse) ───────
            RegenerateButton(
                regenerating = regenerating,
                onClick      = {
                    if (!regenerating) {
                        regenerating = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch { delay(2000L); regenerating = false }
                    }
                }
            )

            // ── 6. TROIS POINTS (Plus d'options) ────────
            Box {
                ActionIconBtn(
                    icon    = Icons.Filled.MoreHoriz,
                    tooltip = "Plus d'options",
                    onClick = {
                        showDropdown = !showDropdown
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                )
                // Dropdown menu
                GabomaDropdownMenu(
                    expanded    = showDropdown,
                    onDismiss   = { showDropdown = false },
                    content     = content,
                    context     = context,
                    clipboard   = clipboard,
                    onReport    = { showReport = true; showDropdown = false }
                )
            }
        }

        // ════════════════════════════════════════════════
        // FEEDBACK NÉGATIF INLINE
        // ════════════════════════════════════════════════
        AnimatedVisibility(
            visible = showFeedback,
            enter   = slideInVertically { -20 } + fadeIn(tween(200)),
            exit    = slideOutVertically { -20 } + fadeOut(tween(160))
        ) {
            FeedbackInlineCard(
                selectedReasons = selectedReasons,
                onToggleReason  = { reason ->
                    selectedReasons = if (reason in selectedReasons)
                        selectedReasons - reason
                    else
                        selectedReasons + reason
                },
                onSubmit        = {
                    showFeedback    = false
                    selectedReasons = emptyList()
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                },
                onDismiss       = {
                    showFeedback    = false
                    disliked        = false
                    selectedReasons = emptyList()
                }
            )
        }

        // ════════════════════════════════════════════════
        // SIGNALER UN PROBLÈME INLINE
        // ════════════════════════════════════════════════
        AnimatedVisibility(
            visible = showReport,
            enter   = slideInVertically { -20 } + fadeIn(tween(200)),
            exit    = slideOutVertically { -20 } + fadeOut(tween(160))
        ) {
            ReportInlineCard(
                text      = reportText,
                onTextChange = { reportText = it },
                onSubmit  = {
                    showReport  = false
                    reportText  = ""
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    Toast.makeText(context, "Signalement envoyé. Merci !", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showReport = false; reportText = "" }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 4. COMPOSABLE ActionIconBtn
// ─────────────────────────────────────────────────────────────
@Composable
fun ActionIconBtn(
    icon        : ImageVector,
    tooltip     : String,
    onClick     : () -> Unit,
    active      : Boolean = false,
    activeColor : androidx.compose.ui.graphics.Color   = BP.Primary,
    modifier    : Modifier = Modifier
) {
    var tooltipVisible by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(1f, spring(0.5f, 600f), label = "btnScale")

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (active) activeColor.copy(alpha = 0.12f)
                    else androidx.compose.ui.graphics.Color.Transparent
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                )
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = tooltip,
                tint               = if (active) activeColor else BP.TextMuted,
                modifier           = Modifier.size(16.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 5. BOUTON RÉGÉNÉRER (spin animé)
// ─────────────────────────────────────────────────────────────
@Composable
fun RegenerateButton(
    regenerating : Boolean,
    onClick      : () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "regenSpin")
    val rotation by infiniteTransition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "regenRotation"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (regenerating) BP.Primary.copy(0.12f) else androidx.compose.ui.graphics.Color.Transparent
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.Refresh,
            "Relancer la Chasse",
            tint     = if (regenerating) BP.Primary else BP.TextMuted,
            modifier = Modifier
                .size(16.dp)
                .then(if (regenerating) Modifier.rotate(rotation) else Modifier)
        )
    }
}

// ─────────────────────────────────────────────────────────────
// 6. DROPDOWN MENU COMPLET
// ─────────────────────────────────────────────────────────────
@Composable
fun GabomaDropdownMenu(
    expanded   : Boolean,
    onDismiss  : () -> Unit,
    content    : String,
    context    : Context,
    clipboard  : androidx.compose.ui.platform.ClipboardManager,
    onReport   : () -> Unit
) {
    DropdownMenu(
        expanded          = expanded,
        onDismissRequest  = onDismiss,
        modifier          = Modifier
            .background(BP.Surface)
            .border(1.dp, BP.Border, RoundedCornerShape(12.dp))
            .widthIn(min = 200.dp, max = 260.dp)
    ) {
        // ── A. Partager ──────────────────────────────
        GabomaDropItem(
            icon  = Icons.Filled.Share,
            label = "Partager",
            onClick = {
                onDismiss()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "$content\n\n— GABOMAGPT · SmartAndJ AI Tech")
                    putExtra(Intent.EXTRA_SUBJECT, "Réponse GABOMAGPT")
                }
                context.startActivity(Intent.createChooser(intent, "Partager via"))
            }
        )
        // ── B. Copier en Markdown ────────────────────
        GabomaDropItem(
            icon  = Icons.Filled.Code,
            label = "Copier en Markdown",
            onClick = {
                onDismiss()
                clipboard.setText(AnnotatedString(content))
                Toast.makeText(context, "Markdown copié !", Toast.LENGTH_SHORT).show()
            }
        )

        Divider(
            thickness = 0.5.dp,
            color     = BP.Border,
            modifier  = Modifier.padding(vertical = 4.dp)
        )

        // ── E. Signaler un problème ──────────────────
        GabomaDropItem(
            icon      = Icons.Filled.Warning,
            label     = "Signaler un problème",
            labelColor = BP.ErrorRed,
            iconColor  = BP.ErrorRed,
            onClick   = onReport
        )
    }
}

@Composable
fun GabomaDropItem(
    icon       : ImageVector,
    label      : String,
    labelColor : androidx.compose.ui.graphics.Color     = BP.TextSecondary,
    iconColor  : androidx.compose.ui.graphics.Color     = BP.TextMuted,
    onClick    : () -> Unit
) {
    DropdownMenuItem(
        text     = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
                Text(
                    label,
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    color = labelColor
                )
            }
        },
        onClick  = onClick,
        modifier = Modifier.background(androidx.compose.ui.graphics.Color.Transparent),
        colors   = MenuDefaults.itemColors(
            textColor = labelColor
        )
    )
}

// ─────────────────────────────────────────────────────────────
// 7. FEEDBACK NÉGATIF INLINE CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun FeedbackInlineCard(
    selectedReasons : List<String>,
    onToggleReason  : (String) -> Unit,
    onSubmit        : () -> Unit,
    onDismiss       : () -> Unit
) {
    Surface(
        modifier      = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        color         = BP.Surface,
        shape         = RoundedCornerShape(12.dp),
        border        = BorderStroke(1.dp, BP.Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Qu'est-ce qui n'allait pas ?",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                    color = BP.TextPrimary
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Close, null, tint = BP.TextMuted, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            // Chips de raisons
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(FEEDBACK_REASONS) { reason ->
                    val isSelected = reason in selectedReasons
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (isSelected) BP.Primary else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .border(
                                1.dp,
                                if (isSelected) BP.Primary else BP.BorderLight,
                                RoundedCornerShape(50.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                onClick           = { onToggleReason(reason) }
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            reason,
                            style = TextStyle(fontSize = 12.sp),
                            color = if (isSelected) BP.BgBase else BP.TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BP.Primary)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onSubmit
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Envoyer",
                        style = TextStyle(
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = BP.BgBase
                        )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 8. SIGNALEMENT INLINE CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun ReportInlineCard(
    text         : String,
    onTextChange : (String) -> Unit,
    onSubmit     : () -> Unit,
    onDismiss    : () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        color    = BP.Surface,
        shape    = RoundedCornerShape(12.dp),
        border   = BorderStroke(1.dp, BP.ErrorRed.copy(0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Signaler cette réponse",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                    color = BP.TextPrimary
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Close, null, tint = BP.TextMuted, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            BasicTextField(
                value         = text,
                onValueChange = onTextChange,
                modifier      = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .background(BP.Surface, RoundedCornerShape(8.dp))
                    .border(1.dp, BP.Border, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                textStyle     = TextStyle(fontSize = 13.sp, color = BP.TextPrimary),
                maxLines      = 6,
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text("Décris le problème...", style = TextStyle(fontSize = 13.sp), color = BP.TextMuted)
                    }
                    inner()
                }
            )
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BP.ErrorRed)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onSubmit
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Envoyer le signalement",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = androidx.compose.ui.graphics.Color.White)
                    )
                }
            }
        }
    }
}
