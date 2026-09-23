/* Ñkyel AI · NkyelNavHost.kt · SmartANDJ AI Technologies
   Auth-gated navigation — Splash → Auth → Onboarding → Chat
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartandj.gabomagpt.presentation.auth.AuthScreen
import com.smartandj.gabomagpt.presentation.auth.AuthState
import com.smartandj.gabomagpt.presentation.auth.AuthViewModel
import com.smartandj.gabomagpt.presentation.chat.ChatViewModel
import com.smartandj.gabomagpt.presentation.chat.NkyelChatScreen
import com.smartandj.gabomagpt.presentation.chat.UserTier
import com.smartandj.gabomagpt.presentation.onboarding.OnboardingScreen
import com.smartandj.gabomagpt.presentation.onboarding.OnboardingViewModel
import com.smartandj.gabomagpt.presentation.onboarding.TermsOfServiceScreen
import com.smartandj.gabomagpt.presentation.onboarding.PrivacyPolicyScreen
import com.smartandj.gabomagpt.presentation.onboarding.AcceptableUsePolicyScreen
import com.smartandj.gabomagpt.presentation.theme.GabomaColors

@Composable
fun NkyelNavHost() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    // ── Auth-gated rendering ─────────────────────────────────
    when (val state = authState) {
        is AuthState.Loading -> {
            // Splash / loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GabomaColors.BgBlackPanther),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GabomaColors.AccentBlackPanther)
            }
        }

        is AuthState.SignedOut -> {
            // Show Clerk AuthView (sign-in + sign-up + Google)
            AuthScreen()
        }

        is AuthState.SignedIn -> {
            // Determine start destination based on onboarding status
            val startDest = if (onboardingViewModel.isOnboardingComplete()) "chat" else "onboarding"

            NavHost(
                navController = navController,
                startDestination = startDest,
            ) {
                // ── Onboarding ──
                composable("onboarding") {
                    OnboardingScreen(
                        clerkFullName = state.fullName,
                        onComplete = {
                            navController.navigate("chat") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        },
                        onOpenTerms = { navController.navigate("terms") },
                        onOpenPrivacy = { navController.navigate("privacy") },
                    )
                }

                // ── Legal pages ──
                composable("terms") {
                    TermsOfServiceScreen(onBack = { navController.popBackStack() })
                }
                composable("privacy") {
                    PrivacyPolicyScreen(onBack = { navController.popBackStack() })
                }
                composable("acceptable-use") {
                    AcceptableUsePolicyScreen(onBack = { navController.popBackStack() })
                }

                // ── Chat (wired to ChatViewModel) ──
                composable("chat") {
                    val chatViewModel: ChatViewModel = hiltViewModel()
                    val chatState by chatViewModel.uiState.collectAsState()

                    val displayName = onboardingViewModel.getSavedDisplayName()
                        ?: state.fullName
                        ?: "Utilisateur"

                    NkyelChatScreen(
                        navController = navController,
                        userName = displayName,
                        userTier = UserTier.AURATA,
                        messages = chatState.messages,
                        isGenerating = chatState.isStreaming,
                        onSend = { text, modelId ->
                            chatViewModel.sendMessage(text, modelId)
                        },
                        onStop = { chatViewModel.cancelStreaming() },
                        onNewChat = { chatViewModel.clearMessages() },
                        onUpsellRequested = {},
                    )
                }
            }
        }
    }
}

@Composable
fun GabomaNavHost() {
    NkyelNavHost()
}

