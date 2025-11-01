package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState

@Composable
fun MainDataScreen(state: MainUiState.DisplayingData) {
    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Đang chờ dữ liệu...",
                        fontSize = 28.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 16.dp) // Add padding to avoid overlap
                ) {
                    items(state.data, key = { it.id }) { data ->
                        DisplayDataItem(data = data)
                        Divider(color = Color.DarkGray, thickness = 1.dp)
                    }
                }
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
            modifier = modifier.background(Color(0x80FFA500)), // Semi-transparent Orange
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = warningMessage,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun DisplayDataItem(data: com.reecotech.androidtvbox.data.model.DisplayData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = data.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = data.value,
                fontSize = 48.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Green,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = data.unit,
                fontSize = 28.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}
