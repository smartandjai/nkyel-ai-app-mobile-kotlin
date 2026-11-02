package com.smartandj.gabomagpt.data.repository

import com.smartandj.gabomagpt.data.remote.GabomaNetworkConfig
import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import com.smartandj.gabomagpt.domain.repository.ChatRepository
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONArray
import org.json.JSONObject

class ChatRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
) : ChatRepository {

    override fun streamMessage(
        message: String,
        model: GabomaChatModel,
        sessionId: String?,
        isLoxoActive: Boolean
    ): Flow<ChatStreamEvent> = callbackFlow {
        trySend(ChatStreamEvent.Session(sessionId ?: "session-gaboma"))
        trySend(ChatStreamEvent.Model(model.displayName))

        if (model == GabomaChatModel.WANDANA) {
            trySend(ChatStreamEvent.ThinkingStart)
        }

        val jsonBody = JSONObject().apply {
            put("model", model.apiValue)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", message)
                })
            })
            put("stream", true)
            sessionId?.let { put("session_id", it) }
        }

        val request = Request.Builder()
            .url("${GabomaNetworkConfig.BASE_URL}/chat/completions")
            // .header("Authorization", "Bearer \${BuildConfig.GABOMA_API_KEY}") // If auth is needed later
            .header("Content-Type", "application/json")
            .header("Accept", "text/event-stream")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                if (data == "[DONE]") {
                    trySend(ChatStreamEvent.Done)
                    eventSource.cancel()
                    close()
                    return
                }

                try {
                    val json = JSONObject(data)

                    // Parsing sources if they come from the backend
                    if (json.has("sources")) {
                        trySend(ChatStreamEvent.ThinkingDone)
                        val sourcesArray = json.optJSONArray("sources")
                        val searchResults = mutableListOf<com.smartandj.gabomagpt.data.remote.dto.TavilySearchResult>()
                        for (i in 0 until (sourcesArray?.length() ?: 0)) {
                            val s = sourcesArray?.optJSONObject(i)
                            if (s != null) {
                                searchResults.add(
                                    com.smartandj.gabomagpt.data.remote.dto.TavilySearchResult(
                                        title = s.optString("title", ""),
                                        url = s.optString("url", ""),
                                        content = s.optString("content", ""),
                                        score = s.optDouble("score", 0.0)
                                    )
                                )
                            }
                        }
                        if (searchResults.isNotEmpty()) {
                            trySend(ChatStreamEvent.Sources(searchResults))
                        }
                    }

                    val choices = json.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val delta = choices.getJSONObject(0).optJSONObject("delta")
                        val content = delta?.optString("content", "")
                        if (!content.isNullOrEmpty()) {
                            trySend(ChatStreamEvent.Token(content))
                        }
                    }
                } catch (e: Exception) {
                    // Parsing error
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                val errorMsg = t?.message ?: "Erreur de connexion Gaboma API"
                trySend(ChatStreamEvent.Error(errorMsg))
                eventSource.cancel()
                close()
            }
        }

        val eventSource = EventSources.createFactory(okHttpClient).newEventSource(request, listener)

        awaitClose {
            eventSource.cancel()
        }
    }
}
