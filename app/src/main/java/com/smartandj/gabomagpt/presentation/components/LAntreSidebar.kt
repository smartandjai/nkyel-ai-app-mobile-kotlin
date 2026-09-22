// presentation/components/LAntreSidebar.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.sp

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  L'ANTRE SIDEBAR - Navigation drawer with glassmorphism & conversation grouping
 *  Features: Haze blur (28dp), vertical gradient overlay, hairline dividers, spring animations
 * ═══════════════════════════════════════════════════════════════════════════════
 */

data class ConversationItem(
    val id: String,
    val title: String,
    val preview: String,
    val timestamp: Long,
    val isSelected: Boolean = false
)

enum class ConversationGroup {
    TODAY, THIS_WEEK, OLDER
}

@Composable
fun LAntreSidebar(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNewConversation: () -> Unit,
    onSelectConversation: (String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    conversations: Map<ConversationGroup, List<ConversationItem>> = emptyMap(),
    selectedConversationId: String? = null,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isOpen,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(
                    dampingRatio = 0.85f,
                    stiffness = 340f
                )
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = spring(
                    dampingRatio = 0.85f,
                    stiffness = 340f
                )
            )
        },
        modifier = modifier
    ) { open ->
        if (!open) return@AnimatedContent

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(GabomaColors.SurfaceDepth2)
        ) {
            // ── HEADER - Text "Ñkyel AI" with dolphin
            // ─────────────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GabomaColors.ElevatedBlackPanther.copy(alpha = 0.7f))
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ñkyel AI",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = GabomaColors.AccentBlackPanther
                )
            }

            // ─────────────────────────────────────────────────────────────────────
            // CONVERSATIONS - Grouped by time period
            // ─────────────────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                // ── STATIC ITEMS (Nouvelle Conversation, Recherche, Projets, Le Sanctuaire)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNewConversation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GabomaColors.AccentBlackPanther
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = "+ Nouvelle Conversation",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = GabomaColors.AbyssBlack
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                StaticSidebarItem(icon = "🔍", label = "Recherche Web")
                HorizontalDivider(color = GabomaColors.Divider, thickness = 0.5.dp)
                StaticSidebarItem(icon = "📁", label = "Missions & Projets")
                HorizontalDivider(color = GabomaColors.Divider, thickness = 0.5.dp)
                StaticSidebarItem(icon = "💎", label = "Le Sanctuaire")

                Spacer(modifier = Modifier.height(16.dp))
                ConversationDivider()

                // ── CONVERSATIONS RÉCENTES
                if (conversations.values.flatten().isNotEmpty()) {
                    ConversationGroupSection(
                        title = "Conversations Récentes",
                        conversations = conversations.values.flatten().sortedByDescending { it.timestamp }.take(10),
                        selectedId = selectedConversationId,
                        onSelect = onSelectConversation,
                        onDelete = onDeleteConversation
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ─────────────────────────────────────────────────────────────────────
            // FOOTER - Quota & Compte Actif
            // ─────────────────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GabomaColors.SurfaceDepth3)
                    .padding(16.dp)
            ) {
                // ── Quota (Au-dessus)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = "Quota",
                        tint = GabomaColors.AccentBlackPanther,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quota d'intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        color = GabomaColors.TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "100%",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = GabomaColors.TextPrimary
                    )
                }

                // ── Profil Utilisateur & Paramètres
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Initiales DA
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GabomaColors.SurfaceDepth3, androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "DA",
                                style = MaterialTheme.typography.labelLarge,
                                color = GabomaColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "danielandj@smartandjai.com",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                                color = GabomaColors.TextPrimary,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 150.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Badge Blue Panther
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                                color = GabomaColors.InfoBlue.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "BLUE PANTHER",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = GabomaColors.InfoBlue
                                )
                            }
                        }
                    }

                    // Bouton L'Antre
                    IconButton(
                        onClick = { /* Aller à l'Antre */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Paramètres",
                            tint = GabomaColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StaticSidebarItem(icon: String, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = GabomaColors.TextPrimary
        )
    }
}

@Composable
private fun ConversationGroupSection(
    title: String,
    conversations: List<ConversationItem>,
    selectedId: String? = null,
    onSelect: (String) -> Unit = {},
    onDelete: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = GabomaColors.TextTertiary,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .padding(horizontal = 12.dp)
        )

        conversations.forEach { item ->
            ConversationListItem(
                conversation = item,
                isSelected = item.id == selectedId,
                onSelect = { onSelect(item.id) },
                onDelete = { onDelete(item.id) }
            )
        }
    }
}

@Composable
private fun ConversationListItem(
    conversation: ConversationItem,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var isHovering by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) GabomaColors.SurfaceDepth3 else GabomaColors.SurfaceDepth2,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onSelect)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = conversation.title,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) GabomaColors.AccentBlackPanther else GabomaColors.TextPrimary,
                maxLines = 1
            )

            Text(
                text = conversation.preview,
                style = MaterialTheme.typography.bodySmall,
                color = GabomaColors.TextSecondary,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Delete button (appears on hover)
        if (isSelected || isHovering) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = GabomaColors.TextTertiary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete conversation",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ConversationDivider() {
    Divider(
        color = GabomaColors.Divider,
        thickness = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

/**
 * Sidebar overlay that allows dismissal when clicking background
 */
@Composable
fun LAntreSidebarWithOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNewConversation: () -> Unit,
    onSelectConversation: (String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    conversations: Map<ConversationGroup, List<ConversationItem>> = emptyMap(),
    selectedConversationId: String? = null
) {
    // Scrim (background overlay)
    if (isOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GabomaColors.AbyssBlack.copy(alpha = 0.32f))
                .clickable(enabled = true, onClick = onDismiss)
        )
    }

    // Sidebar
    LAntreSidebar(
        isOpen = isOpen,
        onDismiss = onDismiss,
        onNewConversation = onNewConversation,
        onSelectConversation = onSelectConversation,
        onDeleteConversation = onDeleteConversation,
        conversations = conversations,
        selectedConversationId = selectedConversationId
    )
}
