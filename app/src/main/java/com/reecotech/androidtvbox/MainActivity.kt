package com.reecotech.androidtvbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.reecotech.androidtvbox.ui.screen.DeviceDisabledScreen
import com.reecotech.androidtvbox.ui.screen.MainDataScreen
import com.reecotech.androidtvbox.ui.screen.WaitingForActivationScreen
import com.reecotech.androidtvbox.ui.theme.AndroidTVBoxTheme
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState
import com.reecotech.androidtvbox.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTVBoxTheme {
                val uiState by viewModel.uiState.collectAsState()
                MainScreen(
                    uiState = uiState,
                    onRequestActivation = { viewModel.requestActivation() }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    uiState: MainUiState,
    onRequestActivation: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        when (uiState) {
            is MainUiState.Loading -> {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MainUiState.WaitingForActivation -> {
                WaitingForActivationScreen(
                    deviceId = uiState.deviceId,
                    statusMessage = uiState.statusMessage,
                    onRequestActivation = onRequestActivation
                )
            }
            is MainUiState.DisplayingData -> {
                MainDataScreen(displayDataList = uiState.data)
            }
            is MainUiState.DeviceDisabled -> {
                DeviceDisabledScreen(reason = uiState.reason)
            }
        }
    }
}
