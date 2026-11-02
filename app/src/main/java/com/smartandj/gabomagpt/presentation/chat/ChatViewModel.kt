package com.smartandj.gabomagpt.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.domain.model.ChatMessage
import com.smartandj.gabomagpt.domain.model.ChatRole
import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import com.smartandj.gabomagpt.domain.usecase.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val selectedModel: GabomaChatModel = GabomaChatModel.AURATA,
    val sessionId: String? = null,
    val isStreaming: Boolean = false,
    val isLoxoActive: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = messages.isEmpty()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendChatMessageUseCase: SendChatMessageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var streamJob: Job? = null

    fun onInputChange(value: String) {
        _uiState.value = _uiState.value.copy(
            input = value,
            errorMessage = null
        )
    }

    fun clearMessages() {
        streamJob?.cancel()
        streamJob = null
        _uiState.value = ChatUiState()
    }

    fun toggleLoxo() {
        _uiState.value = _uiState.value.copy(
            isLoxoActive = !_uiState.value.isLoxoActive
        )
    }

    fun selectModel(model: GabomaChatModel) {
        if (_uiState.value.isStreaming) return

        _uiState.value = _uiState.value.copy(
            selectedModel = model,
            errorMessage = null
        )
    }

    fun sendMessage() {
        val current = _uiState.value
        val message = current.input.trim()

        if (message.isBlank() || current.isStreaming) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatRole.USER,
            content = message
        )

        val assistantMessageId = UUID.randomUUID().toString()

        val assistantMessage = ChatMessage(
            id = assistantMessageId,
            role = ChatRole.ASSISTANT,
            content = "",
            modelDisplayName = current.selectedModel.displayName,
            isStreaming = true
        )

        _uiState.value = current.copy(
            messages = current.messages + userMessage + assistantMessage,
            input = "",
            isStreaming = true,
            errorMessage = null
        )

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            sendChatMessageUseCase(
                message = message,
                model = current.selectedModel,
                sessionId = current.sessionId,
                isLoxoActive = current.isLoxoActive
            ).collect { event ->
                handleStreamEvent(
                    event = event,
                    assistantMessageId = assistantMessageId
                )
            }
        }
    }

    fun cancelStreaming() {
        streamJob?.cancel()
        streamJob = null

        _uiState.value = _uiState.value.copy(
            isStreaming = false,
            messages = _uiState.value.messages.map { message ->
                if (message.isStreaming) {
                    message.copy(isStreaming = false)
                } else {
                    message
                }
            }
        )
    }

    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages
            .lastOrNull { it.role == ChatRole.USER }
            ?: return

        _uiState.value = _uiState.value.copy(
            input = lastUserMessage.content,
            errorMessage = null
        )

        sendMessage()
    }

    fun useSuggestion(text: String) {
        _uiState.value = _uiState.value.copy(
            input = text,
            errorMessage = null
        )
    }

    private fun handleStreamEvent(
        event: ChatStreamEvent,
        assistantMessageId: String
    ) {
        when (event) {
            is ChatStreamEvent.Session -> {
                _uiState.value = _uiState.value.copy(
                    sessionId = event.sessionId
                )
            }

            is ChatStreamEvent.Model -> {
                updateAssistantMessage(assistantMessageId) { message ->
                    message.copy(modelDisplayName = event.displayName)
                }
            }

            is ChatStreamEvent.Token -> {
                updateAssistantMessage(assistantMessageId) { message ->
                    message.copy(
                        content = message.content + event.value,
                        isStreaming = true
                    )
                }
            }

            ChatStreamEvent.Done -> {
                streamJob = null

                _uiState.value = _uiState.value.copy(
                    isStreaming = false,
                    messages = _uiState.value.messages.map { message ->
                        if (message.id == assistantMessageId) {
                            message.copy(isStreaming = false)
                        } else {
                            message
                        }
                    }
                )
            }

            is ChatStreamEvent.ThinkingStart -> {
                updateAssistantMessage(assistantMessageId) { message ->
                    message.copy(isThinking = true)
                }
            }

            is ChatStreamEvent.ThinkingDone -> {
                updateAssistantMessage(assistantMessageId) { message ->
                    message.copy(isThinking = false)
                }
            }

            is ChatStreamEvent.Sources -> {
                updateAssistantMessage(assistantMessageId) { message ->
                    message.copy(
                        sources = event.sources.map {
                            val host = try { java.net.URI(it.url).host ?: it.url } catch(e: Exception) { it.url }
                            com.smartandj.gabomagpt.domain.model.SourceRef(
                                title = it.title,
                                host = host,
                                snippet = it.content,
                                url = it.url, confidence = (it.score.toString().toFloatOrNull() ?: 1.0f)
                            )
                        }
                    )
                }
            }

            is ChatStreamEvent.Artifact -> {
                updateAssistantMessage(assistantMessageId) { message ->
                    message.copy(
                        artifact = event.artifact
                    )
                }
            }

            is ChatStreamEvent.Error -> {
                streamJob = null

                _uiState.value = _uiState.value.copy(
                    isStreaming = false,
                    errorMessage = event.message,
                    messages = _uiState.value.messages.map { message ->
                        if (message.id == assistantMessageId) {
                            message.copy(
                                isStreaming = false,
                                content = if (message.content.isBlank()) {
                                    "Connexion interrompue. Tu peux reessayer."
                                } else {
                                    message.content
                                }
                            )
                        } else {
                            message
                        }
                    }
                )
            }
        }
    }

    private fun updateAssistantMessage(
        assistantMessageId: String,
        transform: (ChatMessage) -> ChatMessage
    ) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages.map { message ->
                if (message.id == assistantMessageId) {
                    transform(message)
                } else {
                    message
                }
            }
        )
    }
}
