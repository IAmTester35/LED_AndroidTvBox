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
import com.reecotech.androidtvbox.ui.theme.AndroidTVBoxTheme
import com.reecotech.androidtvbox.ui.viewmodel.MainViewModel
import android.provider.Settings
import android.content.Intent
import android.net.Uri
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

    private fun setWindowBrightness(isSleep: Boolean) {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = if (isSleep) {
            0.0f // Min brightness
        } else {
            android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE // Restore default
        }
        window.attributes = layoutParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // checkAccessibilityService()
        // checkAppUpdate()

        setContent {
            AndroidTVBoxTheme {
                val uiState by viewModel.uiState.collectAsState()
                
                // Side-effect to control brightness
                androidx.compose.runtime.LaunchedEffect(uiState.isSleepMode) {
                    setWindowBrightness(uiState.isSleepMode)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    MainDataScreen(
                        state = uiState,
                        onConfirmSleep = viewModel::onConfirmSleep,
                        onCancelSleep = viewModel::onCancelSleep
                    )
                }
            }
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = "$packageName/${com.reecotech.androidtvbox.service.AutoStartService::class.java.canonicalName}"
        val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabled?.contains(service) == true
    }

    private fun checkAccessibilityService() {
        if (!isAccessibilityServiceEnabled()) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            android.widget.Toast.makeText(
                this,
                "Vui lòng bật 'AndroidTVBox' trong menu Hỗ trợ tiếp cận (Accessibility) để ứng dụng tự mở sau khi reboot",
                android.widget.Toast.LENGTH_LONG
            ).show()
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
                        val sleepConfig = remoteConfigRepository.getSleepTimeConfig()
                        
                        viewModel.updatePasswordHash(passwordHash)
                        viewModel.updateSleepTimeConfig(sleepConfig)
                        
                        if (updateManager.isUpdateAvailable(latestVersionCode)) {
                            android.widget.Toast.makeText(this@MainActivity, "Found new update: $latestVersionCode", android.widget.Toast.LENGTH_LONG).show()
                            android.widget.Toast.makeText(this@MainActivity, "Downloading update...", android.widget.Toast.LENGTH_SHORT).show()
                            updateManager.downloadAndInstallApk(apkUrl, latestVersionCode)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                
                // Check every 20 minutes
                kotlinx.coroutines.delay(20 * 60 * 1000L)
            }
        }
    }
}
