/**
 * Ñkyel AI — Chat ViewModel (Production)
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Connects to real FastAPI backend via NkyelConversationsRepository + AG-UI streaming.
 */
package com.smartandj.gabomagpt.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartandj.gabomagpt.data.remote.AgUiStreamAdapter
import com.smartandj.gabomagpt.data.remote.NkyelApiConfig
import com.smartandj.gabomagpt.data.repository.NkyelConversationsRepository
import com.smartandj.gabomagpt.data.repository.NkyelMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiMessage(
    val id: String,
    val role: String,
    val content: String,
    val model: String? = null,
    val isStreaming: Boolean = false,
    val sources: List<ChatSource> = emptyList(),
    val toolActivity: String? = null,
)

data class ChatSource(
    val url: String,
    val title: String? = null,
    val snippet: String? = null,
)

@HiltViewModel
class NkyelChatViewModel @Inject constructor(
    private val repo: NkyelConversationsRepository,
    private val streamAdapter: AgUiStreamAdapter,
) : ViewModel() {

    val conversations = repo.conversations
    val currentConversationId = repo.currentConversationId

    private val _messages = MutableStateFlow<List<ChatUiMessage>>(emptyList())
    val messages: StateFlow<List<ChatUiMessage>> = _messages

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedModel = MutableStateFlow(1) // default Ñkyel = Qwen
    val selectedModel: StateFlow<Int> = _selectedModel

    private var streamJob: Job? = null

data class NkyelModelItem(
    val name: String,
    val tag: String,
    val desc: String,
    val modelId: String,
)

    val modelOptions = listOf(
        NkyelModelItem("Ñkyel Max", "Pro", "Raisonnement lourd & synthèse approfondie", NkyelApiConfig.MODEL_NKYEL_MAX),
        NkyelModelItem("Ñkyel", "Pro", "Modèle principal agentique & exécution", NkyelApiConfig.MODEL_NKYEL),
        NkyelModelItem("Ñkyel Chui", "Agent", "Exécution d'outils et sous-agents", NkyelApiConfig.MODEL_NKYEL_CHUI),
        NkyelModelItem("Ñkyel Radi", "Vision", "Compréhension visuelle & multimodal", NkyelApiConfig.MODEL_NKYEL_RADI),
        NkyelModelItem("Ñkyel Light", "", "Réponses rapides & légères", NkyelApiConfig.MODEL_NKYEL_LIGHT),
        NkyelModelItem("Ñkyel Flex", "", "Adaptation dynamique de contexte", NkyelApiConfig.MODEL_NKYEL_FLEX),
    )

    val currentModel: NkyelModelItem get() = modelOptions.getOrElse(_selectedModel.value) { modelOptions[1] }
    val selectedModelId: String get() = currentModel.modelId

    init {
        viewModelScope.launch { repo.fetchConversations() }
    }

    fun setModel(idx: Int) { _selectedModel.value = idx }
    fun clearError() { _error.value = null }

    fun selectConversation(id: String) {
        viewModelScope.launch {
            repo.fetchMessages(id)
            _messages.value = repo.messages.value.map { it.toUi() }
        }
    }

    fun newChat() {
        repo.selectConversation(null)
        repo.clearMessages()
        _messages.value = emptyList()
        _error.value = null
    }

    fun sendMessage(prompt: String) {
        if (prompt.isBlank() || _isStreaming.value) return
        viewModelScope.launch {
            // 1. Create conversation if needed
            var convId = currentConversationId.value
            if (convId == null) {
                val conv = repo.createConversation(
                    title = prompt.take(50),
                    modelProfile = selectedModelId,
                )
                convId = conv?.id
            }

            // 2. Add user message to UI immediately
            val userMsg = ChatUiMessage(
                id = "user_${System.currentTimeMillis()}",
                role = "user",
                content = prompt,
            )
            _messages.value = _messages.value + userMsg

            // 3. Persist user message to Neon
            convId?.let { repo.persistMessage(it, "user", prompt) }

            // 4. Start streaming response
            _isStreaming.value = true
            val assistantId = "assistant_${System.currentTimeMillis()}"
            val buffer = StringBuilder()
            val sources = mutableListOf<ChatSource>()

            _messages.value = _messages.value + ChatUiMessage(
                id = assistantId,
                role = "assistant",
                content = "",
                model = selectedModelId,
                isStreaming = true,
            )

            streamJob = viewModelScope.launch {
                streamAdapter.streamChat(prompt, convId, selectedModelId)
                    .catch { e ->
                        _error.value = e.message ?: "Erreur de streaming"
                        updateLastMessage(assistantId, buffer.toString(), false, sources)
                    }
                    .onCompletion {
                        _isStreaming.value = false
                        updateLastMessage(assistantId, buffer.toString(), false, sources)
                        // Persist assistant response
                        if (convId != null && buffer.isNotEmpty()) {
                            repo.persistMessage(convId, "assistant", buffer.toString(), selectedModelId)
                        }
                    }
                    .collect { event ->
                        when (event.type) {
                            "text_delta", "content_delta", "message.delta" -> {
                                val delta = event.delta ?: event.content ?: ""
                                buffer.append(delta)
                                updateLastMessage(assistantId, buffer.toString(), true, sources)
                            }
                            "sources", "source.added" -> {
                                event.sources?.forEach { s ->
                                    sources.add(ChatSource(s.url, s.title, s.snippet))
                                }
                                updateLastMessage(assistantId, buffer.toString(), true, sources)
                            }
                            "tool.started", "tool.requested" -> {
                                updateLastMessage(assistantId, buffer.toString(), true, sources, event.tool_name)
                            }
                            "error" -> {
                                _error.value = event.error
                            }
                            "done" -> {
                                // handled by onCompletion
                            }
                        }
                    }
            }
        }
    }

    fun stopStreaming() {
        streamJob?.cancel()
        _isStreaming.value = false
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repo.deleteConversation(id)
            if (currentConversationId.value == id) {
                newChat()
            }
        }
    }

    fun renameConversation(id: String, title: String) {
        viewModelScope.launch { repo.updateTitle(id, title) }
    }

    private fun updateLastMessage(
        id: String,
        content: String,
        streaming: Boolean,
        sources: List<ChatSource>,
        toolActivity: String? = null,
    ) {
        _messages.value = _messages.value.map {
            if (it.id == id) it.copy(
                content = content,
                isStreaming = streaming,
                sources = sources,
                toolActivity = toolActivity,
            ) else it
        }
    }

    private fun NkyelMessage.toUi() = ChatUiMessage(
        id = id,
        role = role,
        content = content,
        model = model_id,
    )
}
