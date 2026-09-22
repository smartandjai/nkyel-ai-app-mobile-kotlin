// presentation/settings/AntrScreen.kt
// ══════════════════════════════════════════════════════
// GABOMAGPT — L'ANTRE — Paramètres Style Premium Luxe
// Cards arrondies, séparateurs fins, vocabulaire souverain
// ══════════════════════════════════════════════════════
package com.smartandj.gabomagpt.presentation.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.smartandj.gabomagpt.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import com.smartandj.gabomagpt.presentation.theme.AccentAurata
import com.smartandj.gabomagpt.presentation.theme.AccentForetBlackPanther
import com.smartandj.gabomagpt.presentation.theme.AccentGris
import com.smartandj.gabomagpt.presentation.theme.AccentLoxo
import com.smartandj.gabomagpt.presentation.theme.AccentSonar
import com.smartandj.gabomagpt.presentation.theme.GabomaIcons
import com.smartandj.gabomagpt.presentation.theme.LocalGabomaColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.Color
import com.smartandj.gabomagpt.presentation.theme.*
import com.smartandj.gabomagpt.presentation.chat.IbogaAiIcon

val AccentAurata = Color(0xFFC5A059)
val AccentForetBlackPanther = Color(0xFF0A3D2A)
val AccentGris = Color(0xFF555555)
val AccentLoxo = Color(0xFF059669)
val AccentSonar = Color(0xFFE8333A)


@Composable
fun AntrScreen(
    currentTheme: AppTheme,
    currentAccent: AccentColor,
    onThemeChange: (AppTheme) -> Unit,
    onAccentChange: (AccentColor) -> Unit,
    onBack: () -> Unit
) {
    val gabomaColors = LocalGabomaColors.current
    val scrollState = rememberScrollState()

    var showThemePicker by remember { mutableStateOf(false) }
    var showForcePicker by remember { mutableStateOf(false) }
    var hapticEnabled by remember { mutableStateOf(true) }
    var shadowMode by remember { mutableStateOf(false) }
    var vaultEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gabomaColors.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // ══════════════════════════════════════════
        // LUXE GLASS HEADER — L'Antre Premium 2026
        // ══════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            gabomaColors.elevated.copy(alpha = 0.8f),
                            gabomaColors.elevated.copy(alpha = 0.4f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    IbogaAiIcon(
                        modifier = Modifier.size(24.dp),
                        color = gabomaColors.textPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "L'ANTRE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = gabomaColors.textPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Ton Domaine Souverain",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = gabomaColors.textMuted,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Filled.Add),
                        contentDescription = "Menu",
                        tint = gabomaColors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ══════════════════════════════════════════
            // CARD 1 — COMPTE
            // ══════════════════════════════════════════
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "danielandj@smartandjai.com",
                            fontSize = 14.sp,
                            color = gabomaColors.textPrimary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = gabomaColors.textMuted.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Free",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = gabomaColors.textMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }


            // Promo upgrade avec gradient CTA
            SettingsCardPremium {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "⚡ ALIMENTER LA MEUTE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = gabomaColors.textPrimary
                    )
                    Text(
                        text = "Paiements gérés via Mobile Money Gabon, Airtel, Moov.",
                        fontSize = 13.sp,
                        color = gabomaColors.textMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        gabomaColors.primary,
                                        gabomaColors.primary.copy(alpha = 0.7f)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Text(
                            text = "Alimenter la Meute",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = gabomaColors.background,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // ══════════════════════════════════════════
            // CARD 2 — PROFIL & ABONNEMENT
            // ══════════════════════════════════════════
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.Person,
                    label = "Profil Cadre",
                    onClick = { }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.BatteryChargingFull,
                    label = "ÉNERGIE QUOTIDIENNE",
                    subLabel = "Consommation de tokens",
                    onClick = { }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.CreditCard,
                    label = "Pacte de Chasse",
                    subLabel = "Facturation et abonnement",
                    onClick = { }
                )
            }

            // ══════════════════════════════════════════
            // CARD 3 — FORCES & CAPACITÉS
            // ══════════════════════════════════════════
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.FlashOn,
                    label = "Vecteur de Force",
                    subLabel = "5 disponibles",
                    onClick = { showForcePicker = true }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Extension,
                    label = "Extensions de Traque",
                    subLabel = "2 activées",
                    onClick = { }
                )
            }

            // ══════════════════════════════════════════
            // CARD 4 — APPARENCE
            // ══════════════════════════════════════════
            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.DarkMode,
                    label = "Mode de Forêt",
                    subLabel = currentTheme.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() },
                    onClick = { showThemePicker = true }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Title,
                    label = "Style de Texte",
                    subLabel = "Par défaut",
                    onClick = { }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Mic,
                    label = "Écho (Voix)",
                    onClick = { }
                )
            }

            // ══════════════════════════════════════════
            // CARD 5 — SOUVERAINETÉ
            // ══════════════════════════════════════════
            SettingsCard {
                SettingsRowToggle(
                    icon = Icons.Filled.Smartphone,
                    label = "Retour haptique",
                    checked = hapticEnabled,
                    onCheckedChange = { hapticEnabled = it }
                )
                SettingsDivider()
                SettingsRowToggle(
                    icon = GabomaIcons.Ombre,
                    label = "Mode Ombre",
                    checked = shadowMode,
                    onCheckedChange = { shadowMode = it }
                )
                SettingsDivider()
                SettingsRowToggle(
                    icon = Icons.Filled.Lock,
                    label = "Coffre-Fort Souverain",
                    checked = vaultEnabled,
                    onCheckedChange = { vaultEnabled = it }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Balance,
                    label = "Pacte Politique",
                    trailing = {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AccentForetBlackPanther.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "CONFORME",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = AccentForetBlackPanther,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Link,
                    label = "Liens partagés",
                    onClick = { }
                )
            }

            // ══════════════════════════════════════════
            // SECTION HEADER — REJOINDRE LA MEUTE
            // ══════════════════════════════════════════
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "REJOINDRE LA MEUTE",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.5.sp,
                color = gabomaColors.primary.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            SettingsCard {
                SettingsRow(
                    icon = Icons.Filled.Send,
                    label = "Telegram",
                    subLabel = "@gabomagpt_community",
                    onClick = { }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Message,
                    label = "WhatsApp",
                    onClick = { }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Public,
                    label = "X / Twitter",
                    subLabel = "@gabomagpt",
                    onClick = { }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Filled.Work,
                    label = "LinkedIn",
                    subLabel = "SMARTANDJ AI TECH",
                    onClick = { }
                )
            }

            // ══════════════════════════════════════════
            // DANGER ZONE — DÉCONNEXION
            // ══════════════════════════════════════════
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Filled.Add),
                        contentDescription = null,
                        tint = Color(0xFFE74C3C),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Se déconnecter",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE74C3C),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = rememberVectorPainter(Icons.Filled.Add),
                        contentDescription = null,
                        tint = Color(0xFFE74C3C).copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ══════════════════════════════════════════
            // ACCENTUATIONS — 6 Couleurs Gaboma
            // ══════════════════════════════════════════
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ACCENTUATIONS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.5.sp,
                color = gabomaColors.primary.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vert Gabonais (AccentForet)
                AccentColorSwatch(
                    color = Color(0xFF22C55E),
                    label = "Foret",
                    modifier = Modifier.weight(1f)
                )
                
                // Vert Mousse (AccentLoxo)
                AccentColorSwatch(
                    color = Color(0xFFA3BA99),
                    label = "Loxo",
                    modifier = Modifier.weight(1f)
                )
                
                // Violet (SecondaryBlackPanther)
                AccentColorSwatch(
                    color = Color(0xFF9B8BB3),
                    label = "Panther",
                    modifier = Modifier.weight(1f)
                )
                
                // Violet Nuit (AccentNuit)
                AccentColorSwatch(
                    color = Color(0xFF7C3AED),
                    label = "Nuit",
                    modifier = Modifier.weight(1f)
                )
                
                // Or (AccentMain)
                AccentColorSwatch(
                    color = Color(0xFFC5A059),
                    label = "Or",
                    modifier = Modifier.weight(1f)
                )
                
                // Vert Vif (GlowGreen)
                AccentColorSwatch(
                    color = Color(0xFF22C55E),
                    label = "Glow",
                    modifier = Modifier.weight(1f)
                )
            }

            // ══════════════════════════════════════════
            // SIGNATURE FINALE LUXE
            // ══════════════════════════════════════════
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⚔️ GABOMAGPT 2026 ⚔️",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = gabomaColors.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "By SMARTANDJ AI TECH",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    color = gabomaColors.textMuted,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Ton Génie Souverain ✨",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp,
                    color = gabomaColors.textMuted.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ══════════════════════════════════════════
    // BOTTOM SHEET — Sélecteur de thème
    // ══════════════════════════════════════════
    if (showThemePicker) {
        ThemePickerSheet(
            currentTheme = currentTheme,
            onThemeChange = {
                onThemeChange(it)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false }
        )
    }

    // ══════════════════════════════════════════
    // BOTTOM SHEET — Sélecteur de Force
    // ══════════════════════════════════════════
    if (showForcePicker) {
        ForcePickerSheet(
            onDismiss = { showForcePicker = false }
        )
    }
}

// ══════════════════════════════════════════════
// COMPOSANTS CARD RÉUTILISABLES
// ══════════════════════════════════════════════

@Composable
fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    val gabomaColors = LocalGabomaColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        gabomaColors.elevated.copy(alpha = 0.6f),
                        gabomaColors.elevated.copy(alpha = 0.3f)
                    ),
                    center = Offset(0.5f, 0.5f),
                    radius = 300f
                )
            )
            .border(
                width = 1.dp,
                color = gabomaColors.textPrimary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        content()
    }
}

@Composable
fun SettingsCardPremium(
    content: @Composable ColumnScope.() -> Unit
) {
    val gabomaColors = LocalGabomaColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        gabomaColors.primary.copy(alpha = 0.12f),
                        gabomaColors.elevated.copy(alpha = 0.5f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                )
            )
            .border(
                width = 1.5.dp,
                color = gabomaColors.primary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        content()
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    label: String,
    subLabel: String? = null,
    trailing: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val gabomaColors = LocalGabomaColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = rememberVectorPainter(icon),
            contentDescription = null,
            tint = gabomaColors.textSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = gabomaColors.textPrimary
            )
            subLabel?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = gabomaColors.textMuted,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
        trailing()
    }
}

@Composable
fun SettingsRowToggle(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val gabomaColors = LocalGabomaColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = rememberVectorPainter(icon),
            contentDescription = null,
            tint = gabomaColors.textSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = gabomaColors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = gabomaColors.primary,
                checkedTrackColor = gabomaColors.primary.copy(alpha = 0.3f),
                uncheckedThumbColor = gabomaColors.textMuted,
                uncheckedTrackColor = gabomaColors.textMuted.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun SettingsDivider() {
    val gabomaColors = LocalGabomaColors.current
    HorizontalDivider(
        thickness = 0.5.dp,
        color = gabomaColors.textPrimary.copy(alpha = 0.06f), // Ultra-fin "Hairline" façon Premium
        modifier = Modifier.padding(start = 54.dp)
    )
}

// ══════════════════════════════════════════════
// ACCENT COLOR SWATCH — 6 Accentuations Gaboma
// ══════════════════════════════════════════════

@Composable
fun AccentColorSwatch(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = 1.5.dp,
                    color = color.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// ══════════════════════════════════════════════
// THEME PICKER SHEET
// ══════════════════════════════════════════════

@Composable
private fun ThemePickerSheet(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    val gabomaColors = LocalGabomaColors.current
    val themes = listOf(
        GabomaThemeType.BLACK_PANTHER to "⚫ Black Panther",
        GabomaThemeType.NUIT_LOPE to "🌿 Nuit Lopé (Obsidian)",
        GabomaThemeType.BLEU_NUIT to "🌙 Bleu Nuit",
        GabomaThemeType.AURORE_OGOUE to "☀️ Aurore Ogooué",
        GabomaThemeType.VIOLETTE_MANDRILLE to "💜 Violette Mandrille",
        GabomaThemeType.NEO_BLANC to "◽ Néo Blanc"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = gabomaColors.elevated,
        title = {
            Text(
                text = "Mode de Forêt",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = gabomaColors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                themes.forEach { pair -> val key = pair.first; val label = pair.second
                    val isSelected = key == currentTheme
                    Surface(
                        onClick = { onThemeChange(key) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected)
                            gabomaColors.primary.copy(alpha = 0.15f)
                        else
                            Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected)
                                    gabomaColors.primary.copy(alpha = 0.4f)
                                else
                                    gabomaColors.border,
                                shape = RoundedCornerShape(14.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected)
                                    gabomaColors.primary
                                else
                                    gabomaColors.textPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    painter = rememberVectorPainter(Icons.Filled.Add),
                                    contentDescription = null,
                                    tint = gabomaColors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

// ══════════════════════════════════════════════
// FORCE PICKER SHEET
// ══════════════════════════════════════════════

@Composable
private fun ForcePickerSheet(
    onDismiss: () -> Unit
) {
    val gabomaColors = LocalGabomaColors.current

    val forces = listOf(
        Triple("Ñkyel Chui", "Flash · Réponses rapides", Pair(R.drawable.ic_nkyel, AccentAurata)),
        Triple("Ñkyel Tai", "Raisonnement profond & multimodal", Pair(R.drawable.ic_nkyel, AccentSonar)),
        Triple("OnyxGris", "Mission agentique autonome & livrables", Pair(R.drawable.ic_nkyel, gabomaColors.primary)),
        Triple("Recherche Web", "Deep Research & sources ancrées", Pair(R.drawable.ic_nkyel, AccentLoxo)),
        Triple("Blue Panther", "Mode Créateur Illimité", Pair(R.drawable.ic_nkyel, AccentGris))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = gabomaColors.elevated,
        title = {
            Text(
                text = "Choisir ta Force",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = gabomaColors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                forces.forEachIndexed { index, (name, desc, iconData) ->
                    val (icon, tint) = iconData
                    val isGris = name == "Mode Gris"
                    val isActive = name == "Aurata" // Fake active for now

                    Surface(
                        onClick = { if (!isGris) onDismiss() },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isActive)
                            tint.copy(alpha = 0.1f)
                        else
                            Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = name,
                                tint = if (isGris) gabomaColors.textMuted else tint,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGris) gabomaColors.textMuted else gabomaColors.textPrimary
                                    )
                                    if (isActive) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "[ACTIF]",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tint,
                                            letterSpacing = 1.sp
                                        )
                                    } else if (isGris) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "[BIENTÔT]",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = gabomaColors.textMuted,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = gabomaColors.textMuted
                                )
                            }
                            if (isActive) {
                                Icon(
                                    painter = rememberVectorPainter(Icons.Filled.Add),
                                    contentDescription = null,
                                    tint = tint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
