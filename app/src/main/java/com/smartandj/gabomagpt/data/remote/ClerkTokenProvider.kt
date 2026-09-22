package com.smartandj.gabomagpt.data.remote

import com.clerk.api.Clerk
import com.clerk.api.network.serialization.ClerkResult
import kotlinx.coroutines.CancellationException

/**
 * Shared suspend token accessor against the installed Clerk Android SDK (1.0.28).
 * Retrieves the active session token, or null if unauthenticated or request failed.
 * Preserves coroutine cancellation and handles missing/expired sessions gracefully.
 */
object ClerkTokenProvider {
    suspend fun getValidSessionToken(): String? {
        return try {
            when (val result = Clerk.auth.getToken()) {
                is ClerkResult.Success -> {
                    result.value.takeIf { it.isNotBlank() }
                }
                is ClerkResult.Failure -> {
                    null
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }
}
