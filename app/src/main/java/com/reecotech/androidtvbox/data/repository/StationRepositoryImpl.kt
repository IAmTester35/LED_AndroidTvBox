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

    private var pollingJob: Job? = null
    private val pollingScope = CoroutineScope(Dispatchers.IO)

    override fun startPolling() {
        if (pollingJob?.isActive == true) return

        _status.value = ConnectionStatus.Connecting
        pollingJob = pollingScope.launch {
            while (isActive) {
                try {
                    val response = apiService.getLatestStationData()
                    if (response.success) {
                        val stationDataList = mapToStationData(response)
                        _stations.value = stationDataList
                        _status.value = ConnectionStatus.Connected
                    } else {
                        _status.value = ConnectionStatus.Error("API returned success=false")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching station data")
                    _status.value = ConnectionStatus.Error("${e.javaClass.simpleName}: ${e.message}")
                }

                delay(60 * 1000L) // 1 minute delay
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
            val params = station.parameters.associateBy { it.parameterName }
            
            StationData(
                stationName = station.stationName,
                rainfall24h = params["RAIN"]?.value?.toString() ?: "--",
                waterLevel = params["WATER_LEVEL"]?.value?.toString() ?: "--",
                surfaceSalinity = params["SALT_SURFACE"]?.value?.toString() ?: "--",
                bottomSalinity = params["SALT_BOTTOM"]?.value?.toString() ?: "--"
            )
        }
    }
}
