// data/remote/dto/TavilySearchDto.kt
package com.smartandj.gabomagpt.data.remote.dto

import kotlinx.serialization.Serializable

// ══════════════════════════════════════════════
// TAVILY WEB SEARCH — Deep Research API
// ══════════════════════════════════════════════

@Serializable
data class TavilySearchRequest(
    val query: String,
    val api_key: String,
    val max_results: Int = 5,
    val include_answer: Boolean = true,
    val include_raw_content: Boolean = false
)

@Serializable
data class TavilySearchResponse(
    val answer: String,
    val results: List<TavilySearchResult>,
    val images: List<String>? = null
)

@Serializable
data class TavilySearchResult(
    val title: String,
    val url: String,
    val content: String,
    val score: Double
)

// ══════════════════════════════════════════════
// GROQ LLM — Flash Streaming
// ══════════════════════════════════════════════

@Serializable
data class GroqChatRequest(
    val model: String = "mixtral-8x7b-32768",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 2048,
    val stream: Boolean = true
)

@Serializable
data class GroqMessage(
    val role: String, // "user" | "assistant" | "system"
    val content: String
)

@Serializable
data class GroqChatResponse(
    val id: String,
    val choices: List<GroqChoice>,
    val created: Long,
    val model: String,
    val usage: GroqUsage? = null
)

@Serializable
data class GroqChoice(
    val index: Int,
    val message: GroqMessage,
    val finish_reason: String
)

@Serializable
data class GroqUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

// Stream chunk from Groq
@Serializable
data class GroqStreamEvent(
    val choices: List<GroqStreamChoice>
)

@Serializable
data class GroqStreamChoice(
    val index: Int,
    val delta: GroqDelta? = null,
    val finish_reason: String? = null
)

@Serializable
data class GroqDelta(
    val role: String? = null,
    val content: String? = null
)
