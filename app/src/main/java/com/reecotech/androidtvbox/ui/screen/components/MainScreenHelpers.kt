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
/**
 * Gets detailed error information
 * @return Triple(Title, Description, ErrorCode)
 */
fun getErrorDetails(isConnected: Boolean, hasJsonError: Boolean, errorMessage: String?): Triple<String, String, String> {
    // Ưu tiên hiển thị lỗi cụ thể nếu có (status is Error)
    if (errorMessage != null) {
        return when {
            errorMessage.contains("UnknownHostException") -> Triple(
                "LỖI KẾT NỐI MẠNG",
                "Không thể tìm thấy máy chủ. Vui lòng kiểm tra đường truyền Internet hoặc DNS.",
                "E_DNS_LOOKUP: $errorMessage"
            )
            errorMessage.contains("ConnectException") -> Triple(
                "KHÔNG THỂ KẾT NỐI",
                "Máy chủ từ chối kết nối hoặc không phản hồi.",
                "E_CONNECTION_REFUSED: $errorMessage"
            )
            errorMessage.contains("timeout", ignoreCase = true) || errorMessage.contains("SocketTimeout", ignoreCase = true) -> Triple(
                "QUÁ THỜI GIAN CHỜ",
                "Phản hồi từ máy chủ quá lâu. Vui lòng kiểm tra lại đường truyền mạng.",
                "E_TIMEOUT: $errorMessage"
            )
            errorMessage.contains("HTTP 504") || errorMessage.contains("Gateway Time-out", ignoreCase = true) -> Triple(
                "LỖI GATEWAY TIMEOUT (504)",
                "Máy chủ không phản hồi kịp thời. Hệ thống có thể đang quá tải.",
                "E_HTTP_504: Gateway Time-out"
            )
            errorMessage.contains("HTTP 502") || errorMessage.contains("Bad Gateway", ignoreCase = true) -> Triple(
                "LỖI GATEWAY (502)",
                "Máy chủ gặp sự cố tạm thời (Bad Gateway). Đang thử lại...",
                "E_HTTP_502: Bad Gateway"
            )
            errorMessage.contains("HTTP 503") || errorMessage.contains("Service Unavailable", ignoreCase = true) -> Triple(
                "LỖI DỊCH VỤ (503)",
                "Máy chủ đang bảo trì hoặc quá tải. Đang thử lại...",
                "E_HTTP_503: Service Unavailable"
            )
            errorMessage.contains("HttpException") -> Triple(
                "LỖI MÁY CHỦ (HTTP)",
                "Máy chủ trả về mã lỗi HTTP.",
                "E_HTTP: $errorMessage"
            )
             errorMessage.contains("SerializationException") || errorMessage.contains("JsonDecodingException") || errorMessage.contains("Lỗi dữ liệu") -> Triple(
                "LỖI DỮ LIỆU",
                "Dữ liệu nhận được không đúng định dạng JSON (hoặc trang lỗi HTML).",
                "E_PARSING: $errorMessage"
            )
            errorMessage.contains("success=false") -> Triple(
                "LỖI API",
                "Máy chủ xử lý thất bại yêu cầu.",
                "E_API_LOGIC: $errorMessage"
            )
            else -> Triple(
                "LỖI KHÔNG XÁC ĐỊNH",
                "Đã xảy ra lỗi trong quá trình vận hành.",
                "E_UNKNOWN: $errorMessage"
            )
        }
    }

    // Trường hợp không có error message nhưng trạng thái là disconnect
    // (Thường là trạng thái khởi tạo hoặc khi stop polling)
    if (!isConnected) {
        return Triple(
            "MẤT KẾT NỐI",
            "Đang nỗ lực kết nối đến hệ thống...",
            "STATUS: DISCONNECTED / INITIALIZING"
        )
    }

    return Triple("", "", "")
}
