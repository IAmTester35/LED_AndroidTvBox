package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.background
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
            .background(createBodyGradient())
    ) {
        UpdateTimeBadge(lastUpdateTime = lastUpdateTime)
        StationTable(
            stations = stations,
            modifier = Modifier.weight(1f)
        )
        FooterSection(modifier = Modifier.wrapContentHeight())
    }
}

private fun createBodyGradient() = Brush.radialGradient(
    colorStops = arrayOf(
        UiConstants.BODY_GRADIENT_STOP_1 to UiConstants.BODY_GRADIENT_COLOR_1,
        UiConstants.BODY_GRADIENT_STOP_2 to UiConstants.BODY_GRADIENT_COLOR_2,
        UiConstants.BODY_GRADIENT_STOP_3 to UiConstants.BODY_GRADIENT_COLOR_3
    ),
    center = Offset(
        UiConstants.BODY_GRADIENT_CENTER_X,
        UiConstants.BODY_GRADIENT_CENTER_Y
    ),
    radius = Float.POSITIVE_INFINITY
)
