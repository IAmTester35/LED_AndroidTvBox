package com.reecotech.androidtvbox.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.reecotech.androidtvbox.domain.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DeviceRepository {

    private object PreferencesKeys {
        val DEVICE_ID = stringPreferencesKey("device_id")
    }

    override fun getDeviceId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DEVICE_ID]
        }
    }

    override suspend fun saveDeviceId(deviceId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEVICE_ID] = deviceId
        }
    }
}
