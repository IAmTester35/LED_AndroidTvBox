package com.factory.display.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.factory.display.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val context: Context
) : DeviceRepository {

    private val deviceIdKey = stringPreferencesKey("device_id")

    override fun getDeviceId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[deviceIdKey]
        }
    }

    override suspend fun generateAndSaveDeviceId(): String {
        val newId = UUID.randomUUID().toString()
        context.dataStore.edit { settings ->
            settings[deviceIdKey] = newId
        }
        return newId
    }
}
