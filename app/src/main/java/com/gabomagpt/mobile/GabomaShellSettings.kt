package com.gabomagpt.mobile

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween

@Composable
fun SettingsOverlay(
    ui: ShellUiState,
    onClose: () -> Unit,
    onThemeChange: (GabomaThemePreset) -> Unit,
    onStyleChange: (WritingStyle) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onModelChange: (ForceTier) -> Unit,
    onToggleInvokeLoxo: () -> Unit,
    onToggleRadarLoxo: () -> Unit,
    onToggleModeOmbre: () -> Unit,
    onToggleCoffre: () -> Unit,
    onTogglePacte: () -> Unit,
    onProfileChange: (UserProfileKind) -> Unit
) {
    val theme = ui.settings.theme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f))
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) }
    ) {
        GlassPanel(
            theme = theme,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                SheetGrip(theme)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("L'Antre", color = theme.text, style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = theme.text)
                    }
                }
                SettingsTabsArea(
                    ui = ui,
                    onThemeChange = onThemeChange,
                    onStyleChange = onStyleChange,
                    onFontScaleChange = onFontScaleChange,
                    onModelChange = onModelChange,
                    onToggleInvokeLoxo = onToggleInvokeLoxo,
                    onToggleRadarLoxo = onToggleRadarLoxo,
                    onToggleModeOmbre = onToggleModeOmbre,
                    onToggleCoffre = onToggleCoffre,
                    onTogglePacte = onTogglePacte,
                    onProfileChange = onProfileChange
                )
                Text(
                    text = GabomaPolicyText.FOOTER_PAGE,
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    textAlign = TextAlign.Center,
                    color = theme.text.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun SettingsTabsArea(
    ui: ShellUiState,
    onThemeChange: (GabomaThemePreset) -> Unit,
    onStyleChange: (WritingStyle) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onModelChange: (ForceTier) -> Unit,
    onToggleInvokeLoxo: () -> Unit,
    onToggleRadarLoxo: () -> Unit,
    onToggleModeOmbre: () -> Unit,
    onToggleCoffre: () -> Unit,
    onTogglePacte: () -> Unit,
    onProfileChange: (UserProfileKind) -> Unit
) {
    var activeTab by rememberSaveable { mutableStateOf(SettingsTab.APPARENCE) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsTab.entries.forEach { tab ->
                FilterChip(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    label = { Text(tab.label) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Crossfade(targetState = activeTab, label = "settingsTab") { tab ->
            when (tab) {
                SettingsTab.APPARENCE -> AppearanceSettingsCard(ui, onThemeChange, onStyleChange, onFontScaleChange)
                SettingsTab.DIRECTIVES -> DirectiveSettingsCard(ui, onModelChange, onToggleInvokeLoxo, onToggleRadarLoxo)
                SettingsTab.SOUVERAINETE -> SovereigntySettingsCard(ui, onToggleModeOmbre, onToggleCoffre, onTogglePacte)
                SettingsTab.COMPTE -> AccountSettingsCard(ui, onProfileChange)
                SettingsTab.POLITIQUES -> PolicySettingsCard(ui)
            }
        }
    }
}

@Composable
fun AppearanceSettingsCard(
    ui: ShellUiState,
    onThemeChange: (GabomaThemePreset) -> Unit,
    onStyleChange: (WritingStyle) -> Unit,
    onFontScaleChange: (Float) -> Unit
) {
    val theme = ui.settings.theme
    SettingsGlassCard(theme, "Apparence") {
        ThemeRowGroup(theme, "Choisir le thème") {
            GabomaThemePreset.entries.forEach { preset ->
                GlassSelectableRow(theme, label = preset.id, selected = preset == ui.settings.theme) {
                    onThemeChange(preset)
                }
            }
        }
        ThemeRowGroup(theme, "Style d'écriture") {
            WritingStyle.entries.forEach { style ->
                GlassSelectableRow(theme, label = style.label, selected = style == ui.settings.writingStyle) {
                    onStyleChange(style)
                }
            }
        }
        ThemeRowGroup(theme, "Taille des caractères") {
            listOf(0.9f, 1.0f, 1.1f, 1.2f).forEach { scale ->
                GlassSelectableRow(
                    theme,
                    label = "${(scale * 100).toInt()}%",
                    selected = scale == ui.settings.fontScale
                ) { onFontScaleChange(scale) }
            }
        }
    }
}

@Composable
fun DirectiveSettingsCard(
    ui: ShellUiState,
    onModelChange: (ForceTier) -> Unit,
    onToggleInvokeLoxo: () -> Unit,
    onToggleRadarLoxo: () -> Unit
) {
    val theme = ui.settings.theme
    SettingsGlassCard(theme, "Directive") {
        ThemeRowGroup(theme, "Vecteur de Force") {
            ForceTier.entries.forEach { tier ->
                GlassSelectableRow(
                    theme,
                    label = tier.label,
                    trailing = tier.note,
                    selected = tier == ui.settings.model
                ) { onModelChange(tier) }
            }
        }
        ThemeRowGroup(theme, "Extensions de Traque") {
            SettingsToggleRow(theme, "Invoquer LOXO", ui.settings.invokeLoxo, onToggleInvokeLoxo)
            SettingsToggleRow(theme, "Radar LOXO", ui.settings.radarLoxo, onToggleRadarLoxo)
        }
    }
}

@Composable
fun SovereigntySettingsCard(
    ui: ShellUiState,
    onToggleModeOmbre: () -> Unit,
    onToggleCoffre: () -> Unit,
    onTogglePacte: () -> Unit
) {
    val theme = ui.settings.theme
    SettingsGlassCard(theme, "Souveraineté") {
        ThemeRowGroup(theme, "Protection") {
            SettingsToggleRow(theme, "Mode Ombre", ui.settings.modeOmbre, onToggleModeOmbre)
            SettingsToggleRow(theme, "Coffre-Fort Souverain", ui.settings.coffreFortSouverain, onToggleCoffre)
            SettingsToggleRow(theme, "Pacte Politique", ui.settings.pactePolitiqueAccepted, onTogglePacte, badge = "CONF")
        }
    }
}

@Composable
fun AccountSettingsCard(ui: ShellUiState, onProfileChange: (UserProfileKind) -> Unit) {
    val theme = ui.settings.theme
    SettingsGlassCard(theme, "Compte") {
        ThemeRowGroup(theme, "Profil") {
            UserProfileKind.entries.forEach { kind ->
                GlassSelectableRow(theme, label = kind.label, selected = kind == ui.settings.profileKind) {
                    onProfileChange(kind)
                }
            }
        }
        ThemeRowGroup(theme, "Pacte de Chasse") {
            GlassStaticRow(theme, "Historique paiements", "Voir les factures et abonnements")
            GlassStaticRow(theme, "Alimenter la Meute", "Méthode Netflix · upgrade")
        }
    }
}

@Composable
fun PolicySettingsCard(ui: ShellUiState) {
    val theme = ui.settings.theme
    SettingsGlassCard(theme, "Politiques") {
        ThemeRowGroup(theme, "Confidentialité") {
            Text(
                GabomaPolicyText.PRIVACY,
                color = theme.text.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
        ThemeRowGroup(theme, "Conditions") {
            Text(
                GabomaPolicyText.TERMS,
                color = theme.text.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
