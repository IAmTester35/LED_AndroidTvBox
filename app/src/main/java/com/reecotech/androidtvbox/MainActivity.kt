package com.reecotech.androidtvbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.reecotech.androidtvbox.ui.AppNavigation
import com.reecotech.androidtvbox.ui.theme.AndroidTVBoxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidTVBoxTheme {
                AppNavigation()
            }
        }
    }
}