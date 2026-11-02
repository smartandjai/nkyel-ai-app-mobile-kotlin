/**
 * Ñkyel AI — Production API Configuration
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Single source of truth for all Ñkyel backend communication.
 * Android is an authenticated client of the existing FastAPI backend.
 */
package com.smartandj.gabomagpt.data.remote

object NkyelApiConfig {
    /** Production FastAPI backend */
    const val BASE_URL = "https://api.nkyel.smartandjai.com"

    /** Clerk production publishable key */
    const val CLERK_PUBLISHABLE_KEY = "pk_live_Y2xlcmsuc21hcnRhbmRqYWkuY29tJA"

    // ── API Paths ────────────────────────────────────────────
    const val AUTH_ME = "/api/auth/me"
    const val CONVERSATIONS = "/api/v1/conversations"
    const val MISSIONS = "/api/v1/missions"
    const val WORKGRAPH_NODES = "/api/v1/workgraph/nodes"
    const val WORKGRAPH_EDGES = "/api/v1/workgraph/edges"
    const val EVENTS = "/api/v1/events"
    const val EVENTS_RUNS = "/api/v1/events/runs"
    const val ARTIFACTS = "/api/v1/artifacts"
    const val SOURCES = "/api/v1/sources"
    const val EVIDENCE = "/api/v1/evidence"
    const val CONNECTORS = "/api/v1/connectors"
    const val SKILLS = "/api/v1/skills"
    const val MCP_REGISTRY = "/api/v1/mcp/registry"
    const val MEDIA = "/api/v1/media"
    const val USER_SETTINGS = "/api/v1/user-settings"
    const val NKYEL_RUN = "/api/v1/nkyel/run"
    const val NKYEL_STREAM = "/api/v1/nkyel/stream"
    const val WORKSPACES = "/api/v1/workspaces"
    const val FEEDBACK = "/api/v1/feedback"
    const val MEMORY = "/api/v1/memory"
    const val PROVIDERS = "/api/v1/providers"
    const val VISION_ENGINE = "/api/v1/vision-engine"
    const val WIDE_RESEARCH = "/api/v1/wide-research"
    const val HEALTH = "/api/health"

    // ── Timeouts ─────────────────────────────────────────────
    const val CONNECT_TIMEOUT_MS = 15_000L
    const val READ_TIMEOUT_MS = 90_000L   // SSE streams can be long
    const val WRITE_TIMEOUT_MS = 30_000L

    // ── Model IDs (display only — routing is server-side) ───
    const val MODEL_NKYEL_MAX = "openai/gpt-oss-120b"
    const val MODEL_NKYEL = "Qwen/Qwen3-32B-AWQ"
    const val MODEL_NKYEL_CHUI = "llama-3.3-70b-versatile"
    const val MODEL_NKYEL_RADI = "llama-3.1-8b-instant"
    const val MODEL_NKYEL_LIGHT = "llama-3.1-8b-instant"
    const val MODEL_NKYEL_FLEX = "Qwen/Qwen3-32B-AWQ"
}
