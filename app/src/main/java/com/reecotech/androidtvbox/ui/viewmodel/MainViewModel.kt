package com.reecotech.androidtvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.WebSocketRepository
import com.reecotech.androidtvbox.domain.usecase.GetDeviceIDUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseDisplayDataUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseResult
import com.reecotech.androidtvbox.domain.usecase.TransformDisplayDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDeviceIDUseCase: GetDeviceIDUseCase,
    private val webSocketRepository: WebSocketRepository,
    private val parseDisplayDataUseCase: ParseDisplayDataUseCase,
    private val transformDisplayDataUseCase: TransformDisplayDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val deviceId = getDeviceIDUseCase()
            startDataFlow(deviceId)
        }
    }

    private fun startDataFlow(deviceId: String) {
        webSocketRepository.connect(deviceId)

        viewModelScope.launch {
            combine(
                webSocketRepository.status,
                webSocketRepository.messages
            ) { wsStatus, jsonMessage ->

                val isWebSocketConnected = wsStatus is ConnectionStatus.Connected
                val parseResult = parseDisplayDataUseCase(jsonMessage)
                val hasJsonError = parseResult is ParseResult.JsonError

                // Get current stations if the state already has data
                val currentStations = _uiState.value.stations

                val newStations = if (parseResult is ParseResult.Success) {
                    val displayDataList = parseResult.data
                    if (displayDataList.isNotEmpty()) {
                        transformDisplayDataUseCase(displayDataList)
                    } else {
                        currentStations // Keep old data if new message is empty
                    }
                } else {
                    currentStations // Keep old data on error
                }

                val currentTime = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                    .format(Date())

                MainUiState(
                    stations = newStations,
                    isWebSocketConnected = isWebSocketConnected,
                    hasJsonError = hasJsonError,
                    lastUpdateTime = currentTime
                )

            }.distinctUntilChanged()
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketRepository.disconnect()
    }
}
