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
    AURATA(
        apiValue = "nkyel_chui",
        displayName = "Ñkyel Chui",
        shortName = "Chui",
        description = "Réponses rapides & efficaces",
        tier = "FAST",
        subtitle = "Groq GPT-OSS Fast Chat",
        accent = 0xFFC9A84C,
        accent2 = 0xFFE2C56A
    ),
    NYEL(
        apiValue = "nkyel_tai",
        displayName = "Ñkyel Tai",
        shortName = "Tai",
        description = "Raisonnement profond & multimodal",
        tier = "AGENT",
        subtitle = "Qwen 3 32B Agent",
        accent = 0xFF4A8DFF,
        accent2 = 0xFF00D4AA
    ),
    WANDANA(
        apiValue = "recherche_web",
        displayName = "Recherche Web",
        shortName = "Recherche",
        description = "Recherche web ancrée & faits actuels",
        tier = "WEB",
        subtitle = "Tavily Grounded Synthesis",
        accent = 0xFF19C37D,
        accent2 = 0xFF00D4AA
    ),
    ONYX_GRIS(
        apiValue = "onyxgris",
        displayName = "OnyxGris",
        shortName = "OnyxGris",
        description = "Mission agentique lourde & livrables",
        tier = "MAX",
        subtitle = "DeerFlow 2.0 / Qwen",
        accent = 0xFF9275FF,
        accent2 = 0xFFC9A84C
    ),
    BLACK_PANTHER(
        apiValue = "blue_panther",
        displayName = "Blue Panther",
        shortName = "Panther",
        description = "Mode Créateur Illimité",
        tier = "MAX",
        subtitle = "Créateur Souverain",
        accent = 0xFFFF6E69,
        accent2 = 0xFF00D4AA
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
        get() = this == ONYX_GRIS || this == BLACK_PANTHER

    val isDeepResearch: Boolean
        get() = this == WANDANA

    val backendEngine: String?
        get() = when (this) {
            ONYX_GRIS, BLACK_PANTHER -> "DEERFLOW"
            NYEL -> "NATIVE"
            else -> null
        }
}

typealias GabomaChatModel = NkyelChatModel
