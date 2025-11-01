package com.reecotech.androidtvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.DeviceRepository
import com.reecotech.androidtvbox.domain.FirebaseRepository
import com.reecotech.androidtvbox.domain.WebSocketRepository
import com.reecotech.androidtvbox.domain.model.DeviceStatusState
import com.reecotech.androidtvbox.domain.usecase.GetDeviceIDUseCase
import com.reecotech.androidtvbox.domain.usecase.GetDeviceStatusUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseDisplayDataUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDeviceIDUseCase: GetDeviceIDUseCase,
    private val deviceRepository: DeviceRepository,
    private val getDeviceStatusUseCase: GetDeviceStatusUseCase,
    private val webSocketRepository: WebSocketRepository,
    private val firebaseRepository: FirebaseRepository, // Injected
    private val parseDisplayDataUseCase: ParseDisplayDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var deviceId: String? = null

    // A trigger to restart the data flow when activation happens
    private val activationTrigger = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            val id = getDeviceIDUseCase()
            deviceId = id
            observeDeviceStatus(id)
        }
    }

    private fun observeDeviceStatus(deviceId: String) {
        viewModelScope.launch {
            getDeviceStatusUseCase(deviceId).collect { (status, description) ->
                when (status) {
                    DeviceStatusState.PENDING -> {
                        webSocketRepository.disconnect()
                        _uiState.value = MainUiState.WaitingForActivation(deviceId, "Đang chờ kích hoạt...")
                    }
                    DeviceStatusState.ACTIVATED -> {
                        // Trigger the data observation flow to start
                        if (!activationTrigger.value) {
                             activationTrigger.value = true
                             observeDataFlow(deviceId)
                        }
                    }
                    DeviceStatusState.DISABLED -> {
                        webSocketRepository.disconnect()
                        _uiState.value = MainUiState.DeviceDisabled(description)
                    }
                }
            }
        }
    }

    private fun observeDataFlow(deviceId: String) {
        webSocketRepository.connect(deviceId)

        viewModelScope.launch {
            combine(
                firebaseRepository.connectionStatus,
                webSocketRepository.status,
                webSocketRepository.messages
            ) { isFirebaseConnected, wsStatus, jsonMessage ->

                val isWebSocketConnected = wsStatus is ConnectionStatus.Connected
                val parseResult = parseDisplayDataUseCase(jsonMessage)
                val hasJsonError = parseResult is ParseResult.JsonError

                // Get current data if the state is already DisplayingData
                val currentData = (_uiState.value as? MainUiState.DisplayingData)?.data ?: emptyList()

                val newData = if (parseResult is ParseResult.Success) {
                    parseResult.data.ifEmpty { currentData } // Keep old data if new message is empty
                } else {
                    currentData // Keep old data on error
                }

                MainUiState.DisplayingData(
                    data = newData,
                    isFirebaseConnected = isFirebaseConnected,
                    isWebSocketConnected = isWebSocketConnected,
                    hasJsonError = hasJsonError
                )

            }.distinctUntilChanged()
             .collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun requestActivation() {
        deviceId?.let {
            deviceRepository.requestActivation(it)
            _uiState.value = MainUiState.WaitingForActivation(it, "Đã gửi yêu cầu. Vui lòng chờ...")
        }
    }
}
