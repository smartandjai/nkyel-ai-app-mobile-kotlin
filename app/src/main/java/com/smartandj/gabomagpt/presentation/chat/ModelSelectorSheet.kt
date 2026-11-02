package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import com.smartandj.gabomagpt.presentation.theme.LocalGabomaColors
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.unit.Dp

@Composable
fun ModelSelectorSheet(
    currentModel: GabomaChatModel,
    onModelSelected: (GabomaChatModel) -> Unit,
    onDismiss: () -> Unit
) {
    val gabomaColors = LocalGabomaColors.current
    var selectedModel by remember { mutableStateOf(currentModel) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Black.copy(alpha = 0.32f),
        containerColor = gabomaColors.background,
        tonalElevation = 0.dp,
        sheetMaxWidth = Dp.Unspecified
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Header
            Text(
                text = "Sélectionner un modèle",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = gabomaColors.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val mainModels = GabomaChatModel.values().toList()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mainModels) { model ->
                    ModelItemCard(
                        model = model,
                        isSelected = selectedModel == model,
                        gabomaColors = gabomaColors,
                        onClick = { selectedModel = model }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirm button
            Button(
                onClick = {
                    onModelSelected(selectedModel)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gabomaColors.primary
                )
            ) {
                Text(
                    text = "Confirmer",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = gabomaColors.background
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ModelItemCard(
    model: GabomaChatModel,
    isSelected: Boolean,
    gabomaColors: com.smartandj.gabomagpt.presentation.theme.GabomaThemeColors,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected)
            gabomaColors.primary.copy(alpha = 0.15f)
        else
            gabomaColors.primary.copy(alpha = 0.05f),
        label = "modelBgColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "modelScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.dp else 0.5.dp,
            color = if (isSelected)
                gabomaColors.primary.copy(alpha = 0.6f)
            else
                gabomaColors.primary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Model name
                Text(
                    text = model.displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace,
                    color = gabomaColors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Icons.Filled.Description + Tier
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = model.description,
                        fontSize = 12.sp,
                        color = gabomaColors.textSecondary
                    )

                    // Tier badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = gabomaColors.primary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = model.tier,
                            modifier = Modifier.padding(4.dp, 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = gabomaColors.primary
                        )
                    }
                }
            }

            // Checkmark if selected
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sélectionné",
                    tint = gabomaColors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
