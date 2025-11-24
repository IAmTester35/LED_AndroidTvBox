package com.reecotech.androidtvbox.domain.usecase

import com.reecotech.androidtvbox.data.model.DisplayData
import com.reecotech.androidtvbox.domain.model.StationData
import javax.inject.Inject

class TransformDisplayDataUseCase @Inject constructor() {
    
    // Station names in order (11 stations)
    private val stationNames = listOf(
        "Cái Mười",
        "Phú Đức",
        "Tân Thành",
        "Thị trấn Trà Ôn",
        "Tiểu Thiện",
        "Ngũ tự sống Trà Ngoà",
        "Nhà Đài",
        "Năng Âm",
        "Quới Ân",
        "Cái Ngang",
        "Hòa Hiệp"
    )
    
    operator fun invoke(displayDataList: List<DisplayData>): List<StationData> {
        // Create a map for quick lookup: station_name -> parameter_type -> value
        val dataMap = mutableMapOf<String, MutableMap<String, String>>()
        
        // Parse the DisplayData list
        displayDataList.forEach { data ->
            // Assuming ID format: "stationX_paramY" or station name + parameter identifier
            val parts = data.id.split("_")
            if (parts.size >= 2) {
                val stationKey = parts[0]
                val paramKey = parts[1]
                
                dataMap.getOrPut(stationKey) { mutableMapOf() }[paramKey] = data.value
            }
        }
        
        // Build StationData list for each station
        return stationNames.mapIndexed { index, stationName ->
            val stationKey = "station${index + 1}" // Assuming station1, station2, etc.
            val params = dataMap[stationKey] ?: emptyMap()
            
            StationData(
                stationName = stationName,
                rainfall24h = params["rainfall"] ?: params["rain"] ?: "--",
                waterLevel = params["water"] ?: params["level"] ?: "--",
                surfaceSalinity = params["surface"] ?: params["salinity_surface"] ?: "--",
                bottomSalinity = params["bottom"] ?: params["salinity_bottom"] ?: "--"
            )
        }
    }
}
