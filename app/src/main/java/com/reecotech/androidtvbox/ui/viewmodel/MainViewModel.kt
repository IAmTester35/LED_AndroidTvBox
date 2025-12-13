
package com.reecotech.androidtvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.StationRepository
import com.reecotech.androidtvbox.domain.model.StationData
import com.reecotech.androidtvbox.domain.usecase.GetDeviceIDUseCase
import com.reecotech.androidtvbox.domain.usecase.GetMockStationDataUseCase
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
    private val stationRepository: StationRepository,
    private val getMockStationDataUseCase: GetMockStationDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Toggle this to switch between mock data and real WebSocket data
    private val useMockData = false

    private val _passwordHash = MutableStateFlow("")

    init {
        if (useMockData) {
            startMockDataFlow()
        } else {
            startDataFlow()
        }
    }

    fun updatePasswordHash(hash: String) {
        _passwordHash.value = hash
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
                    isConnected = true,
                    hasJsonError = false,
                    isLoading = false,
                    lastUpdateTime = currentTime,
                    passwordHash = _passwordHash.value
                )

                // Update every 5 seconds
                delay(5000)
            }
        }
    }

    private fun startDataFlow() {
        stationRepository.startPolling()

        viewModelScope.launch {
            combine(
                stationRepository.status,
                stationRepository.stations,
                stationRepository.lastAttemptTime,
                _passwordHash
            ) { status, stations, lastAttemptTime, passwordHash ->

                val isConnected = status is ConnectionStatus.Connected
                val hasError = status is ConnectionStatus.Error
                val errorMessage = if (status is ConnectionStatus.Error) status.message else null

                val retryCount = if (status is ConnectionStatus.Error) status.retryCount else 0

                // Use lastAttemptTime if available, otherwise current time (fallback)
                val timeToDisplay = if (lastAttemptTime > 0) Date(lastAttemptTime) else Date()
                val currentTime = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                    .format(timeToDisplay)

                MainUiState(
                    stations = stations,
                    isConnected = isConnected, // Reusing this field for connection status
                    hasJsonError = hasError,
                    isLoading = false,
                    lastUpdateTime = currentTime,
                    errorMessage = errorMessage,
                    retryCount = retryCount,
                    passwordHash = passwordHash
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
            stationRepository.stopPolling()
        }
    }
}
