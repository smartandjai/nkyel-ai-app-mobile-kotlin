package com.gabomagpt.mobile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding

@Composable
fun GabomaSidebar(
    ui: ShellUiState,
    onSelectConversation: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onChangeModel: (ForceTier) -> Unit,
    onToggleInvokeLoxo: () -> Unit,
    onToggleRadarLoxo: () -> Unit,
    onToggleModeOmbre: () -> Unit,
    onToggleCoffre: () -> Unit,
    onTogglePacte: () -> Unit,
    onSelectProfile: (UserProfileKind) -> Unit
) {
    val theme = ui.settings.theme
    GlassPanel(
        theme = theme,
        modifier = Modifier.fillMaxHeight().width(330.dp),
        shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PulsingGoldDot(theme.primary)
                IbogaAiIcon(theme.primary, Modifier.size(18.dp))
                Text(
                    "GABOMAGPT",
                    color = theme.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            GlassHairline(theme)
            SidebarSectionTitle("En piste")
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(ui.conversations, key = { it.id }) { item ->
                    NavigationDrawerItem(
                        label = {
                            Column {
                                Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    item.preview,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = theme.text.copy(alpha = 0.62f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        selected = item.selected,
                        onClick = { onSelectConversation(item.id) },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = theme.primary.copy(alpha = 0.12f),
                            unselectedContainerColor = Color.Transparent,
                            selectedTextColor = theme.primary,
                            unselectedTextColor = theme.text
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
            GlassHairline(theme)
            SidebarEnergy(theme, ui.energy)
            GlassHairline(theme)
            SidebarSettingsSection(
                settings = ui.settings,
                theme = theme,
                onOpenSettings = onOpenSettings,
                onChangeModel = onChangeModel,
                onToggleInvokeLoxo = onToggleInvokeLoxo,
                onToggleRadarLoxo = onToggleRadarLoxo,
                onToggleModeOmbre = onToggleModeOmbre,
                onToggleCoffre = onToggleCoffre,
                onTogglePacte = onTogglePacte,
                onSelectProfile = onSelectProfile
            )
            GlassHairline(theme)
            SidebarSocials(theme)
            GlassHairline(theme)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AndjSovereignIcon(theme.primary, Modifier.size(16.dp))
                Text(
                    "BY ANDJ • SMARTANDJ TECH",
                    color = theme.text.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun SidebarEnergy(theme: GabomaThemePreset, energy: EnergyState) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("⚡ Énergie Quotidienne", color = theme.text, style = MaterialTheme.typography.titleSmall)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(energy.usedPercent / 100f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Brush.horizontalGradient(listOf(theme.primary, theme.accent)))
            )
        }
        Text(
            "${energy.usedPercent}% • ${energy.remainingToday}% restants",
            color = theme.text.copy(alpha = 0.66f),
            style = MaterialTheme.typography.labelMedium
        )
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = theme.primary, contentColor = theme.bg),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("ALIMENTER LA MEUTE")
        }
    }
}

@Composable
fun SidebarSettingsSection(
    settings: GabomaSettings,
    theme: GabomaThemePreset,
    onOpenSettings: () -> Unit,
    onChangeModel: (ForceTier) -> Unit,
    onToggleInvokeLoxo: () -> Unit,
    onToggleRadarLoxo: () -> Unit,
    onToggleModeOmbre: () -> Unit,
    onToggleCoffre: () -> Unit,
    onTogglePacte: () -> Unit,
    onSelectProfile: (UserProfileKind) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚙️ L'Antre", color = theme.text, style = MaterialTheme.typography.titleSmall)
            Text("PARAMÈTRES", color = theme.primary, style = MaterialTheme.typography.labelMedium)
        }
        AnimatedVisibility(expanded) {
            Column {
                SidebarMiniBlock(theme, title = "Vecteur de Force") {
                    ForceTier.entries.forEach { tier ->
                        GlassMiniRow(
                            theme,
                            label = tier.label,
                            sub = tier.note,
                            selected = tier == settings.model
                        ) { onChangeModel(tier) }
                    }
                }
                SidebarMiniBlock(theme, title = "Extensions de Traque") {
                    ToggleGlassRow(
                        theme,
                        "Invoquer LOXO",
                        settings.invokeLoxo,
                        onToggleInvokeLoxo,
                        icon = { LoxoIcon(theme.primary, Modifier.size(15.dp)) }
                    )
                    ToggleGlassRow(theme, "Radar LOXO", settings.radarLoxo, onToggleRadarLoxo, pulse = settings.radarLoxo)
                }
                SidebarMiniBlock(theme, title = "Souveraineté") {
                    ToggleGlassRow(theme, "Mode Ombre", settings.modeOmbre, onToggleModeOmbre)
                    ToggleGlassRow(theme, "Coffre-Fort Souverain", settings.coffreFortSouverain, onToggleCoffre)
                    ToggleGlassRow(theme, "Pacte Politique", settings.pactePolitiqueAccepted, onTogglePacte, badge = "CONF")
                }
                SidebarMiniBlock(theme, title = "Administration") {
                    GlassMiniRow(theme, "Profil Cadre", selected = settings.profileKind == UserProfileKind.PRO) {
                        onSelectProfile(UserProfileKind.PRO)
                    }
                    GlassMiniRow(theme, "Profil Citoyen", selected = settings.profileKind == UserProfileKind.PUBLIC) {
                        onSelectProfile(UserProfileKind.PUBLIC)
                    }
                    GlassMiniRow(theme, "Pacte de Chasse", sub = "Factures et historique", selected = false) {
                        onOpenSettings()
                    }
                }
                Text(
                    settings.nodeLabel,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = theme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun SidebarSocials(theme: GabomaThemePreset) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Rejoindre la Meute", color = theme.text, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SocialPill(theme, "Telegram")
            SocialPill(theme, "WhatsApp")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SocialPill(theme, "X")
            SocialPill(theme, "LinkedIn")
        }
    }
}
