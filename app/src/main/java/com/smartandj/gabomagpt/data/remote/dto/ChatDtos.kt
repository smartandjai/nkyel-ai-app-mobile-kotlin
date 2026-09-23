package com.smartandj.gabomagpt.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.smartandj.gabomagpt.domain.model.ArtifactItem
import com.smartandj.gabomagpt.domain.model.TodoItemInfo

@Serializable
data class ChatRequestDto(
    @SerialName("session_id")
    val sessionId: String? = null,

    @SerialName("message")
    val message: String,

    @SerialName("model")
    val model: String
)

@Serializable
data class ChatResponseDto(
    @SerialName("session_id")
    val sessionId: String,

    @SerialName("model")
    val model: String,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("answer")
    val answer: String,

    @SerialName("token_estimate")
    val tokenEstimate: Int
)

sealed interface ChatStreamEvent {
    data class Session(val sessionId: String) : ChatStreamEvent
    data class Model(val displayName: String) : ChatStreamEvent
    data class Token(val value: String) : ChatStreamEvent
    data class Sources(val sources: List<TavilySearchResult>) : ChatStreamEvent
    data object ThinkingStart : ChatStreamEvent
    data object ThinkingDone : ChatStreamEvent
    data class Reasoning(val text: String) : ChatStreamEvent
    data class ToolActivity(val name: String, val status: String, val result: String? = null, val isRunning: Boolean = true) : ChatStreamEvent
    data class A2UIRender(val componentType: String, val title: String, val rawJson: String? = null) : ChatStreamEvent
    data class A2ADelegation(val delegationId: String, val parentAgent: String, val targetAgent: String, val taskScope: String) : ChatStreamEvent
    data class MCPServer(val serverId: String, val status: String, val toolCount: Int) : ChatStreamEvent
    data class Todos(val items: List<TodoItemInfo>) : ChatStreamEvent
    data class Verification(val language: String?, val confidence: Float, val verified: Boolean) : ChatStreamEvent
    data class Artifact(val artifact: ArtifactItem) : ChatStreamEvent
    data object Done : ChatStreamEvent
    data class Error(val message: String) : ChatStreamEvent
}
