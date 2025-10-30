package com.reecotech.androidtvbox.data

import com.reecotech.androidtvbox.util.Constants
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

sealed class WebSocketStatus {
    object Connecting : WebSocketStatus()
    object Connected : WebSocketStatus()
    data class Error(val message: String) : WebSocketStatus()
    object Disconnected : WebSocketStatus()
}

@Singleton
class WebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi
) {
    private var webSocket: WebSocket? = null

    private val _messages = MutableStateFlow<String?>(null)
    val messages: StateFlow<String?> = _messages

    private val _status = MutableStateFlow<WebSocketStatus>(WebSocketStatus.Disconnected)
    val status: StateFlow<WebSocketStatus> = _status

    fun connect() {
        _status.value = WebSocketStatus.Connecting
        val request = Request.Builder()
            .url(Constants.WEBSOCKET_URL)
            .build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _status.value = WebSocketStatus.Connected
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                _messages.value = text
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _status.value = WebSocketStatus.Error(t.message ?: "Unknown error")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _status.value = WebSocketStatus.Disconnected
            }
        })
    }

    fun sendMessage(deviceId: String) {
        val adapter = moshi.adapter(DeviceIdMessage::class.java)
        val json = adapter.toJson(DeviceIdMessage(deviceId))
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnected")
    }
}
