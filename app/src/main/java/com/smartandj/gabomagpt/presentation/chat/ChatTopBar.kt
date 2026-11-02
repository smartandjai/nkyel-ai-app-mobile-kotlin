// presentation/chat/ChatTopBar.kt
package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatTopBar(
    onOpenSidebar: () -> Unit,
    onNewChat: () -> Unit,
    onOpenRendu: () -> Unit
) {
    val gabomaColors = LocalGabomaColors.current
    var isGhostMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(gabomaColors.background)
            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── GAUCHE : Logo IbogaAI & Piste Fantôme
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onOpenSidebar) {
                    IbogaAiIcon(
                        modifier = Modifier.size(26.dp),
                        color = gabomaColors.textPrimary
                    )
                }
                
                AnimatedVisibility(visible = isGhostMode, enter = fadeIn(), exit = fadeOut()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .background(GabomaColors.ErrorRed.copy(alpha = 0.1f), CircleShape)
                            .border(1.dp, GabomaColors.ErrorRed.copy(alpha = 0.2f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(GabomaColors.ErrorRed, CircleShape)
                        )
                        Text(
                            text = "PISTE FANTÔME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GabomaColors.ErrorRed,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // ── DROITE : Header Cluster ─────────────────────────
            // 👻 Ombre, 🌳 Okoumé (Le Rendu Étendu)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 👻 Ombre
                IconButton(onClick = { isGhostMode = !isGhostMode }) {
                    Icon(
                        painter = androidx.compose.ui.graphics.vector.rememberVectorPainter(GabomaIcons.Ombre),
                        contentDescription = "Mode Ombre",
                        tint = if (isGhostMode) GabomaColors.ErrorRed else gabomaColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 🌳 L'Okoumé (Rendu Étendu)
                IconButton(onClick = onOpenRendu) {
                    ForetEveilleIcon(
                        modifier = Modifier.size(20.dp),
                        color = gabomaColors.textSecondary
                    )
                }
            }
        }

        // Séparateur
        HorizontalDivider(
            thickness = 0.5.dp,
            color = if (isGhostMode) GabomaColors.ErrorRed.copy(alpha = 0.2f) else gabomaColors.border
        )
    }
}
