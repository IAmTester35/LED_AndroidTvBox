package com.reecotech.androidtvbox.ui.viewmodel

import com.reecotech.androidtvbox.domain.model.StationData

data class MainUiState(
    val stations: List<StationData> = emptyList(),
    val isConnected: Boolean = true,
    val hasJsonError: Boolean = false,
    val isLoading: Boolean = true,
    val lastUpdateTime: String = "",
    val errorMessage: String? = null,
    val retryCount: Int = 1,
    val passwordHash: String = "",
    val sleepTimeConfig: com.reecotech.androidtvbox.data.repository.RemoteConfigRepository.SleepTimeConfig? = null,
    val isSleepMode: Boolean = false,
    val showSleepWarning: Boolean = false,
    val sleepWarningSecondsLeft: Int = 60
)

