package com.reecotech.androidtvbox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.data.DisplayData
import com.reecotech.androidtvbox.data.WebSocketClient
import com.reecotech.androidtvbox.domain.DeviceRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisplayViewModel @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val deviceRepository: DeviceRepository,
    private val moshi: Moshi
) : ViewModel() {

    private val _displayData = MutableStateFlow<List<DisplayData>>(emptyList())
    val displayData: StateFlow<List<DisplayData>> = _displayData

    init {
        connectToWebSocket()
        observeWebSocketMessages()
    }

    private fun connectToWebSocket() {
        viewModelScope.launch {
            val deviceId = deviceRepository.getDeviceId().first()
            if (deviceId != null) {
                webSocketClient.connect()
                webSocketClient.sendMessage(deviceId)
            }
        }
    }

    private fun observeWebSocketMessages() {
        viewModelScope.launch {
            webSocketClient.messages.collect { message ->
                if (message != null) {
                    val listType = Types.newParameterizedType(List::class.java, DisplayData::class.java)
                    val adapter = moshi.adapter<List<DisplayData>>(listType)
                    val data = adapter.fromJson(message)
                    _displayData.value = data ?: emptyList()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient.disconnect()
    }
}
