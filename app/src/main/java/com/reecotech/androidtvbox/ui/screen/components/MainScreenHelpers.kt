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
    val valueExtractor: (StationData) -> String
)

/**
 * Creates list of table parameters with their extractors
 */
fun createTableParameters(): List<TableParameter> = listOf(
    TableParameter("Lượng mưa 24h\n(mm)") { it.rainfall24h },
    TableParameter("Mực nước\n(m)") { it.waterLevel },
    TableParameter("Độ mặn tầng\nmặt (PPT)") { it.surfaceSalinity },
    TableParameter("Độ mặn tầng\nđáy (PPT)") { it.bottomSalinity }
)

/**
 * Gets legend items for footer display
 */
fun getLegendItems(): List<LegendItem> = listOf(
    LegendItem("Cấp 0: Xanh lá cây - Bình thường", Level0Color),
    LegendItem("Cấp 1: Xanh dương nhạt - Nguy cơ thấp", Level1Color),
    LegendItem("Cấp 2: Vàng - Nguy cơ trung bình", Level2Color),
    LegendItem("Cấp 3: Cam - Nguy cơ cao", Level3Color),
    LegendItem("Cấp 4: Đỏ - Nguy cơ cao, cực đoan", Level4Color),
    LegendItem("Cấp 5: Tím - Nguy cơ đặc biệt cao", Level5Color),
    LegendItem("Xám - Không có dữ liệu", NoDataColor)
)

/**
 * Determines background color based on cell value
 */
fun getValueBackgroundColor(value: String): Color {
    return if (value == UiConstants.NO_DATA_PLACEHOLDER) {
        NullCellBackground
    } else {
        DataCellBackground
    }
}

/**
 * Determines text color based on value and warning thresholds
 * 
 * @param value The value to evaluate
 * @return Color corresponding to the warning level
 */
fun getTextColorForValue(value: String): Color {
    val numValue = value.toDoubleOrNull() 
        ?: return UiConstants.INVALID_TEXT_COLOR
    
    return when {
        numValue < UiConstants.THRESHOLD_LEVEL_1 -> Level0Color
        numValue < UiConstants.THRESHOLD_LEVEL_2 -> Level1Color
        numValue < UiConstants.THRESHOLD_LEVEL_3 -> Level2Color
        numValue < UiConstants.THRESHOLD_LEVEL_4 -> Level3Color
        numValue < UiConstants.THRESHOLD_LEVEL_5 -> Level4Color
        else -> Level5Color
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
