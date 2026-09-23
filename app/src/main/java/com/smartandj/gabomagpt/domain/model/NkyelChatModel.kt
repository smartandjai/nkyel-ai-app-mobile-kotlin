package com.smartandj.gabomagpt.domain.model

enum class NkyelChatModel(
    val apiValue: String,
    val displayName: String,
    val shortName: String,
    val description: String,
    val tier: String,
    val subtitle: String,
    val accent: Long,
    val accent2: Long
) {
    CHUI(
        apiValue = "nkyel_chui",
        displayName = "Ñkyel Chui",
        shortName = "Chui",
        description = "Réponses rapides & efficaces",
        tier = "FAST",
        subtitle = "Groq Fast Chat",
        accent = 0xFFC9A84C,
        accent2 = 0xFFE2C56A
    ),
    TAI(
        apiValue = "nkyel_tai",
        displayName = "Ñkyel Tai",
        shortName = "Tai",
        description = "Raisonnement profond & multimodal",
        tier = "AGENT",
        subtitle = "Qwen 3 32B Agent",
        accent = 0xFF4A8DFF,
        accent2 = 0xFF00D4AA
    ),
    RADI(
        apiValue = "nkyel_radi",
        displayName = "Ñkyel Radi",
        shortName = "Radi",
        description = "Langues gabonaises & tâches légères",
        tier = "FAST",
        subtitle = "Langues Gabon & Afrique",
        accent = 0xFF19C37D,
        accent2 = 0xFF00D4AA
    ),
    RECHERCHE_WEB(
        apiValue = "recherche_web",
        displayName = "Recherche Web",
        shortName = "Recherche",
        description = "Recherche web ancrée & faits actuels",
        tier = "WEB",
        subtitle = "Tavily Grounded Synthesis",
        accent = 0xFF00D4AA,
        accent2 = 0xFF19C37D
    ),
    NKYEL_SEER(
        apiValue = "vision_rendu",
        displayName = "Vision & Rendus",
        shortName = "Vision",
        description = "Analyse images, docs et vidéos",
        tier = "VISION",
        subtitle = "Multimodalité & Artefacts",
        accent = 0xFFF0E8D8,
        accent2 = 0xFFC9A84C
    );

    val requiresDeerflow: Boolean
        get() = false

    val isDeepResearch: Boolean
        get() = this == RECHERCHE_WEB

    val backendEngine: String?
        get() = when (this) {
            TAI -> "NATIVE"
            else -> null
        }

    companion object {
        // Backward compatibility aliases
        val AURATA get() = CHUI
        val NYEL get() = TAI
        val WANDANA get() = RECHERCHE_WEB
    }
}

typealias GabomaChatModel = NkyelChatModel
