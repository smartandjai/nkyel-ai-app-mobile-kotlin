package com.smartandj.gabomagpt.domain.model

enum class ChatRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ToolActivityInfo(
    val name: String,
    val status: String = "running",
    val result: String? = null,
    val isRunning: Boolean = true
)

data class A2UICardData(
    val componentType: String,
    val title: String,
    val rawJson: String? = null,
    val isConnected: Boolean = false
)

data class A2ADelegationInfo(
    val delegationId: String,
    val parentAgent: String,
    val targetAgent: String,
    val taskScope: String
)

data class MCPStatusInfo(
    val serverId: String,
    val status: String,
    val toolCount: Int
)

data class TodoItemInfo(
    val id: String,
    val text: String,
    val done: Boolean = false,
    val inProgress: Boolean = false
)

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val content: String,
    val modelDisplayName: String? = null,
    val isStreaming: Boolean = false,
    val isThinking: Boolean = false,
    val reasoningText: String = "",
    val sources: List<SourceRef> = emptyList(),
    val artifact: ArtifactItem? = null,
    val toolActivity: ToolActivityInfo? = null,
    val a2uiCard: A2UICardData? = null,
    val a2aDelegations: List<A2ADelegationInfo> = emptyList(),
    val mcpStatus: MCPStatusInfo? = null,
    val todos: List<TodoItemInfo> = emptyList(),
    val createdAtMillis: Long = System.currentTimeMillis()
)

