package com.smartandj.gabomagpt.stream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Ñkyel AI SSE Streaming event model.
 *
 * Maps 1:1 with the backend SSE events emitted by /api/v1/nkyel.
 * Every event emitted by the agent runtime has a typed Kotlin representation.
 */
sealed class NkyelStreamEvent {
    abstract val timestamp: Double

    // ─── Lifecycle ───────────────────────────────────────────────

    data class StreamStart(
        val threadId: String,
        val mode: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class StreamEnd(
        val threadId: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class Heartbeat(
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Message Streaming ───────────────────────────────────────

    data class MessageChunk(
        val content: String,
        val chunkIndex: Int = 0,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class MessageComplete(
        val content: String,
        val messageId: String? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Tool Execution ──────────────────────────────────────────

    data class ToolStart(
        val toolName: String,
        val toolCallId: String,
        val argsPreview: Map<String, String>? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class ToolProgress(
        val toolCallId: String,
        val output: String,
        val progress: Float? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class ToolEnd(
        val toolName: String,
        val toolCallId: String,
        val result: String? = null,
        val error: String? = null,
        val success: Boolean = true,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Artifacts ───────────────────────────────────────────────

    data class ArtifactCreate(
        val artifactId: String,
        val filename: String,
        val contentType: String,
        val preview: String? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class ArtifactUpdate(
        val artifactId: String,
        val content: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Agent State ─────────────────────────────────────────────

    data class Thinking(
        val content: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class ModeChange(
        val from: String,
        val to: String,
        val reason: String = "",
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class RouteDecision(
        val route: String,
        val confidence: Float,
        val source: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Task Management (Manus-style checklist) ─────────────────

    @Serializable
    data class TodoItem(
        val id: String,
        val text: String,
        val done: Boolean = false,
        val inProgress: Boolean = false
    )

    data class TodoUpdate(
        val todos: List<TodoItem>,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Sovereign Verifier ──────────────────────────────────────

    data class Verification(
        val verified: Boolean,
        val language: String?,
        val languageDisplay: String?,
        val confidence: Float,
        val tag: String?,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Agent Control ───────────────────────────────────────────

    data class TakeoverRequest(
        val userId: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class TakeoverAck(
        val accepted: Boolean,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Verification & Sources ─────────────────────────────────

    data class SourceDiscovered(
        val id: String,
        val title: String,
        val url: String,
        val domain: String = "",
        val snippet: String? = null,
        val favicon: String? = null,
        val qualityLabel: String? = null,
        val index: Int = 0,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class AgentStep(
        val taskId: String,
        val stepName: String,
        val status: String = "running",
        val payload: Map<String, String>? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    data class RunCompleted(
        val runId: String,
        val status: String = "completed",
        val latencyMs: Long? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Errors ──────────────────────────────────────────────────

    data class Error(
        val message: String,
        val code: String = "unknown",
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()

    // ─── Unknown (forward compatibility) ─────────────────────────

    data class Unknown(
        val eventType: String,
        val rawData: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : NkyelStreamEvent()
}

typealias GabomaStreamEvent = NkyelStreamEvent
