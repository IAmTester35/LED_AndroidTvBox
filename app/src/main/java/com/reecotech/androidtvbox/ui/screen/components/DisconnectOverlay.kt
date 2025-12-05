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
    isWebSocketConnected: Boolean,
    hasJsonError: Boolean,
    errorMessage: String? = null
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
                text = errorMessage ?: getErrorMessage(isWebSocketConnected, hasJsonError),
                color = Color.White,
                fontSize = UiConstants.FONT_SIZE_HUGE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(UiConstants.PADDING_MEDIUM))
        }
    }
}
