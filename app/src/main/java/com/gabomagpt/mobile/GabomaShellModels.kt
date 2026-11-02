package com.gabomagpt.mobile

import androidx.compose.ui.graphics.Color

val BlackPantherBg = Color(0xFF020304)
val BlackPantherText = Color(0xFF9B8BB3)
val BlackPantherPrimary = Color(0xFFC5A059)
val BlackPantherAccent = Color(0xFF3B533E)
val ObsidianBg = Color(0xFF050810)
val ObsidianText = Color(0xFF91AD70)
val NoirOledBg = Color(0xFF000000)
val NoirOledText = Color(0xFFF3F3F3)
val BleuNuitBg = Color(0xFF05081A)
val BleuNuitText = Color(0xFF708AB3)
val VertForetBg = Color(0xFF06150C)
val VertForetText = Color(0xFFA3BA99)
val BlancEmeraudeBg = Color(0xFFF8FAF9)
val BlancEmeraudeText = Color(0xFF0A5C43)
val GoldGaboma = Color(0xFFC9A84C)
val TurquoiseIA = Color(0xFF00D4AA)
val GlassSurface = Color(0x1FFFFFFF)
val HairlineLight = Color(0x24FFFFFF)
val HairlineDark = Color(0x18000000)

enum class GabomaThemePreset(
    val id: String,
    val bg: Color,
    val text: Color,
    val primary: Color,
    val accent: Color,
    val halo1: Color,
    val halo2: Color,
    val halo3: Color
) {
    BLACK_PANTHER(
        id = "black-panther",
        bg = BlackPantherBg,
        text = BlackPantherText,
        primary = BlackPantherPrimary,
        accent = BlackPantherAccent,
        halo1 = Color(0x149B8BB3),
        halo2 = Color(0x103B533E),
        halo3 = Color(0x0AC5A059)
    ),
    OBSIDIAN(
        id = "obsidian",
        bg = ObsidianBg,
        text = ObsidianText,
        primary = GoldGaboma,
        accent = Color(0xFF334B37),
        halo1 = Color(0x1091AD70),
        halo2 = Color(0x10334B37),
        halo3 = Color(0x0AC9A84C)
    ),
    NOIR_OLED(
        id = "noir-oled",
        bg = NoirOledBg,
        text = NoirOledText,
        primary = GoldGaboma,
        accent = Color(0xFF2C3138),
        halo1 = Color(0x10FFFFFF),
        halo2 = Color(0x102C3138),
        halo3 = Color(0x0AC9A84C)
    ),
    BLEU_NUIT(
        id = "bleu-nuit",
        bg = BleuNuitBg,
        text = BleuNuitText,
        primary = Color(0xFF88A3C8),
        accent = Color(0xFF233A56),
        halo1 = Color(0x12708AB3),
        halo2 = Color(0x10233A56),
        halo3 = Color(0x0A88A3C8)
    ),
    VERT_FORET(
        id = "vert-foret",
        bg = VertForetBg,
        text = VertForetText,
        primary = Color(0xFFA4C47C),
        accent = Color(0xFF24402F),
        halo1 = Color(0x12A3BA99),
        halo2 = Color(0x1024402F),
        halo3 = Color(0x0AA4C47C)
    ),
    BLANC_EMERAUDE(
        id = "blanc-emeraude",
        bg = BlancEmeraudeBg,
        text = BlancEmeraudeText,
        primary = Color(0xFF0A5C43),
        accent = Color(0xFF7EB8A0),
        halo1 = Color(0x140A5C43),
        halo2 = Color(0x107EB8A0),
        halo3 = Color(0x0A0A5C43)
    )
}

enum class WritingStyle(val label: String) {
    CLASSIQUE("Classique"),
    NET("Net"),
    EDITORIAL("Editorial"),
    DENSE("Dense")
}

enum class UserProfileKind(val label: String) {
    PRO("Profil Cadre"),
    PUBLIC("Profil Citoyen")
}

enum class ForceTier(
    val label: String,
    val accent: Color,
    val note: String,
    val freeArtifactLimit: Int = Int.MAX_VALUE
) {
    AURATA("AURATA", GoldGaboma, "Gratuit · réponses + artefacts légers", freeArtifactLimit = 1),
    SONAR("SONAR", Color(0xFF5D8CFF), "Recherche et comparaison"),
    LOXO("LOXO", Color(0xFF46C98A), "Recherche web et sources"),
    ONYX("ONYX", Color(0xFF9B8BB3), "Automata avancé"),
    BLACK_PANTHER("BLACK PANTHER", BlackPantherPrimary, "Expérience premium native"),
    NKYEL("NKYEL", Color(0xFFE7E1D5), "Souveraineté à venir")
}

enum class ArtifactKind(val label: String) {
    TEXT("Texte"),
    MARKDOWN("MD"),
    HTML("HTML"),
    CODE("Code"),
    PDF("PDF"),
    WORD("Word"),
    EXCEL("Excel"),
    POWERPOINT("PowerPoint")
}

enum class SettingsTab(val label: String) {
    APPARENCE("Apparence"),
    DIRECTIVES("Directive"),
    SOUVERAINETE("Souveraineté"),
    COMPTE("Profil"),
    POLITIQUES("Politiques")
}

data class GabomaSettings(
    val theme: GabomaThemePreset = GabomaThemePreset.BLACK_PANTHER,
    val writingStyle: WritingStyle = WritingStyle.CLASSIQUE,
    val fontScale: Float = 1.0f,
    val model: ForceTier = ForceTier.BLACK_PANTHER,
    val invokeLoxo: Boolean = true,
    val radarLoxo: Boolean = true,
    val modeOmbre: Boolean = false,
    val coffreFortSouverain: Boolean = true,
    val pactePolitiqueAccepted: Boolean = true,
    val profileKind: UserProfileKind = UserProfileKind.PRO,
    val nodeLabel: String = "NODE: LIBREVILLE-S-01"
)

data class EnergyState(
    val usedPercent: Int = 35,
    val remainingToday: Int = 65,
    val freeArtifactRemaining: Int = 1
)

data class SidebarConversation(
    val id: String,
    val title: String,
    val preview: String,
    val group: String,
    val selected: Boolean = false
)

data class ChatMessage(
    val id: String,
    val role: String,
    val content: String,
    val streaming: Boolean = false,
    val artifacts: List<ArtifactCard> = emptyList(),
    val sources: List<String> = emptyList()
)

data class ArtifactCard(
    val id: String,
    val title: String,
    val kind: ArtifactKind,
    val content: String,
    val premiumHint: String? = null
)

data class ShellUiState(
    val settings: GabomaSettings = GabomaSettings(),
    val energy: EnergyState = EnergyState(),
    val conversations: List<SidebarConversation> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val isSearching: Boolean = false,
    val errorMessage: String? = null
)

object GabomaPolicyText {
    const val FOOTER_CHAT = "GaboMaGPT peut faire des erreurs. Vérifiez les informations importantes."
    const val FOOTER_PAGE = "Propulsé par SmartAndJ AI Tech • Conçu par ANDJ"
    const val INPUT_FOOTER = "PROPULSÉ PAR SMARTANDJ AI TECH"
    const val AGENT_HEADER = "AUTOMATA V2.0 • PROPELLED BY SMARTANDJ AI TECH"

    val PRIVACY = """
GabomaGPT traite les directives, messages, fichiers joints et réglages locaux pour fournir la conversation, les artefacts et les fonctions Automata. Les contenus sensibles peuvent être protégés par le Coffre-Fort Souverain, qui représente le mode de chiffrement renforcé côté expérience mobile. Le Radar LOXO et Invoquer LOXO utilisent les sources externes ou documentaires activées par l'utilisateur pour enrichir les réponses et peuvent produire des citations, résumés et artefacts dérivés.

Les conversations peuvent être réutilisées pour la continuité d'expérience, l'historique En piste et l'amélioration du confort produit, selon le profil choisi et les paramètres actifs. Le Mode Ombre réduit la persistance visible dans l'interface et privilégie un comportement discret. Les pièces jointes relevées via Relever un indice sont affichées en prévisualisation avant l'envoi et peuvent être retirées à tout moment avant Lancer.

Les réglages de style d'écriture, taille des caractères, thème et force active sont configurables dans L'Antre. Le Pacte Politique signale l'intention de conformité légale, documentaire et réglementaire, y compris pour les usages professionnels. Le Pacte de Chasse regroupe l'historique de facturation, les traces d'abonnement Alimenter la Meute et les événements liés au profil de compte.
    """.trimIndent()

    val TERMS = """
En utilisant GaboMaGPT, l'utilisateur accepte que les réponses puissent inclure du contenu généré, des approximations et des erreurs de synthèse. Les artefacts produits — texte, MD, HTML, code, PDF, Word, Excel ou PowerPoint — doivent être relus avant tout usage juridique, médical, financier, administratif ou commercial critique. Les limites du plan libre AURATA s'appliquent à la génération d'artefacts avancés et peuvent restreindre le volume ou le type de fichiers rendus.

Le moteur GABOMAGPT AUTOMATA V2.0 est une couche d'orchestration produit. Certaines capacités peuvent faire appel à des services de génération, de recherche ou d'analyse selon la force active. Les compétences agentiques plus profondes, notamment sur ONYX, sont destinées à s'étendre avec de nouveaux skills, automatismes et intégrations.
    """.trimIndent()
}
