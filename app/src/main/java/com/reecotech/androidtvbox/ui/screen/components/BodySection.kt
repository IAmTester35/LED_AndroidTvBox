package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.reecotech.androidtvbox.domain.model.StationData

/**
 * Body section containing update badge, table and footer with radial gradient background
 */
@Composable
fun BodySection(
    stations: List<StationData>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = UiConstants.PADDING_SMALL)
            .drawBehind {
                // Draw top border (light white)
                val borderWidth = UiConstants.BORDER_WIDTH.toPx()
                drawRect(
                    color = Color.White.copy(alpha = 0.5f),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, borderWidth)
                )
                
                // Draw gradient background
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
        // Main content layer with Table and Legend
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                StationTable(
                    stations = stations,
                    modifier = Modifier.weight(0.64f)
                )
                Spacer(modifier = Modifier.weight(0.36f))
            }
            LegendSection(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .wrapContentHeight()
            )
        }

        // Overlay layer for FooterSection
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(0.59f))
            FooterSection(
                modifier = Modifier
                    .weight(0.36f)
                    .fillMaxHeight()
            )
        }
    }
}
