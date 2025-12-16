
package com.reecotech.androidtvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.StationRepository
import com.reecotech.androidtvbox.domain.model.StationData
import com.reecotech.androidtvbox.domain.usecase.GetDeviceIDUseCase
import com.reecotech.androidtvbox.domain.usecase.GetMockStationDataUseCase
import com.reecotech.androidtvbox.data.repository.RemoteConfigRepository
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
    private val getMockStationDataUseCase: GetMockStationDataUseCase,
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Toggle this to switch between mock data and real WebSocket data
    private val useMockData = false

    private val _passwordHash = MutableStateFlow("")

    private var sleepWarningCancelled = false

    init {
        // Initialize polling interval to 60s
        stationRepository.setPollingInterval(60_000L)
        
        startSleepTimer()
        if (useMockData) {
            startMockDataFlow()
        } else {
            startDataFlow()
        }
    }

    fun onConfirmSleep() {
        _uiState.update { it.copy(isSleepMode = true, showSleepWarning = false) }
    }

    fun onCancelSleep() {
        sleepWarningCancelled = true
        _uiState.update { it.copy(showSleepWarning = false) }
    }

    private fun startSleepTimer() {
        viewModelScope.launch {
            var lastSetPollingInterval = 60_000L // Track last interval to avoid redundant calls

            while (true) {
                var delayTime = 60000L // Default to 60s check
                val currentState = _uiState.value
                val targetPollingInterval = if (currentState.isSleepMode) 3_600_000L else 60_000L
                
                if (targetPollingInterval != lastSetPollingInterval) {
                    stationRepository.setPollingInterval(targetPollingInterval)
                    lastSetPollingInterval = targetPollingInterval
                }

                // Priority: Handle Active Countdown
                if (currentState.showSleepWarning && !currentState.isSleepMode) {
                    if (currentState.sleepWarningSecondsLeft > 0) {
                        _uiState.update { it.copy(sleepWarningSecondsLeft = it.sleepWarningSecondsLeft - 1) }
                        delayTime = 1000L // Continue ticking every second
                    } else {
                        onConfirmSleep() // Timeout, enter sleep
                        delayTime = 1000L // Checks immediately to verify state
                    }
                } else {
                    // Periodic Check (Config & Time)
                    val sleepConfig = remoteConfigRepository.getSleepTimeConfig()
                    
                    // Update config in UI state
                     _uiState.update { it.copy(sleepTimeConfig = sleepConfig) }

                    if (sleepConfig != null) {
                        val cal = Calendar.getInstance()
                        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
                        val currentMinute = cal.get(Calendar.MINUTE)
                        val currentTotalMinutes = currentHour * 60 + currentMinute
                        
                        fun parseTimeToMinutes(timeStr: String): Int? {
                            return try {
                                val parts = timeStr.split(":")
                                if (parts.size == 2) {
                                    val h = parts[0].toInt()
                                    val m = parts[1].toInt()
                                    h * 60 + m
                                } else null
                            } catch (e: Exception) {
                                null
                            }
                        }

                        val frMinutes = parseTimeToMinutes(sleepConfig.fr)
                        val toMinutes = parseTimeToMinutes(sleepConfig.to)
                        
                        val isSleepTime = if (frMinutes != null && toMinutes != null) {
                            if (frMinutes == toMinutes) {
                                false
                            } else if (frMinutes < toMinutes) {
                                currentTotalMinutes in frMinutes until toMinutes
                            } else {
                                // Crossing midnight
                                currentTotalMinutes >= frMinutes || currentTotalMinutes < toMinutes
                            }
                        } else false
                        
                        if (isSleepTime) {
                             if (!currentState.isSleepMode && !sleepWarningCancelled) {
                                 // Trigger Warning
                                 _uiState.update { 
                                     it.copy(
                                         showSleepWarning = true, 
                                         sleepWarningSecondsLeft = 60
                                     ) 
                                 }
                                 delayTime = 1000L // Switch to 1s tick for countdown
                             }
                        } else {
                            // Not sleep time, reset
                            if (sleepWarningCancelled || currentState.isSleepMode) {
                                sleepWarningCancelled = false
                                _uiState.update { 
                                    it.copy(
                                        isSleepMode = false, 
                                        showSleepWarning = false
                                    ) 
                                }
                            }
                        }
                    }
                }
                
                delay(delayTime)
            }
        }
    }

    fun updatePasswordHash(hash: String) {
        _passwordHash.value = hash
    }

    fun updateSleepTimeConfig(config: RemoteConfigRepository.SleepTimeConfig?) {
        _uiState.update { it.copy(sleepTimeConfig = config) }
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

                val newState = MainUiState(
                    stations = mockStations,
                    isConnected = true,
                    hasJsonError = false,
                    isLoading = false,
                    lastUpdateTime = currentTime,
                    passwordHash = _passwordHash.value
                )

                _uiState.update { current ->
                    newState.copy(
                        sleepTimeConfig = current.sleepTimeConfig,
                        isSleepMode = current.isSleepMode,
                        showSleepWarning = current.showSleepWarning,
                        sleepWarningSecondsLeft = current.sleepWarningSecondsLeft
                    )
                }

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

                val retryCount = if (status is ConnectionStatus.Error) status.retryCount else 1

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
                    _uiState.update { current ->
                        newState.copy(
                            sleepTimeConfig = current.sleepTimeConfig,
                            isSleepMode = current.isSleepMode,
                            showSleepWarning = current.showSleepWarning,
                            sleepWarningSecondsLeft = current.sleepWarningSecondsLeft
                        )
                    }
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
