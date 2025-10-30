package com.reecotech.androidtvbox.data.remote

import com.reecotech.androidtvbox.data.model.DeviceIdMessage
import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.WebSocketRepository
import com.reecotech.androidtvbox.util.Constants
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi
) : WebSocketRepository {
    private var webSocket: WebSocket? = null
    private var deviceId: String? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _messages = MutableStateFlow<String?>(null)
    override val messages: StateFlow<String?> = _messages

    private val _status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    override val status: StateFlow<ConnectionStatus> = _status

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _status.value = ConnectionStatus.Connected
            deviceId?.let { sendMessage(it) }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            _messages.value = text
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _status.value = ConnectionStatus.Error(t.message ?: "Unknown error")
            reconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _status.value = ConnectionStatus.Disconnected
            // Don't reconnect automatically if closed gracefully by client
            if (code != 1000) {
                reconnect()
            }
        }
    }

    override fun connect(deviceId: String) {
        if (status.value is ConnectionStatus.Connected) return
        this.deviceId = deviceId
        _status.value = ConnectionStatus.Connecting
        val request = Request.Builder()
            .url(Constants.WEBSOCKET_URL)
            .build()
        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
    }

    private fun sendMessage(deviceId: String) {
        val adapter = moshi.adapter(DeviceIdMessage::class.java)
        val json = adapter.toJson(DeviceIdMessage(deviceId))
        webSocket?.send(json)
    }

    private fun reconnect() {
        coroutineScope.launch {
            delay(10000) // Wait 10 seconds
            deviceId?.let { connect(it) }
        }
    }

    override fun disconnect() {
        webSocket?.close(1000, "Client disconnected")
    }
}
