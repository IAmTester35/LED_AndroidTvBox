package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reecotech.androidtvbox.R
import com.reecotech.androidtvbox.ui.theme.DarkBlue
import com.reecotech.androidtvbox.ui.theme.LightGreen
import com.reecotech.androidtvbox.ui.theme.TextColor
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState

@Composable
fun MainDataScreen(state: MainUiState.DisplayingData) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Spacer
            Spacer(modifier = Modifier.weight(0.1f))

            // Main Content
            if (state.data.isEmpty()) {
                Box(modifier = Modifier.weight(0.8f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Đang chờ dữ liệu...",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(horizontal = 48.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(state.data, key = { it.id }) { data ->
                        DisplayDataItem(data = data)
                    }
                }
            }


            // Footer
            Footer(modifier = Modifier.weight(0.15f))
        }

        // Overlay for warnings
        ConnectionStatusWarning(
            isFirebaseConnected = state.isFirebaseConnected,
            isWebSocketConnected = state.isWebSocketConnected,
            hasJsonError = state.hasJsonError,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )
    }
}

@Composable
fun DisplayDataItem(data: com.reecotech.androidtvbox.data.model.DisplayData) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(LightGreen.copy(alpha = 0.5f), LightGreen.copy(alpha = 0.2f))
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .border(2.dp, LightGreen, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = data.title,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = data.value,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = LightGreen,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = data.unit,
                fontSize = 28.sp,
                color = TextColor,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.qr_code),
            contentDescription = "QR Code",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "SCAN ME",
                color = TextColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Được phát triển bởi REECOTECH",
                color = TextColor.copy(alpha = 0.8f),
                fontSize = 16.sp,
            )
        }
    }
}


@Composable
fun ConnectionStatusWarning(
    isFirebaseConnected: Boolean,
    isWebSocketConnected: Boolean,
    hasJsonError: Boolean,
    modifier: Modifier = Modifier
) {
    val warningMessage = when {
        !isFirebaseConnected -> "Mất kết nối"
        !isWebSocketConnected -> "Dữ liệu của bạn có thể đã lỗi thời do mất kết nối internet"
        hasJsonError -> "Đã xảy ra lỗi trong phân tích data, dữ liệu có thể đã sai hoặc lỗi thời"
        else -> null
    }

    if (warningMessage != null) {
        Row(
            modifier = modifier.background(Color(0x99C51515), shape = RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = warningMessage,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
