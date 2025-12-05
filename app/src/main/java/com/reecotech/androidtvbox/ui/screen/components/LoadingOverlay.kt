package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Overlay displayed when data is loading
 */
@Composable
fun LoadingOverlay() {
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
                    UiConstants.HEADER_GRADIENT_END,
                    RoundedCornerShape(UiConstants.CORNER_RADIUS_MEDIUM)
                )
                .padding(UiConstants.PADDING_EXTRA_LARGE)
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(60.dp)
            )
            
            Spacer(modifier = Modifier.height(UiConstants.SPACING_LARGE))
            
            Text(
                text = "Đang tải dữ liệu...",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
