package com.reecotech.androidtvbox.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.reecotech.androidtvbox.ui.screen.components.BodySection
import com.reecotech.androidtvbox.ui.screen.components.DisconnectOverlay
import com.reecotech.androidtvbox.ui.screen.components.HeaderSection
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState

// ============================================================================
// Main Screen Composable
// ============================================================================

/**
 * Main data screen displaying station monitoring data
 * 
 * @param state Current UI state containing station data and connection status
 */
@Composable
fun MainDataScreen(state: MainUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(state = state)
            BodySection(
                stations = state.stations,
                lastUpdateTime = state.lastUpdateTime,
                modifier = Modifier.weight(1f)
            )
        }
        
        if (!state.isWebSocketConnected || state.hasJsonError) {
            DisconnectOverlay(
                isWebSocketConnected = state.isWebSocketConnected,
                hasJsonError = state.hasJsonError
            )
        }
    }
}
