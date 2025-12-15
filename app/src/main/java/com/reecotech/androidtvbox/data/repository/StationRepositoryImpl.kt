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
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepositoryImpl @Inject constructor(
    private val apiService: StationApiService
) : StationRepository {

    private val _stations = MutableStateFlow<List<StationData>>(emptyList())
    override val stations = _stations.asStateFlow()

    private val _status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    override val status = _status.asStateFlow()

    private val _lastAttemptTime = MutableStateFlow<Long>(0L)
    override val lastAttemptTime = _lastAttemptTime.asStateFlow()

    private var pollingJob: Job? = null
    private val pollingScope = CoroutineScope(Dispatchers.IO)

    override fun startPolling() {
        if (pollingJob?.isActive == true) return

        _status.value = ConnectionStatus.Connecting
        pollingJob = pollingScope.launch {
            var consecutiveFailures = 0
            while (isActive) {
                var nextDelay = 30 * 1000L // Default 30s for success
                try {
                    _lastAttemptTime.value = System.currentTimeMillis()
                    // Add timeout to force failure if connection hangs (so retry count increments)
                    kotlinx.coroutines.withTimeout(10_000L) {
                        val response = apiService.getLatestStationData()
                        if (response.success) {
                            val stationDataList = mapToStationData(response)
                            _stations.value = stationDataList
                            _status.value = ConnectionStatus.Connected
                            consecutiveFailures = 0
                        } else {
                            throw Exception("API returned success=false")
                        }
                    }
                } catch (e: Exception) {
                    consecutiveFailures++
                    Timber.e(e, "Error fetching station data")
                    
                    val errorMessage = if (e.message == "API returned success=false") {
                         "API returned success=false"
                    } else {
                        "${e.javaClass.simpleName}: ${e.message}"
                    }
                    _status.value = ConnectionStatus.Error(errorMessage, consecutiveFailures)

                    nextDelay = when (consecutiveFailures) {
                        1 -> 2_000L
                        2 -> 4_000L
                        3 -> 8_000L
                        else -> 16_000L
                    }
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
            var waterLevel = "--"
            var waterLevelAlarm = 0
            var surfaceSalt = "--"
            var surfaceSaltAlarm = 0
            var bottomSalt = "--"
            var bottomSaltAlarm = 0

            for (param in station.parameters) {
                when (param.parameterName) {
                    "RAIN" -> {
                        rain = param.value?.toString() ?: "--"
                        rainAlarm = param.alarmLevel ?: 0
                    }
                    "WATER_LEVEL" -> {
                        waterLevel = param. value?.toString() ?: "--"
                        waterLevelAlarm = param.alarmLevel ?: 0
                    }
                    "SALT_SURFACE" -> {
                        surfaceSalt = param.value?.toString() ?: "--"
                        surfaceSaltAlarm = param.alarmLevel ?: 0
                    }
                    "SALT_BOTTOM" -> {
                        bottomSalt = param.value?.toString() ?: "--"
                        bottomSaltAlarm = param.alarmLevel ?: 0
                    }
                }
            }

            StationData(
                stationName = station.stationName,
                rainfall24h = rain,
                rainfallAlarmLevel = rainAlarm,
                waterLevel = waterLevel,
                waterLevelAlarmLevel = waterLevelAlarm,
                surfaceSalinity = surfaceSalt,
                surfaceSalinityAlarmLevel = surfaceSaltAlarm,
                bottomSalinity = bottomSalt,
                bottomSalinityAlarmLevel = bottomSaltAlarm
            )
        }
    }
}
