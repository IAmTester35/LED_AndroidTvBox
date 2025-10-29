package com.reecotech.androidtvbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ActivationScreen(
    viewModel: ActivationViewModel = hiltViewModel(),
    onActivated: () -> Unit
) {
    val deviceId by viewModel.deviceId.collectAsState()
    val activationState by viewModel.activationState.collectAsState()

    LaunchedEffect(activationState) {
        if (activationState is ActivationState.Activated) {
            onActivated()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (activationState) {
            is ActivationState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {
                if (deviceId == null) {
                    CircularProgressIndicator()
                } else {
                    Text(text = "Device ID:", fontSize = 32.sp)
                    Text(text = deviceId!!, fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { viewModel.onActivationRequested() }) {
                        Text(text = "Yêu cầu Kích hoạt", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Đang chờ kích hoạt từ hệ thống...", fontSize = 20.sp)
                }
            }
        }
    }
}
