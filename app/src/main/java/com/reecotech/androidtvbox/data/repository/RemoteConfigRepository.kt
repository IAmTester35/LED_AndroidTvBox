package com.reecotech.androidtvbox.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
            minimumFetchIntervalInSeconds = 20 * 60 // 20 minute cache
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Default values
        remoteConfig.setDefaultsAsync(mapOf(
            KEY_LATEST_VERSION_CODE to 0,
            KEY_APK_DOWNLOAD_URL to "",
            KEY_PASSWORD_HASH to "99edc2b391da70f08d8aed876b0c2bb1e976bcaff860abc0f29dcd45fd09d1dc",
            KEY_SLEEP_TIME to "{\"fr\": \"17:00\", \"to\": \"07:00\"}",
            KEY_IS_DEBUG to true
        ))
    }

    /**
     * Listen for real-time config updates.
     */
    val configUpdates: Flow<Unit> = callbackFlow {
        val listener = remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Timber.d("Remote Config updated: ${configUpdate.updatedKeys}")
                // When config is updated, fetch and activate manually to get new values
                remoteConfig.activate().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(Unit)
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Timber.e(error, "Remote Config update error")
            }
        })
        awaitClose { listener.remove() }
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

    fun isDebug(): Boolean {
        return remoteConfig.getBoolean(KEY_IS_DEBUG)
    }

    companion object {
        const val KEY_LATEST_VERSION_CODE = "latest_version_code"
        const val KEY_APK_DOWNLOAD_URL = "apk_download_url"
        const val KEY_PASSWORD_HASH = "password_hash"
        const val KEY_SLEEP_TIME = "sleep_time"
        const val KEY_IS_DEBUG = "is_debug"
    }

    @kotlinx.serialization.Serializable
    data class SleepTimeConfig(
        val fr: String,
        val to: String
    )

    fun getSleepTimeConfig(): SleepTimeConfig? {
        val json = remoteConfig.getString(KEY_SLEEP_TIME)
        if (json.isBlank()) return null
        return try {
            kotlinx.serialization.json.Json.decodeFromString<SleepTimeConfig>(json)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse sleep time config")
            null
        }
    }
}
