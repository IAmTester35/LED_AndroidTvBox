package com.factory.display.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.factory.display.data.model.DisplayData
import com.factory.display.domain.usecase.GetDeviceIdUseCase
import com.factory.display.domain.usecase.GetDisplayDataUseCase
import com.factory.display.domain.usecase.StartWebSocketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val startWebSocketUseCase: StartWebSocketUseCase,
    private val getDisplayDataUseCase: GetDisplayDataUseCase
) : ViewModel() {

    private val _deviceId = MutableStateFlow<String?>(null)
    val deviceId: StateFlow<String?> = _deviceId

    private val _showDataScreen = MutableStateFlow(false)
    val showDataScreen: StateFlow<Boolean> = _showDataScreen

    private val _displayData = MutableStateFlow<List<DisplayData>>(emptyList())
    val displayData: StateFlow<List<DisplayData>> = _displayData

    init {
        viewModelScope.launch {
            _deviceId.value = getDeviceIdUseCase()
            startWebSocketUseCase()
            getDisplayDataUseCase().onEach { data ->
                _displayData.value = data
                if (data.isNotEmpty()) {
                    _showDataScreen.value = true
                }
            }.launchIn(viewModelScope)
        }
    }
}
