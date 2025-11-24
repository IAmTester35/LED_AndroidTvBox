package com.reecotech.androidtvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.WebSocketRepository
import com.reecotech.androidtvbox.domain.usecase.GetDeviceIDUseCase
import com.reecotech.androidtvbox.domain.usecase.GetMockStationDataUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseDisplayDataUseCase
import com.reecotech.androidtvbox.domain.usecase.ParseResult
import com.reecotech.androidtvbox.domain.usecase.TransformDisplayDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDeviceIDUseCase: GetDeviceIDUseCase,
    private val webSocketRepository: WebSocketRepository,
    private val parseDisplayDataUseCase: ParseDisplayDataUseCase,
    private val transformDisplayDataUseCase: TransformDisplayDataUseCase,
    private val getMockStationDataUseCase: GetMockStationDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Toggle this to switch between mock data and real WebSocket data
    private val useMockData = true

    init {
        if (useMockData) {
            startMockDataFlow()
        } else {
            viewModelScope.launch {
                val deviceId = getDeviceIDUseCase()
                startDataFlow(deviceId)
            }
        }
    }

    private fun startMockDataFlow() {
        viewModelScope.launch {
            while (true) {
                val currentTime = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                    .format(Date())

                val mockStations = getMockStationDataUseCase().map { station ->
                    // Randomly vary some values slightly to simulate real-time updates
                    station.copy(
                        rainfall24h = if (station.rainfall24h != "--") {
                            val value = station.rainfall24h.toDoubleOrNull() ?: 0.0
                            val variation = Random.nextDouble(-0.5, 0.5)
                            String.format("%.1f", (value + variation).coerceAtLeast(0.0))
                        } else station.rainfall24h,
                        waterLevel = if (station.waterLevel != "--") {
                            val value = station.waterLevel.toDoubleOrNull() ?: 0.0
                            val variation = Random.nextDouble(-0.2, 0.2)
                            String.format("%.1f", (value + variation).coerceAtLeast(0.0))
                        } else station.waterLevel
                    )
                }

                _uiState.value = MainUiState(
                    stations = mockStations,
                    isWebSocketConnected = true,
                    hasJsonError = false,
                    lastUpdateTime = currentTime
                )

                // Update every 5 seconds
                delay(5000)
            }
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
        if (!useMockData) {
            webSocketRepository.disconnect()
        }
    }
}
