package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.reecotech.androidtvbox.domain.model.StationData

/**
 * Body section containing update badge, table and footer with radial gradient background
 */
@Composable
fun BodySection(
    stations: List<StationData>,
    lastUpdateTime: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val startY = size.height * UiConstants.BODY_GRADIENT_STOP_1
                val endY = size.height * UiConstants.BODY_GRADIENT_STOP_3
                
                val range = UiConstants.BODY_GRADIENT_STOP_3 - UiConstants.BODY_GRADIENT_STOP_1
                val middleStop = (UiConstants.BODY_GRADIENT_STOP_2 - UiConstants.BODY_GRADIENT_STOP_1) / range

                val brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to UiConstants.BODY_GRADIENT_COLOR_1,
                        middleStop to UiConstants.BODY_GRADIENT_COLOR_2,
                        1.0f to UiConstants.BODY_GRADIENT_COLOR_3
                    ),
                    start = Offset(0f, startY),
                    end = Offset(0f, endY)
                )
                drawRect(brush = brush)
            }
    ) {
        UpdateTimeBadge(lastUpdateTime = lastUpdateTime)
        StationTable(
            stations = stations,
            modifier = Modifier.weight(1f)
        )
        FooterSection(modifier = Modifier.wrapContentHeight())
    }
}
