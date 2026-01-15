package com.fieldreportpro.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

data class SettingsState(
    val offlineModeSimulated: Boolean,
    val autoSyncWifi: Boolean,
    val compressPhotos: Boolean,
    val lastSyncAt: Long
)

class SettingsDataStore(private val context: Context) {
    private object Keys {
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode_simulated")
        val AUTO_SYNC_WIFI = booleanPreferencesKey("auto_sync_wifi")
        val COMPRESS_PHOTOS = booleanPreferencesKey("compress_photos")
        val LAST_SYNC_AT = longPreferencesKey("last_sync_at")
    }

    val settings: Flow<SettingsState> = context.settingsDataStore.data.map { prefs ->
        SettingsState(
            offlineModeSimulated = prefs[Keys.OFFLINE_MODE] ?: false,
            autoSyncWifi = prefs[Keys.AUTO_SYNC_WIFI] ?: true,
            compressPhotos = prefs[Keys.COMPRESS_PHOTOS] ?: true,
            lastSyncAt = prefs[Keys.LAST_SYNC_AT] ?: 0L
        )
    }

    suspend fun setOfflineModeSimulated(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.OFFLINE_MODE] = enabled
        }
    }

    suspend fun setAutoSyncWifi(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.AUTO_SYNC_WIFI] = enabled
        }
    }

    suspend fun setCompressPhotos(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.COMPRESS_PHOTOS] = enabled
        }
    }

    suspend fun setLastSyncAt(value: Long) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.LAST_SYNC_AT] = value
        }
    }
}
