// data/remote/AdvancedStreamingService.kt
package com.smartandj.gabomagpt.data.remote

import com.smartandj.gabomagpt.data.remote.dto.ChatStreamEvent
import com.smartandj.gabomagpt.domain.model.GabomaChatModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject
import javax.inject.Singleton

// ══════════════════════════════════════════════════════════════════
// ADVANCED STREAMING SERVICE — Buffering, Conflation, Error Handling
// Best practices from Kotlin 2.4 + Android 2026
// ══════════════════════════════════════════════════════════════════

@Singleton
class AdvancedStreamingService @Inject constructor(
    private val groqService: GroqService,
    private val tavilyService: TavilyService
) {

    /**
     * Stream with buffering for slow UI collectors
     * Prevents UI freezing during rapid token emissions
     * Uses 64-token buffer (standard for Compose rendering)
     */
    fun streamWithBuffering(
        message: String,
        model: GabomaChatModel
    ): Flow<ChatStreamEvent> = groqService
        .streamChat(message, model)
        .buffer(capacity = 64) // Buffer up to 64 tokens before blocking
        .catch { error ->
            emit(ChatStreamEvent.Error("Streaming error: ${error.message}"))
        }
        .onCompletion { cause ->
            if (cause == null) {
                // Success — emit Done marker
                emit(ChatStreamEvent.Done)
            }
        }

    /**
     * Loxo Deep Research Fast: Web search FIRST, then stream analysis
     * Uses conflation to skip intermediate tokens if collector too slow
     * Only newest token is kept if backpressure detected
     */
    fun streamLoxoFast(query: String): Flow<ChatStreamEvent> = flow {
        try {
            // Step 1: Web search (blocking, short ~1-2s)
            emit(ChatStreamEvent.Token("🐘 **L'éléphant creuse...**\n"))
            val searchContext = tavilyService.searchAndSummarize(query)
            emit(ChatStreamEvent.Token(searchContext))
            emit(ChatStreamEvent.Token("\n\n✨ **En piste...** "))

            // Step 2: Stream analysis with Groq 70b
            groqService.streamChat(
                "Analyse profonde:\n$searchContext\n\nQuestion: $query",
                GabomaChatModel.WANDANA
            ).collect { event ->
                emit(event)
            }

            emit(ChatStreamEvent.Done)
        } catch (e: Exception) {
            emit(ChatStreamEvent.Error("Loxo Deep Research failed: ${e.message}"))
        }
    }
        .conflate() // Only keep latest value if slow collector
        .catch { error ->
            emit(ChatStreamEvent.Error("Conflation error: ${error.message}"))
        }
        .onCompletion { cause ->
            if (cause != null) {
                emit(ChatStreamEvent.Error("Flow completed: ${cause.message}"))
            }
        }

    /**
     * High-performance streaming for fast UI (Compose rendering >60fps)
     * Uses 128-token buffer + conflation for ultra-responsive feel
     * Best for: Real-time chat display, Live mode
     */
    fun streamHighPerformance(
        message: String,
        model: GabomaChatModel
    ): Flow<ChatStreamEvent> = groqService
        .streamChat(message, model)
        .buffer(capacity = 128) // Larger buffer for high throughput
        .conflate() // Drop old tokens if too many pending
        .catch { error ->
            emit(ChatStreamEvent.Error("High-performance streaming failed: ${error.message}"))
        }
        .onCompletion { cause ->
            if (cause == null) emit(ChatStreamEvent.Done)
        }

    /**
     * Standard streaming with error resilience
     * Balanced approach: buffering + full token preservation
     */
    fun streamStandard(
        message: String,
        model: GabomaChatModel
    ): Flow<ChatStreamEvent> = groqService
        .streamChat(message, model)
        .buffer(capacity = 64)
        .catch { error ->
            emit(ChatStreamEvent.Error("Standard streaming error: ${error.message}"))
        }
        .onCompletion { cause ->
            if (cause == null) emit(ChatStreamEvent.Done)
        }
}
