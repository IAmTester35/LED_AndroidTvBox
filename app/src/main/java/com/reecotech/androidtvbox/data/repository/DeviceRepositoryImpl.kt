package com.reecotech.androidtvbox.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.reecotech.androidtvbox.data.model.DeviceStatus
import com.reecotech.androidtvbox.domain.DeviceRepository
import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.IOException
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DeviceRepository {

    private object PreferencesKeys {
        val DEVICE_ID = stringPreferencesKey("device_id")
    }

    override fun getDeviceId(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                // Catch both IOException and CorruptionException
                if (exception is IOException || exception is CorruptionException) {
                    Timber.e(exception, "Error reading DataStore, returning empty preferences")
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.DEVICE_ID]
            }
    }

    override suspend fun saveDeviceId(deviceId: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.DEVICE_ID] = deviceId
            }
        } catch (e: Exception) {
            // Catch any exception during save to prevent crash
            Timber.e(e, "Error saving to DataStore")
        }
    }

    override fun requestActivation(deviceId: String) {
        // No-op
    }

    override fun listenForDeviceStatus(deviceId: String): Flow<DeviceStatus> {
        return flowOf(DeviceStatus(status = "activate"))
    }
}
