package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
/**
 * Overlay displayed when connection is lost or data error occurs
 */
@Composable
fun DisconnectOverlay(
    isConnected: Boolean,
    hasJsonError: Boolean,
    errorMessage: String? = null,
    retryCount: Int = 0
) {
    if (retryCount <= 15) return
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        val (title, description, errorCode) = getErrorDetails(isConnected, hasJsonError, errorMessage)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = UiConstants.PADDING_LARGE, vertical = UiConstants.PADDING_SMALL),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.SPACING_MEDIUM)
        ) {
            Text(
                text = "$title - $description ($errorCode)",
                color = Color.Black,
                fontSize = UiConstants.FONT_SIZE_NORMAL,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Thử lại: $retryCount",
                color = Color.Black,
                fontSize = UiConstants.FONT_SIZE_NORMAL,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
