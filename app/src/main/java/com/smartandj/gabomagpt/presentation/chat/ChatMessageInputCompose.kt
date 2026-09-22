// presentation/chat/ChatMessageInputCompose.kt
package com.smartandj.gabomagpt.presentation.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import com.smartandj.gabomagpt.presentation.theme.LocalGabomaColors

@Composable
fun ChatMessageInputCompose(
    onSend: (String) -> Unit,
    onSendMemoAudio: (String) -> Unit = {},
    isLoading: Boolean = false,
    isLoxoActive: Boolean = false,
    onToggleLoxo: () -> Unit = {},
    currentModel: GabomaChatModel = GabomaChatModel.AURATA,
    onModelChange: (GabomaChatModel) -> Unit = {},

    blurRadius: androidx.compose.ui.unit.Dp = 24.dp
) {
    val gabomaColors = LocalGabomaColors.current
    var showModelSelector by remember { mutableStateOf(false) }

    // Wrapper sur GabomaInputBarFull avec conversion de thème
    val inputTheme = GabomaInputTheme(
        bgBase          = gabomaColors.background,
        bgSurface       = gabomaColors.elevated,
        bgSurfaceHigh   = gabomaColors.card,
        inputBarBg      = gabomaColors.background.copy(alpha = 0.95f),
        inputBarBorder  = gabomaColors.primary.copy(alpha = 0.1f),
        textPrimary     = gabomaColors.textPrimary,
        textSecondary   = gabomaColors.textSecondary,
        textTertiary    = gabomaColors.textMuted,
        accentPrimary   = gabomaColors.primary,
        accentSecondary = gabomaColors.primary.copy(alpha = 0.8f),
        sendButtonBg    = gabomaColors.primary,
        sendButtonIcon  = gabomaColors.background,
        sidebarDivider  = gabomaColors.primary.copy(alpha = 0.1f),
        errorRed        = Color(0xFFFF3B30)
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        GabomaInputBarFull(
            theme        = inputTheme,
            onSend       = { text, _, _ ->
                if (text.isNotBlank()) {
                    onSend(text)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = gabomaColors.textMuted.copy(alpha = 0.7f),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Gaboma AI, votre IA souveraine, peut faire des erreurs. Vérifiez les informations.",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = gabomaColors.textMuted.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }

    // Model selector bottom sheet
    if (showModelSelector) {
        ModelSelectorSheet(
            currentModel = currentModel,
            onModelSelected = { newModel ->
                onModelChange(newModel)
                showModelSelector = false
            },
            onDismiss = { showModelSelector = false }
        )
    }
}
