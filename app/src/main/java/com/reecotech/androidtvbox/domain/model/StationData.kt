package com.reecotech.androidtvbox.domain.model

data class StationData(
    val stationName: String,
    val rainfall24h: String = "--",     // Lượng mưa 24h (mm)
    val rainfallAlarmLevel: Int = 0,
    val waterLevel: String = "--",      // Mực nước (m)
    val waterLevelAlarmLevel: Int = 0,
    val surfaceSalinity: String = "--", // Độ mặn tầng mặt (PPT)
    val surfaceSalinityAlarmLevel: Int = 0,
    val bottomSalinity: String = "--",   // Độ mặn tầng đáy (PPT)
    val bottomSalinityAlarmLevel: Int = 0
)
