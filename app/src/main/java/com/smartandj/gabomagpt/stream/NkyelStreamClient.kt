package com.smartandj.gabomagpt.stream

import com.smartandj.gabomagpt.data.remote.NkyelNetworkConfig
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NkyelStreamClient — OkHttp SSE client for the Ñkyel AI backend.
 *
 * Features:
 *   - Typed event parsing (JSON → NkyelStreamEvent sealed class)
 *   - Auto-reconnection with Last-Event-ID
 *   - Heartbeat monitoring
 *   - Kotlin Flow emission for Compose consumption
 */
@Singleton
class NkyelStreamClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val DEFAULT_BASE_URL = NkyelNetworkConfig.BASE_URL
        private const val SSE_ENDPOINT = "/api/v1/nkyel"
        private const val RECONNECT_DELAY_MS = 3000L
        private const val MAX_RECONNECT_ATTEMPTS = 5
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private var lastEventId: String? = null
    private var currentEventSource: EventSource? = null

    /**
     * Connect to the SSE stream and emit typed events as a Kotlin Flow.
     */
    fun connect(
        threadId: String,
        message: String,
        authToken: String,
        baseUrl: String = DEFAULT_BASE_URL,
    ): Flow<NkyelStreamEvent> = callbackFlow {
        var reconnectAttempts = 0

        fun createRequest(): Request {
            val url = if (baseUrl == DEFAULT_BASE_URL) NkyelNetworkConfig.AGENT_STREAM_URL else "$baseUrl$SSE_ENDPOINT"
            val bodyJson = """{"message":${json.encodeToString(kotlinx.serialization.serializer(), message)},"user_id":"clerk_user","conversation_id":"$threadId","engine":"NATIVE","features":{"deepResearch":false,"executiveArtifacts":true}}"""
            val requestBody = bodyJson.toRequestBody("application/json".toMediaType())

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .apply {
                    if (authToken.isNotBlank()) {
                        addHeader("Authorization", "Bearer $authToken")
                    }
                }
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
                if (event is NkyelStreamEvent.StreamEnd) {
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
                    trySend(
                        NkyelStreamEvent.Error(
                            message = "Connection lost, reconnecting... (attempt $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS)",
                            code = "reconnecting"
                        )
                    )
                } else {
                    trySend(
                        NkyelStreamEvent.Error(
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
     * Parse a raw SSE event into a typed NkyelStreamEvent.
     */
    private fun parseEvent(eventType: String, data: String): NkyelStreamEvent {
        return try {
            val jsonObj = json.parseToJsonElement(data).jsonObject
            val dataObj = try { jsonObj["data"]?.jsonObject } catch (_: Exception) { null }
            val resolvedType = jsonObj.str("event_type")
                ?: jsonObj.str("ag_ui_type")
                ?: jsonObj.str("type")
                ?: eventType

            when (resolvedType) {
                "stream_start" -> NkyelStreamEvent.StreamStart(
                    threadId = jsonObj.str("thread_id") ?: "",
                    mode = jsonObj.str("mode") ?: "standard"
                )
                "stream_end", "run_completed", "RUN_FINISHED" -> NkyelStreamEvent.RunCompleted(
                    runId = jsonObj.str("run_id") ?: jsonObj.str("thread_id") ?: "",
                    status = jsonObj.str("status") ?: "completed",
                    latencyMs = jsonObj.int("latency_ms")?.toLong()
                )
                "heartbeat" -> NkyelStreamEvent.Heartbeat()

                "message_chunk", "token", "TEXT_MESSAGE_CHUNK" -> NkyelStreamEvent.MessageChunk(
                    content = jsonObj.str("content") ?: dataObj?.str("content") ?: "",
                    chunkIndex = jsonObj.int("chunk_index") ?: 0
                )
                "message_complete", "messages-tuple", "TEXT_MESSAGE_CONTENT" -> NkyelStreamEvent.MessageComplete(
                    content = jsonObj.str("content") ?: dataObj?.str("content") ?: "",
                    messageId = jsonObj.str("message_id")
                )

                "tool_start", "tool.started", "TOOL_CALL_START" -> NkyelStreamEvent.ToolStart(
                    toolName = jsonObj.str("tool_name") ?: jsonObj.str("tool") ?: "",
                    toolCallId = jsonObj.str("tool_call_id") ?: jsonObj.str("run_id") ?: ""
                )
                "tool_progress" -> NkyelStreamEvent.ToolProgress(
                    toolCallId = jsonObj.str("tool_call_id") ?: "",
                    output = jsonObj.str("output") ?: "",
                    progress = jsonObj.float("progress")
                )
                "tool_end", "tool.completed", "TOOL_CALL_RESULT" -> NkyelStreamEvent.ToolEnd(
                    toolName = jsonObj.str("tool_name") ?: jsonObj.str("tool") ?: "",
                    toolCallId = jsonObj.str("tool_call_id") ?: jsonObj.str("run_id") ?: "",
                    result = jsonObj.str("result"),
                    error = jsonObj.str("error"),
                    success = jsonObj.str("success")?.toBoolean() ?: true
                )

                "artifact_create", "artifact_created" -> {
                    val artObj = try { jsonObj["artifact"]?.jsonObject } catch (_: Exception) { null }
                    NkyelStreamEvent.ArtifactCreate(
                        artifactId = artObj?.str("id") ?: artObj?.str("artifact_id") ?: jsonObj.str("artifact_id") ?: "",
                        filename = artObj?.str("filename") ?: jsonObj.str("filename") ?: "livrable.pdf",
                        contentType = artObj?.str("type") ?: jsonObj.str("content_type") ?: "document",
                        preview = artObj?.str("content") ?: jsonObj.str("preview")
                    )
                }

                "thinking", "reasoning", "REASONING_CHUNK" -> NkyelStreamEvent.Thinking(
                    content = jsonObj.str("content") ?: dataObj?.str("content") ?: ""
                )

                "source_found" -> {
                    val srcObj = try { jsonObj["source"]?.jsonObject } catch (_: Exception) { null }
                    NkyelStreamEvent.SourceDiscovered(
                        id = srcObj?.str("id") ?: srcObj?.str("source_id") ?: jsonObj.str("source_id") ?: "",
                        title = srcObj?.str("title") ?: jsonObj.str("title") ?: "Source Vérifiée",
                        url = srcObj?.str("url") ?: jsonObj.str("url") ?: "",
                        domain = srcObj?.str("domain") ?: jsonObj.str("domain") ?: "",
                        snippet = srcObj?.str("snippet") ?: jsonObj.str("snippet"),
                        favicon = srcObj?.str("favicon") ?: jsonObj.str("favicon"),
                        qualityLabel = srcObj?.str("quality_label") ?: jsonObj.str("quality_label")
                    )
                }

                "agent_step", "STEP_STARTED" -> NkyelStreamEvent.AgentStep(
                    taskId = jsonObj.str("task_id") ?: jsonObj.str("run_id") ?: "",
                    stepName = jsonObj.str("step") ?: jsonObj.str("label") ?: "Exécution de la directive",
                    status = jsonObj.str("status") ?: "running"
                )

                "mode_change" -> NkyelStreamEvent.ModeChange(
                    from = jsonObj.str("from") ?: "",
                    to = jsonObj.str("to") ?: "",
                    reason = jsonObj.str("reason") ?: ""
                )
                "route_decision" -> NkyelStreamEvent.RouteDecision(
                    route = jsonObj.str("route") ?: "",
                    confidence = jsonObj.float("confidence") ?: 0f,
                    source = jsonObj.str("source") ?: ""
                )

                "todo_update" -> NkyelStreamEvent.TodoUpdate(
                    todos = parseTodos(jsonObj)
                )

                "verification" -> NkyelStreamEvent.Verification(
                    verified = jsonObj.str("verified")?.toBoolean() ?: false,
                    language = jsonObj.str("language"),
                    languageDisplay = jsonObj.str("language_display"),
                    confidence = jsonObj.float("confidence") ?: 0f,
                    tag = jsonObj.str("tag")
                )

                "error", "RUN_ERROR" -> NkyelStreamEvent.Error(
                    message = jsonObj.str("message") ?: dataObj?.str("message") ?: "Erreur Ñkyel AI",
                    code = jsonObj.str("code") ?: "unknown"
                )

                else -> NkyelStreamEvent.Unknown(
                    eventType = resolvedType,
                    rawData = data
                )
            }
        } catch (e: Exception) {
            NkyelStreamEvent.Error(
                message = "Failed to parse event: ${e.message}",
                code = "parse_error"
            )
        }
    }

    private fun parseTodos(jsonObj: JsonObject): List<NkyelStreamEvent.TodoItem> {
        return try {
            jsonObj["todos"]?.jsonArray?.map { item ->
                val obj = item.jsonObject
                NkyelStreamEvent.TodoItem(
                    id = obj.str("id") ?: "",
                    text = obj.str("text") ?: "",
                    done = obj.str("done")?.toBoolean() ?: false,
                    inProgress = obj.str("in_progress")?.toBoolean() ?: false
                )
            } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun JsonObject.str(key: String): String? =
        this[key]?.jsonPrimitive?.content

    private fun JsonObject.int(key: String): Int? =
        try { this[key]?.jsonPrimitive?.int } catch (_: Exception) { null }

    private fun JsonObject.float(key: String): Float? =
        try { this[key]?.jsonPrimitive?.float } catch (_: Exception) { null }
}

typealias GabomaStreamClient = NkyelStreamClient
