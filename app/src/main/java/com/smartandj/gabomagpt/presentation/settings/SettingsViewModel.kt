package com.smartandj.gabomagpt.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartandj.gabomagpt.presentation.theme.GabomaThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferencesManager: ThemePreferencesManager
) : ViewModel() {

    private val _currentTheme = MutableStateFlow<GabomaThemeType?>(null)
    val currentTheme: StateFlow<GabomaThemeType?> = _currentTheme.asStateFlow()

    private val _currentAccent = MutableStateFlow(AccentColor.default)
    val currentAccent: StateFlow<AccentColor> = _currentAccent.asStateFlow()

    init {
        viewModelScope.launch {
            themePreferencesManager.themeFlow.collect { theme ->
                _currentTheme.value = theme
            }
        }
    }

    fun setTheme(theme: GabomaThemeType) {
        viewModelScope.launch {
            themePreferencesManager.setTheme(theme)
        }
    }

    fun setAccent(accent: AccentColor) {
        _currentAccent.value = accent
    }
}
