package com.reecotech.androidtvbox

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        // CRITICAL: Run BEFORE super.onCreate() to fix corrupted DataStore
        // before Hilt initializes any components that depend on it.
        sanitizeDataStore()
        
        super.onCreate()
        if (BuildConfig.DEBUG) {
            timber.log.Timber.plant(timber.log.Timber.DebugTree())
        }
        
        // Start foreground service as backup (in case MainActivity is not created)
        startStationPollingService()
    }
    
    /**
     * Pre-emptively check and delete corrupted DataStore file.
     * This runs before Hilt initialization, preventing CorruptionException crashes.
     */
    private fun sanitizeDataStore() {
        try {
            val dataStoreDir = File(filesDir, "datastore")
            val preferencesFile = File(dataStoreDir, "settings.preferences_pb")
            
            if (preferencesFile.exists()) {
                // Try to read the file as a basic validation
                val bytes = preferencesFile.readBytes()
                
                // If file is empty or suspiciously small (likely corrupted)
                if (bytes.isEmpty()) {
                    timber.log.Timber.w("DataStore file is empty, deleting...")
                    preferencesFile.delete()
                    return
                }
                
                // Try to parse as protobuf - if it fails, the file is corrupted
                // Protobuf files start with specific field tags, we do a basic sanity check
                // A valid preferences proto should have at least a few bytes of structure
                if (bytes.size < 2) {
                    timber.log.Timber.w("DataStore file too small, likely corrupted. Deleting...")
                    preferencesFile.delete()
                    return
                }
                
                // Additional check: try to verify it's a valid protobuf structure
                // The first byte should be a valid protobuf field tag (not 0x00 or random garbage)
                val firstByte = bytes[0].toInt() and 0xFF
                if (firstByte == 0) {
                    timber.log.Timber.w("DataStore file starts with null byte, likely corrupted. Deleting...")
                    preferencesFile.delete()
                    return
                }
            }
        } catch (e: Exception) {
            // If ANY error occurs while checking, delete the file to be safe
            timber.log.Timber.e(e, "Error checking DataStore file, deleting to prevent crash...")
            try {
                val dataStoreDir = File(filesDir, "datastore")
                val preferencesFile = File(dataStoreDir, "settings.preferences_pb")
                if (preferencesFile.exists()) {
                    preferencesFile.delete()
                }
            } catch (deleteError: Exception) {
                timber.log.Timber.e(deleteError, "Failed to delete corrupted DataStore file")
            }
        }
    }

    private fun startStationPollingService() {
        try {
            val serviceIntent = android.content.Intent(
                this,
                com.reecotech.androidtvbox.service.StationPollingService::class.java
            )
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            timber.log.Timber.i("MainApplication: StationPollingService start requested")
        } catch (e: Exception) {
            timber.log.Timber.e(e, "MainApplication: Failed to start StationPollingService")
        }
    }
}
