package com.gabomagpt.mobile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GabomaSurface(
    theme: GabomaThemePreset,
    fontScale: Float,
    writingStyle: WritingStyle,
    content: @Composable () -> Unit
) {
    val family = when (writingStyle) {
        WritingStyle.CLASSIQUE -> FontFamily.SansSerif
        WritingStyle.NET -> FontFamily.Monospace
        WritingStyle.EDITORIAL -> FontFamily.Serif
        WritingStyle.DENSE -> FontFamily.SansSerif
    }
    val typography = MaterialTheme.typography.copy(
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = family, fontSize = 16.sp * fontScale),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = family, fontSize = 15.sp * fontScale),
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = family, fontSize = 18.sp * fontScale),
        labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = family, fontSize = 12.sp * fontScale)
    )
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = theme.bg,
            surface = theme.bg,
            primary = theme.primary,
            secondary = theme.accent,
            onBackground = theme.text,
            onSurface = theme.text,
            onPrimary = theme.bg
        ),
        typography = typography,
        content = content
    )
}

@Composable
fun AuroraBackground(theme: GabomaThemePreset) {
    val infinite = rememberInfiniteTransition(label = "aurora")
    val driftA by infinite.animateFloat(
        initialValue = -24f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(9_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "driftA"
    )
    val driftB by infinite.animateFloat(
        initialValue = 18f,
        targetValue = -18f,
        animationSpec = infiniteRepeatable(tween(13_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "driftB"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bg)
            .drawBehind {
                drawRect(theme.bg)
                drawCircle(
                    theme.halo1,
                    radius = size.minDimension * 0.38f,
                    center = Offset(size.width * 0.2f + driftA, size.height * 0.18f)
                )
                drawCircle(
                    theme.halo2,
                    radius = size.minDimension * 0.44f,
                    center = Offset(size.width * 0.82f + driftB, size.height * 0.76f)
                )
                drawCircle(
                    theme.halo3,
                    radius = size.minDimension * 0.32f,
                    center = Offset(size.width * 0.52f, size.height * 0.42f)
                )
                for (i in 0..100) {
                    val x = (i * 37 % size.width.toInt()).toFloat()
                    val y = (i * 53 % size.height.toInt()).toFloat()
                    drawCircle(Color.White.copy(alpha = 0.02f), 1.2f, Offset(x, y), blendMode = BlendMode.Softlight)
                }
            }
    )
}

@Composable
fun GlassPanel(
    theme: GabomaThemePreset,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .blur(0.4.dp)
            .background(Color.Transparent)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (theme == GabomaThemePreset.BLANC_EMERAUDE) 0.55f else 0.10f),
                        Color.White.copy(alpha = if (theme == GabomaThemePreset.BLANC_EMERAUDE) 0.35f else 0.06f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
fun GlassHairline(theme: GabomaThemePreset, inset: Dp = 0.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = inset)
            .height(Dp.Hairline)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        if (theme == GabomaThemePreset.BLANC_EMERAUDE) HairlineDark else HairlineLight,
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun GlassMiniRow(
    theme: GabomaThemePreset,
    label: String,
    sub: String? = null,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = if (selected) theme.primary else theme.text, style = MaterialTheme.typography.bodyMedium)
            sub?.let { Text(it, color = theme.text.copy(alpha = 0.56f), style = MaterialTheme.typography.labelSmall) }
        }
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = theme.primary, modifier = Modifier.size(16.dp))
        }
    }
    GlassHairline(theme, inset = 12.dp)
}

@Composable
fun ToggleGlassRow(
    theme: GabomaThemePreset,
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    pulse: Boolean = false,
    badge: String? = null,
    icon: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            icon?.invoke()
            if (pulse) PulsingGoldDot(theme.primary, small = true)
            Text(label, color = theme.text, style = MaterialTheme.typography.bodyMedium)
            badge?.let {
                Surface(color = theme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(999.dp)) {
                    Text(
                        it,
                        color = theme.primary,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
    GlassHairline(theme, inset = 12.dp)
}

@Composable
fun SidebarMiniBlock(theme: GabomaThemePreset, title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(
            title,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            color = theme.text.copy(alpha = 0.56f),
            style = MaterialTheme.typography.labelMedium
        )
        Surface(
            color = Color.White.copy(alpha = 0.04f),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
        ) {
            Column { content() }
        }
    }
}

@Composable
fun RowScope.SocialPill(theme: GabomaThemePreset, label: String) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f)
    ) {
        Text(
            label,
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = theme.text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SheetGrip(theme: GabomaThemePreset) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(theme.text.copy(alpha = 0.22f))
        )
    }
}

@Composable
fun ModelChip(theme: GabomaThemePreset, label: String, accent: Color) {
    Surface(color = accent.copy(alpha = 0.18f), shape = RoundedCornerShape(999.dp)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = accent,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SmallToggleChip(theme: GabomaThemePreset, label: String, enabled: Boolean, onToggle: () -> Unit) {
    Surface(
        color = if (enabled) theme.accent.copy(alpha = 0.24f) else Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label.contains("Invoquer")) {
                LoxoIcon(if (enabled) theme.primary else theme.text, Modifier.size(14.dp))
            }
            Text(
                label,
                color = if (enabled) theme.primary else theme.text,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun SettingsGlassCard(theme: GabomaThemePreset, title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
    ) {
        Column {
            Text(
                title,
                modifier = Modifier.padding(start = 18.dp, top = 16.dp, end = 18.dp, bottom = 6.dp),
                color = theme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

@Composable
fun ThemeRowGroup(theme: GabomaThemePreset, title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(
            title,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            color = theme.text.copy(alpha = 0.62f),
            style = MaterialTheme.typography.labelMedium
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.04f))
        ) {
            content()
        }
    }
}

@Composable
fun GlassSelectableRow(
    theme: GabomaThemePreset,
    label: String,
    trailing: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = if (selected) theme.primary else theme.text, style = MaterialTheme.typography.bodyLarge)
            trailing?.let {
                Text(it, color = theme.text.copy(alpha = 0.58f), style = MaterialTheme.typography.labelMedium)
            }
        }
        if (selected) Icon(Icons.Outlined.Check, contentDescription = null, tint = theme.primary)
    }
    GlassHairline(theme, inset = 16.dp)
}

@Composable
fun SettingsToggleRow(
    theme: GabomaThemePreset,
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(label, color = theme.text, style = MaterialTheme.typography.bodyLarge)
            badge?.let {
                Surface(color = theme.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(999.dp)) {
                    Text(
                        it,
                        color = theme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
    GlassHairline(theme, inset = 16.dp)
}

@Composable
fun GlassStaticRow(theme: GabomaThemePreset, label: String, trailing: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = theme.text, style = MaterialTheme.typography.bodyLarge)
            Text(trailing, color = theme.text.copy(alpha = 0.58f), style = MaterialTheme.typography.labelMedium)
        }
    }
    GlassHairline(theme, inset = 16.dp)
}

@Composable
fun SidebarSectionTitle(text: String) {
    Text(
        text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge
    )
}
