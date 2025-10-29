package com.factory.display.domain.repository

import com.factory.display.data.model.DisplayData
import kotlinx.coroutines.flow.Flow

interface RealtimeDataRepository {
    fun startListening()
    fun stopListening()
    fun getDisplayData(): Flow<List<DisplayData>>
}
