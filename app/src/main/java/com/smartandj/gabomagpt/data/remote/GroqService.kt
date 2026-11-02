// data/remote/GroqService.kt
package com.smartandj.gabomagpt.data.remote

import com.smartandj.gabomagpt.BuildConfig
import com.smartandj.gabomagpt.data.remote.dto.GroqChatRequest
import com.smartandj.gabomagpt.data.remote.dto.GroqMessage
import com.smartandj.gabomagpt.data.remote.dto.GroqStreamEvent
import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

// ══════════════════════════════════════════════════════════════════
// GROQ SERVICE — Real-time LLM Streaming (8b, 70b, Mixtral)
// ══════════════════════════════════════════════════════════════════

@Singleton
class GroqService @Inject constructor(
    private val httpClient: HttpClient
) {
    private val groqApiKey = BuildConfig.GROQ_API_KEY
    private val groqBaseUrl = GabomaNetworkConfig.GROQ_BASE_URL
    private val json = Json { ignoreUnknownKeys = true }

    fun streamChat(
        message: String,
        model: GabomaChatModel,
        systemPrompt: String = "Tu es Gaboma, assistant IA puissant. Réponds en français avec style et clarté."
    ): Flow<ChatStreamEvent> = flow {
        try {
            val groqModel = when (model) {
                GabomaChatModel.AURATA -> GabomaNetworkConfig.GROQ_MODEL_AURATA
                GabomaChatModel.NYEL -> GabomaNetworkConfig.GROQ_MODEL_SONAR
                GabomaChatModel.WANDANA -> GabomaNetworkConfig.GROQ_MODEL_LOXO
                GabomaChatModel.ONYX_GRIS -> GabomaNetworkConfig.GROQ_MODEL_ONYX
                GabomaChatModel.BLACK_PANTHER -> GabomaNetworkConfig.GROQ_MODEL_ONYX
                GabomaChatModel.GABOMA_SEER -> GabomaNetworkConfig.GROQ_MODEL_SONAR
            }

            val request = GroqChatRequest(
                model = groqModel,
                messages = listOf(
                    GroqMessage(role = "system", content = systemPrompt),
                    GroqMessage(role = "user", content = message)
                ),
                temperature = 0.7,
                max_tokens = 2048,
                stream = true
            )

            val response = httpClient.post("$groqBaseUrl/chat/completions") {
                header("Authorization", "Bearer $groqApiKey")
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(GroqChatRequest.serializer(), request))
            }

            response.bodyAsText().lines().forEach { line ->
                if (line.isNotEmpty() && line.startsWith("data:")) {
                    val jsonStr = line.substring(5).trim()
                    if (jsonStr == "[DONE]") {
                        emit(ChatStreamEvent.Done)
                    } else if (jsonStr.isNotEmpty()) {
                        try {
                            val streamEvent = json.decodeFromString(
                                GroqStreamEvent.serializer(),
                                jsonStr
                            )
                            streamEvent.choices.firstOrNull()?.delta?.content?.let {
                                emit(ChatStreamEvent.Token(it))
                            }
                        } catch (e: Exception) {
                            // JSON parse error - skip chunk
                        }
                    }
                }
            }

            emit(ChatStreamEvent.Done)
        } catch (e: Exception) {
            emit(ChatStreamEvent.Error("❌ Erreur Groq: ${e.message}"))
        }
    }

    /**
     * Loxo Mode: Search web FIRST (Tavily), then analyze with Groq
     */
    fun streamLoxoDeepResearch(
        query: String,
        tavilyService: TavilyService
    ): Flow<ChatStreamEvent> = flow {
        try {
            // Step 1: Web search
            emit(ChatStreamEvent.Token("🐘 **L'éléphant creuse...**\n"))
            val searchContext = tavilyService.searchAndSummarize(query)
            emit(ChatStreamEvent.Token(searchContext))
            emit(ChatStreamEvent.Token("\n\n✨ **En piste...**\n"))

            // Step 2: Stream analysis with search context
            val systemPrompt = """
Tu es Loxo, expert en deep research. Tu analyses des résultats web pour fournir réponses précises.

RÈGLE CRITIQUE: Tu dois TOUJOURS rechercher le WEB d'abord avant de générer du CODE.
Si l'utilisateur demande du code: recherche d'abord, puis génère avec contexte web.

Style: Direct, profond, avec sources.
Langue: Français.
            """.trimIndent()

            val analysisPrompt = """
$searchContext

---

Utilisateur: $query

Analyse profonde basée sur les sources ci-dessus:
            """.trimIndent()

            streamChat(analysisPrompt, GabomaChatModel.WANDANA, systemPrompt).collect { event ->
                emit(event)
            }
        } catch (e: Exception) {
            emit(ChatStreamEvent.Error("❌ Erreur Loxo Deep Research: ${e.message}"))
        }
    }
}
