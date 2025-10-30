package com.reecotech.androidtvbox.domain.usecase

import com.reecotech.androidtvbox.domain.DeviceRepository
import com.reecotech.androidtvbox.domain.model.DeviceStatusState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDeviceStatusUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    operator fun invoke(deviceId: String): Flow<Pair<DeviceStatusState, String?>> {
        return deviceRepository.listenForDeviceStatus(deviceId).map { deviceStatus ->
            val state = when (deviceStatus.status) {
                "activate" -> DeviceStatusState.ACTIVATED
                "disable" -> DeviceStatusState.DISABLED
                else -> DeviceStatusState.PENDING
            }
            Pair(state, deviceStatus.description)
        }
    }
}
