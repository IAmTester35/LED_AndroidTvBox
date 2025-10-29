package com.factory.display.domain.repository

import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDeviceId(): Flow<String?>
    suspend fun generateAndSaveDeviceId(): String
}
