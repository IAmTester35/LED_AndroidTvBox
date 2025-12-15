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
            val (title, description, errorCode) = getErrorDetails(isConnected, hasJsonError, errorMessage)

            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.White,
                modifier = Modifier.size(UiConstants.WARNING_ICON_SIZE)
            )
            
            Spacer(modifier = Modifier.height(UiConstants.SPACING_LARGE))
            
            // Title (Generic Error Name)
            Text(
                text = title,
                color = Color.White,
                fontSize = UiConstants.FONT_SIZE_HUGE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(UiConstants.PADDING_MEDIUM))
            
            // Description (Specific Explanation)
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = UiConstants.FONT_SIZE_LARGE,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(UiConstants.PADDING_SMALL))

            // Error Code (Technical Details)
            Text(
                text = errorCode,
                color = Color.White.copy(alpha = 1.0f), // Full opacity for better readability in photos
                fontSize = UiConstants.FONT_SIZE_MEDIUM,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(UiConstants.PADDING_SMALL))
            Text(
                text = "Số lần thử lại: $retryCount",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = UiConstants.FONT_SIZE_MEDIUM,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(UiConstants.PADDING_MEDIUM))
        }
    }
}
