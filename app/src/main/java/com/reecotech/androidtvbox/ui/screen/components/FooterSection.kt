package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.reecotech.androidtvbox.R

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
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UiConstants.PADDING_SMALL)
        ) {
            Text(
                text = UiConstants.SUPPORT_TEXT,
                color = Color.White,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.SemiBold
            )
            
            SocialQRCodeRow()
        }
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
        horizontalArrangement = Arrangement.spacedBy(UiConstants.PADDING_SMALL),
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
                modifier = Modifier.size(UiConstants.QR_CODE_SIZE)
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

