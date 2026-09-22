package com.smartandj.gabomagpt.presentation.chat.tier

import androidx.compose.ui.graphics.Color

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  ÑKYEL AI — TIER SYSTEM
 *  5 tiers alignés PWA ↔ Android (source de vérité unique)
 * ═══════════════════════════════════════════════════════════════════════════════
 */

enum class NkyelTier(
    val displayName: String,
    val description: String,
    val badgeLabel: String,
    val accentColor: Color,
    val isAvailable: Boolean
) {
    CHUI(
        displayName = "Ñkyel Chui",
        description = "Mode rapide & instantané",
        badgeLabel = "CHUI",
        accentColor = Color(0xFFC5A059),
        isAvailable = true
    ),
    TAI(
        displayName = "Ñkyel Tai",
        description = "Raisonnement profond & multimodal",
        badgeLabel = "TAI",
        accentColor = Color(0xFF94A3B8),
        isAvailable = true
    ),
    RECHERCHE(
        displayName = "Recherche Web",
        description = "Recherche & Deep Research Tavily",
        badgeLabel = "RECHERCHE",
        accentColor = Color(0xFF19C37D),
        isAvailable = true
    ),
    ONYX(
        displayName = "OnyxGris",
        description = "Agent AI autonome & livrables",
        badgeLabel = "ONYXGRIS",
        accentColor = Color(0xFF9275FF),
        isAvailable = true
    ),
    BLUE_PANTHER(
        displayName = "Blue Panther",
        description = "Mode Créateur Illimité",
        badgeLabel = "PANTHER",
        accentColor = Color(0xFFFF6E69),
        isAvailable = true
    );

    companion object {
        val default: NkyelTier = CHUI
        val all: List<NkyelTier> = entries.toList()
    }
}

typealias GabomaTier = NkyelTier
