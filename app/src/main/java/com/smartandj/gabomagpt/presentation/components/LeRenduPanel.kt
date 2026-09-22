// presentation/components/LeRenduPanel.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smartandj.gabomagpt.presentation.theme.GabomaColors
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxScope

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  LE RENDU PANEL - Artifact/Output Display Side Panel
 *  Features: Haze blur (32dp), SupportingPaneScaffold layout, sliding animations
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@Composable
fun LeRenduPanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    // AnimatedVisibility with slide + fade
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it / 2 },
            animationSpec = spring(
                dampingRatio = 0.85f,
                stiffness = 340f
            )
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutHorizontally(
            targetOffsetX = { it / 2 },
            animationSpec = spring(
                dampingRatio = 0.85f,
                stiffness = 340f
            )
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                
                .background(GabomaColors.SurfaceDepth2)
        ) {
            // ─────────────────────────────────────────────────────────────────────
            // HEADER - "Le Rendu" title + Icons.Filled.Close button
            // ─────────────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GabomaColors.SurfaceDepth2)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Le Sanctuaire · Rendus & Livrables",
                    style = MaterialTheme.typography.titleMedium,
                    color = GabomaColors.AccentBlackPanther
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = GabomaColors.TextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close panel",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────────────
            // CONTENT - Scrollable artifact display
            // ─────────────────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                content = content
            )
        }
    }
}

/**
 * Simple artifact display with title and rendered content
 */
@Composable
fun ArtifactDisplay(
    title: String,
    content: String,
    contentType: ArtifactType = ArtifactType.TEXT,
    onClose: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = GabomaColors.SurfaceDepth3,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = GabomaColors.TextPrimary
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = GabomaColors.TextSecondary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close artifact",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Render based on content type
        when (contentType) {
            ArtifactType.TEXT -> {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GabomaColors.TextPrimary
                )
            }
            ArtifactType.CODE -> {
                GabomaCodeBlock(code = content, language = "code")
            }
            ArtifactType.MARKDOWN -> {
                GabomaMarkdownRenderer(markdown = content)
            }
            ArtifactType.JSON -> {
                GabomaCodeBlock(code = content, language = "json")
            }
            ArtifactType.HTML -> {
                GabomaCodeBlock(code = content, language = "html")
            }
            ArtifactType.PDF, ArtifactType.DOCX, ArtifactType.PPTX, ArtifactType.XLSX -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = GabomaColors.SurfaceDepth2,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val label = when (contentType) {
                        ArtifactType.PDF -> "Document PDF Officiel"
                        ArtifactType.DOCX -> "Rapport Word DOCX"
                        ArtifactType.PPTX -> "Présentation Slides PPTX"
                        ArtifactType.XLSX -> "Feuille de Calcul Excel XLSX"
                        else -> "Document Officiel"
                    }
                    Text(
                        text = "💎 $label",
                        style = MaterialTheme.typography.titleMedium,
                        color = GabomaColors.AccentBlackPanther
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = content.ifBlank { "Livrable généré par Ñkyel AI avec conformité éditoriale et style souverain." },
                        style = MaterialTheme.typography.bodySmall,
                        color = GabomaColors.TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

enum class ArtifactType {
    TEXT, CODE, MARKDOWN, JSON, HTML, PDF, DOCX, PPTX, XLSX
}

/**
 * Extension function for spring animation parameters
 */
fun spring(
    dampingRatio: Float = 0.85f,
    stiffness: Float = 340f
) = androidx.compose.animation.core.spring(
    dampingRatio = dampingRatio,
    stiffness = stiffness
)
