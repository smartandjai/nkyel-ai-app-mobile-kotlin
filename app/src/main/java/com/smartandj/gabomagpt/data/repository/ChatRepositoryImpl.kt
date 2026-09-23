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

        val userId = try {
            com.clerk.api.Clerk.user?.id ?: "clerk_user_nkyel"
        } catch (_: Exception) {
            "clerk_user_nkyel"
        }

        // Build Ñkyel NkyelRunRequest payload
        val featuresObj = JSONObject().apply {
            put("deepResearch", model.isDeepResearch || isLoxoActive)
            put("executiveArtifacts", true)
            put("requiresDeerflow", model.requiresDeerflow)
            put("agUiCompatible", true)
            put("mcpEnabled", true)
            put("a2aEnabled", true)
        }

        val jsonBody = JSONObject().apply {
            put("message", message)
            put("user_id", userId)
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
                            val rText = json.optString("content", dataObj?.optString("content", json.optString("text", "")))
                            if (rText.isNotEmpty()) {
                                trySend(ChatStreamEvent.Reasoning(rText))
                            }
                        }

                        // 3. Tool execution (DeerFlow / Agentic tools)
                        eventType == "tool.started" || eventType == "tool_start" || agUiType == "TOOL_CALL_START" -> {
                            val toolName = json.optString("tool_name", json.optString("tool", dataObj?.optString("tool_name", "Exécution outil")))
                            trySend(ChatStreamEvent.ThinkingStart)
                            trySend(ChatStreamEvent.ToolActivity(name = toolName, status = "running", isRunning = true))
                        }

                        eventType == "tool.completed" || eventType == "tool_end" || agUiType == "TOOL_CALL_RESULT" -> {
                            val toolName = json.optString("tool_name", json.optString("tool", dataObj?.optString("tool_name", "Outil terminé")))
                            val res = json.optString("result", dataObj?.optString("result", ""))
                            trySend(ChatStreamEvent.ToolActivity(name = toolName, status = "completed", result = res, isRunning = false))
                        }

                        // 4. A2UI (Agent-to-User Interface) safe declarative UI
                        eventType == "vie.a2ui.render.v1" || eventType == "a2ui_render" || agUiType == "A2UI_RENDER" -> {
                            val comp = json.optString("component", dataObj?.optString("component", "google_drive"))
                            val title = json.optString("title", dataObj?.optString("title", "Connexion A2UI Sécurisée"))
                            trySend(ChatStreamEvent.A2UIRender(componentType = comp, title = title, rawJson = trimmed))
                        }

                        // 5. A2A (Agent-to-Agent Multi-Agent Delegation)
                        eventType == "vie.agent.spawned.v1" || eventType == "a2a_delegation" || agUiType == "A2A_DELEGATION" -> {
                            val delId = json.optString("delegation_id", dataObj?.optString("delegation_id", "del_${System.currentTimeMillis()}"))
                            val parent = json.optString("parent_agent", dataObj?.optString("parent_agent", "Ñkyel Principal"))
                            val target = json.optString("target_agent", dataObj?.optString("target_agent", "Spécialiste DeerFlow"))
                            val scope = json.optString("task_scope", dataObj?.optString("task_scope", "Mission multi-agents"))
                            trySend(ChatStreamEvent.A2ADelegation(delegationId = delId, parentAgent = parent, targetAgent = target, taskScope = scope))
                        }

                        // 6. MCP (Model Context Protocol Server)
                        eventType == "mcp.server.connected" || eventType == "mcp_event" || agUiType == "MCP_SERVER_EVENT" -> {
                            val sId = json.optString("server_id", dataObj?.optString("server_id", "deerflow_mcp"))
                            val status = json.optString("status", dataObj?.optString("status", "connected"))
                            val tools = json.optInt("tool_count", dataObj?.optInt("tool_count", 8))
                            trySend(ChatStreamEvent.MCPServer(serverId = sId, status = status, toolCount = tools))
                        }

                        // 7. Todos / Task Checklist (Manus & Deer Flow)
                        eventType == "todo_update" || eventType == "todos" -> {
                            val todoArr = json.optJSONArray("todos") ?: dataObj?.optJSONArray("todos")
                            if (todoArr != null) {
                                val list = mutableListOf<com.smartandj.gabomagpt.domain.model.TodoItemInfo>()
                                for (i in 0 until todoArr.length()) {
                                    val tObj = todoArr.optJSONObject(i)
                                    if (tObj != null) {
                                        list.add(
                                            com.smartandj.gabomagpt.domain.model.TodoItemInfo(
                                                id = tObj.optString("id", "todo_$i"),
                                                text = tObj.optString("text", ""),
                                                done = tObj.optBoolean("done", false),
                                                inProgress = tObj.optBoolean("in_progress", false)
                                            )
                                        )
                                    }
                                }
                                trySend(ChatStreamEvent.Todos(list))
                            }
                        }

                        // 8. Verification (Sovereign languages)
                        eventType == "verification" -> {
                            val v = json.optBoolean("verified", true)
                            val lang = json.optString("language", "Gabon")
                            val conf = json.optDouble("confidence", 0.98).toFloat()
                            trySend(ChatStreamEvent.Verification(language = lang, confidence = conf, verified = v))
                        }

                        // 9. Source discovered (Tavily / Loxo Radar)
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
