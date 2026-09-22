// data/remote/TavilyService.kt
package com.smartandj.gabomagpt.data.remote

import com.smartandj.gabomagpt.BuildConfig
import com.smartandj.gabomagpt.data.remote.dto.TavilySearchRequest
import com.smartandj.gabomagpt.data.remote.dto.TavilySearchResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

// ══════════════════════════════════════════════
// TAVILY SERVICE — Web Search for Deep Research
// ══════════════════════════════════════════════

@Singleton
class TavilyService @Inject constructor(
    private val httpClient: HttpClient
) {
    private val tavilyBaseUrl = "https://api.tavily.com"
    private val tavilyApiKey = BuildConfig.TAVILY_API_KEY

    suspend fun searchWeb(
        query: String,
        maxResults: Int = 5
    ): TavilySearchResponse {
        return try {
            val request = TavilySearchRequest(
                query = query,
                api_key = tavilyApiKey,
                max_results = maxResults,
                include_answer = true
            )

            val response = httpClient.post("$tavilyBaseUrl/search") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            response.body()
        } catch (e: Exception) {
            // Fallback response on error
            TavilySearchResponse(
                answer = "Recherche indisponible: ${e.message}",
                results = emptyList(),
                images = emptyList()
            )
        }
    }

    suspend fun searchAndSummarize(
        query: String
    ): String {
        return try {
            val searchResult = searchWeb(query, maxResults = 3)
            buildString {
                append("📊 **RECHERCHE WEB** — $query\n\n")
                append("**Réponse Directe:**\n${searchResult.answer}\n\n")
                
                if (searchResult.results.isNotEmpty()) {
                    append("**Sources (Top 3):**\n")
                    searchResult.results.take(3).forEachIndexed { idx, result ->
                        append("${idx + 1}. [${result.title}](${result.url})\n")
                        append("   ${result.content.take(150)}...\n\n")
                    }
                }
            }
        } catch (e: Exception) {
            "❌ Erreur recherche: ${e.message}"
        }
    }

    suspend fun searchStructured(
        query: String
    ): List<com.smartandj.gabomagpt.domain.model.SourceRef> {
        return try {
            val searchResult = searchWeb(query, maxResults = 5)
            searchResult.results.map { result ->
                val host = try {
                    java.net.URL(result.url).host
                } catch (e: Exception) {
                    "Source"
                }
                com.smartandj.gabomagpt.domain.model.SourceRef(
                    title = result.title,
                    host = host,
                    url = result.url,
                    snippet = result.content,
                    confidence = result.score.toFloat()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
