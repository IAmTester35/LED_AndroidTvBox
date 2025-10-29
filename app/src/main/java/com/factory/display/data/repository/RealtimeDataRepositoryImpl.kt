package com.factory.display.data.repository

import com.factory.display.data.model.DisplayData
import com.factory.display.data.remote.FactoryWebSocketListener
import com.factory.display.domain.repository.DeviceRepository
import com.factory.display.domain.repository.RealtimeDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeDataRepositoryImpl @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val webSocketListener: FactoryWebSocketListener
) : RealtimeDataRepository {

    private val _displayData = MutableSharedFlow<List<DisplayData>>()

    init {
        webSocketListener.onConnectionFailure = {
            retryConnection()
        }
        webSocketListener.onDataReceived = { data ->
            coroutineScope.launch {
                _displayData.emit(data)
            }
        }
    }

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun startListening() {
        coroutineScope.launch {
            val deviceId = deviceRepository.getDeviceId().first()
            val request = Request.Builder()
                .url(com.factory.display.util.Constants.WEB_SOCKET_URL)
                .build()
            webSocket = client.newWebSocket(request, webSocketListener)
            webSocket?.send("{\\"deviceId\\":\\"$deviceId\\"}")
        }
    }

    override fun stopListening() {
        webSocket?.close(1000, "Normal closure")
    }

    override fun getDisplayData(): Flow<List<DisplayData>> {
        return _displayData.asSharedFlow()
    }

    fun retryConnection() {
        coroutineScope.launch {
            delay(10000) // 10 second delay
            startListening()
        }
    }
}
