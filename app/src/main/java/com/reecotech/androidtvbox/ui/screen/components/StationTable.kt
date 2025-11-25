package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reecotech.androidtvbox.domain.model.StationData
import com.reecotech.androidtvbox.ui.theme.HeaderBackground
import com.reecotech.androidtvbox.ui.theme.HeaderText

/**
 * Station data table with headers and parameter rows
 */
@Composable
fun StationTable(stations: List<StationData>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_LARGE, vertical = UiConstants.PADDING_SMALL)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TableHeaderRow(stations = stations)
            TableParameterRows(
                stations = stations,
                parameters = createTableParameters()
            )
        }
    }
}

@Composable
private fun TableHeaderRow(stations: List<StationData>) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        CornerHeaderCell(Modifier.weight(1.2f))
        stations.forEach { station ->
            TableHeaderCell(station.stationName, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TableParameterRows(
    stations: List<StationData>,
    parameters: List<TableParameter>
) {
    parameters.forEach { parameter ->
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TableCell(
                text = parameter.name,
                modifier = Modifier.weight(1.2f),
                backgroundColor = HeaderBackground,
                textColor = HeaderText,
                isParameterCell = true
            )
            stations.forEach { station ->
                val value = parameter.valueExtractor(station)
                val bgColor = getValueBackgroundColor(value)
                val txtColor = getTextColorForValue(value)
                TableCell(
                    text = value,
                    modifier = Modifier.weight(1f),
                    backgroundColor = bgColor,
                    textColor = txtColor
                )
            }
        }
    }
}

@Composable
fun CornerHeaderCell(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(UiConstants.TABLE_CELL_HEIGHT)
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
                color = HeaderBackground
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = UiConstants.PADDING_SMALL, end = 0.dp)
        ) {
            Text(
                text = UiConstants.TABLE_HEADER_STATION,
                color = HeaderText,
                fontSize = UiConstants.FONT_SIZE_SMALL,
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
                text = UiConstants.TABLE_HEADER_PARAMETER,
                color = HeaderText,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(UiConstants.TABLE_CELL_HEIGHT)
            .padding(UiConstants.BORDER_WIDTH / 2)
            .background(HeaderBackground)
            .padding(UiConstants.PADDING_SMALL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = HeaderText,
            fontSize = UiConstants.FONT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = UiConstants.LINE_HEIGHT_SMALL
        )
    }
}

@Composable
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color = Color.White,
    isParameterCell: Boolean = false
) {
    Box(
        modifier = modifier
            .height(UiConstants.TABLE_CELL_HEIGHT)
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
            fontSize = UiConstants.FONT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = if (isParameterCell) UiConstants.FONT_SIZE_SMALL else UiConstants.LINE_HEIGHT_SMALL
        )
    }
}
