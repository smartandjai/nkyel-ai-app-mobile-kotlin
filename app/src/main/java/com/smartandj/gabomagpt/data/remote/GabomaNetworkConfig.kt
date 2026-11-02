// FILE: app/src/main/java/com/smartandj/gabomagpt/data/remote/GabomaNetworkConfig.kt
package com.smartandj.gabomagpt.data.remote

object GabomaNetworkConfig {
    /**
     * Émulateur Android vers backend local.
     *
     * Pour un téléphone physique :
     * remplace par http://IP_LOCALE_DE_TON_PC:8000/api
     *
     * En production :
     * remplace par https://gabomagpt.andjanalytics.com/api
     */
    const val BASE_URL: String = "http://127.0.0.1:8000/api"

    // ══════════════════════════════════════════
    // EXTERNAL API KEYS — Loaded from BuildConfig
    // (Secrets stored in local.properties, never committed)
    // ══════════════════════════════════════════

    // GROQ API — Flash LLM Streaming
    const val GROQ_BASE_URL: String = "https://api.groq.com/openai/v1"

    // Modèles Groq par tier
    const val GROQ_MODEL_AURATA: String = "llama-3.1-8b-instant"      // ⚡ Rapide 8b
    const val GROQ_MODEL_SONAR: String = "llama-3.3-70b-versatile"     // 🐬 Balancé 70b
    const val GROQ_MODEL_ONYX: String = "mixtral-8x7b-32768"           // 🐆 Puissant Mixtral
    const val GROQ_MODEL_LOXO: String = "llama-3.3-70b-versatile"      // 🔍 Recherche 70b (Tavily enrichit)

    // TAVILY API — Web Search Deep Research
    const val TAVILY_BASE_URL: String = "https://api.tavily.com"

    // ══════════════════════════════════════════
    // TIMEOUTS
    // ══════════════════════════════════════════
    const val CONNECT_TIMEOUT_MS: Long = 30_000
    const val READ_TIMEOUT_MS: Long = 60_000
    const val WRITE_TIMEOUT_MS: Long = 60_000
}
