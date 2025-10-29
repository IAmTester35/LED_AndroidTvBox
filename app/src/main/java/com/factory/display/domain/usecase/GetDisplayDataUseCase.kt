package com.factory.display.domain.usecase

import com.factory.display.data.model.DisplayData
import com.factory.display.domain.repository.RealtimeDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDisplayDataUseCase @Inject constructor(
    private val realtimeDataRepository: RealtimeDataRepository
) {
    operator fun invoke(): Flow<List<DisplayData>> {
        return realtimeDataRepository.getDisplayData()
    }
}
