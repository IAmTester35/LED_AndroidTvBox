package com.reecotech.androidtvbox.data.repository

import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.WebSocketRepository
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketRepositoryImpl @Inject constructor() : WebSocketRepository {

    private var socket: Socket? = null

    private val _messages = MutableStateFlow<String?>(null)
    override val messages = _messages.asStateFlow()

    private val _status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    override val status = _status.asStateFlow()

    override fun connect(deviceId: String) {
        if (socket?.isActive == true) {
            Timber.d("Socket is already active.")
            return
        }

        try {
            val opts = IO.Options().apply {
                reconnection = true
                query = "deviceId=$deviceId"
            }

            // Socket.IO uses http/https URLs
            socket = IO.socket(WEBSOCKET_URL, opts)

            socket?.on(Socket.EVENT_CONNECT) {
                _status.value = ConnectionStatus.Connected
                Timber.i("Socket connected!")
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                _status.value = ConnectionStatus.Disconnected
                Timber.w("Socket disconnected!")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val errorMessage = args.getOrNull(0)?.toString() ?: "Unknown connection error"
                _status.value = ConnectionStatus.Error(errorMessage)
                Timber.e("Socket connection error: $errorMessage")
            }

            socket?.on("message") { args ->
                val data = args.getOrNull(0)?.toString()
                if (data != null) {
                    _messages.value = data
                    Timber.d("Received message: $data")
                }
            }

            _status.value = ConnectionStatus.Connecting
            socket?.connect()

        } catch (e: URISyntaxException) {
            Timber.e(e, "Invalid WebSocket URL")
            _status.value = ConnectionStatus.Error("Invalid URL")
        }
    }

    override fun disconnect() {
        socket?.disconnect()
        socket?.off() // Remove all listeners
        socket = null
        _status.value = ConnectionStatus.Disconnected
        Timber.i("Socket explicitly disconnected and listeners removed.")
    }

    companion object {
        // As per memory, Socket.IO uses http/https URL.
        private const val WEBSOCKET_URL = "https://bd.vncscc.com:3003"
    }
}
