package com.factory.display.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.factory.display.ui.viewmodel.MainViewModel

@Composable
fun WaitingScreen(viewModel: MainViewModel = hiltViewModel()) {
    val deviceId by viewModel.deviceId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (deviceId == null) {
            CircularProgressIndicator()
        } else {
            Text(text = "Device ID:")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = deviceId!!)
            Spacer(modifier = Modifier.height(16.dp))
            QrCodeImage(content = deviceId!!)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Waiting for activation from the system...")
        }
    }
}
