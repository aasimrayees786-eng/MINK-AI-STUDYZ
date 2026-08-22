package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AiModelCatalog
import com.example.data.model.AiModelOption
import com.example.data.model.VoiceAccent
import com.example.data.model.VoiceAccentCatalog
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings_prefs")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("pref_key_app_theme")
        private val KEY_AI_MODEL_ID = stringPreferencesKey("pref_key_ai_model_id")
        private val KEY_VOICE_ACCENT_ID = stringPreferencesKey("pref_key_voice_accent_id")
    }

    val themeModeFlow: Flow<AppThemeMode> = context.dataStore.data.map { preferences ->
        val savedThemeId = preferences[KEY_THEME_MODE] ?: AppThemeMode.SPACE.id
        AppThemeMode.entries.firstOrNull { it.id == savedThemeId } ?: AppThemeMode.SPACE
    }

    val aiModelFlow: Flow<AiModelOption> = context.dataStore.data.map { preferences ->
        val savedModelId = preferences[KEY_AI_MODEL_ID] ?: AiModelCatalog.DEFAULT_MODEL.id
        AiModelCatalog.findModelById(savedModelId)
    }

    val voiceAccentFlow: Flow<VoiceAccent> = context.dataStore.data.map { preferences ->
        val savedAccentId = preferences[KEY_VOICE_ACCENT_ID] ?: VoiceAccentCatalog.DEFAULT_ACCENT.id
        VoiceAccentCatalog.findAccentById(savedAccentId)
    }

    suspend fun saveThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = themeMode.id
        }
    }

    suspend fun saveAiModel(model: AiModelOption) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AI_MODEL_ID] = model.id
        }
    }

    suspend fun saveVoiceAccent(accent: VoiceAccent) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VOICE_ACCENT_ID] = accent.id
        }
    }
}
