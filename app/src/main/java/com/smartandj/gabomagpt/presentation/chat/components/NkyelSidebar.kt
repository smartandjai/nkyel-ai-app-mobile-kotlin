package com.smartandj.gabomagpt.presentation.chat.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import com.smartandj.gabomagpt.presentation.theme.GabomaIcons
import com.smartandj.gabomagpt.presentation.chat.IbogaAiIcon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.GabomaTheme

@Composable
fun GabomaSidebar(
    onClose: () -> Unit,
    onOpenSettings: () -> Unit
) {
    // Pulsing dot animation
    val infiniteTransition = rememberInfiniteTransition()
    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .clip(RoundedCornerShape(0.dp))
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        // HEADER: Gaboma AI + IbogaAiIcon (as close)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "GABOMA",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                IbogaAiIcon(modifier = Modifier.size(24.dp), color = Color.White)
            }
        }
        
        // Dot doré pulsant
        Row(
            modifier = Modifier.padding(start = 44.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(GabomaTheme.colors.primary.copy(alpha = alpha.value))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Système opérationnel", color = GabomaTheme.colors.primary.copy(alpha = 0.8f), fontSize = 11.sp)
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp), color = Color.White.copy(alpha = 0.1f))

        // NOUVELLE PISTE (Accent Or)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { /* Nouvelle Piste */ }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = GabomaIcons.PawNew,
                contentDescription = "Nouvelle Piste",
                tint = GabomaTheme.colors.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Nouvelle Piste",
                color = GabomaTheme.colors.primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Radar Wandana
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(GabomaIcons.RadarWandana, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Radar Wandana", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }

        // Le Rendu (Diamant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(GabomaIcons.Rendu, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Le Rendu", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }

        // Projets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(GabomaIcons.Projets, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Projets", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }

        // Trophées
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(GabomaIcons.Trophee, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Trophées", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // EN PISTE (Section Label)
        Text(
            text = "EN PISTE",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
        )

        // Recent conversations (text only, rows 44dp)
        val recents = listOf("Archéologie quantique", "Analyse du code ZION", "Rapport financier Q3")
        recents.forEach { title ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp), color = Color.White.copy(alpha = 0.1f))

        // ÉNERGIE QUOTIDIENNE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚡", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Énergie Quotidienne", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { 0.35f },
                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = GabomaTheme.colors.primary,
                    trackColor = Color.White.copy(alpha = 0.1f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("35%", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Inverted pill "ALIMENTER LA MEUTE"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GabomaTheme.colors.primary)
                    .clickable { }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("ALIMENTER LA MEUTE", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // BOTTOM ACTIONS
        

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))

        // Footer avatar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenSettings() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(GabomaTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("JD", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Daniel ANDJ", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Black Panther", color = GabomaTheme.colors.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Default.Settings, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
    }
}
