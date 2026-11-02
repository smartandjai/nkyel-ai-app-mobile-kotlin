package com.gabomagpt.mobile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GabomaHomeChatScreen(viewModel: GabomaShellViewModel = hiltViewModel()) {
    val ui by viewModel.ui.collectAsState()
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var directive by rememberSaveable { mutableStateOf("") }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var selectedArtifact by remember { mutableStateOf<ArtifactCard?>(null) }

    LaunchedEffect(ui.errorMessage) {
        ui.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    BackHandler(enabled = showSettings) { showSettings = false }

    GabomaSurface(ui.settings.theme, ui.settings.fontScale, ui.settings.writingStyle) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                GabomaSidebar(
                    ui = ui,
                    onSelectConversation = {
                        viewModel.selectConversation(it)
                        scope.launch { drawerState.close() }
                    },
                    onOpenSettings = {
                        showSettings = true
                        scope.launch { drawerState.close() }
                    },
                    onChangeModel = {
                        viewModel.updateModel(it)
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    },
                    onToggleInvokeLoxo = { viewModel.toggleInvokeLoxo() },
                    onToggleRadarLoxo = { viewModel.toggleRadarLoxo() },
                    onToggleModeOmbre = { viewModel.toggleModeOmbre() },
                    onToggleCoffre = { viewModel.toggleCoffre() },
                    onTogglePacte = { viewModel.togglePacte() },
                    onSelectProfile = { viewModel.updateProfile(it) }
                )
            },
            gesturesEnabled = true
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AuroraBackground(ui.settings.theme)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = ui.settings.theme.accent.copy(alpha = 0.92f),
                                contentColor = ui.settings.theme.text
                            )
                        }
                    },
                    topBar = {
                        GabomaTopBar(
                            theme = ui.settings.theme,
                            isSearching = ui.isSearching,
                            onMenu = { scope.launch { drawerState.open() } },
                            onSettings = { showSettings = true }
                        )
                    },
                    bottomBar = {
                        GabomaInputBar(
                            theme = ui.settings.theme,
                            directive = directive,
                            onDirectiveChange = { directive = it },
                            onSend = {
                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                viewModel.sendDirective(directive)
                                directive = ""
                            },
                            onUpload = { },
                            model = ui.settings.model,
                            invokeLoxo = ui.settings.invokeLoxo,
                            radarLoxo = ui.settings.radarLoxo,
                            onToggleInvokeLoxo = { viewModel.toggleInvokeLoxo() },
                            onToggleRadarLoxo = { viewModel.toggleRadarLoxo() }
                        )
                    }
                ) { padding ->
                    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                        ) {
                            items(ui.messages, key = { it.id }) { message ->
                                MessageBubble(
                                    theme = ui.settings.theme,
                                    message = message,
                                    onOpenArtifact = {
                                        selectedArtifact = it
                                        scope.launch { sheetState.show() }
                                    },
                                    onCopy = {
                                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                    },
                                    onRegenerate = { viewModel.regenerateLast() }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = GabomaPolicyText.FOOTER_CHAT,
                                    color = ui.settings.theme.text.copy(alpha = 0.65f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showSettings,
                    enter = fadeIn(tween(220)) + slideInVertically(tween(280)) { it / 10 },
                    exit = fadeOut(tween(180)) + slideOutVertically(tween(220)) { it / 10 }
                ) {
                    SettingsOverlay(
                        ui = ui,
                        onClose = { showSettings = false },
                        onThemeChange = viewModel::updateTheme,
                        onStyleChange = viewModel::updateStyle,
                        onFontScaleChange = viewModel::updateScale,
                        onModelChange = viewModel::updateModel,
                        onToggleInvokeLoxo = viewModel::toggleInvokeLoxo,
                        onToggleRadarLoxo = viewModel::toggleRadarLoxo,
                        onToggleModeOmbre = viewModel::toggleModeOmbre,
                        onToggleCoffre = viewModel::toggleCoffre,
                        onTogglePacte = viewModel::togglePacte,
                        onProfileChange = viewModel::updateProfile
                    )
                }
            }
        }

        if (selectedArtifact != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedArtifact = null },
                sheetState = sheetState,
                containerColor = ui.settings.theme.bg.copy(alpha = 0.92f),
                dragHandle = { SheetGrip(theme = ui.settings.theme) },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                ArtifactBottomSheet(
                    artifact = selectedArtifact!!,
                    theme = ui.settings.theme,
                    onClose = { selectedArtifact = null }
                )
            }
        }
    }
}

@Composable
fun GabomaTopBar(
    theme: GabomaThemePreset,
    isSearching: Boolean,
    onMenu: () -> Unit,
    onSettings: () -> Unit
) {
    GlassPanel(theme = theme, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = onMenu) {
                        Icon(Icons.Outlined.Menu, contentDescription = "L'Antre", tint = theme.text)
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PulsingGoldDot(theme.primary)
                            Text(
                                "GABOMAGPT",
                                color = theme.text,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text("AUTOMATA ENGINE", color = theme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SmartAndJTechIcon(theme.primary, Modifier.size(18.dp))
                    IconButton(onClick = onSettings) {
                        AndjSovereignIcon(theme.text, Modifier.size(18.dp))
                    }
                }
            }
            AnimatedVisibility(visible = isSearching) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PulsingGoldDot(theme.primary, small = true)
                    Text(
                        "Radar LOXO en chasse…",
                        color = theme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun GabomaInputBar(
    theme: GabomaThemePreset,
    directive: String,
    onDirectiveChange: (String) -> Unit,
    onSend: () -> Unit,
    onUpload: () -> Unit,
    model: ForceTier,
    invokeLoxo: Boolean,
    radarLoxo: Boolean,
    onToggleInvokeLoxo: () -> Unit,
    onToggleRadarLoxo: () -> Unit
) {
    GlassPanel(theme = theme, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                ModelChip(theme, model.label, model.accent)
                SmallToggleChip(theme, "Invoquer LOXO", invokeLoxo, onToggleInvokeLoxo)
                SmallToggleChip(theme, "Radar LOXO", radarLoxo, onToggleRadarLoxo)
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconButton(
                    onClick = onUpload,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(Icons.Outlined.UploadFile, contentDescription = "Relever un indice", tint = theme.text)
                }
                BasicTextField(
                    value = directive,
                    onValueChange = onDirectiveChange,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    textStyle = TextStyle(color = theme.text, fontSize = 16.sp),
                    decorationBox = { inner ->
                        if (directive.isEmpty()) {
                            Text("Directive", color = theme.text.copy(alpha = 0.45f))
                        }
                        inner()
                    }
                )
                Button(
                    onClick = onSend,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary, contentColor = theme.bg),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Lancer")
                }
            }
            Text(
                GabomaPolicyText.INPUT_FOOTER,
                color = theme.text.copy(alpha = 0.54f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
