package com.factory.display.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.factory.display.ui.screen.DataDisplayScreen
import com.factory.display.ui.screen.WaitingScreen
import com.factory.display.ui.theme.AndroidTVBoxTheme
import com.factory.display.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTVBoxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel = hiltViewModel()
                    val showDataScreen by viewModel.showDataScreen.collectAsState()

                    if (showDataScreen) {
                        DataDisplayScreen()
                    } else {
                        WaitingScreen()
                    }
                }
            }
        }
    }
}