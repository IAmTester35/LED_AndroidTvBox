package com.reecotech.androidtvbox.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reecotech.androidtvbox.data.DisplayData

@Composable
fun DisplayScreen(
    viewModel: DisplayViewModel = hiltViewModel()
) {
    val displayData by viewModel.displayData.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(displayData) { data ->
            DisplayDataItem(data = data)
        }
    }
}

@Composable
fun DisplayDataItem(data: DisplayData) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "${data.title}: ${data.value} ${data.unit}", fontSize = 28.sp)
    }
}
