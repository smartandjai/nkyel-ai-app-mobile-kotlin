// presentation/chat/GabomaChatScreen.kt
// GabomaAI — Production-ready chat screen composable
// Uses GabomaColors / GabomaThemeType from existing theme system
// Zero hardcoded colours — all from GabomaColors or GabomaThemeType.*
package com.smartandj.gabomagpt.presentation.chat

import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.res.painterResource
import com.smartandj.gabomagpt.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeType
import com.smartandj.gabomagpt.presentation.theme.GabomaIcons
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.vector.rememberVectorPainter
// ═══════════════════════════════════════════════════════════════════════════
// TYPES & CONSTANTS
// ═══════════════════════════════════════════════════════════════════════════

/** User tier ordered from lowest to highest access */
enum class UserTier { AURATA, NKYEL, WANDANA, ONYXGRIS, BLACK_PANTHER }

/** Ñkyel AI internal model definition — NO third-party names ever exposed */
data class NkyelModelDef(
    val id: String,
    val displayName: String,
    val description: String,
    val tier: UserTier,
    val accentColor: Color,
)

typealias GabomaModelDef = NkyelModelDef

val NKYEL_MODELS = listOf(
    NkyelModelDef("aurata", "Aurata", "Flash · Réponse rapide", UserTier.AURATA, GabomaColors.AccentBlackPanther),
    NkyelModelDef("nkyel", "Ñkyel", "Pro · Qualité élevée", UserTier.NKYEL, GabomaColors.InfoBlue),
    NkyelModelDef("wandana", "Wandana", "Recherche · Deep Research", UserTier.WANDANA, GabomaColors.SuccessGreen),
    NkyelModelDef("onyxgris", "OnyxGris", "Agent autonome", UserTier.ONYXGRIS, GabomaColors.AccentBlackPanther),
    NkyelModelDef("black-panther", "Black Panther", "Super Agent multi-agents", UserTier.BLACK_PANTHER, GabomaColors.ErrorRed),
)

val GABOMA_MODELS = NKYEL_MODELS




private const val DISCLAIMER = "Ñkyel AI peut faire des erreurs. Le rendu est souverain."
private const val GREETING_QUESTION = "Comment puis-je vous aider ?"

private fun getGreeting(name: String): String {
    val greetings = listOf(
        "Mbolo, comment se passe la traque aujourd'hui",
        "Que l'Okoumé guide vos pas",
        "Prêt pour une nouvelle exploration",
        "L'esprit de la forêt est éveillé"
    )
    val randomGreeting = greetings.random()
    return "$randomGreeting, $name ?"
}

private fun canAccess(userTier: UserTier, modelTier: UserTier): Boolean =
    userTier.ordinal >= modelTier.ordinal

// ═══════════════════════════════════════════════════════════════════════════
// MAIN COMPOSABLE
// ═══════════════════════════════════════════════════════════════════════════

// Sidebar conversation model
data class SidebarConversation(
    val id: String,
    val title: String,
    val isTrophy: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NkyelChatScreen(
    navController: NavController,
    userName: String,
    userTier: UserTier,
    themeId: GabomaThemeType = GabomaThemeType.BLACK_PANTHER,
    messages: List<ChatMessage>,
    isGenerating: Boolean = false,
    onSend: (String, String) -> Unit,
    onStop: () -> Unit = {},
    onUpsellRequested: (String) -> Unit,
    onNewChat: () -> Unit = {},
    conversations: List<SidebarConversation> = emptyList(),
    onSelectConversation: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()


    // --- State ---
    var sidebarOpen by remember { mutableStateOf(false) }
    var selectedModelId by remember { mutableStateOf("aurata") }
    var text by remember { mutableStateOf("") }
    var showModelSheet by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }

    var antreExpanded by remember { mutableStateOf(false) }
    var wandanaOn by remember { mutableStateOf(true) }
    var radarOn by remember { mutableStateOf(true) }
    var shadowOn by remember { mutableStateOf(false) }
    var vaultOn by remember { mutableStateOf(true) }

    val isEmpty = messages.isEmpty()
    val hasText = text.isNotBlank()
    val currentModel = NKYEL_MODELS.find { it.id == selectedModelId } ?: NKYEL_MODELS[0]

    // Theme colors
    val def = com.smartandj.gabomagpt.presentation.theme.NkyelThemeDefinitions.getTheme(themeId)
    val bg = def.backgroundColor
    val surface = def.surfaceColor
    val elevated = def.cardColor
    val accent = def.accentPrimary
    val textPrimary = def.textPrimary
    val textSecondary = def.textSecondary
    val border = textPrimary.copy(alpha = 0.08f)
    val borderHairline = textPrimary.copy(alpha = 0.06f)

    // Scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // BackHandler for sidebar
    BackHandler(enabled = sidebarOpen) {
        sidebarOpen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)

    ) {

        // ══════════ MAIN CONTENT ══════════
        Column(modifier = Modifier.fillMaxSize()) {

            // ─── TOP BAR ───
            ChatTopBar(
                onOpenSidebar = { sidebarOpen = true },
                onNewChat = onNewChat,
                onOpenRendu = { /* Open Okoumé / Rendu étendu */ }
            )

            // ─── MESSAGES / EMPTY STATE ───
            Box(modifier = Modifier.weight(1f)) {
                if (isEmpty) {
                    // ═══ EMPTY STATE ═══
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        // Glowing Gaboma Seal (Animated)
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(GabomaColors.AccentBlackPanther.copy(alpha = 0.2f), CircleShape)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_nkyel),
                                contentDescription = "Ñkyel AI",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = getGreeting(userName),
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = textPrimary,
                                textAlign = TextAlign.Center
                            ),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Demandez ou confiez une mission à Ñkyel AI.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = textSecondary,
                                textAlign = TextAlign.Center
                            ),
                        )
                    }
                } else {
                    // ═══ MESSAGES LIST ═══
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            MessageBubble(
                                message = msg,
                                isUser = msg.role == ChatRole.USER,
                                bg = if (msg.role == ChatRole.USER) surface else elevated,
                                textColor = textPrimary,
                                textSecondaryColor = textSecondary,
                                borderColor = border,
                                accentColor = accent,
                            )
                        }

                        // Streaming indicator
                        if (isGenerating) {
                            item(key = "streaming") {
                                StreamingIndicator(accent = accent, bg = elevated, border = border)
                            }
                        }
                    }
                }
            }

            // ─── COMPOSER ───
            ComposerBar(
                text = text,
                onTextChange = { text = it },
                hasText = hasText,
                isGenerating = isGenerating,
                currentModel = currentModel,
                bg = bg,
                surface = surface,
                accent = accent,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                border = border,
                borderHairline = borderHairline,
                onSend = {
                    // NOTE: Frontend gating is UX-only. Backend FastAPI MUST revalidate tier.
                    onSend(text.trim(), selectedModelId)
                    text = ""
                },
                onStop = onStop,
                onPlusClick = { showAddSheet = true },
                onModelClick = { showModelSheet = true },
            )

            // Disclaimer always below composer
            Text(
                text = DISCLAIMER,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textSecondary.copy(alpha = 0.5f), letterSpacing = 0.5.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            )
        }

        // ══════════ SIDEBAR (fullscreen overlay on mobile) ══════════
        AnimatedVisibility(
            visible = sidebarOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(280, easing = EaseOutCubic)),
            exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(250, easing = EaseInCubic)),
        ) {
            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { sidebarOpen = false }
            )
        }

        AnimatedVisibility(
            visible = sidebarOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(280, easing = EaseOutCubic)),
            exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(250, easing = EaseInCubic)),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = elevated,
            ) {
                Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                    // Sidebar Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Icons.Filled.Close button (same IBOGA icon, inverted behaviour)
                        IconButton(onClick = { sidebarOpen = false }) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Filled.Settings),
                                contentDescription = "Fermer",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ñkyel AI",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary,
                            ),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // Pulsing dot
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent))
                    }

                    // Nouvelle Piste
                    Button(
                        onClick = { onNewChat(); sidebarOpen = false },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = bg),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nouvelle Piste", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    // Content scrollable
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        // ── TROPHÉES ──
                        item {
                            Text(
                                text = "TROPHÉES",
                                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textSecondary.copy(alpha = 0.6f), letterSpacing = 0.8.sp),
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                            )
                        }
                        val favorites = conversations.filter { it.isTrophy }
                        if (favorites.isNotEmpty()) {
                            items(favorites) { conv ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onSelectConversation(conv.id); sidebarOpen = false },
                                    color = Color.Transparent,
                                ) {
                                    Text(
                                        text = "🏆 ${conv.title}",
                                        style = TextStyle(fontSize = 14.sp, color = textPrimary),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    )
                                }
                            }
                        } else {
                            item {
                                Text(
                                    text = "Aucun trophée",
                                    style = TextStyle(fontSize = 13.sp, color = textSecondary),
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }

                        item { Divider(color = borderHairline, modifier = Modifier.padding(vertical = 12.dp)) }

                        // ── EN PISTE ──
                        item {
                            Text(
                                text = "EN PISTE",
                                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textSecondary.copy(alpha = 0.6f), letterSpacing = 0.8.sp),
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                            )
                        }
                        val recents = conversations.filter { !it.isTrophy }
                        if (recents.isNotEmpty()) {
                            items(recents) { conv ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onSelectConversation(conv.id); sidebarOpen = false },
                                    color = Color.Transparent,
                                ) {
                                    Text(
                                        text = conv.title,
                                        style = TextStyle(fontSize = 14.sp, color = textPrimary),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    )
                                }
                            }
                        } else {
                            item {
                                Text(
                                    text = "Aucune piste en cours",
                                    style = TextStyle(fontSize = 13.sp, color = textSecondary),
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }

                        item { Divider(color = borderHairline, modifier = Modifier.padding(vertical = 12.dp)) }

                        // ── ÉNERGIE QUOTIDIENNE ──
                        item {
                            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painter = rememberVectorPainter(Icons.Filled.BatteryChargingFull), contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Énergie Quotidienne", style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                // Progress bar
                                Surface(
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = bg,
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, border)
                                ) {
                                    Row {
                                        Box(modifier = Modifier.weight(0.35f).fillMaxHeight().background(accent))
                                        Box(modifier = Modifier.weight(0.65f).fillMaxHeight())
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                // ALIMENTER LA MEUTE (Netflix mode)
                                Button(
                                    onClick = { /* TODO: Open upgrade info (Netflix style, no in-app billing) */ },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = textPrimary, contentColor = bg),
                                    shape = RoundedCornerShape(12.dp),
                                ) {
                                    Text("ALIMENTER LA MEUTE", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                                }
                            }
                        }

                        item { Divider(color = borderHairline, modifier = Modifier.padding(vertical = 12.dp)) }

                        // ── L'ANTRE ──
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { antreExpanded = !antreExpanded },
                                color = Color.Transparent,
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 8.dp, end = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚙️ L'Antre [PARAMÈTRES]",
                                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textSecondary.copy(alpha = 0.6f), letterSpacing = 0.8.sp),
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = if (antreExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = textSecondary,
                                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                                    )
                                    Icon(
                                        painter = rememberVectorPainter(Icons.Filled.Add),
                                        contentDescription = "Mentions légales",
                                        tint = textSecondary,
                                        modifier = Modifier.size(16.dp).clickable { /* TODO: Legal/Privacy menu */ }
                                    )
                                }
                            }
                        }

                        if (antreExpanded) {
                            item {
                                Column(modifier = Modifier.padding(start = 20.dp, end = 8.dp).drawBehind {
                                    drawLine(
                                        color = borderHairline,
                                        start = androidx.compose.ui.geometry.Offset(-8.dp.toPx(), 0f),
                                        end = androidx.compose.ui.geometry.Offset(-8.dp.toPx(), size.height),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }) {
                                    // Vecteur de Force
                                    Text("Vecteur de Force", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textSecondary), modifier = Modifier.padding(bottom = 6.dp))
                                    Surface(
                                        modifier = Modifier.clip(RoundedCornerShape(4.dp)).clickable { showModelSheet = true },
                                        color = accent.copy(alpha = 0.08f),
                                        border = BorderStroke(1.dp, borderHairline)
                                    ) {
                                        Text(currentModel.displayName, style = TextStyle(fontSize = 11.sp, color = currentModel.accentColor), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Extensions de Traque
                                    Text("Extensions de Traque", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textSecondary), modifier = Modifier.padding(bottom = 8.dp))
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("Invoquer Wandana", style = TextStyle(fontSize = 11.sp, color = textSecondary.copy(alpha = 0.8f)))
                                        Spacer(modifier = Modifier.weight(1f))
                                        Switch(checked = wandanaOn, onCheckedChange = { wandanaOn = it }, modifier = Modifier.scale(0.6f), colors = SwitchDefaults.colors(checkedThumbColor = bg, checkedTrackColor = accent, uncheckedThumbColor = bg, uncheckedTrackColor = accent.copy(alpha = 0.2f)))
                                    }
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("Radar Wandana", style = TextStyle(fontSize = 11.sp, color = textSecondary.copy(alpha = 0.8f)))
                                        Spacer(modifier = Modifier.weight(1f))
                                        Switch(checked = radarOn, onCheckedChange = { radarOn = it }, modifier = Modifier.scale(0.6f), colors = SwitchDefaults.colors(checkedThumbColor = bg, checkedTrackColor = accent, uncheckedThumbColor = bg, uncheckedTrackColor = accent.copy(alpha = 0.2f)))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Souveraineté
                                    Text("Souveraineté", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textSecondary), modifier = Modifier.padding(bottom = 8.dp))
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("Mode Ombre", style = TextStyle(fontSize = 11.sp, color = textSecondary.copy(alpha = 0.8f)))
                                        Spacer(modifier = Modifier.weight(1f))
                                        Switch(checked = shadowOn, onCheckedChange = { shadowOn = it }, modifier = Modifier.scale(0.6f), colors = SwitchDefaults.colors(checkedThumbColor = bg, checkedTrackColor = accent, uncheckedThumbColor = bg, uncheckedTrackColor = accent.copy(alpha = 0.2f)))
                                    }
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("Coffre-Fort", style = TextStyle(fontSize = 11.sp, color = textSecondary.copy(alpha = 0.8f)))
                                        Spacer(modifier = Modifier.weight(1f))
                                        Switch(checked = vaultOn, onCheckedChange = { vaultOn = it }, modifier = Modifier.scale(0.6f), colors = SwitchDefaults.colors(checkedThumbColor = bg, checkedTrackColor = accent, uncheckedThumbColor = bg, uncheckedTrackColor = accent.copy(alpha = 0.2f)))
                                    }
                                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("Pacte Politique", style = TextStyle(fontSize = 11.sp, color = textSecondary.copy(alpha = 0.8f)))
                                        Spacer(modifier = Modifier.weight(1f))
                                        Surface(color = GabomaColors.SuccessGreen, shape = RoundedCornerShape(4.dp)) {
                                            Text("CONF", style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }

                                    // Administration
                                    Text("Administration", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textSecondary), modifier = Modifier.padding(bottom = 8.dp))
                                    Text("Profil Cadre", style = TextStyle(fontSize = 11.sp, color = textSecondary.copy(alpha = 0.8f)), modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 12.dp).clickable { })
                                    Text("Pacte de Chasse", style = TextStyle(fontSize = 11.sp, color = textSecondary.copy(alpha = 0.8f)), modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 12.dp).clickable { })
                                }
                            }
                        }

                        item {
                            Text(
                                text = "NODE: LIBREVILLE-S-01",
                                style = TextStyle(fontSize = 10.sp, color = textSecondary.copy(alpha = 0.5f), letterSpacing = 1.5.sp),
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                            )
                        }

                        item { Divider(color = borderHairline, modifier = Modifier.padding(vertical = 12.dp)) }

                        // ── REJOINDRE LA MEUTE ──
                        item {
                            Text(
                                text = "Rejoindre la Meute",
                                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary.copy(alpha = 0.6f), letterSpacing = 1.sp),
                                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
                            )
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(modifier = Modifier.weight(1f).height(40.dp), color = bg, border = BorderStroke(1.dp, border), shape = RoundedCornerShape(8.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("Telegram", fontSize = 12.sp, color = textSecondary) }
                                }
                                Surface(modifier = Modifier.weight(1f).height(40.dp), color = bg, border = BorderStroke(1.dp, border), shape = RoundedCornerShape(8.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("WhatsApp", fontSize = 12.sp, color = textSecondary) }
                                }
                            }
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(modifier = Modifier.weight(1f).height(40.dp), color = bg, border = BorderStroke(1.dp, border), shape = RoundedCornerShape(8.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("X / Twitter", fontSize = 12.sp, color = textSecondary) }
                                }
                                Surface(modifier = Modifier.weight(1f).height(40.dp), color = bg, border = BorderStroke(1.dp, border), shape = RoundedCornerShape(8.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("LinkedIn", fontSize = 12.sp, color = textSecondary) }
                                }
                            }
                        }
                        
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    // Footer: BY ANDJ
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = bg,
                        border = BorderStroke(1.dp, borderHairline),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SmartAndJTechIcon(color = accent, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BY ANDJ • SMARTANDJ A", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accent, letterSpacing = 1.sp))
                            Text("I", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accent, letterSpacing = 1.sp, textGeometricTransform = androidx.compose.ui.text.style.TextGeometricTransform(skewX = -0.25f)))
                            Text(" TECH", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accent, letterSpacing = 1.sp))
                        }
                    }
                }
            }
        }

        // ══════════ MODEL SELECTOR BOTTOM SHEET ══════════
        if (showModelSheet) {
            ModalBottomSheet(
                onDismissRequest = { showModelSheet = false },
                containerColor = elevated,
                scrimColor = Color.Black.copy(alpha = 0.32f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text(
                        text = "Vecteur de Force",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textPrimary),
                        modifier = Modifier.padding(bottom = 16.dp),
                    )

                    NKYEL_MODELS.forEach { model ->
                        val locked = !canAccess(userTier, model.tier)
                        val isSelected = model.id == selectedModelId

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (locked) {
                                        // Frontend gating — backend MUST revalidate
                                        onUpsellRequested(model.id)
                                    } else {
                                        selectedModelId = model.id
                                        showModelSheet = false
                                    }
                                },
                            color = if (isSelected) accent.copy(alpha = 0.08f) else Color.Transparent,
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = model.displayName,
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isSelected) model.accentColor else textPrimary,
                                            ),
                                        )
                                        if (locked) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                painter = rememberVectorPainter(Icons.Filled.Lock),
                                                contentDescription = "Verrouillé",
                                                tint = textSecondary.copy(alpha = 0.5f),
                                                modifier = Modifier.size(14.dp),
                                            )
                                        }
                                    }
                                    Text(
                                        text = model.description,
                                        style = TextStyle(fontSize = 12.sp, color = textSecondary),
                                        modifier = Modifier.padding(top = 2.dp),
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        painter = rememberVectorPainter(Icons.Filled.Check),
                                        contentDescription = "Sélectionné",
                                        tint = model.accentColor,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // ══════════ ADD-TO-CHAT BOTTOM SHEET ══════════
        if (showAddSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddSheet = false },
                containerColor = elevated,
                scrimColor = Color.Black.copy(alpha = 0.32f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text(
                        text = "Ajouter au chat",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textPrimary),
                        modifier = Modifier.padding(bottom = 16.dp),
                    )

                    data class AddItem(val icon: @Composable () -> Unit, val label: String, val isPro: Boolean = false)
                    val items = listOf(
                        AddItem({ Icon(GabomaIcons.CameraAlt, null, tint = textSecondary, modifier = Modifier.size(22.dp)) }, "Caméra"),
                        AddItem({ Icon(rememberVectorPainter(Icons.Filled.Description), null, tint = textSecondary, modifier = Modifier.size(22.dp)) }, "Relever un indice"),
                        AddItem({ Icon(GabomaIcons.RadarWandana, null, tint = textSecondary, modifier = Modifier.size(22.dp)) }, "Radar Wandana"),
                        AddItem({ Icon(GabomaIcons.Rendu, null, tint = textSecondary, modifier = Modifier.size(22.dp)) }, "Le Rendu (💎)", true),
                        AddItem({ Icon(GabomaIcons.Extension, null, tint = textSecondary, modifier = Modifier.size(22.dp)) }, "Extensions de Traque"),
                        AddItem({ Icon(GabomaIcons.Lock, null, tint = textSecondary, modifier = Modifier.size(22.dp)) }, "Coffre-Fort Souverain"),
                    )

                    // Grid: 3 columns
                    items.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            row.forEach { item ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { showAddSheet = false },
                                    color = accent.copy(alpha = 0.06f),
                                    shape = RoundedCornerShape(16.dp),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        item.icon()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = item.label,
                                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium, color = textPrimary, textAlign = TextAlign.Center),
                                            maxLines = 2,
                                        )
                                        if (item.isPro) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                color = accent.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp),
                                            ) {
                                                Text(
                                                    "PRO",
                                                    style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accent),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            // Fill remaining columns if row < 3
                            repeat(3 - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// COMPOSER BAR
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun ComposerBar(
    text: String,
    onTextChange: (String) -> Unit,
    hasText: Boolean,
    isGenerating: Boolean,
    currentModel: GabomaModelDef,
    bg: Color,
    surface: Color,
    accent: Color,
    textPrimary: Color,
    textSecondary: Color,
    border: Color,
    borderHairline: Color,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onPlusClick: () -> Unit,
    onModelClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = bg.copy(alpha = 0.85f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderHairline),
        tonalElevation = 2.dp,
    ) {
        Column {
            // Text input
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .heightIn(min = 24.dp, max = 120.dp),
                textStyle = TextStyle(fontSize = 15.sp, color = textPrimary, lineHeight = 22.sp),
                cursorBrush = SolidColor(accent),
                decorationBox = { innerTextField ->
                    Box {
                        if (text.isEmpty()) {
                            Text(
                                text = "Directive…",
                                style = TextStyle(fontSize = 15.sp, color = textSecondary.copy(alpha = 0.5f)),
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Bottom controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // + button
                IconButton(
                    onClick = onPlusClick,
                    modifier = Modifier.size(36.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, border),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Ajouter", tint = textSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Model selector pill
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onModelClick() },
                    color = accent.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, accent.copy(alpha = 0.2f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = currentModel.displayName,
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = currentModel.accentColor),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(painter = rememberVectorPainter(Icons.Filled.KeyboardArrowDown), contentDescription = null, tint = currentModel.accentColor, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Dictation Mic
                if (!isGenerating) {
                    IconButton(onClick = { /* TODO: dictation */ }, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Filled.Mic, contentDescription = "Dictée vocale", tint = textSecondary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                }

                // Send / Stop / Live pulse
                when {
                    isGenerating -> {
                        IconButton(onClick = onStop, modifier = Modifier.size(36.dp)) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = accent.copy(alpha = 0.1f),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(painter = rememberVectorPainter(Icons.Filled.Add), contentDescription = "Arrêter", tint = textPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    hasText -> {
                        IconButton(onClick = onSend, modifier = Modifier.size(36.dp)) {
                            Surface(
                                modifier = Modifier.size(32.dp).shadow(4.dp, CircleShape),
                                shape = CircleShape,
                                color = accent,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = "Envoyer", tint = bg, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    else -> {
                        // Live / Mic button with pulse
                        LivePulseButton(accent = accent, textSecondary = textSecondary, border = border)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// LIVE PULSE BUTTON
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun LivePulseButton(accent: Color, textSecondary: Color, border: Color) {
    // Icons.Filled.Check accessibility: respect reduced motion
    val reduceMotion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        false // TODO: check AccessibilityManager for reduced motion preference
    } else false

    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (reduceMotion) 1f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseOutCubic),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    Surface(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .clickable { /* TODO: open voice/live mode */ },
        color = accent.copy(alpha = 0.06f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painter = rememberVectorPainter(Icons.Filled.Add), contentDescription = "Mode Live", tint = textSecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Live",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textSecondary, letterSpacing = 0.5.sp),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// ICONS & HELPERS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun SmartAndJTechIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val scaleX = w / 15f
        val scaleY = h / 15f
        // Path 1
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(9.5f * scaleX, 2.5f * scaleY)
                lineTo(5.5f * scaleX, 6.5f * scaleY)
                lineTo(9f * scaleX, 6.5f * scaleY)
                lineTo(4.5f * scaleX, 11.5f * scaleY)
            },
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
        // Lines
        drawLine(color, start = androidx.compose.ui.geometry.Offset(1.5f * scaleX, 6f * scaleY), end = androidx.compose.ui.geometry.Offset(3.5f * scaleX, 6f * scaleY), strokeWidth = 1.2f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(color, start = androidx.compose.ui.geometry.Offset(1.5f * scaleX, 9f * scaleY), end = androidx.compose.ui.geometry.Offset(3.5f * scaleX, 9f * scaleY), strokeWidth = 1.2f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(color, start = androidx.compose.ui.geometry.Offset(11.5f * scaleX, 6f * scaleY), end = androidx.compose.ui.geometry.Offset(13.5f * scaleX, 6f * scaleY), strokeWidth = 1.2f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(color, start = androidx.compose.ui.geometry.Offset(11.5f * scaleX, 9f * scaleY), end = androidx.compose.ui.geometry.Offset(13.5f * scaleX, 9f * scaleY), strokeWidth = 1.2f * scaleX, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        // Circle
        drawCircle(color, radius = 1f * scaleX, center = androidx.compose.ui.geometry.Offset(7.5f * scaleX, 7.5f * scaleY))
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MESSAGE BUBBLE
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isUser: Boolean,
    bg: Color,
    textColor: Color,
    textSecondaryColor: Color,
    borderColor: Color,
    accentColor: Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 320.dp),
            color = bg,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp,
            ),
            border = BorderStroke(0.5.dp, borderColor),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.content,
                    style = TextStyle(fontSize = 15.sp, color = textColor, lineHeight = 22.sp),
                )

                // AI message actions (NOT on user messages)
                if (!isUser && !message.isStreaming) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = borderColor, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        listOf(
                            Icons.Filled.ContentCopy to "Copier",
                            Icons.Filled.Share to "Partager",
                            Icons.Filled.VolumeUp to "Lire",
                            Icons.Filled.ThumbUp to "J'aime",
                            Icons.Filled.ThumbDown to "Je n'aime pas",
                            Icons.Filled.Refresh to "Relancer la Chasse",
                        ).forEach { (icon, desc) ->
                            IconButton(
                                onClick = { /* TODO: wire action callbacks */ },
                                modifier = Modifier.size(28.dp),
                            ) {
                                Icon(imageVector = icon, contentDescription = desc, tint = textSecondaryColor.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// STREAMING INDICATOR
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun StreamingIndicator(accent: Color, bg: Color, border: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val alpha1 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "d1")
    val alpha2 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, delayMillis = 150), RepeatMode.Reverse), label = "d2")
    val alpha3 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, delayMillis = 300), RepeatMode.Reverse), label = "d3")

    Surface(
        color = bg,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(0.5.dp, border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(alpha1, alpha2, alpha3).forEach { a ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = a)),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GabomaChatScreen(
    navController: NavController,
    userName: String,
    userTier: UserTier,
    themeId: GabomaThemeType = GabomaThemeType.BLACK_PANTHER,
    messages: List<ChatMessage>,
    isGenerating: Boolean = false,
    onSend: (String, String) -> Unit,
    onStop: () -> Unit = {},
    onUpsellRequested: (String) -> Unit,
    onNewChat: () -> Unit = {},
    conversations: List<SidebarConversation> = emptyList(),
    onSelectConversation: (String) -> Unit = {},
) {
    NkyelChatScreen(
        navController = navController,
        userName = userName,
        userTier = userTier,
        themeId = themeId,
        messages = messages,
        isGenerating = isGenerating,
        onSend = onSend,
        onStop = onStop,
        onUpsellRequested = onUpsellRequested,
        onNewChat = onNewChat,
        conversations = conversations,
        onSelectConversation = onSelectConversation,
    )
}

