package com.smartandj.gabomagpt.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.GabomaTheme
import com.smartandj.gabomagpt.presentation.chat.IbogaAiIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntreScreen(
    onClose: () -> Unit
) {
    var showSystemInfo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(40.dp)
                ) {
                    IbogaAiIcon(modifier = Modifier.size(24.dp), color = Color.White)
                }
                
                Text(
                    text = "L'Antre",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = { showSystemInfo = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info Système", tint = Color.White)
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // 1. Profil Citoyen
                item {
                    SectionTitle(title = "Profil Citoyen", icon = Icons.Default.Person)
                    CardContainer {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(GabomaTheme.colors.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("JD", color = GabomaTheme.colors.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Daniel Jonathan ANDJ", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                                Text("Citoyen depuis Mars 2026", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                            }
                        }
                        Divider(color = Color.White.copy(alpha = 0.1f))
                        SettingRow(label = "Email", value = "daniel@gabomagpt.com")
                        SettingRow(label = "Téléphone", value = "+241 XX XX XX 00")
                        SettingRow(label = "Langue de Traque", value = "Français ›")
                        SettingRow(label = "Rang", value = "Black Panther", valueColor = GabomaTheme.colors.primary)
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Supprimer mon compte",
                            color = GabomaTheme.colors.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp).clickable { /* delete account */ }
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // 2. Pacte de Chasse
                item {
                    SectionTitle(title = "Pacte de Chasse", icon = Icons.Default.CreditCard)
                    CardContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Black Panther", color = GabomaTheme.colors.primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Renouvellement le 12 Avril", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GabomaTheme.colors.primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Actif", color = GabomaTheme.colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Jauge d'énergie", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = 0.85f,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = GabomaTheme.colors.primary,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // 3. Extensions de Traque
                item {
                    SectionTitle(title = "Extensions de Traque", icon = Icons.Default.Extension)
                    CardContainer {
                        ToggleRow(label = "Radar Wandana", description = "Recherche web profonde", checked = true)
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))
                        ToggleRow(label = "Génération d'images", description = "Création visuelle via prompt", checked = true)
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))
                        ToggleRow(label = "Exécution de code", description = "Environnement de test", checked = false)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // 4. Mode de Forêt
                item {
                    SectionTitle(title = "Mode de Forêt", icon = Icons.Default.DarkMode)
                    CardContainer {
                        SettingRow(label = "Apparence", value = "Sombre")
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))
                        ToggleRow(label = "Noir OLED Absolu", description = "Économie de batterie", checked = true)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                // 5. Écho (Voix)
                item {
                    SectionTitle(title = "Écho (Voix)", icon = Icons.Default.RecordVoiceOver)
                    CardContainer {
                        SettingRow(label = "Style de voix", value = "Masculine ›")
                        SettingRow(label = "Accent", value = "Gabonais ›")
                        SettingRow(label = "Vitesse de lecture", value = "1.0x ›")
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))
                        ToggleRow(label = "Lecture automatique", description = "Joue la voix sur nouvelle réponse", checked = false)
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))
                        ToggleRow(label = "Activation mains-libres", description = "Wake-word pour Live mode", checked = false)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // 6. Coffre-Fort Souverain
                item {
                    SectionTitle(title = "Coffre-Fort Souverain", icon = Icons.Default.Security)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GabomaTheme.colors.primary.copy(alpha = 0.1f))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = GabomaTheme.colors.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Souveraineté des données", color = GabomaTheme.colors.primary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Toutes vos informations sont hébergées et traitées souverainement sur des serveurs au Gabon.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CardContainer {
                        ToggleRow(label = "Mode Ombre", description = "Désactive la sauvegarde et l'entraînement", checked = false)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // 7. Pacte Politique
                item {
                    SectionTitle(title = "Pacte Politique", icon = Icons.Default.Gavel)
                    CardContainer {
                        SettingRow(label = "Conditions d'utilisation", value = "v2.1 ›")
                        SettingRow(label = "Confidentialité", value = "v1.4 ›")
                    }
                }
            }
        }

        // System Info Modal (Confidentiality-Safe)
        if (showSystemInfo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showSystemInfo = false },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A1A1A))
                        .padding(24.dp)
                        .clickable(enabled = false, onClick = {}) // prevent clickthrough
                ) {
                    Column {
                        Text("Statut Système", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Version", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text("Gaboma AI - Build 1.0.0-Gaboma", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nœud", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text("Libreville-S-01", color = GabomaTheme.colors.primary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Statut réseau", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text("Optimal • 14ms", color = GabomaTheme.colors.success, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showSystemInfo = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                        ) {
                            Text("Fermer", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun CardContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(20.dp),
        content = content
    )
}

@Composable
fun SettingRow(label: String, value: String, valueColor: Color = Color.White.copy(alpha = 0.6f)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
        Text(text = value, color = valueColor, fontSize = 15.sp)
    }
}

@Composable
fun ToggleRow(label: String, description: String, checked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = label, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
            Text(text = description, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = { },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GabomaTheme.colors.primary,
                uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
