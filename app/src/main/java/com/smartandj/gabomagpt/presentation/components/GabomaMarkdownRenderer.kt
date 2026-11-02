// presentation/components/GabomaMarkdownRenderer.kt
package com.smartandj.gabomagpt.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.smartandj.gabomagpt.presentation.theme.*
import androidx.compose.ui.unit.sp
val StreamingTextStyle = androidx.compose.ui.text.TextStyle(
    fontSize = 16.sp,
    color = GabomaColors.TextPrimary
)
val MarkdownH1Style = androidx.compose.ui.text.TextStyle(
    fontSize = 24.sp,
    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
    color = GabomaColors.TextPrimary
)
val MarkdownH2Style = androidx.compose.ui.text.TextStyle(
    fontSize = 20.sp,
    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
    color = GabomaColors.TextPrimary
)
val MarkdownH3Style = androidx.compose.ui.text.TextStyle(
    fontSize = 18.sp,
    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
    color = GabomaColors.TextPrimary
)
val CodeBlockStyle = androidx.compose.ui.text.TextStyle(
    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
    fontSize = 14.sp,
    color = GabomaColors.TextSecondary
)


/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMA MARKDOWN RENDERER - Wrapper for mikepenz markdown with custom styling
 *  Features: Streaming support, custom code block styling, theme-aware colors
 * ═══════════════════════════════════════════════════════════════════════════════
 */

/**
 * Main markdown renderer with Gaboma theming
 * - Code blocks: SurfaceDepth3 bg, SurfaceDepth4 header, TurquoiseIA text
 * - Headings: MarkdownH1/H2/H3 styles (24sp → 18sp)
 * - Links: TurquoiseIA with underline
 * - Lists: TextPrimary with proper indentation
 */
@Composable
fun GabomaMarkdownRenderer(
    markdown: String,
    modifier: Modifier = Modifier,
    isStreaming: Boolean = false,
    onCodeBlockClick: ((String, String) -> Unit)? = null
) {
    val contentToRender = if (isStreaming) {
        "$markdown █"
    } else {
        markdown
    }

    Markdown(
        content = contentToRender,
        modifier = modifier.padding(horizontal = 0.dp),
        colors = markdownColors(),
        typography = markdownTypography()
    )
}

/**
 * Custom markdown color scheme aligned with Gaboma design system
 */
@Composable
private fun markdownColors() = com.mikepenz.markdown.m3.markdownColor()

/**
 * Custom markdown typography aligned with Gaboma Inter font system
 */
@Composable
private fun markdownTypography() = com.mikepenz.markdown.m3.markdownTypography()

/**
 * Standalone markdown heading component with custom styling
 */
@Composable
fun MarkdownHeading(
    text: String,
    level: Int = 1,  // 1-3 for H1-H3
    modifier: Modifier = Modifier,
    color: Color = GabomaColors.TextPrimary
) {
    val style = when (level) {
        2 -> MarkdownH2Style
        3 -> MarkdownH3Style
        else -> MarkdownH1Style
    }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

/**
 * Markdown paragraph with proper styling
 */
@Composable
fun MarkdownParagraph(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GabomaColors.TextPrimary,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

/**
 * Markdown code block with copy button
 */
@Composable
fun MarkdownCodeBlock(
    code: String,
    language: String = "",
    modifier: Modifier = Modifier,
    onCopyClick: (() -> Unit)? = null
) {
    GabomaCodeBlock(
        code = code,
        language = language,
        modifier = modifier,
        onCopyClick = onCopyClick
    )
}

/**
 * Markdown blockquote component
 */
@Composable
fun MarkdownBlockquote(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = GabomaColors.TextSecondary,
        modifier = modifier
            .background(
                color = GabomaColors.SurfaceDepth2,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
    )
}

/**
 * Markdown inline code
 */
@Composable
fun MarkdownInlineCode(
    code: String,
    modifier: Modifier = Modifier
) {
    InlineCodeSpan(code = code, modifier = modifier)
}

/**
 * Markdown horizontal rule
 */
@Composable
fun MarkdownDivider(
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Divider(
        color = GabomaColors.Divider,
        thickness = 0.5.dp,
        modifier = modifier.padding(vertical = 12.dp)
    )
}

/**
 * Markdown ordered list item
 */
@Composable
fun MarkdownListItem(
    text: String,
    isOrdered: Boolean = true,
    index: Int = 0,
    modifier: Modifier = Modifier
) {
    val bullet = if (isOrdered) "${index + 1}. " else "• "
    Text(
        text = "$bullet$text",
        style = MaterialTheme.typography.bodyMedium,
        color = GabomaColors.TextPrimary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 16.dp)
    )
}

/**
 * Markdown table cell (simplified)
 */
@Composable
fun MarkdownTableCell(
    text: String,
    isHeader: Boolean = false,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = if (isHeader) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        color = if (isHeader) GabomaColors.AccentBlackPanther else GabomaColors.TextPrimary,
        modifier = modifier
            .background(
                color = if (isHeader) GabomaColors.ElevatedBlackPanther else GabomaColors.SurfaceDepth3,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
            .padding(8.dp)
    )
}
