/* GabomaGPT · AuthViewModel.kt · SmartANDJ AI Technologies
   Auth state machine — drives navigation between Splash → Auth → Chat
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // TODO: Re-enable Clerk auth when SDK is properly configured
        // For now, default to SignedOut so the app can compile and run
        _authState.value = AuthState.SignedOut
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.SignedOut
        }
    }
}
