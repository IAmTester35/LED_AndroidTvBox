package com.reecotech.androidtvbox.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class RemoteConfigRepository @Inject constructor() {

    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3 * 60 // 3 minute cache
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Default values
        remoteConfig.setDefaultsAsync(mapOf(
            KEY_LATEST_VERSION_CODE to 0,
            KEY_APK_DOWNLOAD_URL to ""
        ))
    }

    suspend fun fetchAndActivate(): Boolean = suspendCoroutine { continuation ->
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Remote Config fetch succeeded")
                    continuation.resume(true)
                } else {
                    Timber.e(task.exception, "Remote Config fetch failed")
                    continuation.resume(false)
                }
            }
    }

    fun getLatestVersionCode(): Int {
        return remoteConfig.getLong(KEY_LATEST_VERSION_CODE).toInt()
    }

    fun getApkDownloadUrl(): String {
        return remoteConfig.getString(KEY_APK_DOWNLOAD_URL)
    }

    companion object {
        const val KEY_LATEST_VERSION_CODE = "latest_version_code"
        const val KEY_APK_DOWNLOAD_URL = "apk_download_url"
    }
}
