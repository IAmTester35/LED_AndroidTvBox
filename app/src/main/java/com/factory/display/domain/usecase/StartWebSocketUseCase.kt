package com.factory.display.domain.usecase

import com.factory.display.domain.repository.RealtimeDataRepository
import javax.inject.Inject

class StartWebSocketUseCase @Inject constructor(
    private val realtimeDataRepository: RealtimeDataRepository
) {
    operator fun invoke() {
        realtimeDataRepository.startListening()
    }
}
