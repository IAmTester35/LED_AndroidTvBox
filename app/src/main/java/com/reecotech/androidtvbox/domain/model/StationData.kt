package com.reecotech.androidtvbox.domain.model

data class StationData(
    val stationName: String,
    val rainfall24h: String = "--",     // Lượng mưa 24h (mm)
    val waterLevel: String = "--",      // Mực nước (m)
    val surfaceSalinity: String = "--", // Độ mặn tầng mặt (PPT)
    val bottomSalinity: String = "--"   // Độ mặn tầng đáy (PPT)
)
