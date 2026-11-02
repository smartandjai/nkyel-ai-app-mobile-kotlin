/**
 * Ñkyel AI — Authenticated API Client
 * SmartANDJ AI Technologies · Founder: Daniel Jonathan ANDJ
 *
 * Central HTTP client with Clerk token injection.
 * All API calls go through this client — never bypass for provider calls.
 */
package com.smartandj.gabomagpt.data.remote

import com.clerk.api.Clerk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class UserProfileResponse(
    val id: String? = null,
    val clerk_id: String? = null,
    val email: String? = null,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val tier: String? = "free",
    val credits: Int? = 1000,
    val credits_used: Int? = 0,
    val is_admin: Boolean = false,
    val beta_pioneer: Boolean = false,
)

@Singleton
class NkyelApiClient @Inject constructor() {

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(NkyelApiConfig.CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .readTimeout(NkyelApiConfig.READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .writeTimeout(NkyelApiConfig.WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .build()

    private val sseFactory = EventSources.createFactory(httpClient)

    private suspend fun getToken(): String? {
        return try {
            val session = Clerk.getActiveSession() ?: return null
            session.getToken()
        } catch (e: Exception) {
            null
        }
    }

    private fun buildUrl(path: String): String {
        return if (path.startsWith("http")) path
        else "${NkyelApiConfig.BASE_URL}${if (path.startsWith("/")) "" else "/"}$path"
    }

    suspend fun get(path: String): Response = withContext(Dispatchers.IO) {
        val token = getToken()
        val request = Request.Builder()
            .url(buildUrl(path))
            .apply { token?.let { header("Authorization", "Bearer $it") } }
            .get()
            .build()
        httpClient.newCall(request).execute()
    }

    suspend fun post(path: String, body: String = "{}"): Response = withContext(Dispatchers.IO) {
        val token = getToken()
        val mediaType = "application/json".toMediaType()
        val request = Request.Builder()
            .url(buildUrl(path))
            .apply { token?.let { header("Authorization", "Bearer $it") } }
            .post(body.toRequestBody(mediaType))
            .build()
        httpClient.newCall(request).execute()
    }

    suspend fun patch(path: String, body: String = "{}"): Response = withContext(Dispatchers.IO) {
        val token = getToken()
        val mediaType = "application/json".toMediaType()
        val request = Request.Builder()
            .url(buildUrl(path))
            .apply { token?.let { header("Authorization", "Bearer $it") } }
            .patch(body.toRequestBody(mediaType))
            .build()
        httpClient.newCall(request).execute()
    }

    suspend fun delete(path: String): Response = withContext(Dispatchers.IO) {
        val token = getToken()
        val request = Request.Builder()
            .url(buildUrl(path))
            .apply { token?.let { header("Authorization", "Bearer $it") } }
            .delete()
            .build()
        httpClient.newCall(request).execute()
    }

    /** Fetch current user profile directly from Neon database via FastAPI /v1/users/me */
    suspend fun getMe(): UserProfileResponse? = withContext(Dispatchers.IO) {
        try {
            val response = get("/v1/users/me")
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: return@withContext null
                json.decodeFromString<UserProfileResponse>(bodyStr)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Connect to SSE endpoint for real-time AG-UI events */
    suspend fun connectSSE(
        path: String,
        body: String? = null,
        listener: EventSourceListener
    ): EventSource = withContext(Dispatchers.IO) {
        val token = getToken()
        val requestBuilder = Request.Builder()
            .url(buildUrl(path))
            .apply { token?.let { header("Authorization", "Bearer $it") } }
            .header("Accept", "text/event-stream")

        if (body != null) {
            val mediaType = "application/json".toMediaType()
            requestBuilder.post(body.toRequestBody(mediaType))
        }

        sseFactory.newEventSource(requestBuilder.build(), listener)
    }
}
