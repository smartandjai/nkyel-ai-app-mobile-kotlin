package com.smartandj.gabomagpt.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GabomaPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val SELECTED_MODEL_KEY = stringPreferencesKey("selected_model")
        val WRITING_TONE_KEY = stringPreferencesKey("writing_tone")
        val SHOW_SOURCES_KEY = booleanPreferencesKey("show_sources")
        val LOXO_WEB_DEEP_KEY = booleanPreferencesKey("loxo_web_deep")
        val ADAPTIVE_CONTRAST_KEY = booleanPreferencesKey("adaptive_contrast")
        val VAULT_ACTIVE_KEY = booleanPreferencesKey("vault_active")
    }

    val selectedModelFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[SELECTED_MODEL_KEY] ?: "BLACK_PANTHER"
    }

    val writingToneFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[WRITING_TONE_KEY] ?: "Pro"
    }

    val showSourcesFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_SOURCES_KEY] ?: true
    }

    val loxoWebDeepFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[LOXO_WEB_DEEP_KEY] ?: true
    }

    val adaptiveContrastFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ADAPTIVE_CONTRAST_KEY] ?: true
    }

    val vaultActiveFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[VAULT_ACTIVE_KEY] ?: false
    }

    suspend fun setSelectedModel(model: String) {
        dataStore.edit { preferences -> preferences[SELECTED_MODEL_KEY] = model }
    }

    suspend fun setWritingTone(tone: String) {
        dataStore.edit { preferences -> preferences[WRITING_TONE_KEY] = tone }
    }

    suspend fun toggleShowSources() {
        dataStore.edit { preferences -> 
            val current = preferences[SHOW_SOURCES_KEY] ?: true
            preferences[SHOW_SOURCES_KEY] = !current 
        }
    }

    suspend fun toggleLoxoWebDeep() {
        dataStore.edit { preferences -> 
            val current = preferences[LOXO_WEB_DEEP_KEY] ?: true
            preferences[LOXO_WEB_DEEP_KEY] = !current 
        }
    }

    suspend fun toggleAdaptiveContrast() {
        dataStore.edit { preferences -> 
            val current = preferences[ADAPTIVE_CONTRAST_KEY] ?: true
            preferences[ADAPTIVE_CONTRAST_KEY] = !current 
        }
    }

    suspend fun toggleVaultActive() {
        dataStore.edit { preferences -> 
            val current = preferences[VAULT_ACTIVE_KEY] ?: false
            preferences[VAULT_ACTIVE_KEY] = !current 
        }
    }
}
