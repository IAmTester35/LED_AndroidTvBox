package com.reecotech.androidtvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.domain.DeviceRepository
import com.reecotech.androidtvbox.domain.WebSocketRepository
import com.reecotech.androidtvbox.domain.model.DeviceStatusState
import com.reecotech.androidtvbox.domain.usecase.GetDeviceIDUseCase
import com.reecotech.androidtvbox.domain.usecase.GetDeviceStatusUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseDisplayDataUseCase
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
    private val parseDisplayDataUseCase: ParseDisplayDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    private var deviceId: String? = null

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
                        _uiState.value = MainUiState.WaitingForActivation(deviceId, "Đang chờ kích hoạt từ hệ thống...")
                    }
                    DeviceStatusState.ACTIVATED -> {
                        connectWebSocket(deviceId)
                    }
                    DeviceStatusState.DISABLED -> {
                        webSocketRepository.disconnect()
                        _uiState.value = MainUiState.DeviceDisabled(description)
                    }
                }
            }
        }
    }

    private fun connectWebSocket(deviceId: String) {
        webSocketRepository.connect(deviceId)
        viewModelScope.launch {
            webSocketRepository.messages.collect { jsonString ->
                val data = parseDisplayDataUseCase(jsonString)
                if (data.isNotEmpty()) {
                    _uiState.value = MainUiState.DisplayingData(data)
                } else {
                     _uiState.value = MainUiState.DisplayingData(emptyList())
                }
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
