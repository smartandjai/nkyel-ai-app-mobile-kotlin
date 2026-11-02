package com.smartandj.gabomagpt.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * GabomaGlassContainer
 *
 * Glassmorphism de luxe — blur + border lumineuse (façon Apple Vision Pro).
 * Utilise le blur natif Compose au lieu de Haze pour la compatibilité.
 */
@Composable
fun GabomaGlassContainer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    blurRadius: Dp = 25.dp,
    noiseFactor: Float = 0.05f,
    tintColor: Color = Color.White.copy(alpha = 0.15f),
    borderColor1: Color = Color.White.copy(alpha = 0.4f),
    borderColor2: Color = Color.White.copy(alpha = 0.1f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .blur(blurRadius)
            .background(tintColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor1, borderColor2)
                ),
                shape = shape
            )
    ) {
        content()
    }
}
