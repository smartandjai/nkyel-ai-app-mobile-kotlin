// FILE: app/src/main/java/com/smartandj/gabomagpt/data/remote/NkyelNetworkConfig.kt
package com.smartandj.gabomagpt.data.remote

object NkyelNetworkConfig {
    /**
     * Ñkyel AI Backend Officiel
     * Web reference: https://nkyel.smartandjai.com/
     */
    const val BASE_URL: String = "https://api.nkyel.smartandjai.com"
    const val WEB_URL: String = "https://nkyel.smartandjai.com"

    // ── Routes d'API canoniques ────────────────
    const val AGENT_STREAM_URL: String = "$BASE_URL/api/v1/nkyel/run"
    const val CONVERSATIONS_URL: String = "$BASE_URL/api/v1/conversations"
    const val ARTIFACTS_URL: String = "$BASE_URL/api/v1/artifacts"
    const val MISSIONS_URL: String = "$BASE_URL/api/v1/missions"

    // ── Modèles backend officiels (Routage serveur) ─
    const val MODEL_FAST_CHAT: String = "fast_chat"       // Groq GPT-OSS
    const val MODEL_QWEN_AGENT: String = "qwen3-32b"      // RunPod Qwen
    const val MODEL_DEERFLOW: String = "DEERFLOW"          // Heavy agent / DeerFlow
    const val MODEL_NATIVE: String = "NATIVE"

    // ── Fallback Direct Groq ─────────────────
    const val GROQ_BASE_URL: String = "https://api.groq.com/openai/v1"
    const val GROQ_MODEL_AURATA: String = "llama-3.1-8b-instant"
    const val GROQ_MODEL_SONAR: String = "llama-3.3-70b-versatile"
    const val GROQ_MODEL_LOXO: String = "llama-3.1-70b-versatile"
    const val GROQ_MODEL_ONYX: String = "llama-3.3-70b-versatile"

    // ══════════════════════════════════════════
    // TIMEOUTS
    // ══════════════════════════════════════════
    const val CONNECT_TIMEOUT_MS: Long = 30_000
    const val READ_TIMEOUT_MS: Long = 120_000  // Support des missions longues
    const val WRITE_TIMEOUT_MS: Long = 60_000
}

typealias GabomaNetworkConfig = NkyelNetworkConfig
