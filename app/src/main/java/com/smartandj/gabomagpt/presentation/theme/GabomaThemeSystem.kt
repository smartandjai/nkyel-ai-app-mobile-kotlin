// presentation/theme/GabomaThemeSystem.kt
package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMAGPT 6-THEME SYSTEM - Apple MX + Illuminate 2026 Illuminate
 *  "L'app s'illumine quand l'IA parle" — Gabonaise identity + SmartandJ
 * ═══════════════════════════════════════════════════════════════════════════════
 */

// ─────────────────────────────────────────────────────────────────────────────
// ENUM - Theme Selection (6 Gabonese scenes → premium color systems)
// ─────────────────────────────────────────────────────────────────────────────
enum class GabomaThemeType(
    val displayName: String,
    val description: String,
    val isLight: Boolean = false
) {
    BLACK_PANTHER(
        "Black Panther",
        "Mode agent autonome par défaut"
    ),
    NUIT_LOPE(
        "Nuit Lopé",
        "Forêt équatoriale la nuit — OLED flagship"
    ),
    AURORE_OGOUE(
        "Aurore Ogooué",
        "Fleuve Ogooué à l'aube — Thème blanc émeraude",
        isLight = true
    ),
    BLEU_NUIT(
        "Bleu Nuit",
        "Océan Atlantique à minuit — Profondeur marine"
    ),
    VIOLETTE_MANDRILLE(
        "Violette Mandrille",
        "Mandrill du Gabon + SmartandJ — Brand premium"
    ),
    NEO_BLANC(
        "Néo Blanc",
        "Marbre de Libreville — Apple minimal",
        isLight = true
    );

    val isDark: Boolean get() = !isLight

    companion object {
        val default: GabomaThemeType = BLACK_PANTHER
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DATA CLASSES - Theme Configuration
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Markdown colors per theme — **gras**, H1/H2/H3, code, links, etc.
 * The "80% neutral, 15% accent, 5% glow" rule applied to text rendering.
 */
data class GabomaMarkdownColors(
    val bodyText: Color,           // Normal paragraph text
    val boldText: Color,           // **Gras** text
    val h1: Color,                 // # Titre (level 1)
    val h2: Color,                 // ## Sous-titre (level 2)
    val h3: Color,                 // ### Petit titre (level 3)
    val codeText: Color,           // `Inline code` text
    val codeBg: Color,             // Inline code background
    val codeBlockText: Color,      // Code block text (same as codeText usually)
    val codeBlockBg: Color,        // Code block background
    val linkText: Color,           // [Lien] hyperlink text
    val italicText: Color,         // *Italique* text
    val blockquoteText: Color,     // > Citation text
    val blockquoteBg: Color,       // Citation background
    val dividerLine: Color         // --- horizontal divider
)

/**
 * UI Tokens per theme — sidebar, input bar, buttons, bubbles.
 * Controls glass morphism, surface depths, interactive states.
 */
data class GabomaUITokens(
    val sidebarBg: Color,           // L'Antre drawer background
    val sidebarItemActive: Color,   // Selected conversation highlight
    val sidebarDivider: Color,      // Hairline separator
    val inputBarBg: Color,          // Chat input bar (glassmorphic)
    val inputBarBorder: Color,      // Input bar border
    val sendButtonBg: Color,        // Send/Submit button
    val sendButtonIcon: Color,      // Icon color on button
    val settingsSectionBg: Color,   // Icons.Filled.Settings area background
    val settingsToggleOn: Color,    // Toggle switch when enabled
    val userBubbleBg: Color,        // User message background
    val aiBubbleBg: Color,          // AI message background
    val aiBubbleBorder: Color       // AI bubble border (for definition)
)

/**
 * Master theme definition — contains all colors + tokens for one theme.
 * Handles surface hierarchy, text hierarchy, accent colors, dual glow effects.
 * Philosophy: 80% neutral (rest) + 15% accent (identity) + 5% glow (AI animation)
 */
data class GabomaThemeDefinition(
    val type: GabomaThemeType,
    val name: String,
    val description: String,
    
    // Core surfaces (80% of screen — resting state)
    val backgroundColor: Color,
    val surfaceColor: Color,
    val cardColor: Color,
    
    // Text hierarchy (rest of the 80%)
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    
    // Accent color (15% of screen — identity)
    val accentPrimary: Color,
    val accentSecondary: Color,
    
    // Glow effect when AI is typing (5% of screen, animated)
    // glowColor1 = bottom-left corner; glowColor2 = top-right corner
    val glowColor1: Color,      // Primary glow (e.g., Turquoise IA)
    val glowColor2: Color,      // Secondary glow (e.g., Accent color)
    val glowIntensity: Float,   // 0.0 = off, 1.0 = full brightness (typically 0.10-0.22)
    
    // Markdown-specific colors
    val markdownColors: GabomaMarkdownColors,
    
    // UI components-specific tokens
    val uiTokens: GabomaUITokens
)

// ─────────────────────────────────────────────────────────────────────────────
// THEME DEFINITIONS - 6 complete themes with exact hex codes (user-supplied)
// ─────────────────────────────────────────────────────────────────────────────

object GabomaThemeDefinitions {

    /**
     * 🐾 BLACK PANTHER — Mode agent autonome par défaut. Noir absolu, or sablé.
     * Scène: Panthère noire dans la nuit gabonaise. Noir total, reflets or discrets.
     * Philosophy: 80% black (rest) + 15% gold (identity) + 5% gold glow (AI)
     */
    val BlackPanther = GabomaThemeDefinition(
        type = GabomaThemeType.BLACK_PANTHER,
        name = "Black Panther",
        description = "Mode agent autonome par défaut",
        
        // Surfaces
        backgroundColor = Color(0xFF020304),
        surfaceColor = Color(0xFF0A0908),
        cardColor = Color(0xFF14130F),
        
        // Text
        textPrimary = Color(0xFFEDEAE3),
        textSecondary = Color(0xFF8A8378),
        textTertiary = Color(0xFF525250),
        
        // Accents
        accentPrimary = Color(0xFFC5A059),
        accentSecondary = Color(0xFF1F9D6B),
        
        // Glow
        glowColor1 = Color(0xFFC5A059),
        glowColor2 = Color(0xFF1F9D6B),
        glowIntensity = 0.14f,
        
        // Markdown
        markdownColors = GabomaMarkdownColors(
            bodyText = Color(0xFFEDEAE3),
            boldText = Color(0xFFC5A059),
            h1 = Color(0xFFC5A059),
            h2 = Color(0xFF1F9D6B),
            h3 = Color(0xFF5B8DEF),
            codeText = Color(0xFFD98E3B),
            codeBg = Color(0xFF14130F),
            codeBlockText = Color(0xFFD98E3B),
            codeBlockBg = Color(0xFF14130F),
            linkText = Color(0xFF5B8DEF),
            italicText = Color(0xFF8A8378),
            blockquoteText = Color(0xFFC5A059),
            blockquoteBg = Color(0xFF14130F),
            dividerLine = Color(0xFF28251E)
        ),
        
        // UI Tokens
        uiTokens = GabomaUITokens(
            sidebarBg = Color(0x18020304),
            sidebarItemActive = Color(0x22C5A059),
            sidebarDivider = Color(0xFF28251E),
            inputBarBg = Color(0x330A0908),
            inputBarBorder = Color(0x44C5A059),
            sendButtonBg = Color(0xFFC5A059),
            sendButtonIcon = Color(0xFF020304),
            settingsSectionBg = Color(0xFF14130F),
            settingsToggleOn = Color(0xFF1F9D6B),
            userBubbleBg = Color(0xFF28251E),
            aiBubbleBg = Color(0xFF14130F),
            aiBubbleBorder = Color(0xFF28251E)
        )
    )

    /**
     * 🌑 NUIT LOPÉ — Forêt équatoriale la nuit. Noir absolu, or discret, turquoise bioluminescent.
     * Scène: Lopé à 2h du matin. Noir total, reflets or dans les feuilles, lueur turquoise des insectes.
     * Philosophy: 80% black (rest) + 15% gold (structure) + 5% turquoise (AI glow)
     */
    val NuitLope = GabomaThemeDefinition(
        type = GabomaThemeType.NUIT_LOPE,
        name = "Nuit Lopé",
        description = "Forêt équatoriale la nuit — OLED flagship",
        
        // Surfaces — 80% of screen
        backgroundColor = Color(0xFF050507),     // Almost-black, slight blue tint
        surfaceColor = Color(0xFF0D0D12),        // One step above
        cardColor = Color(0xFF0F0F14),           // Elevated surfaces
        
        // Text hierarchy
        textPrimary = Color(0xFFEDECE6),         // Off-white, warm tone
        textSecondary = Color(0xFF888680),       // Warm gray
        textTertiary = Color(0xFF4A4840),        // Dark gray
        
        // Accents — 15% of screen
        accentPrimary = Color(0xFFC9A84C),       // Or Équateur (gold)
        accentSecondary = Color(0xFF0A3D2A),     // Vert forêt très foncé
        
        // Glow — 5% of screen, animated corners
        glowColor1 = Color(0xFF00D4AA),          // Turquoise IA (bottom-left)
        glowColor2 = Color(0xFFC9A84C),          // Or gold (top-right)
        glowIntensity = 0.18f,
        
        // Markdown
        markdownColors = GabomaMarkdownColors(
            bodyText = Color(0xFFEDECE6),
            boldText = Color(0xFFC9A84C),        // H1 → Or
            h1 = Color(0xFFC9A84C),
            h2 = Color(0xFF00D4AA),              // Turquoise
            h3 = Color(0xFF4ADE80),              // Vert clair
            codeText = Color(0xFF00D4AA),
            codeBg = Color(0xFF0F0F18),
            codeBlockText = Color(0xFF00D4AA),
            codeBlockBg = Color(0xFF0F0F18),
            linkText = Color(0xFF4ADE80),        // Vert clair
            italicText = Color(0xFFB8B6AE),      // Warm gray
            blockquoteText = Color(0xFF00D4AA),
            blockquoteBg = Color(0xFF0F0F14),
            dividerLine = Color(0xFF1A1A25)
        ),
        
        // UI Tokens
        uiTokens = GabomaUITokens(
            sidebarBg = Color(0x180A0A0F),
            sidebarItemActive = Color(0x22C9A84C),
            sidebarDivider = Color(0xFF1E1E28),
            inputBarBg = Color(0x330A0A0F),
            inputBarBorder = Color(0x44C9A84C),
            sendButtonBg = Color(0xFFC9A84C),
            sendButtonIcon = Color(0xFF050507),
            settingsSectionBg = Color(0xFF0F0F14),
            settingsToggleOn = Color(0xFF00D4AA),
            userBubbleBg = Color(0xFF0A3D2A),    // Vert forêt
            aiBubbleBg = Color(0xFF0F0F14),
            aiBubbleBorder = Color(0xFF1A1A25)
        )
    )

    /**
     * ☀️ AURORE OGOOUÉ — Fleuve Ogooué à l'aube. Blanc laiteux, vert émeraude, or chaud.
     * Scène: Ogooué sunrise. Blanc sur l'eau, vert émeraude dans les berges, soleil montant en or.
     * Philosophy: 80% white (rest) + 15% emerald (structure) + 5% emerald glow (AI typing)
     */
    val AuroreOgoue = GabomaThemeDefinition(
        type = GabomaThemeType.AURORE_OGOUE,
        name = "Aurore Ogooué",
        description = "Fleuve Ogooué à l'aube — Thème blanc émeraude",
        
        // Surfaces
        backgroundColor = Color(0xFFF8F8F4),     // Warm white
        surfaceColor = Color(0xFFFFFFFF),        // Pure white
        cardColor = Color(0xFFF5F8F6),           // Slight green tint
        
        // Text
        textPrimary = Color(0xFF18181B),         // Near-black
        textSecondary = Color(0xFF6B6968),       // Warm gray
        textTertiary = Color(0xFFB0AEA8),        // Light gray
        
        // Accents
        accentPrimary = Color(0xFF059669),       // Émeraude (NOT drapeau #007A4D direct)
        accentSecondary = Color(0xFFA67C2E),     // Dark gold on white
        
        // Glow
        glowColor1 = Color(0xFF00C896),          // Émeraude vif (bottom-left)
        glowColor2 = Color(0xFFA67C2E),          // Gold foncé (top-right)
        glowIntensity = 0.10f,                   // Subtler on light bg
        
        // Markdown
        markdownColors = GabomaMarkdownColors(
            bodyText = Color(0xFF18181B),
            boldText = Color(0xFF059669),        // Émeraude
            h1 = Color(0xFFA67C2E),              // Or foncé
            h2 = Color(0xFF0D9488),              // Teal foncé
            h3 = Color(0xFF00C896),              // Émeraude vif
            codeText = Color(0xFF064E3B),        // Vert très foncé
            codeBg = Color(0xFFECFDF5),          // Vert très clair
            codeBlockText = Color(0xFF064E3B),
            codeBlockBg = Color(0xFFECFDF5),
            linkText = Color(0xFF0369A1),        // Bleu Gabon (drapeau)
            italicText = Color(0xFF6B6968),
            blockquoteText = Color(0xFF059669),
            blockquoteBg = Color(0xFFF0F7F4),
            dividerLine = Color(0xFFD4D9D6)
        ),
        
        // UI Tokens
        uiTokens = GabomaUITokens(
            sidebarBg = Color(0xFFFAFAF7),
            sidebarItemActive = Color(0xFFE0F2ED),
            sidebarDivider = Color(0xFFE5E9E7),
            inputBarBg = Color(0xFFF5F5F0),
            inputBarBorder = Color(0xFFC5D9CF),
            sendButtonBg = Color(0xFF059669),
            sendButtonIcon = Color(0xFFFFFFFF),
            settingsSectionBg = Color(0xFFFAFAF7),
            settingsToggleOn = Color(0xFF00C896),
            userBubbleBg = Color(0xFFE0F2ED),    // Light émeraude
            aiBubbleBg = Color(0xFFFFFFFF),
            aiBubbleBorder = Color(0xFFE5E9E7)
        )
    )

    /**
     * 🌊 BLEU NUIT — Océan Atlantique gabonais à minuit. Bleu abysse, or, cyan.
     * Scène: Libreville from Atlantic. Bleu abysse, ville lights or, vagues cyan.
     * Philosophy: 80% blue-black (deep) + 15% gold (city lights) + 5% cyan glow (AI)
     */
    val BleuNuit = GabomaThemeDefinition(
        type = GabomaThemeType.BLEU_NUIT,
        name = "Bleu Nuit",
        description = "Océan Atlantique à minuit — Profondeur marine",
        
        // Surfaces
        backgroundColor = Color(0xFF060A14),     // Deep blue-black
        surfaceColor = Color(0xFF0C1220),        // Blue night
        cardColor = Color(0xFF0A0F1A),           // Slight elevation
        
        // Text
        textPrimary = Color(0xFFEFF6FF),         // Cold white
        textSecondary = Color(0xFFADB8D4),       // Blue-gray
        textTertiary = Color(0xFF4A5568),        // Dark blue
        
        // Accents
        accentPrimary = Color(0xFFC9A84C),       // Or Équateur
        accentSecondary = Color(0xFF1D4ED8),     // Bleu Gabon (drapeau)
        
        // Glow
        glowColor1 = Color(0xFF38BDF8),          // Cyan sky (bottom-left)
        glowColor2 = Color(0xFF1D4ED8),          // Bleu Gabon (top-right)
        glowIntensity = 0.20f,
        
        // Markdown
        markdownColors = GabomaMarkdownColors(
            bodyText = Color(0xFFEFF6FF),
            boldText = Color(0xFF7DD3FC),        // Bleu ciel clair
            h1 = Color(0xFFC9A84C),              // Or
            h2 = Color(0xFF38BDF8),              // Cyan
            h3 = Color(0xFF93C5FD),              // Bleu pâle
            codeText = Color(0xFFFDE68A),        // Jaune miel
            codeBg = Color(0xFF0A1628),          // Dark blue-black
            codeBlockText = Color(0xFFFDE68A),
            codeBlockBg = Color(0xFF0A1628),
            linkText = Color(0xFF60A5FA),        // Bleu clair
            italicText = Color(0xFFADB8D4),
            blockquoteText = Color(0xFF38BDF8),
            blockquoteBg = Color(0xFF0F1E38),
            dividerLine = Color(0xFF1A2844)
        ),
        
        // UI Tokens
        uiTokens = GabomaUITokens(
            sidebarBg = Color(0x18060A14),
            sidebarItemActive = Color(0x220C3680),
            sidebarDivider = Color(0xFF1A2844),
            inputBarBg = Color(0x330C1220),
            inputBarBorder = Color(0x44C9A84C),
            sendButtonBg = Color(0xFFC9A84C),
            sendButtonIcon = Color(0xFF060A14),
            settingsSectionBg = Color(0xFF0C1220),
            settingsToggleOn = Color(0xFF38BDF8),
            userBubbleBg = Color(0xFF1D4ED8),    // Bleu Gabon
            aiBubbleBg = Color(0xFF0C1220),
            aiBubbleBorder = Color(0xFF1A2844)
        )
    )

    /**
     * 💜 VIOLETTE MANDRILLE — Le mandrill du Gabon + SmartandJ. Violet, rouge, jaune.
     * Scène: Mandrill face. Bleu et rouge naturel, pelage sombre, couleurs vives qui disent "je suis là".
     * Philosophy: 80% violet-black (structure) + 15% red+yellow (brand) + 5% violet-red glow (AI)
     */
    val VioletteMAndrille = GabomaThemeDefinition(
        type = GabomaThemeType.VIOLETTE_MANDRILLE,
        name = "Violette Mandrille",
        description = "Mandrill du Gabon + SmartandJ — Brand premium",
        
        // Surfaces
        backgroundColor = Color(0xFF08060F),     // Noir violacé
        surfaceColor = Color(0xFF100C1A),        // Violet nuit
        cardColor = Color(0xFF0F0B18),           // Elevated
        
        // Text
        textPrimary = Color(0xFFF5F3FF),         // Blanc lavande
        textSecondary = Color(0xFFD8B4FE),       // Violet clair
        textTertiary = Color(0xFF9333EA),        // Violet moyen
        
        // Accents
        accentPrimary = Color(0xFFE8333A),       // Rouge wow (CTA)
        accentSecondary = Color(0xFFFFD600),     // Jaune SmartandJ (highlight)
        
        // Glow
        glowColor1 = Color(0xFFA855F7),          // Violet-rose (bottom-left)
        glowColor2 = Color(0xFFE8333A),          // Rouge wow (top-right)
        glowIntensity = 0.22f,
        
        // Markdown
        markdownColors = GabomaMarkdownColors(
            bodyText = Color(0xFFF5F3FF),
            boldText = Color(0xFFFFD600),        // Jaune SmartandJ
            h1 = Color(0xFFFFD600),              // Jaune
            h2 = Color(0xFFE8333A),              // Rouge wow
            h3 = Color(0xFFD8B4FE),              // Violet clair
            codeText = Color(0xFFC4B5FD),        // Violet pâle
            codeBg = Color(0xFF0F0A1E),          // Very dark purple
            codeBlockText = Color(0xFFC4B5FD),
            codeBlockBg = Color(0xFF0F0A1E),
            linkText = Color(0xFFFDA4AF),        // Rose-red doux
            italicText = Color(0xFFD8B4FE),
            blockquoteText = Color(0xFFE8333A),
            blockquoteBg = Color(0xFF1A1030),
            dividerLine = Color(0xFF2E1F50)
        ),
        
        // UI Tokens
        uiTokens = GabomaUITokens(
            sidebarBg = Color(0x1808060F),
            sidebarItemActive = Color(0x226D28D9),
            sidebarDivider = Color(0xFF2E1F50),
            inputBarBg = Color(0x33100C1A),
            inputBarBorder = Color(0x44E8333A),
            sendButtonBg = Color(0xFFE8333A),    // Rouge (action)
            sendButtonIcon = Color(0xFFFFFFFF),
            settingsSectionBg = Color(0xFF100C1A),
            settingsToggleOn = Color(0xFFA855F7),
            userBubbleBg = Color(0xFF6D28D9),    // Violet SmartandJ
            aiBubbleBg = Color(0xFF100C1A),
            aiBubbleBorder = Color(0xFF2E1F50)
        )
    )

    /**
     * ⚪ NEO BLANC — Marbre de Libreville. Blanc pur, veines or, lumière directe.
     * Scène: Marble of Libreville. Pure white, gold veins, direct light.
     * Philosophy: 80% white (minimalist) + 15% gold ONLY (single accent) + 5% gold glow (AI subtle)
     */
    val NeoBlanc = GabomaThemeDefinition(
        type = GabomaThemeType.NEO_BLANC,
        name = "Neo Blanc",
        description = "Marbre de Libreville — Apple minimal",
        
        // Surfaces
        backgroundColor = Color(0xFFFAFAF8),     // Warm white
        surfaceColor = Color(0xFFF5F5F0),        // Slightly off-white
        cardColor = Color(0xFFF0EEE8),           // Elevated
        
        // Text
        textPrimary = Color(0xFF0A5C43),         // Tokiwa
        textSecondary = Color(0xFF56565C),       // Gray
        textTertiary = Color(0xFF86868B),        // Light gray
        
        // Accents
        accentPrimary = Color(0xFFB8922A),       // Or foncé
        accentSecondary = Color(0xFFD4A843),     // Or lumineux
        
        // Glow
        glowColor1 = Color(0xFFD4A843),          // Or lumineux (bottom-left)
        glowColor2 = Color(0xFFB8922A),          // Or foncé (top-right)
        glowIntensity = 0.08f,                   // Very subtle on white
        
        // Markdown
        markdownColors = GabomaMarkdownColors(
            bodyText = Color(0xFF0A5C43),
            boldText = Color(0xFF053828),        // Vert très foncé pour le gras
            h1 = Color(0xFFB8922A),              // Or
            h2 = Color(0xFF56565C),              // Gray
            h3 = Color(0xFF86868B),              // Lighter gray
            codeText = Color(0xFF1C1C1E),        // Dark for contrast
            codeBg = Color(0xFFF0EEE8),
            codeBlockText = Color(0xFF1C1C1E),
            codeBlockBg = Color(0xFFF0EEE8),
            linkText = Color(0xFF007AFF),        // Bleu iOS
            italicText = Color(0xFF555555),
            blockquoteText = Color(0xFF555555),
            blockquoteBg = Color(0xFFF5F5F0),
            dividerLine = Color(0xFFE5E5E0)
        ),
        
        // UI Tokens
        uiTokens = GabomaUITokens(
            sidebarBg = Color(0xFFFFFFFF),
            sidebarItemActive = Color(0xFFF5F5F0),
            sidebarDivider = Color(0xFFE5E5E0),
            inputBarBg = Color(0xFFF5F5F0),
            inputBarBorder = Color(0xFFCCCCC8),
            sendButtonBg = Color(0xFFB8922A),    // Or foncé
            sendButtonIcon = Color(0xFFFFFFFF),
            settingsSectionBg = Color(0xFFFFFFFF),
            settingsToggleOn = Color(0xFFB8922A),
            userBubbleBg = Color(0xFFF0EEE8),
            aiBubbleBg = Color(0xFFF5F5F0),
            aiBubbleBorder = Color(0xFFE5E5E0)
        )
    )



    // ─────────────────────────────────────────────────────────────────────────────
    // THEME FACTORY
    // ─────────────────────────────────────────────────────────────────────────────

    fun getTheme(type: GabomaThemeType): GabomaThemeDefinition = when (type) {
        GabomaThemeType.BLACK_PANTHER -> BlackPanther
        GabomaThemeType.NUIT_LOPE -> NuitLope
        GabomaThemeType.AURORE_OGOUE -> AuroreOgoue
        GabomaThemeType.BLEU_NUIT -> BleuNuit
        GabomaThemeType.VIOLETTE_MANDRILLE -> VioletteMAndrille
        GabomaThemeType.NEO_BLANC -> NeoBlanc
    }

    fun getThemeByName(name: String): GabomaThemeDefinition? {
        val type = GabomaThemeType.values().find { it.displayName == name }
        return type?.let { getTheme(it) }
    }
}
