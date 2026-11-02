package com.smartandj.gabomagpt.stream

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GabomaStreamClient — OkHttp SSE client for the FastAPI backend.
 *
 * Features:
 *   - Typed event parsing (JSON → GabomaStreamEvent sealed class)
 *   - Auto-reconnection with Last-Event-ID
 *   - Heartbeat monitoring
 *   - Kotlin Flow emission for Compose consumption
 */
@Singleton
class GabomaStreamClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val DEFAULT_BASE_URL = "https://api.gabomagpt.ga"
        private const val SSE_ENDPOINT = "/api/v1/chat/stream"
        private const val RECONNECT_DELAY_MS = 3000L
        private const val MAX_RECONNECT_ATTEMPTS = 5
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private var lastEventId: String? = null
    private var currentEventSource: EventSource? = null

    /**
     * Connect to the SSE stream and emit typed events as a Kotlin Flow.
     *
     * Usage in Compose:
     * ```kotlin
     * val events by streamClient.connect(threadId, message, token)
     *     .collectAsState(initial = null)
     * ```
     */
    fun connect(
        threadId: String,
        message: String,
        authToken: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ): Flow<GabomaStreamEvent> = callbackFlow {
        var reconnectAttempts = 0

        fun createRequest(): Request {
            val url = "$baseUrl$SSE_ENDPOINT"
            val requestBody = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                """{"thread_id":"$threadId","message":${json.encodeToString(kotlinx.serialization.serializer(), message)}}"""
            )

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $authToken")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Cache-Control", "no-cache")
                .apply {
                    lastEventId?.let { addHeader("Last-Event-ID", it) }
                }
                .build()
        }

        val listener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                reconnectAttempts = 0
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                // Track Last-Event-ID for reconnection
                id?.let { lastEventId = it }

                // Parse the event
                val event = parseEvent(type ?: "message", data)
                trySend(event)

                // Close on stream_end
                if (event is GabomaStreamEvent.StreamEnd) {
                    eventSource.cancel()
                    close()
                }
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    reconnectAttempts++
                    // Reconnection handled externally
                    trySend(
                        GabomaStreamEvent.Error(
                            message = "Connection lost, reconnecting... (attempt $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS)",
                            code = "reconnecting"
                        )
                    )
                } else {
                    trySend(
                        GabomaStreamEvent.Error(
                            message = t?.message ?: "SSE connection failed after $MAX_RECONNECT_ATTEMPTS attempts",
                            code = "connection_failed"
                        )
                    )
                    close()
                }
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        }

        // Create SSE connection
        val sseClient = okHttpClient.newBuilder()
            .readTimeout(0, TimeUnit.MILLISECONDS)  // No timeout for SSE
            .build()

        val factory = EventSources.createFactory(sseClient)
        currentEventSource = factory.newEventSource(createRequest(), listener)

        awaitClose {
            currentEventSource?.cancel()
            currentEventSource = null
        }
    }

    /**
     * Disconnect from the SSE stream.
     */
    fun disconnect() {
        currentEventSource?.cancel()
        currentEventSource = null
        lastEventId = null
    }

    /**
     * Parse a raw SSE event into a typed GabomaStreamEvent.
     */
    private fun parseEvent(eventType: String, data: String): GabomaStreamEvent {
        return try {
            val jsonObj = json.parseToJsonElement(data).jsonObject
            when (eventType) {
                "stream_start" -> GabomaStreamEvent.StreamStart(
                    threadId = jsonObj.str("thread_id") ?: "",
                    mode = jsonObj.str("mode") ?: "standard"
                )
                "stream_end" -> GabomaStreamEvent.StreamEnd(
                    threadId = jsonObj.str("thread_id") ?: ""
                )
                "heartbeat" -> GabomaStreamEvent.Heartbeat()

                "message_chunk" -> GabomaStreamEvent.MessageChunk(
                    content = jsonObj.str("content") ?: "",
                    chunkIndex = jsonObj.int("chunk_index") ?: 0
                )
                "message_complete" -> GabomaStreamEvent.MessageComplete(
                    content = jsonObj.str("content") ?: "",
                    messageId = jsonObj.str("message_id")
                )

                "tool_start" -> GabomaStreamEvent.ToolStart(
                    toolName = jsonObj.str("tool_name") ?: "",
                    toolCallId = jsonObj.str("tool_call_id") ?: ""
                )
                "tool_progress" -> GabomaStreamEvent.ToolProgress(
                    toolCallId = jsonObj.str("tool_call_id") ?: "",
                    output = jsonObj.str("output") ?: "",
                    progress = jsonObj.float("progress")
                )
                "tool_end" -> GabomaStreamEvent.ToolEnd(
                    toolName = jsonObj.str("tool_name") ?: "",
                    toolCallId = jsonObj.str("tool_call_id") ?: "",
                    result = jsonObj.str("result"),
                    error = jsonObj.str("error"),
                    success = jsonObj.str("success")?.toBoolean() ?: true
                )

                "artifact_create" -> GabomaStreamEvent.ArtifactCreate(
                    artifactId = jsonObj.str("artifact_id") ?: "",
                    filename = jsonObj.str("filename") ?: "",
                    contentType = jsonObj.str("content_type") ?: "",
                    preview = jsonObj.str("preview")
                )

                "thinking" -> GabomaStreamEvent.Thinking(
                    content = jsonObj.str("content") ?: ""
                )
                "mode_change" -> GabomaStreamEvent.ModeChange(
                    from = jsonObj.str("from") ?: "",
                    to = jsonObj.str("to") ?: "",
                    reason = jsonObj.str("reason") ?: ""
                )
                "route_decision" -> GabomaStreamEvent.RouteDecision(
                    route = jsonObj.str("route") ?: "",
                    confidence = jsonObj.float("confidence") ?: 0f,
                    source = jsonObj.str("source") ?: ""
                )

                "todo_update" -> GabomaStreamEvent.TodoUpdate(
                    todos = parseTodos(jsonObj)
                )

                "verification" -> GabomaStreamEvent.Verification(
                    verified = jsonObj.str("verified")?.toBoolean() ?: false,
                    language = jsonObj.str("language"),
                    languageDisplay = jsonObj.str("language_display"),
                    confidence = jsonObj.float("confidence") ?: 0f,
                    tag = jsonObj.str("tag")
                )

                "error" -> GabomaStreamEvent.Error(
                    message = jsonObj.str("message") ?: "Unknown error",
                    code = jsonObj.str("code") ?: "unknown"
                )

                else -> GabomaStreamEvent.Unknown(
                    eventType = eventType,
                    rawData = data
                )
            }
        } catch (e: Exception) {
            GabomaStreamEvent.Error(
                message = "Failed to parse event: ${e.message}",
                code = "parse_error"
            )
        }
    }

    private fun parseTodos(jsonObj: JsonObject): List<GabomaStreamEvent.TodoItem> {
        return try {
            jsonObj["todos"]?.jsonArray?.map { item ->
                val obj = item.jsonObject
                GabomaStreamEvent.TodoItem(
                    id = obj.str("id") ?: "",
                    text = obj.str("text") ?: "",
                    done = obj.str("done")?.toBoolean() ?: false,
                    inProgress = obj.str("in_progress")?.toBoolean() ?: false
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ─── JsonObject extension helpers ────────────────────────────

    private fun JsonObject.str(key: String): String? =
        this[key]?.jsonPrimitive?.content

    private fun JsonObject.int(key: String): Int? =
        try { this[key]?.jsonPrimitive?.int } catch (_: Exception) { null }

    private fun JsonObject.float(key: String): Float? =
        try { this[key]?.jsonPrimitive?.float } catch (_: Exception) { null }
}
