package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reecotech.androidtvbox.R
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState

/**
 * Header section with title, logo, and last update time
 */
@Composable
fun HeaderSection(state: MainUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(createHeaderGradient())
            .alpha(UiConstants.HEADER_OPACITY)
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
                .background(
                    color = UiConstants.BADGE_BACKGROUND,
                    shape = RoundedCornerShape(UiConstants.CORNER_RADIUS_UPDATE_BADGE)
                )
                .padding(
                    horizontal = UiConstants.PADDING_SMALL,
                    vertical = UiConstants.PADDING_SMALL
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = UiConstants.UPDATE_TIME_ICON,
                fontSize = UiConstants.FONT_SIZE_SMALL
            )
            Text(
                text = "${UiConstants.UPDATE_TIME_PREFIX}${lastUpdateTime.ifEmpty { UiConstants.LOADING_TEXT }}",
                color = UiConstants.BADGE_TEXT,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.SemiBold
            )
        }
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
