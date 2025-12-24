package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.reecotech.androidtvbox.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

/**
 * Footer section with map, support info and branding
 * Designed to match the provided UI image
 */
@Composable
fun FooterSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(UiConstants.FOOTER_BACKGROUND)
            .padding(
                horizontal = UiConstants.PADDING_LARGE,
                vertical = UiConstants.PADDING_SMALL
            ),
    ) {
        // Header title bar
        MapTitleBar()
        
        // Map image
        MapSection(
            modifier = Modifier.fillMaxHeight(0.68f)
        )
        
        // Bottom section with support info, social QR codes, and branding
        BottomInfoSection()
    }
}

/**
 * Blue title bar for the map section
 */
@Composable
private fun MapTitleBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(UiConstants.STATION_NAME_BACKGROUND)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bản đồ vị trí 11 trạm Khí tượng thủy văn tỉnh Vĩnh Long",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Map image section
 */
@Composable
private fun MapSection(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)),
        painter = painterResource(id = R.drawable.map),
        contentDescription = "Bản đồ vị trí 11 trạm KTTV",
        contentScale = ContentScale.Crop
    )
}

/**
 * Bottom section containing support info, social QR codes, large QR code and logo
 */
@Composable
private fun BottomInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(UiConstants.FOOTER_BACKGROUND),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Column 1: Occupies 70% width
        Column(
            modifier = Modifier.weight(0.7f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SupportPhoneSection()
            
            SocialQRCodeRow()
            
            Image(
                painter = painterResource(id = R.drawable.logo_reeco),
                contentDescription = "REECO Logo",
                modifier = Modifier
                    .width(UiConstants.LOGO_REECO_WIDTH)
                    .height(UiConstants.LOGO_REECO_HEIGHT)
                    .align(Alignment.End)
                    .padding(end = UiConstants.PADDING_LARGE),
                contentScale = ContentScale.Fit
            )
        }
        
        // Column 2: Occupies the remaining width (approx 30%)
        Column(
            modifier = Modifier
                .weight(0.3f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.qr_code),
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(100.dp) // Increased size
                    .background(Color.White)
                    .padding(5.dp)
            )
        }
    }
}

/**
 * Support phone number section
 */
@Composable
private fun SupportPhoneSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(UiConstants.FOOTER_BACKGROUND)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PhoneIcon(
            size = 16.dp,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Hỗ trợ kỹ thuật: ")
                }
                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)) {
                    append("0901 880 386")
                }
            },
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

/**
 * Legend section with scrolling warning levels (for use in parent layout)
 */
@Composable
fun LegendSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_LARGE, vertical = UiConstants.PADDING_SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = UiConstants.LEGEND_TITLE,
            color = Color.White,
            fontSize = UiConstants.FONT_SIZE_LARGE,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = UiConstants.PADDING_SMALL)
        )
        
        LegendMarquee(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PhoneIcon(
    size: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Canvas(modifier = Modifier.size(size)) {
        val path = Path().apply {
            // Scale to fit the 24x24 path into the canvas size
            val scale = this@Canvas.size.minDimension / 24f
            
            moveTo(6.62f * scale, 10.79f * scale)
            cubicTo(8.06f * scale, 13.62f * scale, 10.38f * scale, 15.93f * scale, 13.21f * scale, 17.38f * scale)
            lineTo(15.41f * scale, 15.18f * scale)
            cubicTo(15.68f * scale, 14.91f * scale, 16.08f * scale, 14.82f * scale, 16.43f * scale, 14.94f * scale)
            cubicTo(17.55f * scale, 15.31f * scale, 18.76f * scale, 15.51f * scale, 20.0f * scale, 15.51f * scale)
            cubicTo(20.55f * scale, 15.51f * scale, 21.0f * scale, 15.96f * scale, 21.0f * scale, 16.51f * scale)
            lineTo(21.0f * scale, 20.0f * scale)
            cubicTo(21.0f * scale, 20.55f * scale, 20.55f * scale, 21.0f * scale, 20.0f * scale, 21.0f * scale)
            cubicTo(10.61f * scale, 21.0f * scale, 3.0f * scale, 13.39f * scale, 3.0f * scale, 4.0f * scale)
            cubicTo(3.0f * scale, 3.45f * scale, 3.45f * scale, 3.0f * scale, 4.0f * scale, 3.0f * scale)
            lineTo(7.5f * scale, 3.0f * scale)
            cubicTo(8.05f * scale, 3.0f * scale, 8.5f * scale, 3.45f * scale, 8.5f * scale, 4.0f * scale)
            cubicTo(8.5f * scale, 5.25f * scale, 8.7f * scale, 6.45f * scale, 9.07f * scale, 7.57f * scale)
            cubicTo(9.18f * scale, 7.92f * scale, 9.1f * scale, 8.31f * scale, 8.82f * scale, 8.59f * scale)
            lineTo(6.62f * scale, 10.79f * scale)
            close()
        }
        
        drawPath(
            path = path,
            color = color
        )
    }
}

@Composable
private fun LegendMarquee(modifier: Modifier = Modifier) {
    val legendItems = getLegendItems()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        delay(1000) // Initial delay to ensure layout is ready
        while (true) {
            val max = scrollState.maxValue
            if (max > 0) {
                scrollState.scrollTo(0)
                // Speed: 60 pixels per second approx
                val duration = max * 16
                scrollState.animateScrollTo(
                    value = max,
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = LinearEasing
                    )
                )
            } else {
                delay(100)
            }
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState, enabled = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(screenWidth))
            
            legendItems.forEach { item ->
                LegendItemRow(item.text, item.color)
                Spacer(modifier = Modifier.width(UiConstants.PADDING_EXTRA_LARGE))
            }
            
            Spacer(modifier = Modifier.width(screenWidth))
        }
    }
}

@Composable
private fun LegendItemRow(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(UiConstants.LEGEND_INDICATOR_SIZE)
                .background(color, RoundedCornerShape(UiConstants.CORNER_RADIUS_SMALL))
        )
        Spacer(modifier = Modifier.width(UiConstants.PADDING_MEDIUM))
        Text(
            text = text,
            color = Color.White,
            fontSize = UiConstants.FONT_SIZE_LARGE
        )
    }
}

@Composable
private fun SocialQRCodeRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = UiConstants.PADDING_LARGE),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            R.drawable.qr_eec,
            R.drawable.qr_facebook,
            R.drawable.qr_tiktok,
            R.drawable.qr_youtube,
            R.drawable.qr_zalo,
            R.drawable.qr_linkedin
        ).forEach { qrDrawable ->
            Image(
                painter = painterResource(id = qrDrawable),
                contentDescription = "QR Code",
                modifier = Modifier.size(35.dp)
            )
        }
    }
}
