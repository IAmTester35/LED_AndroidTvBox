package com.reecotech.androidtvbox.ui.viewmodel

import com.reecotech.androidtvbox.data.model.DisplayData

sealed class MainUiState {
    object Loading : MainUiState()
    data class WaitingForActivation(val deviceId: String, val statusMessage: String) : MainUiState()
    data class DisplayingData(val data: List<DisplayData>) : MainUiState()
    data class DeviceDisabled(val reason: String?) : MainUiState()
}
