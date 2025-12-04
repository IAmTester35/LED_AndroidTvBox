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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.reecotech.androidtvbox.R
import androidx.compose.ui.unit.dp
/**
 * Footer section with legend, support info, and branding
 */
@Composable
fun FooterSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(UiConstants.FOOTER_BACKGROUND)
            .padding(
                horizontal = UiConstants.PADDING_MEDIUM,
                vertical = UiConstants.PADDING_SMALL
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendAndSupportSection(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(UiConstants.SPACING_MEDIUM))
        BrandingSection()
        Spacer(modifier = Modifier.width(UiConstants.SPACING_MEDIUM))
        MonitoringDataQRSection()
    }
}

@Composable
private fun LegendAndSupportSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = UiConstants.LEGEND_TITLE,
            color = Color.White,
            fontSize = UiConstants.FONT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = UiConstants.PADDING_SMALL)
        )
        
        LegendGrid()
        
        Spacer(modifier = Modifier.height(UiConstants.PADDING_SMALL))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            PhoneIcon(
                size = 12.dp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.width(UiConstants.SPACING_SMALL))
            
            Text(
                text = buildAnnotatedString {
                    append("Hỗ trợ kỹ thuật: ")
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append("0901 880 386")
                    }
                },
                color = Color.White,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.width(UiConstants.SPACING_LARGE))
            
            SocialQRCodeRow()
        }
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
private fun LegendGrid() {
    val legendItems = getLegendItems()
    val columnCount = 2
    
    Row(modifier = Modifier.fillMaxWidth()) {
        for (columnIndex in 0 until columnCount) {
            Column(modifier = Modifier.weight(1f)) {
                legendItems
                    .chunked(legendItems.size / columnCount + legendItems.size % columnCount)
                    .getOrNull(columnIndex)
                    ?.forEach { item ->
                        LegendItemRow(item.text, item.color)
                        Spacer(modifier = Modifier.height(UiConstants.SPACING_TINY))
                    }
            }
            if (columnIndex < columnCount - 1) {
                Spacer(modifier = Modifier.width(UiConstants.PADDING_SMALL))
            }
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
        Spacer(modifier = Modifier.width(UiConstants.PADDING_SMALL))
        Text(
            text = text,
            color = Color.White,
            fontSize = UiConstants.FONT_SIZE_TINY
        )
    }
}

@Composable
private fun BrandingSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_reeco),
            contentDescription = "REECO Logo",
            modifier = Modifier
                .width(UiConstants.LOGO_REECO_WIDTH)
                .height(UiConstants.LOGO_REECO_HEIGHT)
        )
    }
}

@Composable
private fun SocialQRCodeRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            R.drawable.qr_eec,
            R.drawable.qr_linkedin,
            R.drawable.qr_youtube,
            R.drawable.qr_tiktok,
            R.drawable.qr_facebook,
            R.drawable.qr_zalo
        ).forEach { qrDrawable ->
            Image(
                painter = painterResource(id = qrDrawable),
                contentDescription = "QR Code",
                modifier = Modifier.size(UiConstants.QR_CODE_SIZE * 1.65f)
            )
        }
    }
}

@Composable
private fun MonitoringDataQRSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(UiConstants.CORNER_RADIUS_SMALL)
            )
            .padding(UiConstants.PADDING_SMALL)
    ) {
        Text(
            text = "Số liệu quan trắc",
            color = Color.Black,
            fontSize = UiConstants.FONT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = UiConstants.SPACING_TINY)
        )
        
        Image(
            painter = painterResource(id = R.drawable.qr_code),
            contentDescription = "Monitoring Data QR Code",
            modifier = Modifier.size(UiConstants.QR_CODE_LARGE_SIZE)
        )
    }
}

