package com.reecotech.androidtvbox.data.repository

import com.reecotech.androidtvbox.data.model.StationResponse
import com.reecotech.androidtvbox.data.remote.StationApiService
import com.reecotech.androidtvbox.domain.ConnectionStatus
import com.reecotech.androidtvbox.domain.StationRepository
import com.reecotech.androidtvbox.domain.model.StationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepositoryImpl @Inject constructor(
    private val apiService: StationApiService,
    private val apiLogger: com.reecotech.androidtvbox.util.ApiLogger
) : StationRepository {

    private val _stations = MutableStateFlow<List<StationData>>(emptyList())
    override val stations = _stations.asStateFlow()

    private val _status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    override val status = _status.asStateFlow()

    private val _lastAttemptTime = MutableStateFlow<Long>(0L)
    override val lastAttemptTime = _lastAttemptTime.asStateFlow()

    private var pollingJob: Job? = null
    private val pollingScope = CoroutineScope(Dispatchers.IO)
    
    // Default 180 seconds (optimized to reduce server load)
    private var currentPollingInterval = 180_000L

    override fun setPollingInterval(intervalMs: Long) {
        if (currentPollingInterval == intervalMs) return
        
        Timber.d("Updating polling interval: $intervalMs ms")
        currentPollingInterval = intervalMs
        
        // If currently polling, restart to apply new interval immediately
        if (pollingJob?.isActive == true) {
            stopPolling()
            startPolling()
        }
    }

    override fun startPolling() {
        if (pollingJob?.isActive == true) return

        _status.value = ConnectionStatus.Connecting
        pollingJob = pollingScope.launch {
            var consecutiveFailures = 0
            while (isActive) {
                var nextDelay = currentPollingInterval // Use dynamic interval
                val startTime = System.currentTimeMillis()
                var result = "Unknown"
                var capturedResponse: String? = null

                try {
                    _lastAttemptTime.value = startTime
                    // Add timeout to force failure if connection hangs (so retry count increments)
                    kotlinx.coroutines.withTimeout(10_000L) {
                        val response = apiService.getLatestStationData()
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.success) {
                                // Capture response as JSON for logging
                                capturedResponse = try {
                                    kotlinx.serialization.json.Json.encodeToString(body)
                                } catch (e: Exception) {
                                    body.toString()
                                }
                                
                                val stationDataList = mapToStationData(body)
                                _stations.value = stationDataList
                                _status.value = ConnectionStatus.Connected
                                consecutiveFailures = 0
                                result = "Success"
                            } else {
                                result = "Error: API returned success=false"
                                capturedResponse = body?.toString() ?: response.errorBody()?.string()
                                throw Exception("API returned success=false")
                            }
                        } else {
                            // Handle HTTP errors explicitly (502, 503, 404, etc.)
                            result = "Error: HTTP ${response.code()} ${response.message()}"
                            capturedResponse = response.errorBody()?.string()
                            throw Exception("HTTP ${response.code()} ${response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    // Rethrow CancellationException (unless it's a Timeout, which we want to retry)
                    if (e is kotlinx.coroutines.CancellationException && e !is kotlinx.coroutines.TimeoutCancellationException) {
                        throw e
                    }

                    consecutiveFailures++
                    Timber.e(e, "Error fetching station data (Attempt $consecutiveFailures)")
                    
                    val errorMessage = if (e.message == "API returned success=false") {
                         "API returned success=false"
                    } else if (e is kotlinx.serialization.SerializationException) {
                        "Lỗi dữ liệu: API trả về không đúng định dạng (có thể là HTML lỗi 502/503)"
                    } else if (e is kotlinx.coroutines.TimeoutCancellationException) {
                        "Quá thời gian chờ (10s)"
                    } else {
                        "${e.javaClass.simpleName}: ${e.message}"
                    }
                    
                    if (result == "Unknown") {
                        result = "Error: $errorMessage"
                    }
                    
                    _status.value = ConnectionStatus.Error(errorMessage, consecutiveFailures)

                    nextDelay = when (consecutiveFailures) {
                        1 -> 10_000L
                        2 -> 15_000L
                        else -> 30_000L
                    }
                } finally {
                    val endTime = System.currentTimeMillis()
                    apiLogger.logApiCall(startTime, endTime, result, capturedResponse)
                }

                delay(nextDelay)
            }
        }
    }

    override fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
        _status.value = ConnectionStatus.Disconnected
    }

    private fun mapToStationData(response: StationResponse): List<StationData> {
        return response.data.map { station ->
            // Optimize: Iterate directly instead of creating intermediate Maps (which causes memory churn)
            var rain = "--"
            var rainAlarm = 0
            var rainEnable = true
            var waterLevel = "--"
            var waterLevelAlarm = 0
            var waterLevelEnable = true
            var surfaceSalt = "--"
            var surfaceSaltAlarm = 0
            var surfaceSaltEnable = true
            var bottomSalt = "--"
            var bottomSaltAlarm = 0
            var bottomSaltEnable = true

            for (param in station.parameters) {
                when (param.parameterName) {
                    "RAIN" -> {
                        rain = param.value?.toString() ?: "--"
                        rainAlarm = param.alarmLevel ?: 0
                        rainEnable = param.isEnable ?: true
                    }
                    "WATER_LEVEL" -> {
                        waterLevel = param. value?.toString() ?: "--"
                        waterLevelAlarm = param.alarmLevel ?: 0
                        waterLevelEnable = param.isEnable ?: true
                    }
                    "SALT_SURFACE" -> {
                        surfaceSalt = param.value?.toString() ?: "--"
                        surfaceSaltAlarm = param.alarmLevel ?: 0
                        surfaceSaltEnable = param.isEnable ?: true
                    }
                    "SALT_BOTTOM" -> {
                        bottomSalt = param.value?.toString() ?: "--"
                        bottomSaltAlarm = param.alarmLevel ?: 0
                        bottomSaltEnable = param.isEnable ?: true
                    }
                }
            }

            StationData(
                stationName = station.stationName,
                rainfall24h = rain,
                rainfallAlarmLevel = rainAlarm,
                rainfallEnable = rainEnable,
                waterLevel = waterLevel,
                waterLevelAlarmLevel = waterLevelAlarm,
                waterLevelEnable = waterLevelEnable,
                surfaceSalinity = surfaceSalt,
                surfaceSalinityAlarmLevel = surfaceSaltAlarm,
                surfaceSalinityEnable = surfaceSaltEnable,
                bottomSalinity = bottomSalt,
                bottomSalinityAlarmLevel = bottomSaltAlarm,
                bottomSalinityEnable = bottomSaltEnable,
                isOnline = station.connectionStatus == "online"
            )
        }
    }
}
