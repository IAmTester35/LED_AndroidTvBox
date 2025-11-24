package com.reecotech.androidtvbox.ui.viewmodel

import com.reecotech.androidtvbox.domain.model.StationData

data class MainUiState(
    val stations: List<StationData> = emptyList(),
    val isWebSocketConnected: Boolean = true,
    val hasJsonError: Boolean = false,
    val lastUpdateTime: String = ""
)

