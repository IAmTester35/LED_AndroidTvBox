package com.reecotech.androidtvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.data.model.DisplayData
import com.reecotech.androidtvbox.data.remote.FirebaseRepository
import com.reecotech.androidtvbox.domain.WebSocketRepository
import com.reecotech.androidtvbox.domain.usecase.GetDeviceIDUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseDisplayDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainUiState {
    object Loading : MainUiState()
    data class WaitingForActivation(val deviceId: String, val statusMessage: String) : MainUiState()
    data class DisplayingData(val data: List<DisplayData>) : MainUiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDeviceIDUseCase: GetDeviceIDUseCase,
    private val firebaseRepository: FirebaseRepository,
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
            _uiState.value = MainUiState.WaitingForActivation(id, "Đang chờ kích hoạt từ hệ thống...")
            observeActivation(id)
        }
    }

    private fun observeActivation(deviceId: String) {
        viewModelScope.launch {
            firebaseRepository.listenForActivation(deviceId).collect { isActivated ->
                if (isActivated) {
                    _uiState.value = MainUiState.DisplayingData(emptyList())
                    connectWebSocket(deviceId)
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
                }
            }
        }
    }

    fun requestActivation() {
        deviceId?.let {
            firebaseRepository.requestActivation(it)
             _uiState.value = MainUiState.WaitingForActivation(it, "Đã gửi yêu cầu. Vui lòng chờ...")
        }
    }
}
