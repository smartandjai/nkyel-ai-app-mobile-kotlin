/* GabomaGPT · OnboardingViewModel.kt · SmartANDJ AI Technologies
   Onboarding state machine — 4 steps: Consent → Identity → Language → Telemetry
   Fondateur : Daniel Jonathan ANDJ */

package com.smartandj.gabomagpt.presentation.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Onboarding Step ──────────────────────────────────────────
enum class OnboardingStep {
    CONSENT, IDENTITY, LANGUAGE, TELEMETRY
}

// ── Gabonese Languages ───────────────────────────────────────
data class GabomaLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val isPrimary: Boolean = false,
)

val GABOMA_LANGUAGES = listOf(
    GabomaLanguage("fr-GA", "Français (Gabon)", "Français", isPrimary = true),
    GabomaLanguage("fan",   "Fang",             "Fang"),
    GabomaLanguage("pun",   "Punu",             "Yipunu"),
    GabomaLanguage("mye",   "Myènè",            "Omyènè"),
    GabomaLanguage("nzb",   "Nzébi",            "Nzébi"),
)

// ── UI State ─────────────────────────────────────────────────
data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.CONSENT,
    // Step 1: Consent
    val tosAccepted: Boolean = false,
    // Step 2: Identity
    val displayName: String = "",
    val clerkName: String = "", // Pre-filled from Clerk, user can override
    // Step 3: Language
    val selectedLanguages: Set<String> = setOf("fr-GA"),
    val primaryLocale: String = "fr-GA",
    // Step 4: Telemetry
    val telemetryEnabled: Boolean = false,
    // General
    val isSubmitting: Boolean = false,
    val error: String? = null,
)

private const val PREFS_NAME = "gaboma_onboarding"
private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
private const val KEY_DISPLAY_NAME = "display_name"
private const val KEY_LANGUAGES = "selected_languages"
private const val KEY_PRIMARY_LOCALE = "primary_locale"
private const val KEY_TELEMETRY = "telemetry_enabled"

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    /** Check if onboarding was already completed */
    fun isOnboardingComplete(): Boolean =
        prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)

    /** Pre-fill the name from Clerk (called after auth) */
    fun prefillFromClerk(fullName: String?) {
        val name = fullName?.trim()?.takeIf { it.isNotEmpty() } ?: ""
        _state.update { it.copy(displayName = name, clerkName = name) }
    }

    // ── Step navigation ──────────────────────────────────────
    fun goToStep(step: OnboardingStep) {
        _state.update { it.copy(currentStep = step, error = null) }
    }

    fun nextStep() {
        _state.update { current ->
            val next = when (current.currentStep) {
                OnboardingStep.CONSENT   -> OnboardingStep.IDENTITY
                OnboardingStep.IDENTITY  -> OnboardingStep.LANGUAGE
                OnboardingStep.LANGUAGE  -> OnboardingStep.TELEMETRY
                OnboardingStep.TELEMETRY -> OnboardingStep.TELEMETRY // last step
            }
            current.copy(currentStep = next, error = null)
        }
    }

    fun previousStep() {
        _state.update { current ->
            val prev = when (current.currentStep) {
                OnboardingStep.CONSENT   -> OnboardingStep.CONSENT // first step
                OnboardingStep.IDENTITY  -> OnboardingStep.CONSENT
                OnboardingStep.LANGUAGE  -> OnboardingStep.IDENTITY
                OnboardingStep.TELEMETRY -> OnboardingStep.LANGUAGE
            }
            current.copy(currentStep = prev, error = null)
        }
    }

    // ── Field updates ────────────────────────────────────────
    fun setTosAccepted(accepted: Boolean) {
        _state.update { it.copy(tosAccepted = accepted) }
    }

    fun setDisplayName(name: String) {
        _state.update { it.copy(displayName = name) }
    }

    fun toggleLanguage(code: String) {
        _state.update { current ->
            val updated = current.selectedLanguages.toMutableSet()
            if (code in updated && updated.size > 1) {
                updated.remove(code)
            } else {
                updated.add(code)
            }
            // If primary was removed, auto-set the first remaining
            val newPrimary = if (current.primaryLocale in updated) {
                current.primaryLocale
            } else {
                updated.first()
            }
            current.copy(selectedLanguages = updated, primaryLocale = newPrimary)
        }
    }

    fun setPrimaryLocale(code: String) {
        _state.update { current ->
            val langs = current.selectedLanguages.toMutableSet()
            langs.add(code) // Ensure primary is always selected
            current.copy(primaryLocale = code, selectedLanguages = langs)
        }
    }

    fun setTelemetryEnabled(enabled: Boolean) {
        _state.update { it.copy(telemetryEnabled = enabled) }
    }

    // ── Validation ───────────────────────────────────────────
    fun canProceed(): Boolean = when (_state.value.currentStep) {
        OnboardingStep.CONSENT   -> _state.value.tosAccepted
        OnboardingStep.IDENTITY  -> _state.value.displayName.trim().length >= 2
        OnboardingStep.LANGUAGE  -> _state.value.selectedLanguages.isNotEmpty()
        OnboardingStep.TELEMETRY -> true // always can proceed
    }

    // ── Submit ───────────────────────────────────────────────
    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            try {
                // Save to SharedPreferences
                prefs.edit()
                    .putBoolean(KEY_ONBOARDING_COMPLETE, true)
                    .putString(KEY_DISPLAY_NAME, _state.value.displayName.trim())
                    .putStringSet(KEY_LANGUAGES, _state.value.selectedLanguages)
                    .putString(KEY_PRIMARY_LOCALE, _state.value.primaryLocale)
                    .putBoolean(KEY_TELEMETRY, _state.value.telemetryEnabled)
                    .apply()

                _state.update { it.copy(isSubmitting = false) }
                onComplete()
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSubmitting = false, error = "Erreur: ${e.message}")
                }
            }
        }
    }

    /** Get saved display name after onboarding */
    fun getSavedDisplayName(): String? = prefs.getString(KEY_DISPLAY_NAME, null)
}
