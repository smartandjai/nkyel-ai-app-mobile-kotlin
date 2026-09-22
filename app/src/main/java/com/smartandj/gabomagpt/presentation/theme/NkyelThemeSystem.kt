// presentation/theme/NkyelThemeSystem.kt
package com.smartandj.gabomagpt.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  ÑKYEL AI 6-THEME SYSTEM - Apple MX + Illuminate 2026
 *  "L'app s'illumine quand l'IA parle" — African Sovereign Identity + SmartandJ
 * ═══════════════════════════════════════════════════════════════════════════════
 */

// ─────────────────────────────────────────────────────────────────────────────
// ENUM - Theme Selection (6 Sovereign scenes → premium color systems)
// ─────────────────────────────────────────────────────────────────────────────
enum class NkyelThemeType(
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
        "Mandrill souverain + SmartandJ — Brand premium"
    ),
    NEO_BLANC(
        "Néo Blanc",
        "Marbre de Libreville — Minimal souverain",
        isLight = true
    ),
    OBSIDIAN(
        "Obsidian",
        "Forêt équatoriale la nuit — OLED flagship"
    ),
    NOIR_OLED(
        "Noir OLED",
        "Forêt équatoriale la nuit — OLED flagship"
    ),
    BLANC_EMERAUDE(
        "Blanc Émeraude",
        "Fleuve Ogooué à l'aube — Thème blanc émeraude",
        isLight = true
    );

    val isDark: Boolean get() = !isLight

    companion object {
        val default: NkyelThemeType = BLACK_PANTHER
    }
}

typealias GabomaThemeType = NkyelThemeType

// ─────────────────────────────────────────────────────────────────────────────
// DATA CLASSES - Theme Configuration
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Markdown colors per theme — **gras**, H1/H2/H3, code, links, etc.
 * The "80% neutral, 15% accent, 5% glow" rule applied to text rendering.
 */
data class NkyelMarkdownColors(
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

typealias GabomaMarkdownColors = NkyelMarkdownColors

/**
 * UI Tokens per theme — sidebar, input bar, buttons, bubbles.
 * Controls glass morphism, surface depths, interactive states.
 */
data class NkyelUITokens(
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

typealias GabomaUITokens = NkyelUITokens

/**
 * Master theme definition — contains all colors + tokens for one theme.
 * Handles surface hierarchy, text hierarchy, accent colors, dual glow effects.
 * Philosophy: 80% neutral (rest) + 15% accent (identity) + 5% glow (AI animation)
 */
data class NkyelThemeDefinition(
    val type: NkyelThemeType,
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
    val glowColor1: Color,      // Primary glow (e.g., Turquoise IA)
    val glowColor2: Color,      // Secondary glow (e.g., Accent color)
    val glowIntensity: Float,   // 0.0 = off, 1.0 = full brightness
    
    // Markdown-specific colors
    val markdownColors: NkyelMarkdownColors,
    
    // UI components-specific tokens
    val uiTokens: NkyelUITokens
)

typealias GabomaThemeDefinition = NkyelThemeDefinition

// ─────────────────────────────────────────────────────────────────────────────
// THEME DEFINITIONS - 6 complete themes with exact hex codes
// ─────────────────────────────────────────────────────────────────────────────

object NkyelThemeDefinitions {

    val BlackPanther = NkyelThemeDefinition(
        type = NkyelThemeType.BLACK_PANTHER,
        name = "Black Panther",
        description = "Mode agent autonome par défaut",
        
        backgroundColor = Color(0xFF020304),
        surfaceColor = Color(0xFF0A0908),
        cardColor = Color(0xFF14130F),
        
        textPrimary = Color(0xFFEDEAE3),
        textSecondary = Color(0xFF8A8378),
        textTertiary = Color(0xFF525250),
        
        accentPrimary = Color(0xFFC5A059),
        accentSecondary = Color(0xFF1F9D6B),
        
        glowColor1 = Color(0xFFC5A059),
        glowColor2 = Color(0xFF1F9D6B),
        glowIntensity = 0.14f,
        
        markdownColors = NkyelMarkdownColors(
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
        
        uiTokens = NkyelUITokens(
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

    val NuitLope = NkyelThemeDefinition(
        type = NkyelThemeType.NUIT_LOPE,
        name = "Nuit Lopé",
        description = "Forêt équatoriale la nuit — OLED flagship",
        
        backgroundColor = Color(0xFF050507),
        surfaceColor = Color(0xFF0D0D12),
        cardColor = Color(0xFF0F0F14),
        
        textPrimary = Color(0xFFEDECE6),
        textSecondary = Color(0xFF888680),
        textTertiary = Color(0xFF4A4840),
        
        accentPrimary = Color(0xFFC9A84C),
        accentSecondary = Color(0xFF0A3D2A),
        
        glowColor1 = Color(0xFF00D4AA),
        glowColor2 = Color(0xFFC9A84C),
        glowIntensity = 0.18f,
        
        markdownColors = NkyelMarkdownColors(
            bodyText = Color(0xFFEDECE6),
            boldText = Color(0xFFC9A84C),
            h1 = Color(0xFFC9A84C),
            h2 = Color(0xFF00D4AA),
            h3 = Color(0xFF4ADE80),
            codeText = Color(0xFF00D4AA),
            codeBg = Color(0xFF0F0F18),
            codeBlockText = Color(0xFF00D4AA),
            codeBlockBg = Color(0xFF0F0F18),
            linkText = Color(0xFF4ADE80),
            italicText = Color(0xFFB8B6AE),
            blockquoteText = Color(0xFF00D4AA),
            blockquoteBg = Color(0xFF0F0F14),
            dividerLine = Color(0xFF1A1A25)
        ),
        
        uiTokens = NkyelUITokens(
            sidebarBg = Color(0x180A0A0F),
            sidebarItemActive = Color(0x22C9A84C),
            sidebarDivider = Color(0xFF1E1E28),
            inputBarBg = Color(0x330A0A0F),
            inputBarBorder = Color(0x44C9A84C),
            sendButtonBg = Color(0xFFC9A84C),
            sendButtonIcon = Color(0xFF050507),
            settingsSectionBg = Color(0xFF0F0F14),
            settingsToggleOn = Color(0xFF00D4AA),
            userBubbleBg = Color(0xFF0A3D2A),
            aiBubbleBg = Color(0xFF0F0F14),
            aiBubbleBorder = Color(0xFF1A1A25)
        )
    )

    val AuroreOgoue = NkyelThemeDefinition(
        type = NkyelThemeType.AURORE_OGOUE,
        name = "Aurore Ogooué",
        description = "Fleuve Ogooué à l'aube — Thème blanc émeraude",
        
        backgroundColor = Color(0xFFF8F8F4),
        surfaceColor = Color(0xFFFFFFFF),
        cardColor = Color(0xFFF5F8F6),
        
        textPrimary = Color(0xFF18181B),
        textSecondary = Color(0xFF6B6968),
        textTertiary = Color(0xFFB0AEA8),
        
        accentPrimary = Color(0xFF059669),
        accentSecondary = Color(0xFFA67C2E),
        
        glowColor1 = Color(0xFF00C896),
        glowColor2 = Color(0xFFA67C2E),
        glowIntensity = 0.10f,
        
        markdownColors = NkyelMarkdownColors(
            bodyText = Color(0xFF18181B),
            boldText = Color(0xFF059669),
            h1 = Color(0xFFA67C2E),
            h2 = Color(0xFF0D9488),
            h3 = Color(0xFF00C896),
            codeText = Color(0xFF064E3B),
            codeBg = Color(0xFFECFDF5),
            codeBlockText = Color(0xFF064E3B),
            codeBlockBg = Color(0xFFECFDF5),
            linkText = Color(0xFF0369A1),
            italicText = Color(0xFF6B6968),
            blockquoteText = Color(0xFF059669),
            blockquoteBg = Color(0xFFF0F7F4),
            dividerLine = Color(0xFFD4D9D6)
        ),
        
        uiTokens = NkyelUITokens(
            sidebarBg = Color(0xFFFAFAF7),
            sidebarItemActive = Color(0xFFE0F2ED),
            sidebarDivider = Color(0xFFE5E9E7),
            inputBarBg = Color(0xFFF5F5F0),
            inputBarBorder = Color(0xFFC5D9CF),
            sendButtonBg = Color(0xFF059669),
            sendButtonIcon = Color(0xFFFFFFFF),
            settingsSectionBg = Color(0xFFFAFAF7),
            settingsToggleOn = Color(0xFF00C896),
            userBubbleBg = Color(0xFFE0F2ED),
            aiBubbleBg = Color(0xFFFFFFFF),
            aiBubbleBorder = Color(0xFFE5E9E7)
        )
    )

    val BleuNuit = NkyelThemeDefinition(
        type = NkyelThemeType.BLEU_NUIT,
        name = "Bleu Nuit",
        description = "Océan Atlantique à minuit — Profondeur marine",
        
        backgroundColor = Color(0xFF060A14),
        surfaceColor = Color(0xFF0C1220),
        cardColor = Color(0xFF0A0F1A),
        
        textPrimary = Color(0xFFEFF6FF),
        textSecondary = Color(0xFFADB8D4),
        textTertiary = Color(0xFF4A5568),
        
        accentPrimary = Color(0xFFC9A84C),
        accentSecondary = Color(0xFF1D4ED8),
        
        glowColor1 = Color(0xFF38BDF8),
        glowColor2 = Color(0xFF1D4ED8),
        glowIntensity = 0.20f,
        
        markdownColors = NkyelMarkdownColors(
            bodyText = Color(0xFFEFF6FF),
            boldText = Color(0xFF7DD3FC),
            h1 = Color(0xFFC9A84C),
            h2 = Color(0xFF38BDF8),
            h3 = Color(0xFF93C5FD),
            codeText = Color(0xFFFDE68A),
            codeBg = Color(0xFF0A1628),
            codeBlockText = Color(0xFFFDE68A),
            codeBlockBg = Color(0xFF0A1628),
            linkText = Color(0xFF60A5FA),
            italicText = Color(0xFFADB8D4),
            blockquoteText = Color(0xFF38BDF8),
            blockquoteBg = Color(0xFF0F1E38),
            dividerLine = Color(0xFF1A2844)
        ),
        
        uiTokens = NkyelUITokens(
            sidebarBg = Color(0x18060A14),
            sidebarItemActive = Color(0x220C3680),
            sidebarDivider = Color(0xFF1A2844),
            inputBarBg = Color(0x330C1220),
            inputBarBorder = Color(0x44C9A84C),
            sendButtonBg = Color(0xFFC9A84C),
            sendButtonIcon = Color(0xFF060A14),
            settingsSectionBg = Color(0xFF0C1220),
            settingsToggleOn = Color(0xFF38BDF8),
            userBubbleBg = Color(0xFF1D4ED8),
            aiBubbleBg = Color(0xFF0C1220),
            aiBubbleBorder = Color(0xFF1A2844)
        )
    )

    val VioletteMAndrille = NkyelThemeDefinition(
        type = NkyelThemeType.VIOLETTE_MANDRILLE,
        name = "Violette Mandrille",
        description = "Mandrill souverain + SmartandJ — Brand premium",
        
        backgroundColor = Color(0xFF08060F),
        surfaceColor = Color(0xFF100C1A),
        cardColor = Color(0xFF0F0B18),
        
        textPrimary = Color(0xFFF5F3FF),
        textSecondary = Color(0xFFD8B4FE),
        textTertiary = Color(0xFF9333EA),
        
        accentPrimary = Color(0xFFE8333A),
        accentSecondary = Color(0xFFFFD600),
        
        glowColor1 = Color(0xFFA855F7),
        glowColor2 = Color(0xFFE8333A),
        glowIntensity = 0.22f,
        
        markdownColors = NkyelMarkdownColors(
            bodyText = Color(0xFFF5F3FF),
            boldText = Color(0xFFFFD600),
            h1 = Color(0xFFFFD600),
            h2 = Color(0xFFE8333A),
            h3 = Color(0xFFD8B4FE),
            codeText = Color(0xFFC4B5FD),
            codeBg = Color(0xFF0F0A1E),
            codeBlockText = Color(0xFFC4B5FD),
            codeBlockBg = Color(0xFF0F0A1E),
            linkText = Color(0xFFFDA4AF),
            italicText = Color(0xFFD8B4FE),
            blockquoteText = Color(0xFFE8333A),
            blockquoteBg = Color(0xFF1A1030),
            dividerLine = Color(0xFF2E1F50)
        ),
        
        uiTokens = NkyelUITokens(
            sidebarBg = Color(0x1808060F),
            sidebarItemActive = Color(0x226D28D9),
            sidebarDivider = Color(0xFF2E1F50),
            inputBarBg = Color(0x33100C1A),
            inputBarBorder = Color(0x44E8333A),
            sendButtonBg = Color(0xFFE8333A),
            sendButtonIcon = Color(0xFFFFFFFF),
            settingsSectionBg = Color(0xFF100C1A),
            settingsToggleOn = Color(0xFFA855F7),
            userBubbleBg = Color(0xFF6D28D9),
            aiBubbleBg = Color(0xFF100C1A),
            aiBubbleBorder = Color(0xFF2E1F50)
        )
    )

    val NeoBlanc = NkyelThemeDefinition(
        type = NkyelThemeType.NEO_BLANC,
        name = "Neo Blanc",
        description = "Marbre de Libreville — Minimal souverain",
        
        backgroundColor = Color(0xFFFAFAF8),
        surfaceColor = Color(0xFFF5F5F0),
        cardColor = Color(0xFFF0EEE8),
        
        textPrimary = Color(0xFF0A5C43),
        textSecondary = Color(0xFF56565C),
        textTertiary = Color(0xFF86868B),
        
        accentPrimary = Color(0xFFB8922A),
        accentSecondary = Color(0xFFD4A843),
        
        glowColor1 = Color(0xFFD4A843),
        glowColor2 = Color(0xFFB8922A),
        glowIntensity = 0.08f,
        
        markdownColors = NkyelMarkdownColors(
            bodyText = Color(0xFF0A5C43),
            boldText = Color(0xFF053828),
            h1 = Color(0xFFB8922A),
            h2 = Color(0xFF56565C),
            h3 = Color(0xFF86868B),
            codeText = Color(0xFF1C1C1E),
            codeBg = Color(0xFFF0EEE8),
            codeBlockText = Color(0xFF1C1C1E),
            codeBlockBg = Color(0xFFF0EEE8),
            linkText = Color(0xFF007AFF),
            italicText = Color(0xFF555555),
            blockquoteText = Color(0xFF555555),
            blockquoteBg = Color(0xFFF5F5F0),
            dividerLine = Color(0xFFE5E5E0)
        ),
        
        uiTokens = NkyelUITokens(
            sidebarBg = Color(0xFFFFFFFF),
            sidebarItemActive = Color(0xFFF5F5F0),
            sidebarDivider = Color(0xFFE5E5E0),
            inputBarBg = Color(0xFFF5F5F0),
            inputBarBorder = Color(0xFFCCCCC8),
            sendButtonBg = Color(0xFFB8922A),
            sendButtonIcon = Color(0xFFFFFFFF),
            settingsSectionBg = Color(0xFFFFFFFF),
            settingsToggleOn = Color(0xFFB8922A),
            userBubbleBg = Color(0xFFF0EEE8),
            aiBubbleBg = Color(0xFFF5F5F0),
            aiBubbleBorder = Color(0xFFE5E5E0)
        )
    )

    fun getTheme(type: NkyelThemeType): NkyelThemeDefinition = when (type) {
        NkyelThemeType.BLACK_PANTHER -> BlackPanther
        NkyelThemeType.NUIT_LOPE, NkyelThemeType.OBSIDIAN, NkyelThemeType.NOIR_OLED -> NuitLope
        NkyelThemeType.AURORE_OGOUE, NkyelThemeType.BLANC_EMERAUDE -> AuroreOgoue
        NkyelThemeType.BLEU_NUIT -> BleuNuit
        NkyelThemeType.VIOLETTE_MANDRILLE -> VioletteMAndrille
        NkyelThemeType.NEO_BLANC -> NeoBlanc
    }

    fun getThemeByName(name: String): NkyelThemeDefinition? {
        val type = NkyelThemeType.values().find { it.displayName == name }
        return type?.let { getTheme(it) }
    }
}

typealias GabomaThemeDefinitions = NkyelThemeDefinitions
