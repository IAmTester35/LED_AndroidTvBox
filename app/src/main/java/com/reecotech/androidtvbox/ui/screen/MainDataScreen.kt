package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reecotech.androidtvbox.R
import com.reecotech.androidtvbox.domain.model.StationData
import com.reecotech.androidtvbox.ui.theme.*
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState

// ============================================================================
// Constants
// ============================================================================

/**
 * UI dimension and styling constants for MainDataScreen
 */
private object UiConstants {
    // Spacing
    val PADDING_SMALL = 4.dp
    val PADDING_MEDIUM = 8.dp
    val PADDING_LARGE = 16.dp
    val PADDING_EXTRA_LARGE = 32.dp
    val SPACING_TINY = 2.dp
    val SPACING_SMALL = 4.dp
    val SPACING_MEDIUM = 8.dp
    val SPACING_LARGE = 16.dp
    
    // Sizes
    val LOGO_GOVERNMENT_SIZE = 70.dp
    val LOGO_REECO_WIDTH = 140.dp
    val LOGO_REECO_HEIGHT = 50.dp
    val QR_CODE_SIZE = 24.dp
    val WARNING_ICON_SIZE = 64.dp
    val LEGEND_INDICATOR_SIZE = 12.dp
    val TABLE_CELL_HEIGHT = 50.dp
    val BORDER_WIDTH = 2.dp
    val CORNER_RADIUS_SMALL = 2.dp
    val CORNER_RADIUS_MEDIUM = 16.dp
    
    // Font sizes
    val FONT_SIZE_TINY = 8.sp
    val FONT_SIZE_SMALL = 10.sp
    val FONT_SIZE_NORMAL = 12.sp
    val FONT_SIZE_MEDIUM = 14.sp
    val FONT_SIZE_LARGE = 18.sp
    val FONT_SIZE_EXTRA_LARGE = 20.sp
    val FONT_SIZE_HUGE = 32.sp
    val LINE_HEIGHT_SMALL = 12.sp
    
    // Colors
    val BACKGROUND_SKY_BLUE = Color(0xFF87CEEB)
    val OVERLAY_DARK = Color(0x99000000)
    val ERROR_RED = Color(0xFFCC0000)
    val FOOTER_BACKGROUND = Color(0xFF003366)
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

/**
 * Legend item configuration for footer section
 */
private data class LegendItem(
    val text: String,
    val color: Color
)

/**
 * Table parameter configuration
 */
private data class TableParameter(
    val name: String,
    val valueExtractor: (StationData) -> String
)

// ============================================================================
// Main Screen Composable
// ============================================================================

/**
 * Main data screen displaying station monitoring data
 * 
 * @param state Current UI state containing station data and connection status
 */
@Composable
fun MainDataScreen(state: MainUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UiConstants.BACKGROUND_SKY_BLUE)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(state = state)
            StationTable(
                stations = state.stations,
                modifier = Modifier.weight(1f)
            )
            FooterSection(modifier = Modifier.wrapContentHeight())
        }
        
        if (!state.isWebSocketConnected || state.hasJsonError) {
            DisconnectOverlay(
                isWebSocketConnected = state.isWebSocketConnected,
                hasJsonError = state.hasJsonError
            )
        }
    }
}

// ============================================================================
// Header Section
// ============================================================================

/**
 * Header section with title, logo, and last update time
 */
@Composable
fun HeaderSection(state: MainUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(createHeaderGradient())
            .padding(vertical = UiConstants.PADDING_MEDIUM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderContent()
        UpdateTimeBadge(lastUpdateTime = state.lastUpdateTime)
    }
}

@Composable
private fun HeaderContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_LARGE),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_government),
            contentDescription = "Government Logo",
            modifier = Modifier.size(UiConstants.LOGO_GOVERNMENT_SIZE)
        )
        Spacer(modifier = Modifier.width(UiConstants.SPACING_LARGE))
        
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = UiConstants.HEADER_TITLE,
                color = Color.Red,
                fontSize = UiConstants.FONT_SIZE_EXTRA_LARGE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = UiConstants.HEADER_SUBTITLE,
                color = Color.Yellow,
                fontSize = UiConstants.FONT_SIZE_LARGE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UpdateTimeBadge(lastUpdateTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = UiConstants.PADDING_MEDIUM, end = UiConstants.PADDING_LARGE),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = UiConstants.BADGE_BACKGROUND,
                    shape = RoundedCornerShape(UiConstants.CORNER_RADIUS_MEDIUM)
                )
                .padding(
                    horizontal = UiConstants.PADDING_LARGE,
                    vertical = UiConstants.PADDING_SMALL + UiConstants.SPACING_TINY
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = UiConstants.UPDATE_TIME_ICON,
                fontSize = UiConstants.FONT_SIZE_MEDIUM
            )
            Text(
                text = "${UiConstants.UPDATE_TIME_PREFIX}${lastUpdateTime.ifEmpty { UiConstants.LOADING_TEXT }}",
                color = UiConstants.BADGE_TEXT,
                fontSize = UiConstants.FONT_SIZE_NORMAL,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun createHeaderGradient() = Brush.linearGradient(
    colorStops = arrayOf(
        UiConstants.HEADER_GRADIENT_START_OFFSET to UiConstants.HEADER_GRADIENT_START,
        UiConstants.HEADER_GRADIENT_END_OFFSET to UiConstants.HEADER_GRADIENT_END
    ),
    start = Offset.Zero,
    end = Offset(UiConstants.HEADER_GRADIENT_END_X, UiConstants.HEADER_GRADIENT_END_Y)
)

// ============================================================================
// Table Section
// ============================================================================

/**
 * Station data table with headers and parameter rows
 */
@Composable
fun StationTable(stations: List<StationData>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(UiConstants.PADDING_MEDIUM)
    ) {
        TableHeaderRow(stations = stations)
        TableParameterRows(
            stations = stations,
            parameters = createTableParameters()
        )
    }
}

@Composable
private fun TableHeaderRow(stations: List<StationData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderBackground)
            .border(UiConstants.BORDER_WIDTH, Color.Transparent)
    ) {
        CornerHeaderCell(Modifier.weight(1.2f))
        stations.forEach { station ->
            TableHeaderCell(station.stationName, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TableParameterRows(
    stations: List<StationData>,
    parameters: List<TableParameter>
) {
    parameters.forEach { parameter ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(UiConstants.BORDER_WIDTH, Color.Transparent)
        ) {
            TableCell(
                text = parameter.name,
                modifier = Modifier.weight(1.2f),
                backgroundColor = HeaderBackground,
                textColor = HeaderText
            )
            stations.forEach { station ->
                val value = parameter.valueExtractor(station)
                val bgColor = getValueBackgroundColor(value)
                val txtColor = getTextColorForValue(value)
                TableCell(
                    text = value,
                    modifier = Modifier.weight(1f),
                    backgroundColor = bgColor,
                    textColor = txtColor
                )
            }
        }
    }
}

@Composable
fun CornerHeaderCell(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(UiConstants.TABLE_CELL_HEIGHT)
            .background(HeaderBackground)
            .border(UiConstants.BORDER_WIDTH, Color.Transparent)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = UiConstants.BORDER_WIDTH.toPx()
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = UiConstants.PADDING_SMALL, end = 0.dp)
        ) {
            Text(
                text = UiConstants.TABLE_HEADER_STATION,
                color = HeaderText,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 0.dp, start = UiConstants.PADDING_SMALL)
        ) {
            Text(
                text = UiConstants.TABLE_HEADER_PARAMETER,
                color = HeaderText,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(UiConstants.TABLE_CELL_HEIGHT)
            .background(HeaderBackground)
            .border(UiConstants.BORDER_WIDTH, Color.Transparent)
            .padding(UiConstants.PADDING_SMALL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = HeaderText,
            fontSize = UiConstants.FONT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = UiConstants.LINE_HEIGHT_SMALL
        )
    }
}

@Composable
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .height(UiConstants.TABLE_CELL_HEIGHT)
            .background(backgroundColor)
            .border(UiConstants.BORDER_WIDTH, Color.Transparent)
            .padding(UiConstants.PADDING_SMALL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = UiConstants.FONT_SIZE_MEDIUM,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================================
// Footer Section
// ============================================================================

/**
 * Footer section with legend, support info, and branding
 */
@Composable
fun FooterSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(UiConstants.FOOTER_BACKGROUND)
            .padding(
                horizontal = UiConstants.PADDING_LARGE,
                vertical = UiConstants.PADDING_MEDIUM
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendAndSupportSection(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(UiConstants.SPACING_LARGE))
        BrandingSection()
    }
}

@Composable
private fun LegendAndSupportSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = UiConstants.LEGEND_TITLE,
            color = Color.White,
            fontSize = UiConstants.FONT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = UiConstants.PADDING_SMALL)
        )
        
        LegendGrid()
        
        Spacer(modifier = Modifier.height(UiConstants.PADDING_MEDIUM))
        
        Text(
            text = UiConstants.SUPPORT_TEXT,
            color = Color.White,
            fontSize = UiConstants.FONT_SIZE_NORMAL,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LegendGrid() {
    val legendItems = getLegendItems()
    val columnCount = 2
    
    Row(modifier = Modifier.fillMaxWidth()) {
        for (columnIndex in 0 until columnCount) {
            Column(modifier = Modifier.weight(1f)) {
                legendItems
                    .chunked(legendItems.size / columnCount + legendItems.size % columnCount)
                    .getOrNull(columnIndex)
                    ?.forEach { item ->
                        LegendItemRow(item.text, item.color)
                        Spacer(modifier = Modifier.height(UiConstants.SPACING_TINY))
                    }
            }
            if (columnIndex < columnCount - 1) {
                Spacer(modifier = Modifier.width(UiConstants.PADDING_MEDIUM))
            }
        }
    }
}

@Composable
private fun LegendItemRow(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(UiConstants.LEGEND_INDICATOR_SIZE)
                .background(color, RoundedCornerShape(UiConstants.CORNER_RADIUS_SMALL))
        )
        Spacer(modifier = Modifier.width(UiConstants.PADDING_SMALL))
        Text(
            text = text,
            color = Color.White,
            fontSize = UiConstants.FONT_SIZE_TINY
        )
    }
}

@Composable
private fun BrandingSection() {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_reeco),
            contentDescription = "REECO Logo",
            modifier = Modifier
                .width(UiConstants.LOGO_REECO_WIDTH)
                .height(UiConstants.LOGO_REECO_HEIGHT)
        )
        
        Spacer(modifier = Modifier.height(UiConstants.PADDING_SMALL))
        
        QRCodeRow()
    }
}

@Composable
private fun QRCodeRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(UiConstants.PADDING_SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            R.drawable.qr_eec,
            R.drawable.qr_linkedin,
            R.drawable.qr_youtube,
            R.drawable.qr_tiktok,
            R.drawable.qr_facebook2,
            R.drawable.qr_zalo
        ).forEach { qrDrawable ->
            Image(
                painter = painterResource(id = qrDrawable),
                contentDescription = "QR Code",
                modifier = Modifier.size(UiConstants.QR_CODE_SIZE)
            )
        }
    }
}

// ============================================================================
// Overlay Components
// ============================================================================

/**
 * Overlay displayed when connection is lost or data error occurs
 */
@Composable
fun DisconnectOverlay(
    isWebSocketConnected: Boolean,
    hasJsonError: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UiConstants.OVERLAY_DARK),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    UiConstants.ERROR_RED,
                    RoundedCornerShape(UiConstants.CORNER_RADIUS_MEDIUM)
                )
                .padding(UiConstants.PADDING_EXTRA_LARGE)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.White,
                modifier = Modifier.size(UiConstants.WARNING_ICON_SIZE)
            )
            
            Spacer(modifier = Modifier.height(UiConstants.SPACING_LARGE))
            
            Text(
                text = getErrorMessage(isWebSocketConnected, hasJsonError),
                color = Color.White,
                fontSize = UiConstants.FONT_SIZE_HUGE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(UiConstants.PADDING_MEDIUM))
            
            Text(
                text = "Dữ liệu có thể đã lỗi thời",
                color = Color.White,
                fontSize = UiConstants.FONT_SIZE_LARGE,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================================
// Helper Functions
// ============================================================================

/**
 * Creates list of table parameters with their extractors
 */
private fun createTableParameters(): List<TableParameter> = listOf(
    TableParameter("Lượng mưa 24h\n(mm)") { it.rainfall24h },
    TableParameter("Mực nước\n(m)") { it.waterLevel },
    TableParameter("Độ mặn tầng\nmặt (PPT)") { it.surfaceSalinity },
    TableParameter("Độ mặn tầng\nđáy (PPT)") { it.bottomSalinity }
)

/**
 * Gets legend items for footer display
 */
private fun getLegendItems(): List<LegendItem> = listOf(
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
private fun getValueBackgroundColor(value: String): Color {
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
private fun getErrorMessage(isWebSocketConnected: Boolean, hasJsonError: Boolean): String {
    return when {
        !isWebSocketConnected -> "MẤT KẾT NỐI INTERNET"
        hasJsonError -> "LỖI DỮ LIỆU"
        else -> "LỖI HỆ THỐNG"
    }
}
