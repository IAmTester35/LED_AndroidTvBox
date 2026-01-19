package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import com.reecotech.androidtvbox.domain.model.StationData
import com.reecotech.androidtvbox.ui.theme.HeaderBackground
import com.reecotech.androidtvbox.ui.theme.HeaderText

/**
 * Station data table with headers and parameter rows
 */
@Composable
fun StationTable(stations: List<StationData>, modifier: Modifier = Modifier) {
    val parameters = createTableParameters()
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_L, vertical = UiConstants.PADDING_SMALL)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(UiConstants.CORNER_RADIUS_MEDIUM)),
    ) {
        val firstColWidth = this@BoxWithConstraints.maxWidth * UiConstants.COLUMN_WIDTH_PERCENT_FIRST
        val remainingWidth = this@BoxWithConstraints.maxWidth - firstColWidth
        val otherColWidth = if (parameters.isNotEmpty()) remainingWidth / parameters.size else 0.dp
        
        val headerWeight = 1.4f
        val totalWeights = stations.size + headerWeight
        val rowHeight = this@BoxWithConstraints.maxHeight / totalWeights
        val density = LocalDensity.current
        
        // Base font size for standard rows
        val baseFontSize = with(density) { (rowHeight.toPx() * 0.42f).toSp() }
        val headerFontSize = with(density) { ((rowHeight * headerWeight).toPx() * 0.25f).toSp() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(UiConstants.CORNER_RADIUS_MEDIUM))
        ) {
            TableHeaderRow(
                parameters = parameters, 
                firstColWidth = firstColWidth, 
                otherColWidth = otherColWidth,
                fontSize = headerFontSize,
                modifier = Modifier.weight(headerWeight)
            )
            if (stations.isNotEmpty()) {
                TableStationRows(
                    stations = stations,
                    parameters = parameters,
                    firstColWidth = firstColWidth,
                    otherColWidth = otherColWidth,
                    baseFontSize = baseFontSize,
                    modifier = Modifier.weight(stations.size.toFloat())
                )
            }
        }
    }
}

@Composable
private fun TableHeaderRow(
    parameters: List<TableParameter>, 
    firstColWidth: androidx.compose.ui.unit.Dp, 
    otherColWidth: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CornerHeaderCell(Modifier.width(firstColWidth).fillMaxHeight(), fontSize = fontSize)
        parameters.forEach { parameter ->
            TableHeaderCell(parameter.name, Modifier.width(otherColWidth).fillMaxHeight(), isOnline = true, fontSize = fontSize)
        }
    }
}

@Composable
private fun TableStationRows(
    stations: List<StationData>,
    parameters: List<TableParameter>,
    firstColWidth: androidx.compose.ui.unit.Dp,
    otherColWidth: androidx.compose.ui.unit.Dp,
    baseFontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        stations.forEach { station ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                TableCell(
                    text = station.stationName,
                    modifier = Modifier.width(firstColWidth).fillMaxHeight(),
                    backgroundColor = if (station.isOnline) UiConstants.STATION_NAME_BACKGROUND else UiConstants.STATION_NAME_DISCONNECTED_BACKGROUND,
                    textColor = UiConstants.STATION_NAME_TEXT,
                    isParameterCell = true,
                    fontSize = baseFontSize * 0.85f // Station name slightly smaller
                )
                parameters.forEach { parameter ->
                    val (rawValue, alarmValue, isEnabled) = parameter.valueExtractor(station)
                    val textValue = if (isEnabled) rawValue else ""
                    val isNoData = textValue == UiConstants.NO_DATA_PLACEHOLDER
                    
                    val bgColor = if (isEnabled) getValueBackgroundColor(textValue) else UiConstants.NO_DATA_COLOR_DISABLE
                    val txtColor = if (isNoData || !station.isOnline) UiConstants.NO_DATA_COLOR else getTextColorForValue(alarmValue)
                    
                    val fontSize = if (isNoData) baseFontSize * 1.2f else baseFontSize
                    val fontWeight = if (isNoData) FontWeight.Black else FontWeight.ExtraBold
                    
                    TableCell(
                        text = textValue,
                        modifier = Modifier.width(otherColWidth).fillMaxHeight(),
                        backgroundColor = bgColor,
                        textColor = txtColor,
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    )
                }
            }
        }
    }
}

@Composable
fun CornerHeaderCell(modifier: Modifier = Modifier, fontSize: androidx.compose.ui.unit.TextUnit) {
    Box(
        modifier = modifier
            .padding(UiConstants.BORDER_WIDTH / 2)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = UiConstants.BORDER_WIDTH.toPx()
            val halfStroke = strokeWidth / 2
            
            // Calculate diagonal length and offsets
            val diagLen = kotlin.math.sqrt(size.width * size.width + size.height * size.height)
            
            // Vector perpendicular to diagonal (w, h) is (h, -w)
            // We want to move in direction (h, -w) for the upper triangle
            // Shift vector s = (sx, sy) = (halfStroke * h/len, -halfStroke * w/len)
            val sx = halfStroke * size.height / diagLen
            val sy = -halfStroke * size.width / diagLen
            
            // Top-Right Triangle (Upper)
            // Intersection with Top Edge (y=0): x_top = sx - sy*w/h
            // Intersection with Right Edge (x=w): y_right = sy + h - sx*h/w
            
            val x_top = sx - sy * size.width / size.height
            val y_right = sy + size.height - sx * size.height / size.width
            
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width, 0f) // Top Right Corner
                    lineTo(size.width, y_right) // Intersection with Right Edge
                    lineTo(x_top, 0f) // Intersection with Top Edge
                    close()
                },
                color = HeaderBackground
            )
            
            // Bottom-Left Triangle (Lower)
            // Shift in opposite direction (-sx, -sy)
            // Intersection with Left Edge (x=0): y_left = -sy + sx*h/w
            // Intersection with Bottom Edge (y=h): x_bottom = -sx + w + sy*w/h
            
            val y_left = -sy + sx * size.height / size.width
            val x_bottom = -sx + size.width + sy * size.width / size.height
            
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, size.height) // Bottom Left Corner
                    lineTo(x_bottom, size.height) // Intersection with Bottom Edge
                    lineTo(0f, y_left) // Intersection with Left Edge
                    close()
                },
                color = UiConstants.STATION_NAME_BACKGROUND
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = UiConstants.PADDING_SMALL)
        ) {
            Text(
                text = UiConstants.TABLE_HEADER_PARAMETER,
                color = HeaderText,
                fontSize = fontSize * 0.8f,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 0.dp, start = UiConstants.PADDING_SMALL)
        ) {
            Text(
                text = UiConstants.TABLE_HEADER_STATION,
                color = UiConstants.STATION_NAME_TEXT,
                fontSize = fontSize * 0.8f,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier, isOnline: Boolean = true, fontSize: androidx.compose.ui.unit.TextUnit) {
    Box(
        modifier = modifier
            .padding(UiConstants.BORDER_WIDTH / 2)
            .background(if (isOnline) HeaderBackground else UiConstants.NO_DATA_COLOR)
            .padding(UiConstants.PADDING_SMALL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = HeaderText,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = fontSize
        )
    }
}

@Composable
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color = Color.White,
    isParameterCell: Boolean = false,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Box(
        modifier = modifier
            .padding(UiConstants.BORDER_WIDTH / 2)
            .background(backgroundColor)
            .padding(
                horizontal = UiConstants.PADDING_SMALL,
                vertical = if (isParameterCell) UiConstants.SPACING_TINY else UiConstants.PADDING_SMALL
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            lineHeight = fontSize
        )
    }
}
