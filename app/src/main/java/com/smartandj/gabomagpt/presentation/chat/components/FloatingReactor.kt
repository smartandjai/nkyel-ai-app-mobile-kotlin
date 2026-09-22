package com.smartandj.gabomagpt.presentation.chat.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.presentation.theme.GabomaTheme
import com.smartandj.gabomagpt.presentation.theme.GabomaIcons

data class NkyelModel(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector
)

typealias GabomaModel = NkyelModel

val NKYEL_MODELS = listOf(
    NkyelModel("aurata", "Aurata", "Mode Flash — rapide, exécution quotidienne", Icons.Default.Bolt),
    NkyelModel("nkyel", "Ñkyel", "Mode Pro — raisonnement logique profond", Icons.Default.Psychology),
    NkyelModel("onyxgris", "OnyxGris", "Agent Perroquet Gris — tâches simples", Icons.Default.SmartToy),
    NkyelModel("blackpanther", "Black Panther", "Le GOAT — multi-agent autonome", Icons.Default.Pets),
    NkyelModel("wandana", "Wandana", "L'Éléphant — recherche web profonde", Icons.Default.TravelExplore)
)

val GABOMA_MODELS = NKYEL_MODELS

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FloatingReactor(
    modifier: Modifier = Modifier,
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSend: (String, String) -> Unit,
    isGenerating: Boolean,
    onStop: () -> Unit
) {
    var activeModel by remember { mutableStateOf(NKYEL_MODELS.first()) }
    var isMenuOpen by remember { mutableStateOf(false) }
    var isLiveMode by remember { mutableStateOf(false) }

    val hasInput = inputText.trim().isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Dropdown Menu (Cinematic deployment)
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium)
            ) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.padding(bottom = 76.dp).align(Alignment.BottomStart)
        ) {
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp)
            ) {
                NKYEL_MODELS.forEach { model ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                activeModel = model
                                isMenuOpen = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = model.icon, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = model.name,
                                color = if (activeModel.id == model.id) GabomaTheme.colors.primary else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = model.description,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                // Attachments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { /* Handle attachment */ }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Pièces jointes locales", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
        }

        // The Floating Pill Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Left Axis: + Button or Active Model Pill
            AnimatedContent(targetState = isMenuOpen, label = "master_button") { menuOpen ->
                if (!menuOpen) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .clickable { isMenuOpen = true }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = activeModel.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeModel.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    IconButton(onClick = { isMenuOpen = false }) {
                        Text(text = "✕", color = Color.White)
                    }
                }
            }

            // Center Input
            TextField(
                value = inputText,
                onValueChange = onInputChanged,
                placeholder = { Text("Message ${activeModel.name}...", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = GabomaTheme.colors.primary
                ),
                maxLines = 5
            )

            // Right Axis: Mic/Wave or Send Button
            AnimatedContent(
                targetState = hasInput || isGenerating,
                label = "right_axis",
                transitionSpec = {
                    scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                }
            ) { isTypingOrGenerating ->
                if (isTypingOrGenerating) {
                    // Send or Stop button
                    IconButton(
                        onClick = {
                            if (isGenerating) onStop() else {
                                onSend(inputText, activeModel.id)
                                onInputChanged("")
                            }
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .background(GabomaTheme.colors.primary, CircleShape)
                    ) {
                        if (isGenerating) {
                            // Stop square
                            Box(modifier = Modifier.size(12.dp).background(Color.Black, RoundedCornerShape(2.dp)))
                        } else {
                            // Send arrow
                            Text(text = "↑", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                } else {
                    // Mic and Wave
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* Mic dictation */ }) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = "Dictée", tint = Color.White)
                        }
                        
                        // Breathing Wave
                        val infiniteTransition = rememberInfiniteTransition()
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        IconButton(onClick = { isLiveMode = !isLiveMode }) {
                            Icon(imageVector = Icons.Default.GraphicEq, contentDescription = "Live", tint = Color.White, modifier = Modifier.scale(if(isLiveMode) scale else 1f))
                        }
                    }
                }
            }
        }
    }
}
