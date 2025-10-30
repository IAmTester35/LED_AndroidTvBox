package com.reecotech.androidtvbox.domain.usecase

import com.reecotech.androidtvbox.domain.DeviceRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class GetDeviceIDUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(): String {
        val existingDeviceId = deviceRepository.getDeviceId().first()
        return if (existingDeviceId.isNullOrBlank()) {
            val newDeviceId = UUID.randomUUID().toString()
            deviceRepository.saveDeviceId(newDeviceId)
            newDeviceId
        } else {
            existingDeviceId
        }
    }
}
