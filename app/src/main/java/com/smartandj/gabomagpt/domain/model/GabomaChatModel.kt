package com.smartandj.gabomagpt.domain.model

enum class GabomaChatModel(
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
        apiValue = "aurata",
        displayName = "Aurata",
        shortName = "Aurata",
        description = "Mode fondamental",
        tier = "FREE",
        subtitle = "Réponse rapide",
        accent = 0xFFC9A84C,
        accent2 = 0xFFE2C56A
    ),
    NYEL(
        apiValue = "nyel",
        displayName = "Ñkyel",
        shortName = "Ñkyel",
        description = "Mode avancé",
        tier = "PRO",
        subtitle = "Qualité élevée",
        accent = 0xFF4A8DFF,
        accent2 = 0xFF00D4AA
    ),
    WANDANA(
        apiValue = "wandana",
        displayName = "Wandana",
        shortName = "Wandana",
        description = "Recherche & Deep Recherche",
        tier = "RAG",
        subtitle = "Deep Research",
        accent = 0xFF19C37D,
        accent2 = 0xFF00D4AA
    ),
    ONYX_GRIS(
        apiValue = "onyxgris",
        displayName = "OnyxGris",
        shortName = "OnyxGris",
        description = "Agent IA autonome",
        tier = "MAX",
        subtitle = "Agent autonome",
        accent = 0xFF9275FF,
        accent2 = 0xFFC9A84C
    ),
    BLACK_PANTHER(
        apiValue = "black_panther",
        displayName = "Black Panther",
        shortName = "Panther",
        description = "Super Agent multi-agents",
        tier = "MAX",
        subtitle = "Super Agent multi-agents",
        accent = 0xFFFF6E69,
        accent2 = 0xFF00D4AA
    ),
    GABOMA_SEER(
        apiValue = "gabomaseer",
        displayName = "GabomaSeer",
        shortName = "GabomaSeer",
        description = "Vision",
        tier = "VISION",
        subtitle = "Analyse images, docs et vidéos",
        accent = 0xFFF0E8D8,
        accent2 = 0xFFC9A84C
    )
}
