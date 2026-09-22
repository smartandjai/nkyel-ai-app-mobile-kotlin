/* GabomaGPT · AuthViewModel.kt · SmartANDJ AI Technologies
   Auth state machine — drives navigation between Splash → Auth → Chat
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.clerk.api.Clerk
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Represents the three possible auth states for the app. */
sealed interface AuthState {
    /** SDK still loading / checking session */
    data object Loading : AuthState
    /** User is authenticated — contains display info */
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
class AuthViewModel @Inject constructor() : ViewModel() {

    val authState: StateFlow<AuthState> = combine(
        Clerk.isInitialized,
        Clerk.userFlow
    ) { isInitialized, user ->
        when {
            !isInitialized -> AuthState.Loading
            user != null -> AuthState.SignedIn(
                userId = user.id,
                fullName = listOfNotNull(user.firstName, user.lastName).joinToString(" ").trim().ifEmpty { null },
                avatarUrl = user.imageUrl,
                email = user.primaryEmailAddress?.emailAddress
            )
            else -> AuthState.SignedOut
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState.Loading
    )

    fun signOut() {
        viewModelScope.launch {
            try {
                Clerk.auth.signOut()
            } catch (_: Exception) {}
        }
    }
}
