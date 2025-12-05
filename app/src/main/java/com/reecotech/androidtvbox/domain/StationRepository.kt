package com.reecotech.androidtvbox.domain

import com.reecotech.androidtvbox.domain.model.StationData
import kotlinx.coroutines.flow.StateFlow

// A simplified sealed class for status, can be expanded later
sealed class ConnectionStatus {
    object Connecting : ConnectionStatus()
    object Connected : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
    object Disconnected : ConnectionStatus()
}

interface StationRepository {
    val stations: StateFlow<List<StationData>>
    val status: StateFlow<ConnectionStatus>

    fun startPolling()
    fun stopPolling()
}
