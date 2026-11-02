package com.smartandj.gabomagpt.stream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Brick 4 (Kotlin side) — Sealed class SSE event model.
 *
 * Maps 1:1 with the Python SSEEventType enum in sse_events.py.
 * Every event the backend can emit has a typed Kotlin representation.
 */
sealed class GabomaStreamEvent {
    abstract val timestamp: Double

    // ─── Lifecycle ───────────────────────────────────────────────

    data class StreamStart(
        val threadId: String,
        val mode: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class StreamEnd(
        val threadId: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class Heartbeat(
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    // ─── Message Streaming ───────────────────────────────────────

    data class MessageChunk(
        val content: String,
        val chunkIndex: Int = 0,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class MessageComplete(
        val content: String,
        val messageId: String? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    // ─── Tool Execution ──────────────────────────────────────────

    data class ToolStart(
        val toolName: String,
        val toolCallId: String,
        val argsPreview: Map<String, String>? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class ToolProgress(
        val toolCallId: String,
        val output: String,
        val progress: Float? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class ToolEnd(
        val toolName: String,
        val toolCallId: String,
        val result: String? = null,
        val error: String? = null,
        val success: Boolean = true,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    // ─── Artifacts ───────────────────────────────────────────────

    data class ArtifactCreate(
        val artifactId: String,
        val filename: String,
        val contentType: String,
        val preview: String? = null,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class ArtifactUpdate(
        val artifactId: String,
        val content: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    // ─── Agent State ─────────────────────────────────────────────

    data class Thinking(
        val content: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class ModeChange(
        val from: String,
        val to: String,
        val reason: String = "",
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class RouteDecision(
        val route: String,
        val confidence: Float,
        val source: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

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
    ) : GabomaStreamEvent()

    // ─── Gabonese Verifier ───────────────────────────────────────

    data class Verification(
        val verified: Boolean,
        val language: String?,
        val languageDisplay: String?,
        val confidence: Float,
        val tag: String?,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    // ─── Agent Control ───────────────────────────────────────────

    data class TakeoverRequest(
        val userId: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    data class TakeoverAck(
        val accepted: Boolean,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    // ─── Errors ──────────────────────────────────────────────────

    data class Error(
        val message: String,
        val code: String = "unknown",
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()

    // ─── Unknown (forward compatibility) ─────────────────────────

    data class Unknown(
        val eventType: String,
        val rawData: String,
        override val timestamp: Double = System.currentTimeMillis() / 1000.0
    ) : GabomaStreamEvent()
}
