/* Ñkyel AI · AuthViewModel.kt · SmartANDJ AI Technologies
   Auth state machine driven by Clerk + Neon PostgreSQL via FastAPI
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerk.api.Clerk
import com.smartandj.gabomagpt.data.remote.NkyelApiClient
import com.smartandj.gabomagpt.data.remote.UserProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Represents the three possible auth states for the app. */
sealed interface AuthState {
    /** SDK still loading / checking session */
    data object Loading : AuthState
    /** User is authenticated with verified real profile */
    data class SignedIn(
        val userId: String,
        val fullName: String?,
        val avatarUrl: String?,
        val email: String?
    ) : AuthState
    /** No active session */
    data object SignedOut : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiClient: NkyelApiClient,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    init {
        checkAuth()
    }

    /** Check active Clerk session and load real profile from Neon backend */
    fun checkAuth() {
        viewModelScope.launch {
            try {
                val session = Clerk.session
                val clerkUser = Clerk.user

                if (session != null || clerkUser != null) {
                    val userId = clerkUser?.id ?: session?.id ?: ""
                    val name = listOfNotNull(clerkUser?.firstName, clerkUser?.lastName)
                        .joinToString(" ")
                        .ifBlank { null }
                        ?: clerkUser?.primaryEmailAddress?.emailAddress?.substringBefore("@")
                        ?: "Membre Ñkyel"
                    val avatar = clerkUser?.imageUrl
                    val email = clerkUser?.primaryEmailAddress?.emailAddress

                    _authState.value = AuthState.SignedIn(
                        userId = userId,
                        fullName = name,
                        avatarUrl = avatar,
                        email = email
                    )

                    // Fetch authoritative Neon profile from FastAPI backend (/v1/users/me)
                    try {
                        val profile = apiClient.getMe()
                        if (profile != null) {
                            _userProfile.value = profile
                            _authState.value = AuthState.SignedIn(
                                userId = profile.clerk_id ?: userId,
                                fullName = profile.full_name ?: name,
                                avatarUrl = profile.avatar_url ?: avatar,
                                email = profile.email ?: email
                            )
                        }
                    } catch (_: Exception) {
                        // Keep Clerk profile if backend call times out
                    }
                } else {
                    _authState.value = AuthState.SignedOut
                }
            } catch (e: Exception) {
                _authState.value = AuthState.SignedOut
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                Clerk.auth.signOut()
            } catch (_: Exception) {}
            _authState.value = AuthState.SignedOut
            _userProfile.value = null
        }
    }
}
