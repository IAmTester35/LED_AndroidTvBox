package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reecotech.androidtvbox.R
import com.reecotech.androidtvbox.domain.model.StationData
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState

@Composable
fun MainDataScreen(state: MainUiState) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF87CEEB))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            HeaderSection(state = state)
            
            // Table
            StationTable(stations = state.stations, modifier = Modifier.weight(1f))
            
            // Footer
            FooterSection(modifier = Modifier.wrapContentHeight())
        }
        
        // Disconnect Overlay
        if (!state.isWebSocketConnected || state.hasJsonError) {
            DisconnectOverlay(
                isWebSocketConnected = state.isWebSocketConnected,
                hasJsonError = state.hasJsonError
            )
        }
    }
}

@Composable
fun HeaderSection(state: MainUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF006699), Color(0xFF0099CC))
                )
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Government Logo (left side)
            Image(
                painter = painterResource(id = R.drawable.logo_government),
                contentDescription = "Government Logo",
                modifier = Modifier.size(70.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Titles (center)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SỞ NÔNG NGHIỆP VÀ MÔI TRƯỜNG TỈNH VĨNH LONG",
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "BẢNG TỔNG HỢP THÔNG TIN 11 TRẠM QUAN TRẮC KTTV",
                    color = Color.Yellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Last update time
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Thời gian cập nhật: ${state.lastUpdateTime.ifEmpty { "Đang tải..." }}",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun StationTable(stations: List<StationData>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Table Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF004D5C))
                .border(1.dp, Color.Black)
        ) {
            TableHeaderCell("THÔNG SỐ", Modifier.weight(1.2f))
            stations.forEach { station ->
                TableHeaderCell(station.stationName, Modifier.weight(1f))
            }
        }
        
        // Parameter rows
        val parameters = listOf(
            "Lượng mưa 24h\n(mm)" to { it: StationData -> it.rainfall24h },
            "Mực nước\n(m)" to { it: StationData -> it.waterLevel },
            "Độ mặn tầng\nmặt (PPT)" to { it: StationData -> it.surfaceSalinity },
            "Độ mặn tầng\nđáy (PPT)" to { it: StationData -> it.bottomSalinity }
        )
        
        parameters.forEach { (paramName, getValue) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
            ) {
                TableCell(paramName, Modifier.weight(1.2f), Color(0xFF808080))
                stations.forEach { station ->
                    val value = getValue(station)
                    val cellColor = getColorForValue(value)
                    TableCell(value, Modifier.weight(1f), cellColor)
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(50.dp)
            .border(1.dp, Color.White)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Yellow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier, backgroundColor: Color) {
    Box(
        modifier = modifier
            .height(50.dp)
            .background(backgroundColor)
            .border(1.dp, Color.Black)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

fun getColorForValue(value: String): Color {
    // Parse numeric value if possible
    val numValue = value.toDoubleOrNull() ?: return Color(0xFFE0E0E0) // Gray for "--" or invalid
    
    // Color coding based on level (this is simplified, adjust based on actual requirements)
    return when {
        numValue < 1.0 -> Color(0xFF00FF00)      // Green (Cấp 0)
        numValue < 5.0 -> Color(0xFF00FFFF)      // Cyan (Cấp 1)
        numValue < 10.0 -> Color(0xFFFFFF00)     // Yellow (Cấp 2)
        numValue < 20.0 -> Color(0xFFFFA500)     // Orange (Cấp 3)
        numValue < 50.0 -> Color(0xFFFF0000)     // Red (Cấp 4)
        else -> Color(0xFF800080)                 // Purple (Cấp 5)
    }
}

@Composable
fun FooterSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF003366))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Legend
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "*Chú giải:",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                LegendItem("Cấp 0: Xanh lá cây - Bình thường", Color(0xFF00FF00))
                Spacer(modifier = Modifier.width(8.dp))
                LegendItem("Cấp 4: Đỏ - Nguy cơ cao, cực đoạn", Color(0xFFFF0000))
            }
            Row {
                LegendItem("Cấp 1: Xám dương - Nguy cơ thấp", Color(0xFF00FFFF))
                Spacer(modifier = Modifier.width(8.dp))
                LegendItem("Cấp 5: Tím - Nguy cơ đặc biệt cao", Color(0xFF800080))
            }
            Row {
                LegendItem("Cấp 2: Vàng - Nguy cơ trung bình", Color(0xFFFFFF00))
                Spacer(modifier = Modifier.width(8.dp))
                LegendItem("Xám - Không có dữ liệu", Color(0xFFE0E0E0))
            }
            LegendItem("Cấp 3: Cam - Nguy cơ cao", Color(0xFFFFA500))
            
            // Contact info
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hỗ trợ kỹ thuật: 0901 880 386",
                color = Color.White,
                fontSize = 10.sp
            )
        }
        
        // QR Codes Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.qr_eec),
                contentDescription = "EEC QR",
                modifier = Modifier.size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.qr_linkedin),
                contentDescription = "LinkedIn QR",
                modifier = Modifier.size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.qr_youtube),
                contentDescription = "YouTube QR",
                modifier = Modifier.size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.qr_tiktok),
                contentDescription = "TikTok QR",
                modifier = Modifier.size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.qr_facebook2),
                contentDescription = "Facebook QR",
                modifier = Modifier.size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.qr_zalo),
                contentDescription = "Zalo QR",
                modifier = Modifier.size(60.dp)
            )
        }
        
        // REECO Logo
        Image(
            painter = painterResource(id = R.drawable.logo_reeco),
            contentDescription = "REECO Logo",
            modifier = Modifier.height(60.dp).width(120.dp)
        )
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 8.sp
        )
    }
}

@Composable
fun DisconnectOverlay(
    isWebSocketConnected: Boolean,
    hasJsonError: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)), // Semi-transparent black
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(Color(0xFFCC0000), RoundedCornerShape(16.dp))
                .padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val message = when {
                !isWebSocketConnected -> "MẤT KẾT NỐI INTERNET"
                hasJsonError -> "LỖI DỮ LIỆU"
                else -> "LỖI HỆ THỐNG"
            }
            
            Text(
                text = message,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Dữ liệu có thể đã lỗi thời",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
