// presentation/components/GabomaIlluminatedBackground.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeDefinition

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMA ILLUMINATED BACKGROUND - "L'app s'illumine quand l'IA parle"
 *  Inspired by Reference Design 2026 animate glow effect
 *  Pulsating radial gradients at corners when AI is generating
 * ═══════════════════════════════════════════════════════════════════════════════
 */

@Composable
fun NkyelIlluminatedBackground(
    isAITyping: Boolean,
    theme: GabomaThemeDefinition,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nkyel_glow")

    // 🌊 Pulsation de l'intensité du glow (pulse quand l'IA répond)
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = if (isAITyping) theme.glowIntensity else 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nkyel_glow_alpha"
    )

    // 🔄 Rotation du gradient (effet "vivant" — spinning glow)
    val gradientAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isAITyping) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradientAngle"
    )

    // ⚡ Transition douce ON/OFF (when isAITyping toggles)
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isAITyping) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "onOffAlpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // ─────────────────────────────────────────────────────────────────────────
        // LAYER 0: Base background (80% of rule)
        // ─────────────────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.backgroundColor)
        )

        // ─────────────────────────────────────────────────────────────────────────
        // LAYER 1: Glow radial that pulses (corner bottom-left + top-right)
        // The "5%" glow effect when AI speaks
        // ─────────────────────────────────────────────────────────────────────────
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { 
                    alpha = animatedAlpha * glowAlpha * 6f  // Amplified for visibility
                }
        ) {
            // Point de lumière 1 — coin bas gauche (glowColor1)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        theme.glowColor1.copy(alpha = 0.4f),
                        theme.glowColor1.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(0f, size.height),
                    radius = size.width * 0.7f
                ),
                radius = size.width * 0.7f,
                center = Offset(0f, size.height)
            )

            // Point de lumière 2 — coin haut droit (glowColor2)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        theme.glowColor2.copy(alpha = 0.3f),
                        theme.glowColor2.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(size.width, 0f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width, 0f)
            )
        }

        // ─────────────────────────────────────────────────────────────────────────
        // LAYER 2: Content (rendered on top of glow)
        // ─────────────────────────────────────────────────────────────────────────
        content()
    }
}

@Composable
fun GabomaIlluminatedBackground(
    isAITyping: Boolean,
    theme: GabomaThemeDefinition,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = NkyelIlluminatedBackground(isAITyping, theme, modifier, content)

/**
 * Simplified variant — just the glow effect without content wrapper.
 * Use when you already have a Box() and just need the background layer.
 */
@Composable
fun NkyelGlowOverlay(
    isAITyping: Boolean,
    theme: GabomaThemeDefinition,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nkyel_glow_overlay")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = if (isAITyping) theme.glowIntensity else 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isAITyping) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "onOffAlpha"
    )

    androidx.compose.foundation.Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { 
                alpha = animatedAlpha * glowAlpha * 6f
            }
    ) {
        // Corner glow 1 (glowColor1)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    theme.glowColor1.copy(alpha = 0.4f),
                    theme.glowColor1.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = Offset(0f, size.height),
                radius = size.width * 0.7f
            ),
            radius = size.width * 0.7f,
            center = Offset(0f, size.height)
        )

        // Corner glow 2 (glowColor2)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    theme.glowColor2.copy(alpha = 0.3f),
                    theme.glowColor2.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(size.width, 0f),
                radius = size.width * 0.6f
            ),
            radius = size.width * 0.6f,
            center = Offset(size.width, 0f)
        )
    }
}

@Composable
fun GabomaGlowOverlay(
    isAITyping: Boolean,
    theme: GabomaThemeDefinition,
    modifier: Modifier = Modifier
) = NkyelGlowOverlay(isAITyping, theme, modifier)

