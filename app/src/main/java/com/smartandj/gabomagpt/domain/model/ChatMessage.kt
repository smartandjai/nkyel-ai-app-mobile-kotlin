package com.smartandj.gabomagpt.domain.model

enum class ChatRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val content: String,
    val modelDisplayName: String? = null,
    val isStreaming: Boolean = false,
    val isThinking: Boolean = false,
    val sources: List<SourceRef> = emptyList(),
    val artifact: ArtifactItem? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)
