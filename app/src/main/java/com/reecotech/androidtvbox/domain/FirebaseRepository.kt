package com.reecotech.androidtvbox.domain

import com.reecotech.androidtvbox.data.model.DeviceStatus
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    val connectionStatus: Flow<Boolean>
    fun requestActivation(deviceId: String)
    fun listenForDeviceStatus(deviceId: String): Flow<DeviceStatus>
}
