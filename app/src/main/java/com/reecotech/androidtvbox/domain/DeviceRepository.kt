package com.reecotech.androidtvbox.domain

import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDeviceId(): Flow<String?>
    suspend fun saveDeviceId(deviceId: String)
}
