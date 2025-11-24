package com.reecotech.androidtvbox.domain.usecase

import com.reecotech.androidtvbox.domain.model.StationData
import javax.inject.Inject

class GetMockStationDataUseCase @Inject constructor() {
    
    operator fun invoke(): List<StationData> {
        return listOf(
            StationData(
                stationName = "Cái Mười",
                rainfall24h = "0.8",
                waterLevel = "1.0",
                surfaceSalinity = "2.0",
                bottomSalinity = "0.6"
            ),
            StationData(
                stationName = "Phú Đức",
                rainfall24h = "0.8",
                waterLevel = "1.0",
                surfaceSalinity = "--",
                bottomSalinity = "1.6"
            ),
            StationData(
                stationName = "Tân Thành",
                rainfall24h = "0.8",
                waterLevel = "2.0",
                surfaceSalinity = "--",
                bottomSalinity = "0.7"
            ),
            StationData(
                stationName = "Thị trấn Trà Ôn",
                rainfall24h = "12.2",
                waterLevel = "0.6",
                surfaceSalinity = "2.1",
                bottomSalinity = "1.0"
            ),
            StationData(
                stationName = "Tiểu Thiện",
                rainfall24h = "53.8",
                waterLevel = "1.6",
                surfaceSalinity = "1.2",
                bottomSalinity = "0.7"
            ),
            StationData(
                stationName = "Ngũ tự sống Trà Ngoà",
                rainfall24h = "210",
                waterLevel = "--",
                surfaceSalinity = "1.1",
                bottomSalinity = "0.8"
            ),
            StationData(
                stationName = "Nhà Đài",
                rainfall24h = "65.2",
                waterLevel = "2.1",
                surfaceSalinity = "--",
                bottomSalinity = "--"
            ),
            StationData(
                stationName = "Năng Âm",
                rainfall24h = "20.8",
                waterLevel = "1.0",
                surfaceSalinity = "0.6",
                bottomSalinity = "0.2"
            ),
            StationData(
                stationName = "Quới Ân",
                rainfall24h = "10.2",
                waterLevel = "1.1",
                surfaceSalinity = "4.3",
                bottomSalinity = "1.8"
            ),
            StationData(
                stationName = "Cái Ngang",
                rainfall24h = "5.6",
                waterLevel = "1.0",
                surfaceSalinity = "1.1",
                bottomSalinity = "0.6"
            ),
            StationData(
                stationName = "Hòa Hiệp",
                rainfall24h = "0.8",
                waterLevel = "0.8",
                surfaceSalinity = "0.8",
                bottomSalinity = "0.2"
            )
        )
    }
}
