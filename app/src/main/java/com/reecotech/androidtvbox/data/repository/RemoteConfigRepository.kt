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
            minimumFetchIntervalInSeconds = 5 * 60 // 5 minute cache
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Default values
        remoteConfig.setDefaultsAsync(mapOf(
            KEY_LATEST_VERSION_CODE to 0,
            KEY_APK_DOWNLOAD_URL to "",
            KEY_PASSWORD_HASH to "99edc2b391da70f08d8aed876b0c2bb1e976bcaff860abc0f29dcd45fd09d1dc"
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

    fun getPasswordHash(): String {
        return remoteConfig.getString(KEY_PASSWORD_HASH)
    }

    companion object {
        const val KEY_LATEST_VERSION_CODE = "latest_version_code"
        const val KEY_APK_DOWNLOAD_URL = "apk_download_url"
        const val KEY_PASSWORD_HASH = "password_hash"
    }
}
