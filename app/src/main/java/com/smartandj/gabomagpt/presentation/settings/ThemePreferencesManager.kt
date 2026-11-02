package com.smartandj.gabomagpt.presentation.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 *  THEME PREFERENCES MANAGER - Persists theme selection to DataStore
 *  Handles: GabomaThemeType persistence, default theme fallback
 * ═══════════════════════════════════════════════════════════════════════════════
 */
class ThemePreferencesManager(private val dataStore: DataStore<Preferences>) {

    companion object {
        val THEME_KEY = stringPreferencesKey("app_theme_v2")
        val LEGACY_THEME_KEY = stringPreferencesKey("app_theme")  // Fallback for old versions
        val LEGACY_ACCENT_KEY = stringPreferencesKey("app_accent")

        // Default theme on first launch
        const val DEFAULT_THEME = "BLACK_PANTHER"
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Flow: Current theme as GabomaThemeType
    // ─────────────────────────────────────────────────────────────────────────
    val themeFlow: Flow<GabomaThemeType> = dataStore.data.map { preferences ->
        val themeString = preferences[THEME_KEY] 
            ?: preferences[LEGACY_THEME_KEY]  // Fallback to legacy key
            ?: DEFAULT_THEME
        
        try {
            GabomaThemeType.valueOf(themeString)
        } catch (e: IllegalArgumentException) {
            // Fallback if theme string is invalid
            GabomaThemeType.BLACK_PANTHER
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Getter: Current theme (suspended)
    // ─────────────────────────────────────────────────────────────────────────
    suspend fun getCurrentTheme(): GabomaThemeType {
        val themeString = dataStore.data.map { it[THEME_KEY] ?: DEFAULT_THEME }.first()
        return try {
            GabomaThemeType.valueOf(themeString)
        } catch (e: IllegalArgumentException) {
            GabomaThemeType.BLACK_PANTHER
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Setter: Save theme selection
    // ─────────────────────────────────────────────────────────────────────────
    suspend fun setTheme(themeType: GabomaThemeType) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeType.name
        }
    }

    suspend fun setThemeByName(themeName: String) {
        try {
            val themeType = GabomaThemeType.valueOf(themeName)
            setTheme(themeType)
        } catch (e: IllegalArgumentException) {
            // Silently ignore invalid theme names
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Legacy method (backward compatibility)
    // ─────────────────────────────────────────────────────────────────────────
    suspend fun setAccent(accent: String) {
        dataStore.edit { preferences ->
            preferences[LEGACY_ACCENT_KEY] = accent
        }
    }
}

