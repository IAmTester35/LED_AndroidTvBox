package com.reecotech.androidtvbox.domain

import kotlinx.coroutines.flow.StateFlow

// A simplified sealed class for status, can be expanded later
sealed class ConnectionStatus {
    object Connecting : ConnectionStatus()
    object Connected : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
    object Disconnected : ConnectionStatus()
}

interface WebSocketRepository {
    val messages: StateFlow<String?>
    val status: StateFlow<ConnectionStatus>

    fun connect(deviceId: String)
    fun disconnect()
}
