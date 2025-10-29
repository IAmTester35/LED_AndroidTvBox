package com.reecotech.androidtvbox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reecotech.androidtvbox.data.WebSocketClient
import com.reecotech.androidtvbox.domain.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


sealed class ActivationState {
    object Idle : ActivationState()
    object Loading : ActivationState()
    object Activated : ActivationState()
    data class Error(val message: String) : ActivationState()
}

@HiltViewModel
class ActivationViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val webSocketClient: WebSocketClient
) : ViewModel() {

    private val _deviceId = MutableStateFlow<String?>(null)
    val deviceId: StateFlow<String?> = _deviceId

    private val _activationState = MutableStateFlow<ActivationState>(ActivationState.Idle)
    val activationState: StateFlow<ActivationState> = _activationState

    init {
        viewModelScope.launch {
            val storedDeviceId = deviceRepository.getDeviceId().first()
            if (storedDeviceId.isNullOrEmpty()) {
                val newDeviceId = UUID.randomUUID().toString()
                deviceRepository.saveDeviceId(newDeviceId)
                _deviceId.value = newDeviceId
            } else {
                _deviceId.value = storedDeviceId
            }
        }
    }

    fun onActivationRequested() {
        viewModelScope.launch {
            _activationState.value = ActivationState.Loading
            // In a real scenario, we would make an API call here.
            // For Sprint 1, we will simulate a successful activation.
            kotlinx.coroutines.delay(2000) // Simulate network delay
            _activationState.value = ActivationState.Activated
        }
    }
}
