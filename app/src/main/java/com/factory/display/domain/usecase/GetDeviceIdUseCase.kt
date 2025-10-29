package com.factory.display.domain.usecase

import com.factory.display.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetDeviceIdUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(): String {
        val currentId = deviceRepository.getDeviceId().first()
        return if (currentId.isNullOrBlank()) {
            deviceRepository.generateAndSaveDeviceId()
        } else {
            currentId
        }
    }
}
