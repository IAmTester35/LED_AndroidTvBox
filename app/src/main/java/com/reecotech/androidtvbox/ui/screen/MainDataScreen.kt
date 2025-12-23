package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.reecotech.androidtvbox.ui.screen.components.BodySection
import com.reecotech.androidtvbox.ui.screen.components.DisconnectOverlay
import com.reecotech.androidtvbox.ui.screen.components.HeaderSection
import com.reecotech.androidtvbox.ui.screen.components.LoadingOverlay
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.zIndex

// ============================================================================
// Main Screen Composable
// ============================================================================

/**
 * Main data screen displaying station monitoring data
 * 
 * @param state Current UI state containing station data and connection status
 */
@Composable
fun MainDataScreen(
    state: MainUiState,
    onConfirmSleep: () -> Unit = {},
    onCancelSleep: () -> Unit = {}
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
        com.reecotech.androidtvbox.ui.screen.components.AspectRatioBox(
            aspectRatio = 2f,
            backgroundColor = Color.Black
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
            ) {
                HeaderSection(state = state)
                BodySection(
                    stations = state.stations,
                    lastUpdateTime = state.lastUpdateTime,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        if (state.isLoading) {

            LoadingOverlay()
        } else if (!state.isConnected || state.hasJsonError) {
            DisconnectOverlay(
                isConnected = state.isConnected,
                hasJsonError = state.hasJsonError,
                errorMessage = state.errorMessage,
                retryCount = state.retryCount
            )
        }

        if (state.showSleepWarning) {
            AlertDialog(
                onDismissRequest = { /* Prevent dismiss by clicking outside */ },
                title = { Text(text = "Cảnh báo") },
                text = { Text(text = "Đã đến giờ ngủ, bạn có muốn tắt màn hình không?\nTự động tắt sau ${state.sleepWarningSecondsLeft} giây") },
                confirmButton = {
                    Button(onClick = onConfirmSleep) {
                        Text("Có")
                    }
                },
                dismissButton = {
                    Button(onClick = onCancelSleep) {
                        Text("Không")
                    }
                },
                modifier = Modifier.zIndex(100f) // Ensure dialog sits on top
            )
        }

        if (state.isSleepMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .zIndex(200f) // Highest z-index to cover everything
            )
        }
    }
}
