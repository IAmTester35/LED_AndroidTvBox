package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    if (retryCount <= 3) return
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        val (title, description, errorCode) = getErrorDetails(isConnected, hasJsonError, errorMessage)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(UiConstants.ERROR_RED)
                .padding(horizontal = UiConstants.PADDING_LARGE, vertical = UiConstants.PADDING_SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
            
            Spacer(modifier = Modifier.width(UiConstants.SPACING_LARGE))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = UiConstants.FONT_SIZE_LARGE,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = UiConstants.FONT_SIZE_NORMAL,
                    fontWeight = FontWeight.Normal
                )

                Text(
                    text = errorCode,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = UiConstants.FONT_SIZE_SMALL,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(UiConstants.SPACING_MEDIUM))

            Text(
                text = "Thử lại: $retryCount",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = UiConstants.FONT_SIZE_NORMAL,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
