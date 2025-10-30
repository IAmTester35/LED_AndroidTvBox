package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reecotech.androidtvbox.ui.theme.AndroidTVBoxTheme

@Composable
fun DeviceDisabledScreen(
    reason: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "THIẾT BỊ ĐÃ BỊ VÔ HIỆU HÓA",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 48.sp,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        if (!reason.isNullOrBlank()) {
            Text(
                text = "Lý do: $reason",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp,
                    color = Color.LightGray
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = "Vui lòng liên hệ với nhà cung cấp để được hỗ trợ.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 24.sp,
                color = Color.LightGray
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceDisabledScreenPreview() {
    AndroidTVBoxTheme {
        DeviceDisabledScreen(reason = "Hết hạn thanh toán.")
    }
}
