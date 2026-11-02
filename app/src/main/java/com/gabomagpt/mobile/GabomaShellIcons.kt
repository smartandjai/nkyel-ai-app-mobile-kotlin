package com.gabomagpt.mobile

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PulsingGoldDot(color: Color, small: Boolean = false) {
    val infinite = rememberInfiniteTransition(label = "dot")
    val scale by infinite.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "scale"
    )
    Box(
        modifier = Modifier
            .size(if (small) 7.dp else 10.dp)
            .drawBehind {
                drawCircle(color.copy(alpha = 0.24f), radius = size.minDimension / 2 * scale)
                drawCircle(color, radius = size.minDimension / 2)
            }
    )
}

fun ArtifactKind.icon(): ImageVector = when (this) {
    ArtifactKind.TEXT -> Icons.Outlined.Article
    ArtifactKind.MARKDOWN -> Icons.Outlined.Description
    ArtifactKind.HTML -> Icons.Outlined.GridOn
    ArtifactKind.CODE -> Icons.Outlined.ContentCopy
    ArtifactKind.PDF -> Icons.Outlined.PictureAsPdf
    ArtifactKind.WORD -> Icons.Outlined.Description
    ArtifactKind.EXCEL -> Icons.Outlined.GridOn
    ArtifactKind.POWERPOINT -> Icons.Outlined.Slideshow
}

@Composable
fun AndjSovereignIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.5f, 0f)
            lineTo(size.width, size.height * 0.35f)
            lineTo(size.width * 0.82f, size.height)
            lineTo(size.width * 0.18f, size.height)
            lineTo(0f, size.height * 0.35f)
            close()
        }
        drawPath(path = path, color = color.copy(alpha = 0.15f), style = Fill)
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = size.minDimension * 0.08f, join = StrokeJoin.Round)
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.58f),
            end = Offset(size.width * 0.68f, size.height * 0.58f),
            strokeWidth = size.minDimension * 0.08f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.24f),
            end = Offset(size.width * 0.5f, size.height * 0.76f),
            strokeWidth = size.minDimension * 0.08f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun IbogaAiIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRoundRect(
            color = color.copy(alpha = 0.12f),
            cornerRadius = CornerRadius(size.minDimension * 0.35f, size.minDimension * 0.35f)
        )
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(size.minDimension * 0.35f, size.minDimension * 0.35f),
            style = Stroke(width = size.minDimension * 0.08f)
        )
        drawCircle(color = color, radius = size.minDimension * 0.09f, center = Offset(size.width * 0.35f, size.height * 0.42f))
        drawCircle(color = color, radius = size.minDimension * 0.09f, center = Offset(size.width * 0.65f, size.height * 0.42f))
        drawLine(
            color = color,
            start = Offset(size.width * 0.3f, size.height * 0.68f),
            end = Offset(size.width * 0.7f, size.height * 0.68f),
            strokeWidth = size.minDimension * 0.08f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun LoxoIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawCircle(color.copy(alpha = 0.12f), radius = size.minDimension / 2)
        drawCircle(color, radius = size.minDimension * 0.44f, style = Stroke(width = size.minDimension * 0.08f))
        drawCircle(color, radius = size.minDimension * 0.12f)
        drawLine(
            color,
            Offset(size.width * 0.52f, size.height * 0.52f),
            Offset(size.width * 0.82f, size.height * 0.82f),
            size.minDimension * 0.08f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SmartAndJTechIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.08f
        drawLine(color, Offset(size.width * 0.18f, size.height * 0.2f), Offset(size.width * 0.82f, size.height * 0.2f), stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.18f, size.height * 0.5f), Offset(size.width * 0.82f, size.height * 0.5f), stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.18f, size.height * 0.8f), Offset(size.width * 0.82f, size.height * 0.8f), stroke, cap = StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.5f, size.height * 0.2f), Offset(size.width * 0.5f, size.height * 0.8f), stroke, cap = StrokeCap.Round)
    }
}
