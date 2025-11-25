package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reecotech.androidtvbox.R
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState
import kotlin.math.cos
import kotlin.math.sin

/**
 * Header section with title, logo, and last update time
 */
@Composable
fun HeaderSection(state: MainUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(createHeaderGradient())
            .padding(vertical = UiConstants.PADDING_SMALL),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderContent()
    }
}

@Composable
private fun HeaderContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_LARGE),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_government),
            contentDescription = "Government Logo",
            modifier = Modifier.size(UiConstants.LOGO_GOVERNMENT_SIZE)
        )
        Spacer(modifier = Modifier.width(UiConstants.SPACING_LARGE))
        
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = UiConstants.HEADER_TITLE,
                color = Color.Red,
                fontSize = UiConstants.FONT_SIZE_HEADER_TITLE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = UiConstants.HEADER_SUBTITLE,
                color = Color.Yellow,
                fontSize = UiConstants.FONT_SIZE_HEADER_SUBTITLE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UpdateTimeBadge(lastUpdateTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_LARGE, vertical = UiConstants.SPACING_TINY),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(UiConstants.CORNER_RADIUS_UPDATE_BADGE),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .background(
                    color = Color(0xFFD7F9FF),
                    shape = RoundedCornerShape(UiConstants.CORNER_RADIUS_UPDATE_BADGE)
                )
                .padding(
                    horizontal = UiConstants.PADDING_SMALL,
                    vertical = UiConstants.SPACING_TINY
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClockIcon(
                size = 12.dp,
                color = UiConstants.BADGE_TEXT
            )
            Spacer(modifier = Modifier.width(UiConstants.SPACING_SMALL))
            Text(
                text = "${UiConstants.UPDATE_TIME_PREFIX}${lastUpdateTime.ifEmpty { UiConstants.LOADING_TEXT }}",
                color = UiConstants.BADGE_TEXT,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ClockIcon(
    size: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Canvas(modifier = Modifier.size(size)) {
        val canvasSize = this.size.minDimension
        val center = Offset(canvasSize / 2, canvasSize / 2)
        val radius = canvasSize / 2
        
        // Draw clock circle
        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = radius * 0.15f)
        )
        
        // Draw hour hand (pointing to 10 o'clock)
        val hourAngle = Math.toRadians(-60.0) // 10 o'clock position
        val hourHandLength = radius * 0.5f
        drawLine(
            color = color,
            start = center,
            end = Offset(
                center.x + (hourHandLength * cos(hourAngle)).toFloat(),
                center.y + (hourHandLength * sin(hourAngle)).toFloat()
            ),
            strokeWidth = radius * 0.12f,
            cap = StrokeCap.Round
        )
        
        // Draw minute hand (pointing to 2 o'clock)
        val minuteAngle = Math.toRadians(60.0) // 2 o'clock position
        val minuteHandLength = radius * 0.7f
        drawLine(
            color = color,
            start = center,
            end = Offset(
                center.x + (minuteHandLength * cos(minuteAngle)).toFloat(),
                center.y + (minuteHandLength * sin(minuteAngle)).toFloat()
            ),
            strokeWidth = radius * 0.12f,
            cap = StrokeCap.Round
        )
        
        // Draw center dot
        drawCircle(
            color = color,
            radius = radius * 0.1f,
            center = center
        )
    }
}

private fun createHeaderGradient() = Brush.linearGradient(
    colorStops = arrayOf(
        UiConstants.HEADER_GRADIENT_START_OFFSET to UiConstants.HEADER_GRADIENT_START,
        UiConstants.HEADER_GRADIENT_END_OFFSET to UiConstants.HEADER_GRADIENT_END
    ),
    start = Offset.Zero,
    end = Offset(UiConstants.HEADER_GRADIENT_END_X, UiConstants.HEADER_GRADIENT_END_Y)
)
