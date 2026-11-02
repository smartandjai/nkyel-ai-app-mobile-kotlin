package com.smartandj.gabomagpt.presentation.chat.tier

import androidx.compose.ui.graphics.Color

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  GABOMA AI — TIER SYSTEM (Vecteurs de Force)
 *  5 tiers alignés Web ↔ Android (source de vérité unique)
 * ═══════════════════════════════════════════════════════════════════════════════
 */

enum class GabomaTier(
    val displayName: String,
    val description: String,
    val badgeLabel: String,
    val accentColor: Color,
    val isAvailable: Boolean
) {
    AURATA(
        displayName = "Aurata",
        description = "Mode fondamental",
        badgeLabel = "AURATA",
        accentColor = Color(0xFFC5A059),
        isAvailable = true
    ),
    NYEL(
        displayName = "Ñyel",
        description = "Mode avancé",
        badgeLabel = "ÑYEL",
        accentColor = Color(0xFF94A3B8),
        isAvailable = false
    ),
    WANDANA(
        displayName = "Wandana",
        description = "Recherche & Deep Recherche",
        badgeLabel = "WANDANA",
        accentColor = Color(0xFFA855F7),
        isAvailable = false
    ),
    ONYX(
        displayName = "OnyxGris",
        description = "Agent AI autonome",
        badgeLabel = "ONYXGRIS",
        accentColor = Color(0xFFE2E8F0),
        isAvailable = false
    ),
    BLACK_PANTHER(
        displayName = "Black Panther",
        description = "Super Agent multi-agent GabomaOrchestrator",
        badgeLabel = "BLACK PANTHER",
        accentColor = Color(0xFFC5A059),
        isAvailable = false
    );

    companion object {
        val default: GabomaTier = AURATA
        val all: List<GabomaTier> = entries.toList()
    }
}
