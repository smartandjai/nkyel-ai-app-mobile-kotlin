package com.smartandj.gabomagpt.data.repository

import com.smartandj.gabomagpt.data.remote.NkyelNetworkConfig
import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.data.remote.dto.TavilySearchResult
import com.smartandj.gabomagpt.domain.model.ArtifactItem
import com.smartandj.gabomagpt.domain.model.ArtifactType
import com.smartandj.gabomagpt.domain.model.NkyelChatModel
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
        model: NkyelChatModel,
        sessionId: String?,
        isLoxoActive: Boolean
    ): Flow<ChatStreamEvent> = callbackFlow {
        val convId = sessionId ?: "conv_nkyel_${System.currentTimeMillis()}"
        trySend(ChatStreamEvent.Session(convId))
        trySend(ChatStreamEvent.Model(model.displayName))

        if (model == NkyelChatModel.WANDANA || model.isDeepResearch) {
            trySend(ChatStreamEvent.ThinkingStart)
        }

        // Build Ñkyel NkyelRunRequest payload
        val featuresObj = JSONObject().apply {
            put("deepResearch", model.isDeepResearch || isLoxoActive)
            put("executiveArtifacts", true)
            put("requiresDeerflow", model.requiresDeerflow)
        }

        val jsonBody = JSONObject().apply {
            put("message", message)
            put("user_id", "clerk_user_nkyel")
            put("conversation_id", convId)
            put("engine", model.backendEngine)
            put("features", featuresObj)
            // Legacy compatibility fields
            put("model", model.apiValue)
            put("stream", true)
        }

        val requestBuilder = Request.Builder()
            .url(NkyelNetworkConfig.AGENT_STREAM_URL)
            .header("Content-Type", "application/json")
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")

        // Retrieve Clerk token if available
        try {
            val clerkToken = com.smartandj.gabomagpt.data.remote.ClerkTokenProvider.getValidSessionToken()
            if (!clerkToken.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $clerkToken")
            }
        } catch (_: Exception) {
            // Unauthenticated or mock mode
        }

        val request = requestBuilder
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val sourcesAccumulator = mutableListOf<TavilySearchResult>()

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                val trimmed = data.trim()
                if (trimmed == "[DONE]") {
                    trySend(ChatStreamEvent.Done)
                    eventSource.cancel()
                    close()
                    return
                }

                try {
                    val json = JSONObject(trimmed)
                    val eventType = json.optString("event_type", json.optString("type", type ?: "message"))
                    val agUiType = json.optString("ag_ui_type", "")
                    val dataObj = json.optJSONObject("data")

                    when {
                        // 1. Text message chunk (Fast Chat / Agent synthesis)
                        eventType == "token" || agUiType == "TEXT_MESSAGE_CHUNK" -> {
                            val content = json.optString("content", dataObj?.optString("content", ""))
                            if (content.isNotEmpty()) {
                                trySend(ChatStreamEvent.Token(content))
                            }
                        }

                        // 2. Reasoning / Thinking
                        eventType == "reasoning" || agUiType == "REASONING_CHUNK" -> {
                            trySend(ChatStreamEvent.ThinkingStart)
                        }

                        // 3. Tool execution
                        eventType == "tool.started" || agUiType == "TOOL_CALL_START" -> {
                            trySend(ChatStreamEvent.ThinkingStart)
                        }

                        eventType == "tool.completed" || agUiType == "TOOL_CALL_RESULT" -> {
                            trySend(ChatStreamEvent.ThinkingDone)
                        }

                        // 4. Source discovered (Tavily / Loxo Radar)
                        eventType == "source_found" || (agUiType == "STATE_DELTA" && (json.has("source") || json.has("sources"))) -> {
                            val srcObj = json.optJSONObject("source") ?: dataObj?.optJSONObject("source")
                            if (srcObj != null) {
                                val s = TavilySearchResult(
                                    title = srcObj.optString("title", "Source Vérifiée"),
                                    url = srcObj.optString("url", ""),
                                    content = srcObj.optString("snippet", srcObj.optString("content", "")),
                                    score = srcObj.optDouble("confidence", 1.0)
                                )
                                sourcesAccumulator.add(s)
                                trySend(ChatStreamEvent.Sources(sourcesAccumulator.toList()))
                            }
                        }

                        // 5. Artifact created (Native DOCX / PPTX / PDF)
                        eventType == "artifact_created" || json.has("artifact") -> {
                            val artJson = json.optJSONObject("artifact")
                                ?: json.optJSONObject("payload")
                                ?: dataObj?.optJSONObject("artifact")

                            if (artJson != null) {
                                val artId = artJson.optString("id", artJson.optString("artifact_id", "art_${System.currentTimeMillis()}"))
                                val artTitle = artJson.optString("title", "Livrable Ñkyel AI")
                                val rawType = artJson.optString("type", artJson.optString("artifact_type", "document")).uppercase()
                                val storageUrl = artJson.optString("storage_url", artJson.optString("url", ""))
                                val sizeBytes = artJson.optLong("size_bytes", 0L)

                                val parsedType = when {
                                    rawType.contains("PDF") -> ArtifactType.PDF
                                    rawType.contains("DOCX") || rawType.contains("WORD") -> ArtifactType.DOCX
                                    rawType.contains("PPTX") || rawType.contains("PRESENTATION") -> ArtifactType.PPTX
                                    rawType.contains("XLSX") || rawType.contains("EXCEL") -> ArtifactType.XLSX
                                    rawType.contains("CODE") -> ArtifactType.CODE
                                    rawType.contains("HTML") -> ArtifactType.HTML
                                    else -> ArtifactType.MARKDOWN
                                }

                                val artifact = ArtifactItem(
                                    id = artId,
                                    title = artTitle,
                                    type = parsedType,
                                    content = artJson.optString("content", "Livrable disponible au téléchargement."),
                                    filePath = storageUrl,
                                    storageUrl = storageUrl,
                                    sizeBytes = if (sizeBytes > 0) sizeBytes else null,
                                    footer = "Généré par Ñkyel AI"
                                )
                                trySend(ChatStreamEvent.Artifact(artifact))
                            }
                        }

                        // 6. Complete consolidated message
                        eventType == "messages-tuple" || agUiType == "TEXT_MESSAGE_CONTENT" -> {
                            trySend(ChatStreamEvent.ThinkingDone)
                            val content = json.optString("content", dataObj?.optString("content", ""))
                            if (content.isNotEmpty()) {
                                // If no tokens were streamed, pass content
                                trySend(ChatStreamEvent.Token(content))
                            }
                        }

                        // 7. Run finished
                        eventType == "run_completed" || agUiType == "RUN_FINISHED" -> {
                            trySend(ChatStreamEvent.ThinkingDone)
                            trySend(ChatStreamEvent.Done)
                            eventSource.cancel()
                            close()
                        }

                        // 8. Error
                        eventType == "error" || agUiType == "RUN_ERROR" -> {
                            val err = json.optString("message", dataObj?.optString("message", "Erreur Ñkyel AI"))
                            trySend(ChatStreamEvent.Error(err))
                            eventSource.cancel()
                            close()
                        }

                        // 9. Fallback OpenAI format (choices[0].delta.content)
                        json.has("choices") -> {
                            val choices = json.optJSONArray("choices")
                            if (choices != null && choices.length() > 0) {
                                val delta = choices.getJSONObject(0).optJSONObject("delta")
                                val content = delta?.optString("content", "")
                                if (!content.isNullOrEmpty()) {
                                    trySend(ChatStreamEvent.Token(content))
                                }
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignore non-json lines or heartbeats
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                val errorMsg = t?.message ?: "Erreur de connexion Ñkyel AI (${response?.code ?: "inconnu"})"
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
