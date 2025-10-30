package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reecotech.androidtvbox.data.model.DisplayData

@Composable
fun MainDataScreen(displayDataList: List<DisplayData>) {
    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        if (displayDataList.isEmpty()) {
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
                contentPadding = PaddingValues(16.dp)
            ) {
                items(displayDataList, key = { it.id }) { data ->
                    DisplayDataItem(data = data)
                    Divider(color = Color.DarkGray, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun DisplayDataItem(data: DisplayData) {
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
