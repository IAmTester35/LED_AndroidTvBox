package com.reecotech.androidtvbox.util

import android.content.Context
import android.provider.Settings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiLogger @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mutex = Mutex()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val firestore = Firebase.firestore

    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }

    /**
     * Logs an API call with timing, result and raw response.
     * Guaranteed not to crash due to file I/O or Firestore errors.
     */
    suspend fun logApiCall(startTime: Long, endTime: Long, result: String, responseBody: String? = null) {
        val duration = endTime - startTime
        
        // 1. Log to local file
        logToLocalFile(startTime, endTime, duration, result, responseBody)

        // 2. Sync to Firestore for remote monitoring
        syncToFirestore(startTime, endTime, duration, result, responseBody)

        // 3. Periodically cleanup old logs (once a day or every X calls)
        cleanupOldLogs()
    }

    private var lastCleanupTime = 0L

    private suspend fun cleanupOldLogs() {
        val now = System.currentTimeMillis()
        // Run cleanup at most once every 24 hours to save resources
        if (now - lastCleanupTime < 24 * 60 * 60 * 1000L) return
        
        lastCleanupTime = now

        withContext(Dispatchers.IO) {
            // --- 1. Cleanup Local Files ---
            try {
                val logDir = File(context.filesDir, "logs")
                if (logDir.exists()) {
                    val sevenDaysAgo = now - (7 * 24 * 60 * 60 * 1000L)
                    logDir.listFiles()?.forEach { file ->
                        if (file.lastModified() < sevenDaysAgo) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error cleaning up local logs")
            }

            // --- 2. Cleanup Firestore Collection (Day 8 ago) ---
            try {
                // Calculate name of collection from exactly 8 days ago
                val targetMillis = now - (8 * 24 * 60 * 60 * 1000L)
                val oldCollectionName = "api_logs_${fileDateFormat.format(Date(targetMillis))}"

                firestore.collection("devices")
                    .document(deviceId)
                    .collection(oldCollectionName)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) return@addOnSuccessListener
                        
                        val batch = firestore.batch()
                        snapshot.documents.forEach { doc ->
                            batch.delete(doc.reference)
                        }
                        batch.commit().addOnFailureListener { e ->
                            Timber.w("Batch delete for $oldCollectionName failed: ${e.message}")
                        }
                    }
                    .addOnFailureListener { e ->
                        Timber.w("Failed to fetch old collection $oldCollectionName: ${e.message}")
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error starting Firestore cleanup")
            }
        }
    }

    private suspend fun logToLocalFile(startTime: Long, endTime: Long, duration: Long, result: String, responseBody: String?) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    val logDir = File(context.filesDir, "logs")
                    if (!logDir.exists()) {
                        logDir.mkdirs()
                    }

                    val fileName = "api_log_${fileDateFormat.format(Date())}.txt"
                    val logFile = File(logDir, fileName)

                    val startFormatted = dateFormat.format(Date(startTime))
                    val endFormatted = dateFormat.format(Date(endTime))
                    
                    val logEntry = "[$startFormatted] -> [$endFormatted] | Duration: ${duration}ms | Result: $result | Response: ${responseBody ?: "N/A"}\n"

                    FileOutputStream(logFile, true).use { output ->
                        output.write(logEntry.toByteArray())
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to write API log to file")
                }
            }
        }
    }

    private fun syncToFirestore(startTime: Long, endTime: Long, duration: Long, result: String, responseBody: String?) {
        try {
            val logData = hashMapOf(
                "startTime" to dateFormat.format(Date(startTime)),
                "endTime" to dateFormat.format(Date(endTime)),
                "durationMs" to duration,
                "result" to result,
                "apiResponse" to (responseBody ?: "N/A"),
                "timestamp" to FieldValue.serverTimestamp()
            )

            // Collection per day: api_logs_20240107
            val dailyCollectionName = "api_logs_${fileDateFormat.format(Date(startTime))}"
            
            // Format ID as HHmmss_Result (since date is in collection name)
            val idTimeFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
            val logId = "${idTimeFormat.format(Date(startTime))}_$result"
                .replace(" ", "_")
                .replace("/", "_")
                .replace(":", "-")

            // Collection structure: devices/{deviceId}/{dailyCollectionName}/{logId}
            firestore.collection("devices")
                .document(deviceId)
                .collection(dailyCollectionName)
                .document(logId)
                .set(logData)
                .addOnFailureListener { e ->
                    Timber.w("Firestore sync failed: ${e.message}")
                }
        } catch (e: Exception) {
            Timber.e(e, "Error initiating Firestore sync")
        }
    }
}
