package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.ui.graphics.Color
import com.reecotech.androidtvbox.domain.model.StationData
import com.reecotech.androidtvbox.ui.theme.*

/**
 * Legend item configuration for footer section
 */
data class LegendItem(
    val text: String,
    val color: Color
)

/**
 * Table parameter configuration
 */
data class TableParameter(
    val name: String,
    val valueExtractor: (StationData) -> Pair<String, Int>
)

/**
 * Creates list of table parameters with their extractors
 */
fun createTableParameters(): List<TableParameter> = listOf(
    TableParameter("Lượng mưa 24h\n(mm)") { Pair(it.rainfall24h, it.rainfallAlarmLevel) },
    TableParameter("Mực nước\n(m)") { Pair(it.waterLevel, it.waterLevelAlarmLevel) },
    TableParameter("Độ mặn tầng\nmặt (PPT)") { Pair(it.surfaceSalinity, it.surfaceSalinityAlarmLevel) },
    TableParameter("Độ mặn tầng\nđáy (PPT)") { Pair(it.bottomSalinity, it.bottomSalinityAlarmLevel) }
)

/**
 * Gets legend items for footer display
 */
fun getLegendItems(): List<LegendItem> = listOf(
    LegendItem("Cấp 0: Xanh lá cây - Bình thường", Level0Color),
    LegendItem("Cấp 1: Xanh dương - Nguy cơ thấp (theo dõi thời tiết)", Level1Color),
    LegendItem("Cấp 2: Vàng - Nguy cơ trung bình (theo dõi thường xuyền)", Level2Color),
    LegendItem("Cấp 3: Cam - Nguy cơ cao, cực đoan (phòng ngừa, chuẩn bị)", Level3Color),
    LegendItem("Cấp 4: Đỏ - Nguy cơ rất cao, cực đoan (cảnh giác, làm theo hướng dẫn)", Level4Color),
    LegendItem("Cấp 5: Tím - Thảm họa (tuân thủ chỉ đạo, sẵn sàng ứng phó)", Level5Color),
    LegendItem("Xám - Không có dữ liệu", NoDataColor)
)

/**
 * Determines background color based on cell value
 */
fun getValueBackgroundColor(value: String): Color {
    return DataCellBackground
}

/**
 * Determines text color based on alarm value
 * 
 * @param alarmValue The alarm level (0-5)
 * @return Color corresponding to the warning level
 */
fun getTextColorForValue(alarmValue: Int): Color {
    return when (alarmValue) {
        0 -> Level0Color
        1 -> Level1Color
        2 -> Level2Color
        3 -> Level3Color
        4 -> Level4Color
        5 -> Level5Color
        else -> Level0Color // Default to normal if unknown
    }
}

/**
 * Gets appropriate error message based on error type
 */
fun getErrorMessage(isWebSocketConnected: Boolean, hasJsonError: Boolean): String {
    return when {
        !isWebSocketConnected -> "MẤT KẾT NỐI INTERNET"
        hasJsonError -> "LỖI DỮ LIỆU"
        else -> "LỖI HỆ THỐNG"
    }
}
