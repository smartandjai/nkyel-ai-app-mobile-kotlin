package com.gabomagpt.mobile

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class GabomaSettingsStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("gabomagpt_settings.preferences_pb") }
    )

    private object Keys {
        val theme = stringPreferencesKey("theme")
        val writingStyle = stringPreferencesKey("writing_style")
        val fontScale = floatPreferencesKey("font_scale")
        val model = stringPreferencesKey("model")
        val invokeLoxo = booleanPreferencesKey("invoke_loxo")
        val radarLoxo = booleanPreferencesKey("radar_loxo")
        val modeOmbre = booleanPreferencesKey("mode_ombre")
        val coffre = booleanPreferencesKey("coffre")
        val pacte = booleanPreferencesKey("pacte")
        val profile = stringPreferencesKey("profile")
        val energy = intPreferencesKey("energy")
        val freeArtifactRemaining = intPreferencesKey("free_artifact_remaining")
    }

    val settingsFlow: Flow<GabomaSettings> = dataStore.data.map { prefs ->
        GabomaSettings(
            theme = GabomaThemePreset.entries.firstOrNull { it.id == prefs[Keys.theme] }
                ?: GabomaThemePreset.BLACK_PANTHER,
            writingStyle = WritingStyle.entries.firstOrNull { it.name == prefs[Keys.writingStyle] }
                ?: WritingStyle.CLASSIQUE,
            fontScale = prefs[Keys.fontScale] ?: 1.0f,
            model = ForceTier.entries.firstOrNull { it.name == prefs[Keys.model] }
                ?: ForceTier.BLACK_PANTHER,
            invokeLoxo = prefs[Keys.invokeLoxo] ?: true,
            radarLoxo = prefs[Keys.radarLoxo] ?: true,
            modeOmbre = prefs[Keys.modeOmbre] ?: false,
            coffreFortSouverain = prefs[Keys.coffre] ?: true,
            pactePolitiqueAccepted = prefs[Keys.pacte] ?: true,
            profileKind = UserProfileKind.entries.firstOrNull { it.name == prefs[Keys.profile] }
                ?: UserProfileKind.PRO
        )
    }

    val energyFlow: Flow<EnergyState> = dataStore.data.map { prefs ->
        val used = prefs[Keys.energy] ?: 35
        EnergyState(
            usedPercent = used,
            remainingToday = (100 - used).coerceAtLeast(0),
            freeArtifactRemaining = prefs[Keys.freeArtifactRemaining] ?: 1
        )
    }

    suspend fun updateSettings(block: (GabomaSettings) -> GabomaSettings) {
        val current = settingsFlow.first()
        val next = block(current)
        dataStore.edit { prefs ->
            prefs[Keys.theme] = next.theme.id
            prefs[Keys.writingStyle] = next.writingStyle.name
            prefs[Keys.fontScale] = next.fontScale
            prefs[Keys.model] = next.model.name
            prefs[Keys.invokeLoxo] = next.invokeLoxo
            prefs[Keys.radarLoxo] = next.radarLoxo
            prefs[Keys.modeOmbre] = next.modeOmbre
            prefs[Keys.coffre] = next.coffreFortSouverain
            prefs[Keys.pacte] = next.pactePolitiqueAccepted
            prefs[Keys.profile] = next.profileKind.name
        }
    }

    suspend fun useFreeArtifact() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.freeArtifactRemaining] ?: 1
            prefs[Keys.freeArtifactRemaining] = (current - 1).coerceAtLeast(0)
        }
    }

    suspend fun bumpEnergyUsage(delta: Int = 2) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.energy] ?: 35
            prefs[Keys.energy] = (current + delta).coerceAtMost(100)
        }
    }
}
