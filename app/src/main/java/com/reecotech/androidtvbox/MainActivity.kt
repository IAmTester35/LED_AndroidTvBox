package com.reecotech.androidtvbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.reecotech.androidtvbox.ui.screen.MainDataScreen
import com.reecotech.androidtvbox.ui.screen.components.AspectRatioBox
import com.reecotech.androidtvbox.ui.theme.AndroidTVBoxTheme
import com.reecotech.androidtvbox.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @javax.inject.Inject
    lateinit var remoteConfigRepository: com.reecotech.androidtvbox.data.repository.RemoteConfigRepository

    @javax.inject.Inject
    lateinit var updateManager: com.reecotech.androidtvbox.util.UpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAppUpdate()

        setContent {
            AndroidTVBoxTheme {
                val uiState by viewModel.uiState.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    MainDataScreen(state = uiState)
                }
            }
        }
    }

    private fun checkAppUpdate() {
        lifecycleScope.launch {
            while (true) {
                try {
                    val fetched = remoteConfigRepository.fetchAndActivate()
                    if (fetched) {
                        val latestVersionCode = remoteConfigRepository.getLatestVersionCode()
                        val apkUrl = remoteConfigRepository.getApkDownloadUrl()
                        val passwordHash = remoteConfigRepository.getPasswordHash()
                        
                        viewModel.updatePasswordHash(passwordHash)
                        
                        if (updateManager.isUpdateAvailable(latestVersionCode)) {
                            android.widget.Toast.makeText(this@MainActivity, "Found new update: $latestVersionCode", android.widget.Toast.LENGTH_LONG).show()
                            android.widget.Toast.makeText(this@MainActivity, "Downloading update...", android.widget.Toast.LENGTH_SHORT).show()
                            updateManager.downloadAndInstallApk(apkUrl, latestVersionCode)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                
                // Check every 15 minutes
                kotlinx.coroutines.delay(15 * 60 * 1000L)
            }
        }
    }
}
