/**
 * Ñkyel AI — Core Product Screens
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Ultra-professional native Compose implementations matching the Next.js PWA experience.
 * Inspired by the best of Manus, Claude, ChatGPT, unified under Ñkyel brand & architecture.
 */
package com.smartandj.gabomagpt.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import com.smartandj.gabomagpt.R
import com.smartandj.gabomagpt.presentation.chat.NkyelChatViewModel
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════
// MODEL SELECTOR BOTTOM SHEET — 6 Canonical Sovereign Models
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NkyelModelSelectorSheet(
    modelOptions: List<NkyelChatViewModel.NkyelModelItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Text(
                    "Sélectionner un modèle",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GabomaColors.AccentBlackPanther.copy(alpha = 0.15f),
                ) {
                    Text(
                        "Souverain",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GabomaColors.AccentBlackPanther,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            modelOptions.forEachIndexed { idx, model ->
                val isSelected = idx == selectedIndex
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) GabomaColors.AccentBlackPanther.copy(alpha = 0.12f) else Color.Transparent,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GabomaColors.AccentBlackPanther.copy(alpha = 0.5f)) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onSelect(idx)
                            onDismiss()
                        },
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    model.name,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                if (model.tag.isNotBlank()) {
                                    Spacer(Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (model.tag == "Pro") GabomaColors.AccentBlackPanther else MaterialTheme.colorScheme.surfaceVariant,
                                    ) {
                                        Text(
                                            model.tag,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (model.tag == "Pro") Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.5.dp),
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(
                                model.desc,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Sélectionné",
                                tint = GabomaColors.AccentBlackPanther,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// HOME SCREEN — Matching Next.js Home component
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelHomeScreen(
    navController: NavController,
    userName: String,
    viewModel: NkyelChatViewModel = hiltViewModel(),
) {
    var text by remember { mutableStateOf("") }
    var searchEnabled by remember { mutableStateOf(false) }
    var deepResearchEnabled by remember { mutableStateOf(false) }
    var agentModeEnabled by remember { mutableStateOf(true) }
    var showModelPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val launchMission = {
        if (text.isNotBlank()) {
            val prompt = text.trim()
            text = ""
            scope.launch {
                viewModel.sendMessage(prompt)
                navController.navigate("chat") { launchSingleTop = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(36.dp))

        // Small refined brand badge adapted to theme (no huge logo)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GabomaColors.AccentBlackPanther.copy(alpha = 0.25f)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nkyel),
                    contentDescription = "Ñkyel",
                    tint = GabomaColors.AccentBlackPanther,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Ñkyel AI",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "· VIFLOW™",
                    fontSize = 11.sp,
                    color = GabomaColors.AccentBlackPanther,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Greeting title
        Text(
            "Que souhaitez-vous accomplir ?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Intelligence agentique autonome · Qwen3 & GPT-OSS",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        // Category cards
        val categories = listOf(
            Triple("Construire", "Applications, sites, automatisations", Color(0xFF2563EB)),
            Triple("Créer", "Documents, présentations, visuels", Color(0xFFC49A48)),
            Triple("Rechercher", "Analyse, exploration, veille", Color(0xFF27856A)),
            Triple("Automatiser", "Workflows, tâches récurrentes", Color(0xFF7857B5)),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            categories.forEach { (title, desc, color) ->
                Card(
                    modifier = Modifier.weight(1f).height(96.dp),
                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f)),
                    shape = RoundedCornerShape(14.dp),
                    onClick = { text = "Créer un projet : $desc" },
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = color)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            desc,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Example prompt pills
        val promptExamples = listOf(
            "Analyse les tendances technologiques en 2026",
            "Prépare un plan de recherche de marché",
            "Rédige un rapport d'architecture logicielle",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            promptExamples.forEach { ex ->
                SuggestionChip(
                    onClick = { text = ex.substringAfter(" ") },
                    label = { Text(ex, fontSize = 11.sp, maxLines = 1) },
                    shape = RoundedCornerShape(12.dp),
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Advanced Composer Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            tonalElevation = 2.dp,
        ) {
            Column(Modifier.padding(12.dp)) {
                // Mode toggles row (Search, Deep, Agent)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        selected = searchEnabled,
                        onClick = { searchEnabled = !searchEnabled },
                        label = { Text("Web", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Public, null, Modifier.size(13.dp)) },
                        shape = RoundedCornerShape(10.dp),
                    )
                    FilterChip(
                        selected = deepResearchEnabled,
                        onClick = { deepResearchEnabled = !deepResearchEnabled },
                        label = { Text("Approfondi", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Explore, null, Modifier.size(13.dp)) },
                        shape = RoundedCornerShape(10.dp),
                    )
                    FilterChip(
                        selected = agentModeEnabled,
                        onClick = { agentModeEnabled = !agentModeEnabled },
                        label = { Text("Agent", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Psychology, null, Modifier.size(13.dp)) },
                        shape = RoundedCornerShape(10.dp),
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Text field
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Décrivez votre mission ou question...", fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { launchMission() }),
                )

                Spacer(Modifier.height(6.dp))

                // Action buttons row (Attach, Mic, Model, Send)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { /* File picker */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.AttachFile, "Joindre", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { /* Voice input */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Mic, "Micro", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.weight(1f))

                    // Model tag selector
                    val selectedModelIdx by viewModel.selectedModel.collectAsState()
                    val currentModel = viewModel.modelOptions.getOrElse(selectedModelIdx) { viewModel.modelOptions[1] }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GabomaColors.AccentBlackPanther.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clickable { showModelPicker = true }
                            .padding(end = 8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                currentModel.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GabomaColors.AccentBlackPanther,
                            )
                            if (currentModel.tag.isNotBlank()) {
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    currentModel.tag,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    FilledIconButton(
                        onClick = { launchMission() },
                        enabled = text.isNotBlank(),
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = GabomaColors.AccentBlackPanther,
                            contentColor = Color.Black,
                        ),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Lancer", Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "SmartANDJ AI Technologies · Habakkuk Vision Engine™",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
        Spacer(Modifier.height(16.dp))
    }

    if (showModelPicker) {
        val selectedModelIdx by viewModel.selectedModel.collectAsState()
        NkyelModelSelectorSheet(
            modelOptions = viewModel.modelOptions,
            selectedIndex = selectedModelIdx,
            onSelect = { viewModel.setModel(it) },
            onDismiss = { showModelPicker = false },
        )
    }
}

// ═══════════════════════════════════════════════════════════
// CHAT SCREEN — Ultra-professional with Neon persistence & AG-UI
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelChatScreen(
    navController: NavController,
    viewModel: NkyelChatViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val currentConvId by viewModel.currentConversationId.collectAsState()
    val error by viewModel.error.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var inputText by remember { mutableStateOf("") }
    var searchEnabled by remember { mutableStateOf(false) }
    var deepResearchEnabled by remember { mutableStateOf(false) }
    var agentModeEnabled by remember { mutableStateOf(true) }
    var showChatModelPicker by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll on new messages
    LaunchedEffect(messages.size, messages.lastOrNull()?.content?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val handleSend = {
        if (inputText.isNotBlank()) {
            val prompt = inputText.trim()
            inputText = ""
            viewModel.sendMessage(prompt)
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top bar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                }
                Column(Modifier.weight(1f)) {
                    val activeConv = conversations.find { it.id == currentConvId }
                    val title = activeConv?.title
                        ?: if (messages.isNotEmpty()) messages.first().content.take(35)
                        else "Nouvelle mission"
                    Text(
                        title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isStreaming) {
                            Box(
                                Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF27856A))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("En cours d'exécution...", fontSize = 11.sp, color = Color(0xFF27856A))
                        } else {
                            val selectedModelIdx by viewModel.selectedModel.collectAsState()
                            val currentModel = viewModel.modelOptions.getOrElse(selectedModelIdx) { viewModel.modelOptions[1] }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showChatModelPicker = true },
                            ) {
                                Text(
                                    "${currentModel.name} · Actif",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (currentModel.tag.isNotBlank()) {
                                    Spacer(Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (currentModel.tag == "Pro") GabomaColors.AccentBlackPanther.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    ) {
                                        Text(
                                            currentModel.tag,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GabomaColors.AccentBlackPanther,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isStreaming) {
                    IconButton(onClick = { viewModel.stopStreaming() }) {
                        Icon(Icons.Default.Stop, "Arrêter", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Error banner
        error?.let { err ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(err, Modifier.weight(1f), color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
                }
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            // Empty state when no messages: Small subtle badge adapted to theme
            if (messages.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Small theme-adapted brand mark (not a loud logo)
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GabomaColors.AccentBlackPanther.copy(alpha = 0.3f)),
                            modifier = Modifier.size(44.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_nkyel),
                                    contentDescription = "Ñkyel",
                                    tint = GabomaColors.AccentBlackPanther,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "Que souhaitez-vous accomplir ?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Posez une question ou confiez une mission autonome.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                        )

                        Spacer(Modifier.height(24.dp))

                        val promptChips = listOf(
                            "Analyse les tendances technologiques en 2026",
                            "Prépare un plan de recherche de marché",
                            "Rédige un rapport d'architecture logicielle",
                            "Automatise l'extraction de données web",
                        )
                        promptChips.forEach { chipText ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        inputText = chipText.substringAfter(" ")
                                    },
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(chipText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.Default.ChevronRight, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                if (msg.role == "user") {
                    // User message bubble (sleek right aligned)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Surface(
                            shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                            color = GabomaColors.AccentBlackPanther.copy(alpha = 0.20f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GabomaColors.AccentBlackPanther.copy(alpha = 0.35f)),
                            modifier = Modifier.widthIn(max = 310.dp),
                        ) {
                            Text(
                                msg.content,
                                Modifier.padding(14.dp),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                } else {
                    // Assistant message (Claude / ChatGPT style with subtle brand mark)
                    Column(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Small subtle theme-adapted badge
                            Icon(
                                painter = painterResource(id = R.drawable.ic_nkyel),
                                contentDescription = "Ñkyel",
                                tint = GabomaColors.AccentBlackPanther,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Ñkyel", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            msg.model?.let {
                                Text(
                                    " · ${it.take(18)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(msg.content)) },
                                modifier = Modifier.size(28.dp),
                            ) {
                                Icon(Icons.Default.ContentCopy, "Copier", Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(Modifier.height(6.dp))

                        // Tool activity banner if active
                        msg.toolActivity?.let { toolName ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp),
                            ) {
                                Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Search, null, Modifier.size(13.dp), tint = GabomaColors.AccentBlackPanther)
                                    Spacer(Modifier.width(6.dp))
                                    Text(toolName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    if (msg.isStreaming) {
                                        Spacer(Modifier.width(8.dp))
                                        CircularProgressIndicator(Modifier.size(12.dp), strokeWidth = 1.5.dp, color = GabomaColors.AccentBlackPanther)
                                    }
                                }
                            }
                        }

                        // Content or reflection
                        if (msg.content.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(
                                        msg.content,
                                        fontSize = 14.sp,
                                        lineHeight = 21.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                    if (msg.isStreaming) {
                                        Spacer(Modifier.height(4.dp))
                                        Box(
                                            Modifier.size(8.dp, 14.dp).background(GabomaColors.AccentBlackPanther)
                                        )
                                    }
                                }
                            }
                        } else if (msg.isStreaming) {
                            Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = GabomaColors.AccentBlackPanther)
                                Spacer(Modifier.width(10.dp))
                                Text("Réflexion et analyse en cours...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Sources row if any
                        if (msg.sources.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                msg.sources.forEachIndexed { i, src ->
                                    AssistChip(
                                        onClick = { /* Open source link */ },
                                        label = { Text("${i + 1} ${src.title ?: src.url}", maxLines = 1, fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.Link, null, Modifier.size(12.dp)) },
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Composer (Input Bar)
        Surface(
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                // Mode chip row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        selected = searchEnabled,
                        onClick = { searchEnabled = !searchEnabled },
                        label = { Text("Web", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Public, null, Modifier.size(12.dp)) },
                        shape = RoundedCornerShape(8.dp),
                    )
                    FilterChip(
                        selected = deepResearchEnabled,
                        onClick = { deepResearchEnabled = !deepResearchEnabled },
                        label = { Text("Approfondi", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Explore, null, Modifier.size(12.dp)) },
                        shape = RoundedCornerShape(8.dp),
                    )
                    FilterChip(
                        selected = agentModeEnabled,
                        onClick = { agentModeEnabled = !agentModeEnabled },
                        label = { Text("Agent", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Psychology, null, Modifier.size(12.dp)) },
                        shape = RoundedCornerShape(8.dp),
                    )
                    val selectedModelIdx by viewModel.selectedModel.collectAsState()
                    val currentModel = viewModel.modelOptions.getOrElse(selectedModelIdx) { viewModel.modelOptions[1] }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { showChatModelPicker = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        ) {
                            Text(
                                currentModel.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GabomaColors.AccentBlackPanther,
                            )
                            if (currentModel.tag.isNotBlank()) {
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    currentModel.tag,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // Input + actions
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { /* Attach */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.AttachFile, "Joindre", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message Ñkyel...", fontSize = 14.sp) },
                        shape = RoundedCornerShape(22.dp),
                        singleLine = false,
                        maxLines = 4,
                        enabled = !isStreaming,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { handleSend() }),
                    )

                    Spacer(Modifier.width(8.dp))

                    if (isStreaming) {
                        FilledIconButton(
                            onClick = { viewModel.stopStreaming() },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.size(42.dp),
                        ) {
                            Icon(Icons.Default.Stop, "Arrêter")
                        }
                    } else {
                        FilledIconButton(
                            onClick = { handleSend() },
                            enabled = inputText.isNotBlank(),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = GabomaColors.AccentBlackPanther,
                                contentColor = Color.Black,
                            ),
                            modifier = Modifier.size(42.dp),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Envoyer")
                        }
                    }
                }
            }
        }
    }

    if (showChatModelPicker) {
        val selectedModelIdx by viewModel.selectedModel.collectAsState()
        NkyelModelSelectorSheet(
            modelOptions = viewModel.modelOptions,
            selectedIndex = selectedModelIdx,
            onSelect = { viewModel.setModel(it) },
            onDismiss = { showChatModelPicker = false },
        )
    }
}

// ═══════════════════════════════════════════════════════════
// SANCTUARY SCREEN — Real Deliverables & Artifacts Storage
// ═══════════════════════════════════════════════════════════

data class SanctuaryArtifact(
    val id: String,
    val title: String,
    val project: String,
    val date: String,
    val type: String, // PPT, PDF, CODE, XLSX, IMG, MP4, APP
    val size: String,
    val previewText: String,
)

@Composable
fun NkyelSanctuaryScreen(
    navController: NavController,
) {
    val tabs = listOf("Tous", "Documents", "Présentations", "Tableurs", "Code", "Images")
    var selectedTab by remember { mutableStateOf("Tous") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedArtifact by remember { mutableStateOf<SanctuaryArtifact?>(null) }

    val sampleArtifacts = remember {
        listOf(
            SanctuaryArtifact("art-1", "Brief de Marché Luxe & Parfumerie", "Étude Stratégique", "Aujourd'hui, 10:24", "PPT", "4.2 MB", "# Briefing Stratégique\nAnalyse du marché de la haute parfumerie mondiale 2026. Opportunités émergentes et positionnement premium."),
            SanctuaryArtifact("art-2", "Rapport d'Architecture Logicielle", "Ñkyel Core V2", "Hier, 18:40", "PDF", "1.8 MB", "# Architecture Ñkyel 2026\nArchitecture hybride FastAPI, DeerFlow, Neon et Next.js 16. Protocoles VIFLOW et Habakkuk."),
            SanctuaryArtifact("art-3", "Modèle Prévisionnel Financier", "Stratégie Croissance", "28 Oct 2026", "XLSX", "850 KB", "Données prévisionnelles 2026-2028. ARR projeté, ratios d'efficience GPU et structure des coûts d'inférence."),
            SanctuaryArtifact("art-4", "Composant Jetpack Compose Studio", "Android App", "25 Oct 2026", "CODE", "42 KB", "@Composable\nfun NkyelStudioInteractive() {\n    // Implementation native Android\n}"),
            SanctuaryArtifact("art-5", "Rendu Graphique Identité Visuelle", "Brand Assets", "22 Oct 2026", "IMG", "12.4 MB", "Identité Black Panther et or sablé. Design tokens et charte d'interface."),
        )
    }

    val filtered = sampleArtifacts.filter {
        (selectedTab == "Tous" || when (selectedTab) {
            "Documents" -> it.type == "PDF"
            "Présentations" -> it.type == "PPT"
            "Tableurs" -> it.type == "XLSX"
            "Code" -> it.type == "CODE"
            "Images" -> it.type == "IMG"
            else -> true
        }) && it.title.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Sanctuary",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GabomaColors.AccentBlackPanther.copy(alpha = 0.15f),
                ) {
                    Text(
                        "${sampleArtifacts.size} livrables",
                        fontSize = 11.sp,
                        color = GabomaColors.AccentBlackPanther,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher dans Sanctuary...") },
            leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
        )

        // Filter chips
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0),
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent,
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab, fontSize = 13.sp, fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal) },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Artifact list
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(filtered, key = { it.id }) { art ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    onClick = { selectedArtifact = art },
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Type Badge
                        val badgeColor = when (art.type) {
                            "PPT" -> Color(0xFFD97706)
                            "PDF" -> Color(0xFFDC2626)
                            "XLSX" -> Color(0xFF16A34A)
                            "CODE" -> Color(0xFF2563EB)
                            else -> GabomaColors.AccentBlackPanther
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = badgeColor.copy(alpha = 0.18f),
                            modifier = Modifier.size(46.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(art.type, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = badgeColor)
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(art.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(art.project, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(" · ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(art.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedArtifact?.let { art ->
        Dialog(onDismissRequest = { selectedArtifact = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp),
                tonalElevation = 6.dp,
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GabomaColors.AccentBlackPanther.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 10.dp),
                        ) {
                            Text(art.type, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GabomaColors.AccentBlackPanther, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text(art.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        IconButton(onClick = { selectedArtifact = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, "Fermer")
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Projet : ${art.project} · ${art.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    ) {
                        Text(
                            art.previewText,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontFamily = if (art.type == "CODE") FontFamily.Monospace else FontFamily.Default,
                            modifier = Modifier.padding(14.dp),
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = { /* Export */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GabomaColors.AccentBlackPanther, contentColor = Color.Black),
                        ) {
                            Icon(Icons.Default.Download, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Télécharger")
                        }
                        OutlinedButton(
                            onClick = { selectedArtifact = null },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Fermer")
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// STUDIOS SCREEN — 15 Specialized Studios Hub (Next.js PWA)
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelStudiosScreen(
    navController: NavController,
) {
    val studioTabs = listOf("Apps & Web", "Jeux", "Audio", "Documents", "Intent Lock")
    var selectedStudio by remember { mutableStateOf("Apps & Web") }
    var isPlayingGame by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Studios de Création", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GabomaColors.AccentBlackPanther.copy(alpha = 0.15f),
                ) {
                    Text("15 Studios", fontSize = 11.sp, color = GabomaColors.AccentBlackPanther, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }

        // Studios Tab Row
        ScrollableTabRow(
            selectedTabIndex = studioTabs.indexOf(selectedStudio).coerceAtLeast(0),
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent,
        ) {
            studioTabs.forEach { tab ->
                Tab(
                    selected = selectedStudio == tab,
                    onClick = { selectedStudio = tab },
                    text = { Text(tab, fontSize = 13.sp, fontWeight = if (selectedStudio == tab) FontWeight.SemiBold else FontWeight.Normal) },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Studio Workspace Content
        Box(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            when (selectedStudio) {
                "Apps & Web" -> {
                    Column(Modifier.fillMaxSize()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Code, null, tint = GabomaColors.AccentBlackPanther)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Studio Apps & Web", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(Modifier.weight(1f))
                                    FilledTonalButton(onClick = { /* Export */ }) {
                                        Text("Exporter le Code")
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text("Génération d'applications fullstack Next.js, React et Jetpack Compose avec rendu instantané.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                ) {
                                    Column(Modifier.padding(14.dp)) {
                                        Text("// Live Generated Code Preview", color = Color(0xFF64748B), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                        Spacer(Modifier.height(6.dp))
                                        Text("export default function App() {\n  return (\n    <div className=\"nkyel-preview\">\n      <h1>Mission Accomplie</h1>\n    </div>\n  )\n}", color = Color(0xFF38BDF8), fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }

                "Jeux" -> {
                    Column(Modifier.fillMaxSize()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Studio Jeux & Simulations 2D/3D", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Génération autonome de logiques de jeux Canvas, WebGL et shaders interactifs.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(24.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF1E1B4B),
                                    modifier = Modifier.size(240.dp, 160.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(if (isPlayingGame) Icons.Default.Pause else Icons.Default.PlayArrow, null, Modifier.size(48.dp), tint = GabomaColors.AccentBlackPanther)
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = { isPlayingGame = !isPlayingGame },
                                    colors = ButtonDefaults.buttonColors(containerColor = GabomaColors.AccentBlackPanther, contentColor = Color.Black),
                                ) {
                                    Text(if (isPlayingGame) "Mettre en Pause" else "Lancer la Simulation")
                                }
                            }
                        }
                    }
                }

                "Audio" -> {
                    Column(Modifier.fillMaxSize()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Studio Audio & Podcasts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("Synthèse vocale multilingue, production sonore et nettoyage audio haute fidélité.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(24.dp))
                                // Simulated Waveform
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(60.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    val waveHeights = listOf(20, 35, 55, 40, 25, 48, 60, 32, 18, 45, 50, 30, 22)
                                    waveHeights.forEach { h ->
                                        Box(
                                            Modifier.width(6.dp).height(h.dp).clip(RoundedCornerShape(3.dp)).background(GabomaColors.AccentBlackPanther)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    FilledTonalButton(onClick = { /* Export */ }) {
                                        Icon(Icons.Default.Download, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Exporter Audio (.wav)")
                                    }
                                }
                            }
                        }
                    }
                }

                "Documents" -> {
                    Column(Modifier.fillMaxSize()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Studio Documents & Éditions Exécutives", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("Rapports de direction, notes de synthèse et livres blancs formatés aux standards internationaux.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.weight(1f).fillMaxWidth().padding(8.dp),
                                    tonalElevation = 1.dp,
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("RAPPORT STRATÉGIQUE · 2026", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(Modifier.height(6.dp))
                                        Text("Synthèse exécutive prête pour export PDF et distribution sécurisée.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                "Intent Lock" -> {
                    Column(Modifier.fillMaxSize()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Habakkuk Vision Engine™ — Intent Lock", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("Verrouillage de l'intention humaine. Aucun agent ne dévie de l'objectif validé.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(20.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF27856A).copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27856A)),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Shield, null, tint = Color(0xFF27856A))
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text("Vecteur de Confiance : 99.8%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF27856A))
                                            Text("Contrôle opérateur préservé sous VIFLOW.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// CONNECTORS SCREEN — Real Connectors & MCP Catalog
// ═══════════════════════════════════════════════════════════

data class ConnectorItem(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val isMcp: Boolean,
    var isConnected: Boolean,
)

@Composable
fun NkyelConnectorsScreen(
    navController: NavController,
) {
    val connectorCategories = listOf("Tous", "Productivité", "Développement", "Business", "Finance")
    var selectedCat by remember { mutableStateOf("Tous") }
    var searchQuery by remember { mutableStateOf("") }

    val connectors = remember {
        mutableStateListOf(
            ConnectorItem("c1", "Google Workspace", "Gmail, Drive, Docs et Calendar", "Productivité", isMcp = true, isConnected = true),
            ConnectorItem("c2", "GitHub", "Dépôts, pull requests, issues et CI/CD", "Développement", isMcp = true, isConnected = true),
            ConnectorItem("c3", "Slack", "Messages, canaux et notifications d'équipe", "Productivité", isMcp = false, isConnected = true),
            ConnectorItem("c4", "Notion", "Bases de données et documentation de projet", "Productivité", isMcp = false, isConnected = false),
            ConnectorItem("c5", "Neon PostgreSQL", "Base de données relationnelle serverless", "Développement", isMcp = true, isConnected = true),
            ConnectorItem("c6", "Cloudflare R2", "Stockage binaire durable d'artéfacts", "Développement", isMcp = true, isConnected = true),
            ConnectorItem("c7", "RunPod GPU", "Inférence Qwen3 32B AWQ et GPT-OSS 120B", "Développement", isMcp = true, isConnected = true),
            ConnectorItem("c8", "Stripe", "Gestion des paiements et souscriptions", "Finance", isMcp = false, isConnected = false),
            ConnectorItem("c9", "Linear", "Suivi des tâches et tickets d'ingénierie", "Productivité", isMcp = true, isConnected = false),
            ConnectorItem("c10", "E2B Sandbox", "Environnement d'exécution de code sécurisé", "Développement", isMcp = true, isConnected = true),
        )
    }

    val filtered = connectors.filter {
        (selectedCat == "Tous" || it.category == selectedCat) &&
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Connecteurs & Outils MCP", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GabomaColors.AccentBlackPanther.copy(alpha = 0.15f),
                ) {
                    Text("${connectors.count { it.isConnected }} connectés", fontSize = 11.sp, color = GabomaColors.AccentBlackPanther, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher un connecteur ou outil MCP...") },
            leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
        )

        ScrollableTabRow(
            selectedTabIndex = connectorCategories.indexOf(selectedCat).coerceAtLeast(0),
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent,
        ) {
            connectorCategories.forEach { cat ->
                Tab(
                    selected = selectedCat == cat,
                    onClick = { selectedCat = cat },
                    text = { Text(cat, fontSize = 13.sp, fontWeight = if (selectedCat == cat) FontWeight.SemiBold else FontWeight.Normal) },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(filtered, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(42.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.name.take(1), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GabomaColors.AccentBlackPanther)
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                if (item.isMcp) {
                                    Spacer(Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF2563EB).copy(alpha = 0.18f),
                                    ) {
                                        Text("MCP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(item.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Switch(
                            checked = item.isConnected,
                            onCheckedChange = { checked ->
                                val idx = connectors.indexOfFirst { it.id == item.id }
                                if (idx != -1) {
                                    connectors[idx] = connectors[idx].copy(isConnected = checked)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// PROGRAMS SCREEN — Automated Workflows & Routines
// ═══════════════════════════════════════════════════════════

data class ProgramItemUi(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val trigger: String,
    val runsCount: Int,
    val isActive: Boolean = true,
)

@Composable
fun NkyelProgramsScreen(
    navController: NavController,
) {
    val categories = listOf("Tout", "Business", "Contenu", "Dev", "Analyse", "Équipe")
    var activeCategory by remember { mutableStateOf("Tout") }
    var searchQuery by remember { mutableStateOf("") }

    val initialPrograms = listOf(
        ProgramItemUi("prg-1", "Veille Stratégique Marché", "Business", "Extraction quotidienne et synthèse de signaux faibles concurrentiels", "Quotidien 08:00", 142),
        ProgramItemUi("prg-2", "Audit Sécurité OWASP & Dépendances", "Dev", "Inspection automatique du code, analyse CVE et détection de secrets", "Par commit", 88),
        ProgramItemUi("prg-3", "Rapport Financier Automatisé", "Analyse", "Extraction des ratios, modélisation DCF et synthèse de performance", "Hebdomadaire", 53),
        ProgramItemUi("prg-4", "Digest Presse Multilingue Souverain", "Contenu", "Traduction multilingue et curation d'actualités technologiques", "Quotidien 07:30", 210),
        ProgramItemUi("prg-5", "Routage des Tâches Agentiques", "Équipe", "Orchestration multi-agents et répartition des missions complexes", "Sur événement", 95),
    )
    val programs = remember { mutableStateListOf(*initialPrograms.toTypedArray()) }

    val filtered = programs.filter {
        (activeCategory == "Tout" || it.category == activeCategory) &&
        (it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Programmes", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text(
                        "Workflows & routines connectés au moteur backend",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { /* New workflow */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GabomaColors.AccentBlackPanther,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nouveau", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Category pills
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == activeCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { activeCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GabomaColors.AccentBlackPanther.copy(alpha = 0.2f),
                        selectedLabelColor = GabomaColors.AccentBlackPanther,
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { prg ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(prg.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Spacer(Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GabomaColors.AccentBlackPanther.copy(alpha = 0.15f),
                            ) {
                                Text(
                                    prg.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GabomaColors.AccentBlackPanther,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(prg.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(4.dp))
                            Text(prg.trigger, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(12.dp))
                            Text("·  ${prg.runsCount} exécutions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.weight(1f))
                            Button(
                                onClick = {
                                    navController.navigate("chat") { launchSingleTop = true }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp),
                            ) {
                                Text("Exécuter", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// PROJECTS SCREEN — Chrono-Graphe & Version Branches
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelProjectsScreen(
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Chrono-Graphe™", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { /* New branch */ },
                    colors = ButtonDefaults.buttonColors(containerColor = GabomaColors.AccentBlackPanther, contentColor = Color.Black),
                ) {
                    Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nouvelle Branche")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("Ligne Temporelle des Versions", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            val versions = listOf(
                Triple("v3 — Ébauche Finale Validée", "Aujourd'hui, 11:20 · Par l'opérateur (Humain)", true),
                Triple("v2 — Synthèse et Corrections", "Hier, 17:45 · Agent DeerFlow", false),
                Triple("v1 — Initialisation du Plan", "29 Oct 2026 · Habakkuk Engine", false),
            )

            items(versions) { (title, subtitle, isCurrent) ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) GabomaColors.AccentBlackPanther.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, GabomaColors.AccentBlackPanther) else null,
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(10.dp).clip(CircleShape).background(if (isCurrent) GabomaColors.AccentBlackPanther else MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(Modifier.height(3.dp))
                            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isCurrent) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GabomaColors.AccentBlackPanther,
                            ) {
                                Text("Actuelle", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// SEARCH / RESEARCH SCREEN — Deep Evidence & Sources
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelSearchScreen(
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Recherche & Preuves Vérifiées", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF27856A).copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27856A).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, null, tint = Color(0xFF27856A))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("8 Sources Analysées · 100% Vérifiables", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF27856A))
                            Text("Chaque affirmation est indexée avec son extrait source certifié.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            val sources = listOf(
                "Rapport Gartner 2026 : Agentic AI Platforms",
                "Documentation Officielle Next.js 16 Proxy Architecture",
                "Spécification AG-UI & A2UI Streaming Protocol",
                "RunPod Serverless Inference AWQ Benchmarks",
            )
            items(sources) { src ->
                Card(shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Link, null, Modifier.size(16.dp), tint = GabomaColors.AccentBlackPanther)
                        Spacer(Modifier.width(10.dp))
                        Text(src, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.OpenInNew, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// SKILLS SCREEN — Agent Skills Catalog
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelSkillsScreen(
    navController: NavController,
) {
    val skills = listOf(
        Triple("Analyse Financière Pro", "Modélisation DCF, ratios et valorisation de bilans", "34k utilisations"),
        Triple("Recherche Brevets & Antériorité", "Recherche sémantique USPTO et WIPO", "119k utilisations"),
        Triple("Audit de Sécurité Code & API", "Détection de vulnérabilités OWASP et fuites de secrets", "39k utilisations"),
        Triple("Générateur de Pitch Decks", "Structuration narrative Sequoia Capital en slides", "23k utilisations"),
    )

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Compétences Agentiques", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(skills) { (title, desc, stats) ->
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Spacer(Modifier.weight(1f))
                            Text(stats, fontSize = 11.sp, color = GabomaColors.AccentBlackPanther)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// WORKSPACE SCREEN — Real data from WorkGraph store
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelWorkspaceScreen(
    navController: NavController,
    viewModel: NkyelChatViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val currentConvId by viewModel.currentConversationId.collectAsState()

    val hasMission = messages.isNotEmpty() || isStreaming

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        // Top bar
        Surface(tonalElevation = 1.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Espace de travail",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.weight(1f))
                if (isStreaming) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF27856A).copy(alpha = 0.15f),
                    ) {
                        Row(Modifier.padding(8.dp, 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27856A)))
                            Spacer(Modifier.width(6.dp))
                            Text("En cours", fontSize = 12.sp, color = Color(0xFF27856A), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        if (!hasMission) {
            // Empty state
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Default.GridView, null, Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(16.dp))
                Text("Aucune mission active", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text("Lancez une mission depuis l'accueil pour voir l'exécution en temps réel.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp,
                    modifier = Modifier.widthIn(max = 300.dp),
                    lineHeight = 20.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(containerColor = GabomaColors.AccentBlackPanther, contentColor = Color.Black),
                ) {
                    Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nouvelle mission")
                }
            }
        } else {
            // Active workspace with conversation
            LazyColumn(
                Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Mission header
                item {
                    val title = conversations.find { it.id == currentConvId }?.title ?: "Mission en cours"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Mission", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                                color = GabomaColors.AccentBlackPanther,
                            )
                        }
                    }
                }

                // Recent messages as timeline
                item {
                    Text("Activité récente", fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp))
                }
                items(messages.takeLast(5)) { msg ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (msg.role == "user") GabomaColors.AccentBlackPanther.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                if (msg.role == "user") "Vous" else "Ñkyel",
                                fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(msg.content.take(200), fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// SETTINGS SCREEN — Premium native UX
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelSettingsScreen(
    navController: NavController,
    userName: String,
    userEmail: String? = null,
    avatarUrl: String? = null,
    credits: Int = 2480,
    tier: String = "pro",
    onSignOut: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                }
                Text("Paramètres", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold))
            }
        }

        // Real User card from Clerk
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (!avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Photo de profil",
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, GabomaColors.AccentBlackPanther, CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(
                            Modifier.size(52.dp).clip(CircleShape).background(GabomaColors.AccentBlackPanther),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(userName.take(1).uppercase(), fontSize = 22.sp,
                                fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(userName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        userEmail?.let {
                            Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GabomaColors.AccentBlackPanther.copy(alpha = 0.2f),
                            ) {
                                Text(
                                    "PLAN ${tier.uppercase()}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GabomaColors.AccentBlackPanther,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "$credits crédits",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = GabomaColors.AccentBlackPanther,
                            )
                        }
                    }
                }
            }
        }

        // Settings groups
        val groups = listOf(
            "Compte & Identité" to listOf("Profil Clerk", "Sécurité & Passkeys", "Sessions actives"),
            "Personnalisation" to listOf("Thème (Black Panther)", "Mon Ñkyel", "Langue", "Mémoire DeerMem"),
            "Agents & Raisonnement" to listOf("Modèle Principal (Qwen3 32B)", "Mission Max (GPT-OSS 120B)", "Approbations VIFLOW"),
            "Connexions" to listOf("Connecteurs MCP", "Comptes liés", "Stockage R2"),
            "Confidentialité" to listOf("Données souveraines", "Export", "Consentement RGPD"),
            "VIFLOW" to listOf("Mode calme", "Notifications", "Vecteur de confiance"),
            "Application" to listOf("Version 2.0.0 (Production)", "Support SmartANDJ", "Déconnexion"),
        )

        groups.forEach { (group, items) ->
            item {
                Text(
                    group,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
            }
            item {
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column {
                        items.forEachIndexed { idx, label ->
                            val isLogout = label == "Déconnexion"
                            ListItem(
                                headlineContent = {
                                    Text(
                                        label,
                                        fontSize = 14.sp,
                                        color = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isLogout) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                },
                                leadingContent = if (isLogout) {
                                    { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)) }
                                } else null,
                                trailingContent = {
                                    if (!isLogout) {
                                        Icon(Icons.Default.ChevronRight, null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                                    }
                                },
                                modifier = Modifier.clickable {
                                    if (isLogout) {
                                        onSignOut()
                                    }
                                },
                            )
                            if (idx < items.size - 1) {
                                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// PLACEHOLDER — Fallback
// ═══════════════════════════════════════════════════════════

@Composable
fun NkyelPlaceholderScreen(title: String, icon: String) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.Info, null, Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
        Spacer(Modifier.height(16.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("Bientôt disponible", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    }
}
