// ============================================================
// GabomaGPT — BARRE D'INPUT COMPLÈTE
// Kotlin / Jetpack Compose 2026
// ============================================================

package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.smartandj.gabomagpt.R
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.*
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.smartandj.gabomagpt.presentation.theme.GabomaIcons
import androidx.compose.ui.graphics.vector.ImageVector

// ─────────────────────────────────────────────────────────────
// 1. DATA MODELS
// ─────────────────────────────────────────────────────────────

private const val MAX_TEXT_CHARS = 44_000

enum class AttachmentType { IMAGE, PDF, DOCUMENT, LONG_TEXT, AUDIO }

enum class LoxoAction { UPLOAD_IMAGE, UPLOAD_DOC, UPLOAD_VIDEO, INVOQUER_LOXO, RADAR_LOXO, MCP }

data class GabomaAttachment(
    val id        : String = UUID.randomUUID().toString(),
    val name      : String,
    val type      : AttachmentType,
    val uri       : Any?    = null,
    val wordCount : Int     = 0,
    val sizeLabel : String  = "",
    val durationMs: Long    = 0L
) {
    companion object {
        fun fromLongText(text: String): GabomaAttachment {
            val words = text.trim().split("\\s+".toRegex()).size
            return GabomaAttachment(
                name      = "Texte_${System.currentTimeMillis()}.txt",
                type      = AttachmentType.LONG_TEXT,
                uri       = text,
                wordCount = words,
                sizeLabel = "${text.length} caractères"
            )
        }

        fun fromAudioMemo(durationMs: Long, uri: Any?): GabomaAttachment {
            val mm = (durationMs / 60000).toInt()
            val ss = ((durationMs % 60000) / 1000).toInt()
            return GabomaAttachment(
                name       = "Mémo_%02d%02d.m4a".format(mm, ss),
                type       = AttachmentType.AUDIO,
                uri        = uri,
                durationMs = durationMs,
                sizeLabel  = "%d:%02d".format(mm, ss)
            )
        }
    }
}

data class GabomaMessage(
    val id          : String                  = UUID.randomUUID().toString(),
    val text        : String,
    val attachments : List<GabomaAttachment>  = emptyList(),
    val isAudio     : Boolean                 = false
)

// ─────────────────────────────────────────────────────────────
// 2. DESIGN TOKENS
// ─────────────────────────────────────────────────────────────

data class GabomaInputTheme(
    val bgBase          : Color = Color(0xFF0A0A0F),
    val bgSurface       : Color = Color(0xFF0F0F14),
    val bgSurfaceHigh   : Color = Color(0xFF1A1A20),
    val inputBarBg      : Color = Color.Transparent,
    val inputBarBorder  : Color = Color(0x1AFFFFFF),
    val textPrimary     : Color = Color(0xFFF0EFE8),
    val textSecondary   : Color = Color(0xFFB8B6A8),
    val textTertiary    : Color = Color(0xFF6E6C62),
    val accentPrimary   : Color = Color(0xFFC9A84C),
    val accentSecondary : Color = Color(0xFF00D4AA),
    val sendButtonBg    : Color = Color(0xFFC9A84C),
    val sendButtonIcon  : Color = Color(0xFF0A0A0F),
    val sidebarDivider  : Color = Color(0xFF1E1E28),
    val errorRed        : Color = Color(0xFFFF3B30)
)

// ─────────────────────────────────────────────────────────────
// 3. COMPOSANTS AUXILIAIRES
// ─────────────────────────────────────────────────────────────

@Composable
private fun LoxoExpandedMenu(
    theme    : GabomaInputTheme,
    selectedModel: String?,
    onModelSelect: (String) -> Unit,
    onAction : (LoxoAction) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xCC000000), // bg-elevated avec transparence
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── SECTION 1 : MODÈLES ──
            Text(
                text = "MODÈLES GABOMA",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textTertiary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ModelMenuItem("BLACK PANTHER", "Super Agent multi-agents", GabomaColors.AccentBlackPanther, GabomaIcons.lucide_ghost, selectedModel == "BLACK PANTHER", theme) { onModelSelect("BLACK PANTHER") }
                ModelMenuItem("AURATA", "Mode fondamental", GabomaColors.AccentBlackPanther, GabomaIcons.lucide_ghost, selectedModel == "AURATA", theme) { onModelSelect("AURATA") }
                ModelMenuItem("ÑKYEL", "Mode avancé", GabomaColors.InfoBlue, GabomaIcons.lucide_ghost, selectedModel == "ÑKYEL", theme) { onModelSelect("ÑKYEL") }
                ModelMenuItem("ONYX GRIS", "Agent IA autonome", GabomaColors.AccentBlackPanther, GabomaIcons.lucide_ghost, selectedModel == "ONYX GRIS", theme) { onModelSelect("ONYX GRIS") }
                ModelMenuItem("WANDANA", "L'Éléphant — recherche web", GabomaColors.AccentBlackPanther, GabomaIcons.Wandana, selectedModel == "WANDANA", theme) { onModelSelect("WANDANA") }
            }

            HorizontalDivider(color = theme.inputBarBorder, thickness = 0.5.dp)

            // ── SECTION 2 : CONNECTEURS ──
            Text(
                text = "CONNECTEURS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textTertiary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LoxoMenuItem(Modifier.weight(1f), "Caméra", GabomaIcons.CameraAlt, LoxoAction.UPLOAD_IMAGE, theme, onAction)
                LoxoMenuItem(Modifier.weight(1f), "Fichier", GabomaIcons.lucide_file_text, LoxoAction.UPLOAD_DOC, theme, onAction)
                LoxoMenuItem(Modifier.weight(1f), "Cloud MCP", GabomaIcons.lucide_layers, LoxoAction.MCP, theme, onAction)
            }
        }
    }
}

@Composable
private fun ModelMenuItem(
    name: String,
    desc: String,
    accentColor: Color,
    icon: ImageVector,
    isSelected: Boolean,
    theme: GabomaInputTheme,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0x1AC9A84C) else Color.Transparent // var(--accent-10)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) theme.accentPrimary else theme.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = theme.textPrimary)
                Text(text = desc, fontSize = 11.sp, color = theme.textSecondary)
            }
        }
    }
}

@Composable
private fun LoxoMenuItem(
    modifier: Modifier = Modifier,
    label  : String,
    icon   : ImageVector,
    action : LoxoAction,
    theme  : GabomaInputTheme,
    onAction: (LoxoAction) -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onAction(action) },
        shape = RoundedCornerShape(12.dp),
        color = Color(0x0DFFFFFF) // hover:bg-white/5
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = theme.textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = theme.textSecondary
                ),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GabomaWaves(isLive: Boolean, modifier: Modifier = Modifier) {
    val barCount = if (isLive) 12 else 4
    val infiniteTransition = rememberInfiniteTransition(label = "waves")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1.0f + (Math.random() * 0.5f).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(if(isLive) 400 else 1200, delayMillis = i * 100, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_scale_$i"
            )
            Box(
                modifier = Modifier
                    .width(if (isLive) 4.dp else 2.5.dp)
                    .height(if (isLive) (20.dp * scale) else (14.dp * scale))
                    .background(Color(0xFFC9A84C), CircleShape) // accent color
            )
        }
    }
}

@Composable
private fun MemoRecordingUI(
    seconds: Int,
    theme: GabomaInputTheme,
    onCancel: () -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCancel, modifier = Modifier
            .size(40.dp)
            .background(Color(0x33FFFFFF), CircleShape)) {
            Icon(Icons.Filled.Close, contentDescription = "Annuler", tint = theme.textPrimary)
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GabomaWaves(isLive = true)
            Text(
                text = "%d:%02d".format(seconds / 60, seconds % 60),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = theme.textPrimary
                )
            )
        }

        IconButton(onClick = onSend, modifier = Modifier
            .size(40.dp)
            .background(theme.errorRed.copy(alpha = 0.8f), CircleShape)) {
            Icon(Icons.Filled.Check, contentDescription = "Envoyer", tint = Color.White)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// 4. COMPOSANT PRINCIPAL
// ─────────────────────────────────────────────────────────────

@Composable
fun GabomaInputBarFull(
    theme        : GabomaInputTheme = GabomaInputTheme(),
    onSend       : (String, String?, List<GabomaAttachment>) -> Unit = { _, _, _ -> }
) {
    var text            by rememberSaveable { mutableStateOf("") }
    var loxoOpen        by remember { mutableStateOf(false) }
    var isLiveMode      by remember { mutableStateOf(false) }
    var isRecordingMemo by remember { mutableStateOf(false) }
    var memoSeconds     by remember { mutableIntStateOf(0) }
    var attachments     by remember { mutableStateOf<List<GabomaAttachment>>(emptyList()) }
    var selectedModel   by remember { mutableStateOf<String?>(null) }
    val haptic           = LocalHapticFeedback.current

    val hasText = text.trim().isNotEmpty()

    // ── Timer mémo vocal
    LaunchedEffect(isRecordingMemo) {
        if (isRecordingMemo) {
            memoSeconds = 0
            while (isRecordingMemo) {
                delay(1000L)
                memoSeconds++
            }
        } else {
            memoSeconds = 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // ── Menu LOXO monte
            AnimatedVisibility(
                visible = loxoOpen && !isLiveMode && !isRecordingMemo,
                enter   = slideInVertically { 60 } + fadeIn(tween(220)),
                exit    = slideOutVertically { 60 } + fadeOut(tween(180))
            ) {
                LoxoExpandedMenu(
                    theme    = theme,
                    selectedModel = selectedModel,
                    onModelSelect = {
                        selectedModel = it
                        loxoOpen = false
                    },
                    onAction = { action ->
                        loxoOpen = false
                        when (action) {
                            LoxoAction.UPLOAD_IMAGE -> { /* open image picker */ }
                            LoxoAction.UPLOAD_DOC   -> { /* open doc picker */ }
                            LoxoAction.MCP          -> { /* MCP connection */ }
                            else -> {}
                        }
                    }
                )
            }

            // ── The Pill (Haze Glassmorphism 2.0)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(0x08FFFFFF), // bg-white/3
                border = BorderStroke(1.dp, Color(0x1AFFFFFF))
            ) {
                AnimatedContent(
                    targetState = isLiveMode || isRecordingMemo,
                    label = "LiveMode_Transition",
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }
                ) { isLiveOrMemo ->
                    if (isLiveOrMemo) {
                        // ── LIVE MODE / MEMO DOCKED ──
                        MemoRecordingUI(
                            seconds = memoSeconds,
                            theme = theme,
                            onCancel = {
                                isLiveMode = false
                                isRecordingMemo = false
                            },
                            onSend = {
                                isLiveMode = false
                                isRecordingMemo = false
                                val audioAttachment = GabomaAttachment.fromAudioMemo(
                                    durationMs = memoSeconds * 1000L,
                                    uri = "audio://memo_${System.currentTimeMillis()}"
                                )
                                onSend("", selectedModel, listOf(audioAttachment))
                            }
                        )
                    } else {
                        // ── STANDARD LAYOUT ──
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // ── LEFT: Master Button (+) ou Model Pill ──
                            AnimatedContent(
                                targetState = selectedModel != null,
                                label = "MasterButton_Transition"
                            ) { hasModel ->
                                if (hasModel) {
                                    Surface(
                                        onClick = { loxoOpen = !loxoOpen },
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0x1AC9A84C), // var(--accent-10)
                                        border = BorderStroke(1.dp, Color(0x33C9A84C))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val icon = if (selectedModel == "WANDANA") GabomaIcons.Wandana else GabomaIcons.lucide_ghost
                                            Icon(imageVector = icon, contentDescription = null, tint = theme.accentPrimary, modifier = Modifier.size(14.dp))
                                            Text(
                                                text = selectedModel ?: "",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = theme.accentPrimary
                                            )
                                        }
                                    }
                                } else {
                                    IconButton(
                                        onClick = { loxoOpen = !loxoOpen },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0x0DFFFFFF), CircleShape)
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = "Menu", tint = theme.textPrimary)
                                    }
                                }
                            }

                            // ── CENTER: Textarea ──
                            BasicTextField(
                                value = text,
                                onValueChange = { text = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 24.dp),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = theme.textPrimary,
                                    lineHeight = 24.sp
                                ),
                                cursorBrush = SolidColor(theme.accentPrimary),
                                maxLines = 5,
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (text.isEmpty()) {
                                            Text("Demandez à Gaboma...", color = theme.textTertiary, fontSize = 16.sp)
                                        }
                                        innerTextField()
                                    }
                                }
                            )

                            // ── RIGHT: Mic/Wave ou Send ──
                            AnimatedContent(
                                targetState = hasText,
                                label = "ActionBlock_Transition"
                            ) { hasInput ->
                                if (hasInput) {
                                    IconButton(
                                        onClick = {
                                            onSend(text, selectedModel, attachments)
                                            text = ""
                                            attachments = emptyList()
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(theme.accentPrimary, CircleShape)
                                    ) {
                                        Icon(Icons.Filled.ArrowUpward, contentDescription = "Envoyer", tint = theme.bgBase)
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { isRecordingMemo = true },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(Icons.Filled.Mic, contentDescription = "Dictée", tint = theme.textSecondary)
                                        }
                                        IconButton(
                                            onClick = { isLiveMode = true },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            GabomaWaves(isLive = false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Disclaimer
            Text(
                text = "Gaboma AI peut faire des erreurs. Votre discernement reste souverain.",
                fontSize = 11.sp,
                color = theme.textTertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
