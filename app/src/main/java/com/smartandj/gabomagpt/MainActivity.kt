// MainActivity.kt
package com.smartandj.gabomagpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartandj.gabomagpt.presentation.navigation.NkyelNavHost
import com.smartandj.gabomagpt.presentation.settings.SettingsViewModel
import com.smartandj.gabomagpt.presentation.settings.ThemePreferencesManager
import com.smartandj.gabomagpt.presentation.theme.NkyelThemeDefinitions
import com.smartandj.gabomagpt.presentation.theme.ZionCoreTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themePreferencesManager: ThemePreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val currentTheme  by settingsViewModel.currentTheme.collectAsState()
            val currentAccent by settingsViewModel.currentAccent.collectAsState()

            // Load the selected 6-theme from preferences
            val selectedThemeType by themePreferencesManager.themeFlow.collectAsState(initial = null)
            val nkyelTheme = selectedThemeType?.let { NkyelThemeDefinitions.getTheme(it) }

            // Provide CompositionLocal + apply theme
            ZionCoreTheme(
                theme         = null,
                accent        = currentAccent,
                nkyelTheme    = nkyelTheme,
                content       = {
                    NkyelNavHost()
                }
            )
        }
    }
}

