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
        // Use negative margin and clip to hide outer borders
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(
                    x = -UiConstants.BORDER_WIDTH,
                    y = -UiConstants.BORDER_WIDTH
                )
                .clipToBounds()
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
        modifier = Modifier
            .fillMaxWidth()
            .border(UiConstants.BORDER_WIDTH, UiConstants.GRID_BORDER_COLOR)
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
            modifier = Modifier
                .fillMaxWidth()
                .border(UiConstants.BORDER_WIDTH, UiConstants.GRID_BORDER_COLOR)
        ) {
            TableCell(
                text = parameter.name,
                modifier = Modifier.weight(1.2f),
                backgroundColor = HeaderBackground,
                textColor = HeaderText
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
            .background(HeaderBackground)
            .border(UiConstants.BORDER_WIDTH, UiConstants.GRID_BORDER_COLOR)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = UiConstants.BORDER_WIDTH.toPx()
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
            .background(HeaderBackground)
            .border(UiConstants.BORDER_WIDTH, UiConstants.GRID_BORDER_COLOR)
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
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .height(UiConstants.TABLE_CELL_HEIGHT)
            .background(backgroundColor)
            .border(UiConstants.BORDER_WIDTH, UiConstants.GRID_BORDER_COLOR)
            .padding(UiConstants.PADDING_SMALL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = UiConstants.FONT_SIZE_SMALL,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
