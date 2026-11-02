package com.gabomagpt.mobile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartandj.gabomagpt.data.remote.TavilyService
import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.domain.usecase.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GabomaShellViewModel @Inject constructor(
    private val store: GabomaSettingsStore,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val tavilyService: TavilyService
) : ViewModel() {

    private val conversations = MutableStateFlow(
        listOf(
            SidebarConversation("1", "Audit produit Gabon", "Comparatif premium 2026", "Aujourd'hui", true),
            SidebarConversation("2", "Strategie LOXO", "Sources web et citations", "Aujourd'hui"),
            SidebarConversation("3", "Mode ONYX agent", "Skills Automata V2.0", "Cette semaine"),
            SidebarConversation("4", "Palette Black Panther", "Aurora et halos", "Cette semaine")
        )
    )

    private val messages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "m1",
                role = "assistant",
                content = "La forêt s'éveille... GABOMAGPT AUTOMATA V2.0 est prêt.",
                artifacts = listOf(
                    ArtifactCard(
                        "a0",
                        "Brief de lancement.md",
                        ArtifactKind.MARKDOWN,
                        "# Brief\n\nBlack Panther est prêt."
                    )
                )
            )
        )
    )

    private val isSearching = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private var sessionId: String? = null
    private var streamJob: Job? = null

    val settings = store.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GabomaSettings()
    )

    val energy = store.energyFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EnergyState()
    )

    val ui = combine(
        combine(settings, energy, conversations) { s, e, c -> Triple(s, e, c) },
        combine(messages, isSearching, errorMessage) { m, search, err -> Triple(m, search, err) }
    ) { (settings, energy, conversations), (messages, searching, error) ->
        ShellUiState(
            settings = settings,
            energy = energy,
            conversations = conversations,
            messages = messages,
            isSearching = searching,
            errorMessage = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShellUiState())

    fun clearError() {
        errorMessage.value = null
    }

    fun updateTheme(theme: GabomaThemePreset) = mutateSettings { it.copy(theme = theme) }
    fun updateStyle(style: WritingStyle) = mutateSettings { it.copy(writingStyle = style) }
    fun updateScale(scale: Float) = mutateSettings { it.copy(fontScale = scale) }
    fun updateModel(model: ForceTier) = mutateSettings { it.copy(model = model) }
    fun toggleInvokeLoxo() = mutateSettings { it.copy(invokeLoxo = !it.invokeLoxo) }
    fun toggleRadarLoxo() = mutateSettings { it.copy(radarLoxo = !it.radarLoxo) }
    fun toggleModeOmbre() = mutateSettings { it.copy(modeOmbre = !it.modeOmbre) }
    fun toggleCoffre() = mutateSettings { it.copy(coffreFortSouverain = !it.coffreFortSouverain) }
    fun togglePacte() = mutateSettings { it.copy(pactePolitiqueAccepted = !it.pactePolitiqueAccepted) }
    fun updateProfile(profile: UserProfileKind) = mutateSettings { it.copy(profileKind = profile) }

    fun selectConversation(id: String) {
        conversations.value = conversations.value.map { it.copy(selected = it.id == id) }
    }

    fun sendDirective(text: String) {
        val cleaned = text.trim()
        if (cleaned.isBlank()) return

        val currentSettings = settings.value
        val userId = "u${System.currentTimeMillis()}"
        val assistantId = "a${System.currentTimeMillis()}"

        messages.value = messages.value + ChatMessage(id = userId, role = "user", content = cleaned)
        messages.value = messages.value + ChatMessage(
            id = assistantId,
            role = "assistant",
            content = "",
            streaming = true
        )

        updateConversationPreview(cleaned)

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            errorMessage.value = null
            val sources = fetchSourcesIfNeeded(cleaned, currentSettings)
            if (sources.isNotEmpty()) {
                patchAssistantMessage(assistantId) { it.copy(sources = sources) }
            }

            val isLoxoActive = currentSettings.invokeLoxo ||
                currentSettings.radarLoxo ||
                currentSettings.model == ForceTier.LOXO

            val chatModel = currentSettings.model.toChatModel()
            val energySnapshot = energy.value
            var fullContent = StringBuilder()

            sendChatMessageUseCase(
                message = cleaned,
                model = chatModel,
                sessionId = sessionId,
                isLoxoActive = isLoxoActive
            ).collect { event ->
                when (event) {
                    is ChatStreamEvent.Session -> sessionId = event.sessionId
                    is ChatStreamEvent.Model -> Unit
                    is ChatStreamEvent.Token -> {
                        fullContent.append(event.value)
                        patchAssistantMessage(assistantId) {
                            it.copy(content = fullContent.toString(), streaming = true)
                        }
                    }
                    is ChatStreamEvent.ThinkingStart, ChatStreamEvent.ThinkingDone, is ChatStreamEvent.Artifact, is ChatStreamEvent.Sources -> {
                        // Not handled yet
                    }
                    ChatStreamEvent.Done -> {
                        val artifacts = buildArtifactsFor(
                            text = cleaned,
                            response = fullContent.toString(),
                            model = currentSettings.model,
                            energy = energySnapshot
                        )
                        patchAssistantMessage(assistantId) {
                            it.copy(streaming = false, artifacts = artifacts)
                        }
                        if (currentSettings.model == ForceTier.AURATA && artifacts.isNotEmpty()) {
                            store.useFreeArtifact()
                        }
                        store.bumpEnergyUsage()
                    }
                    is ChatStreamEvent.Error -> {
                        errorMessage.value = event.message
                        patchAssistantMessage(assistantId) {
                            val fallback = if (fullContent.isBlank()) {
                                "Connexion interrompue. ${event.message}"
                            } else {
                                fullContent.toString()
                            }
                            it.copy(content = fallback, streaming = false)
                        }
                    }
                }
            }
        }
    }

    fun regenerateLast() {
        val lastUser = messages.value.lastOrNull { it.role == "user" } ?: return
        sendDirective(lastUser.content)
    }

    fun cancelStreaming() {
        streamJob?.cancel()
        streamJob = null
        messages.value = messages.value.map { message ->
            if (message.streaming) message.copy(streaming = false) else message
        }
    }

    private suspend fun fetchSourcesIfNeeded(text: String, settings: GabomaSettings): List<String> {
        if (!settings.radarLoxo && !settings.invokeLoxo) return emptyList()
        isSearching.value = true
        return try {
            val response = tavilyService.searchWeb(text, maxResults = 4)
            response.results.mapNotNull { result ->
                result.url.takeIf { it.isNotBlank() }
            }.ifEmpty {
                listOfNotNull(response.answer.takeIf { it.isNotBlank() }?.let { "radar-loxo.recherche" })
            }
        } catch (_: Exception) {
            emptyList()
        } finally {
            isSearching.value = false
        }
    }

    private fun updateConversationPreview(text: String) {
        val title = text.take(42).let { if (text.length > 42) "$it…" else it }
        conversations.value = conversations.value.map { conversation ->
            if (conversation.selected) {
                conversation.copy(title = title, preview = text.take(80))
            } else {
                conversation
            }
        }
    }

    private fun patchAssistantMessage(
        assistantId: String,
        transform: (ChatMessage) -> ChatMessage
    ) {
        messages.value = messages.value.map { message ->
            if (message.id == assistantId) transform(message) else message
        }
    }

    private fun buildArtifactsFor(
        text: String,
        response: String,
        model: ForceTier,
        energy: EnergyState
    ): List<ArtifactCard> {
        if (model == ForceTier.AURATA && energy.freeArtifactRemaining <= 0) {
            return listOf(
                ArtifactCard(
                    id = "locked",
                    title = "Le Rendu premium",
                    kind = ArtifactKind.TEXT,
                    content = "Le plan AURATA a atteint sa limite quotidienne d'artefacts avancés.",
                    premiumHint = "Activer ton Génie PREMIUM ou Alimenter la Meute"
                )
            )
        }

        val body = response.ifBlank { text }
        return listOf(
            ArtifactCard(
                id = UUID.randomUUID().toString(),
                title = "Synthese.md",
                kind = ArtifactKind.MARKDOWN,
                content = "# Synthèse\n\n$body\n\n- Force: ${model.label}\n- Origine: GABOMAGPT AUTOMATA V2.0"
            ),
            ArtifactCard(
                id = UUID.randomUUID().toString(),
                title = "Apercu.html",
                kind = ArtifactKind.HTML,
                content = "<html><body style='background:#020304;color:#C5A059;font-family:sans-serif'><h1>Le Rendu</h1><p>$body</p></body></html>"
            ),
            ArtifactCard(
                id = UUID.randomUUID().toString(),
                title = "Snippets.kt",
                kind = ArtifactKind.CODE,
                content = "// Généré par Automata V2.0\nfun automate() = \"\"\"$body\"\"\".trimIndent()"
            )
        )
    }

    private fun mutateSettings(block: (GabomaSettings) -> GabomaSettings) {
        viewModelScope.launch { store.updateSettings(block) }
    }
}
