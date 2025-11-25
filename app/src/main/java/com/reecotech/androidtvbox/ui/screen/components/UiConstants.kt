package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * UI dimension and styling constants for MainDataScreen
 */
object UiConstants {
    // Spacing
    val PADDING_SMALL = 4.dp
    val PADDING_MEDIUM = 8.dp
    val PADDING_LARGE = 16.dp
    val PADDING_EXTRA_LARGE = 32.dp
    val SPACING_TINY = 2.dp
    val SPACING_SMALL = 4.dp
    val SPACING_MEDIUM = 8.dp
    val SPACING_LARGE = 12.dp
    
    // Sizes
    val LOGO_GOVERNMENT_SIZE = 70.dp
    val LOGO_REECO_WIDTH = 120.dp
    val LOGO_REECO_HEIGHT = 40.dp
    val QR_CODE_SIZE = 22.dp
    val QR_CODE_LARGE_SIZE = 80.dp
    val WARNING_ICON_SIZE = 64.dp
    val LEGEND_INDICATOR_SIZE = 8.dp
    val TABLE_CELL_HEIGHT = 45.dp
    val BORDER_WIDTH = 2.dp
    val GRID_BORDER_COLOR = Color.Transparent
    val CORNER_RADIUS_SMALL = 2.dp
    val CORNER_RADIUS_UPDATE_BADGE = 10.dp
    val CORNER_RADIUS_MEDIUM = 16.dp
    
    // Font sizes
    val FONT_SIZE_TINY = 7.sp
    val FONT_SIZE_SMALL = 8.sp
    val FONT_SIZE_NORMAL = 12.sp
    val FONT_SIZE_MEDIUM = 14.sp
    val FONT_SIZE_LARGE = 18.sp
    val FONT_SIZE_HEADER_TITLE = 26.sp
    val FONT_SIZE_HEADER_SUBTITLE = 22.sp
    val FONT_SIZE_HUGE = 32.sp
    val LINE_HEIGHT_SMALL = 12.sp
    
    // Colors
    val BACKGROUND_SKY_BLUE = Color(0xFF87CEEB)
    val OVERLAY_DARK = Color(0x99000000)
    val ERROR_RED = Color(0xFFCC0000)
    val FOOTER_BACKGROUND = Color.Transparent
    val BADGE_BACKGROUND = Color(0xE6FFFFFF)
    val BADGE_TEXT = Color(0xFF006064)
    val INVALID_TEXT_COLOR = Color(0xFF666666)
    
    // Header Gradient
    val HEADER_GRADIENT_START = Color(0xFF85DFFF)
    val HEADER_GRADIENT_END = Color(0xFF009CFF)
    val HEADER_GRADIENT_START_OFFSET = 0.5871f
    val HEADER_GRADIENT_END_OFFSET = 1.0053f
    val HEADER_GRADIENT_END_X = 1000f
    val HEADER_GRADIENT_END_Y = 20f
    val HEADER_OPACITY = 0.6f
    
    // Body Gradient (Radial)
    val BODY_GRADIENT_CENTER_X = 0.5003f
    val BODY_GRADIENT_CENTER_Y = 0.2602f
    val BODY_GRADIENT_RADIUS = 0.7693f
    val BODY_GRADIENT_COLOR_1 = Color(0xFF01D7FD)
    val BODY_GRADIENT_COLOR_2 = Color(0xFF114656)
    val BODY_GRADIENT_COLOR_3 = Color(0xFF002C5C)
    val BODY_GRADIENT_STOP_1 = 0.0f
    val BODY_GRADIENT_STOP_2 = 0.6587f
    val BODY_GRADIENT_STOP_3 = 1.0f
    
    // Text
    const val HEADER_TITLE = "SỞ NÔNG NGHIỆP VÀ MÔI TRƯỜNG TỈNH VĨNH LONG"
    const val HEADER_SUBTITLE = "BẢNG TỔNG HỢP THÔNG TIN 11 TRẠM QUAN TRẮC KTTV"
    const val SUPPORT_TEXT = "Hỗ trợ kỹ thuật: 0901 880 386"
    const val LEGEND_TITLE = "*Chú giải:"
    const val TABLE_HEADER_STATION = "TRẠM"
    const val TABLE_HEADER_PARAMETER = "THÔNG SỐ"
    const val LOADING_TEXT = "Đang tải..."
    const val UPDATE_TIME_PREFIX = "Thời gian cập nhật: "
    const val UPDATE_TIME_ICON = "⏰ "
    const val NO_DATA_PLACEHOLDER = "--"
    
    // Warning thresholds
    const val THRESHOLD_LEVEL_1 = 1.0
    const val THRESHOLD_LEVEL_2 = 5.0
    const val THRESHOLD_LEVEL_3 = 10.0
    const val THRESHOLD_LEVEL_4 = 20.0
    const val THRESHOLD_LEVEL_5 = 50.0
}
