/**
 * Ñkyel AI — AG-UI SSE Stream Adapter
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Real-time agent event streaming via authenticated SSE.
 * Maps backend AG-UI events to Kotlin state.
 */
package com.smartandj.gabomagpt.data.remote

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class AgUiEvent(
    val type: String = "",
    val data: String? = null,
    val content: String? = null,
    val delta: String? = null,
    val tool_name: String? = null,
    val tool_call_id: String? = null,
    val node_id: String? = null,
    val agent_id: String? = null,
    val status: String? = null,
    val mission_id: String? = null,
    val run_id: String? = null,
    val sources: List<AgUiSource>? = null,
    val artifact: AgUiArtifact? = null,
    val error: String? = null,
)

@Serializable
data class AgUiSource(
    val url: String = "",
    val title: String? = null,
    val snippet: String? = null,
    val domain: String? = null,
)

@Serializable
data class AgUiArtifact(
    val id: String = "",
    val title: String? = null,
    val type: String? = null,
    val url: String? = null,
    val size: Long? = null,
)

@Singleton
class AgUiStreamAdapter @Inject constructor(
    private val apiClient: NkyelApiClient,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Connect to the Ñkyel SSE streaming endpoint.
     * Returns a Flow of AgUiEvent for lifecycle-aware collection.
     */
    fun streamRun(
        prompt: String,
        conversationId: String?,
        engine: String,
    ): Flow<AgUiEvent> = callbackFlow {
        val payload = buildString {
            append("{\"prompt\":\"${prompt.replace("\"", "\\\"").replace("\n", "\\n")}\"")
            append(",\"engine\":\"$engine\"")
            conversationId?.let { append(",\"conversation_id\":\"$it\"") }
            append(",\"stream\":true}")
        }

        var eventSource: EventSource? = null

        val listener = object : EventSourceListener() {
            override fun onEvent(es: EventSource, id: String?, type: String?, data: String) {
                if (data == "[DONE]") {
                    trySend(AgUiEvent(type = "done"))
                    close()
                    return
                }
                try {
                    val event = json.decodeFromString<AgUiEvent>(data)
                    trySend(event.copy(type = type ?: event.type))
                } catch (_: Exception) {
                    // Try as text delta
                    trySend(AgUiEvent(type = type ?: "text_delta", delta = data))
                }
            }

            override fun onFailure(es: EventSource, t: Throwable?, response: Response?) {
                trySend(AgUiEvent(type = "error", error = t?.message ?: "SSE connection failed"))
                close()
            }

            override fun onClosed(es: EventSource) {
                trySend(AgUiEvent(type = "done"))
                close()
            }
        }

        eventSource = apiClient.connectSSE(
            NkyelApiConfig.NKYEL_RUN,
            payload,
            listener
        )

        awaitClose { eventSource?.cancel() }
    }

    /** Stream for simple chat completions */
    fun streamChat(
        prompt: String,
        conversationId: String?,
        model: String,
    ): Flow<AgUiEvent> = callbackFlow {
        val payload = buildString {
            append("{\"messages\":[{\"role\":\"user\",\"content\":\"${prompt.replace("\"", "\\\"").replace("\n", "\\n")}\"}]")
            append(",\"model\":\"$model\"")
            conversationId?.let { append(",\"conversation_id\":\"$it\"") }
            append(",\"stream\":true}")
        }

        var eventSource: EventSource? = null

        val listener = object : EventSourceListener() {
            override fun onEvent(es: EventSource, id: String?, type: String?, data: String) {
                if (data == "[DONE]") {
                    trySend(AgUiEvent(type = "done"))
                    close()
                    return
                }
                try {
                    val parsed = json.decodeFromString<AgUiEvent>(data)
                    trySend(parsed)
                } catch (_: Exception) {
                    trySend(AgUiEvent(type = "text_delta", delta = data))
                }
            }

            override fun onFailure(es: EventSource, t: Throwable?, response: Response?) {
                trySend(AgUiEvent(type = "error", error = t?.message ?: "Stream failed"))
                close()
            }

            override fun onClosed(es: EventSource) { close() }
        }

        eventSource = apiClient.connectSSE(
            NkyelApiConfig.NKYEL_STREAM,
            payload,
            listener
        )

        awaitClose { eventSource?.cancel() }
    }
}
