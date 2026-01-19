package com.reecotech.androidtvbox.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.reecotech.androidtvbox.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var downloadId: Long = -1

    fun isUpdateAvailable(remoteVersionCode: Int): Boolean {
        return remoteVersionCode > BuildConfig.VERSION_CODE
    }

    suspend fun downloadAndInstallApk(url: String, versionCode: Int) = withContext(Dispatchers.IO) {
        if (url.isEmpty()) return@withContext

        val apkFileName = "update_$versionCode.apk"
        val tmpFileName = "update_$versionCode.tmp"
        
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val apkFile = File(dir, apkFileName)
        val tmpFile = File(dir, tmpFileName)

        // Check if final APK file already exists
        if (apkFile.exists() && apkFile.length() > 0) {
            Timber.d("APK file already exists. Skipping download and starting install...")
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(context, "Update file ready. Installing...", android.widget.Toast.LENGTH_SHORT).show()
            }
            installApk(context, apkFile)
            return@withContext
        }

        Timber.d("Starting download from: $url")

        // Clean up old APKs and TMPs
        cleanUpOldApks()

        // Delete any existing temp file
        if (tmpFile.exists()) {
            tmpFile.delete()
        }

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Update")
            .setDescription("Downloading version $versionCode...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, tmpFileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)

        // Poll for download status
        var downloading = true
        val startTime = System.currentTimeMillis()
        val TIMEOUT_MS = 20 * 60 * 1000L // 20 minutes timeout

        while (downloading) {
            // Check for timeout
            if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
                Timber.e("Download timed out after 20 minutes. Cancelling...")
                downloadManager.remove(downloadId)
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Update failed: Download timed out", android.widget.Toast.LENGTH_LONG).show()
                }
                return@withContext
            }

            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            
            if (cursor.moveToFirst()) {
                val statusColumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (statusColumnIndex != -1) {
                    val status = cursor.getInt(statusColumnIndex)
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            downloading = false
                            Timber.d("Download complete. Renaming and starting install...")
                            
                            // Rename .tmp to .apk
                            if (tmpFile.exists()) {
                                if (tmpFile.renameTo(apkFile)) {
                                    withContext(Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Download complete. Installing...", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                    installApk(context, apkFile)
                                } else {
                                    Timber.e("Failed to rename temp file to APK")
                                    withContext(Dispatchers.Main) {
                                        android.widget.Toast.makeText(context, "Update failed: Rename error", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                 Timber.e("Temp file not found after download")
                            }
                        }
                        DownloadManager.STATUS_FAILED -> {
                            downloading = false
                            Timber.e("Download failed")
                             withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(context, "Download failed", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            cursor.close()
            
            if (downloading) {
                delay(1000) // Poll every second
            }
        }
    }

    private fun cleanUpOldApks() {
        try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            dir?.listFiles()?.forEach { file ->
                if (file.name.startsWith("update_") && (file.name.endsWith(".apk") || file.name.endsWith(".tmp"))) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clean up old APKs")
        }
    }

    private suspend fun installApk(context: Context, file: File) {
        if (!file.exists()) {
            Timber.e("APK file not found at ${file.absolutePath}")
            return
        }

        // 1. Try Device Owner OR System App with 'INSTALL_PACKAGES' permission
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
        val isDeviceOwner = dpm.isDeviceOwnerApp(context.packageName)
        val hasInstallPermission = context.checkSelfPermission("android.permission.INSTALL_PACKAGES") == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (isDeviceOwner || hasInstallPermission) {
            Timber.i("App is Device Owner ($isDeviceOwner) or has Install Permission ($hasInstallPermission). Attempting silent install via PackageInstaller...")
            installViaPackageInstaller(context, file)
            return
        }

        // 2. Try silent install via Shell (System App / Root)
        Timber.d("Not Device Owner. Attempting SILENT INSTALL via Shell...")
        if (installSilent(file.absolutePath)) {
            Timber.i("Silent install command sent successfully.")
            return
        }

        Timber.d("Silent install failed. Trying root method...")
        if (installRoot(file.absolutePath)) {
            Timber.i("Root install command sent successfully.")
            return
        }

        // 3. Fallback: Standard Android Install Intent (Non-Root)
        Timber.i("Root/Silent failed. Fallback to Standard Intent Install (User Interaction Required).")
        withContext(Dispatchers.Main) {
            installViaIntent(context, file)
        }
    }

    private suspend fun installViaPackageInstaller(context: Context, apkFile: File) = withContext(Dispatchers.IO) {
        val packageInstaller = context.packageManager.packageInstaller
        val params = android.content.pm.PackageInstaller.SessionParams(
            android.content.pm.PackageInstaller.SessionParams.MODE_FULL_INSTALL
        )
        params.setAppPackageName(context.packageName)

        try {
            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)

            java.io.FileInputStream(apkFile).use { input ->
                session.openWrite("package_update", 0, -1).use { output ->
                    input.copyTo(output)
                }
            }

            val intent = Intent(context, com.reecotech.androidtvbox.receiver.AppUpdateReceiver::class.java)
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_MUTABLE
            )

            session.commit(pendingIntent.intentSender)
            session.close()
            Timber.d("PackageInstaller session committed (Device Owner or System App)")

        } catch (e: Exception) {
            Timber.e(e, "PackageInstaller install failed")
             withContext(Dispatchers.Main) {
                 installViaIntent(context, apkFile)
             }
        }
    }

    private fun installViaIntent(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Intent install failed")
        }
    }

    private suspend fun installSilent(path: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // Note: This only works if the app is a System App or has special signature permissions
            val command = "pm install -r $path"
            val process = Runtime.getRuntime().exec(command)
            val result = process.waitFor()
            Timber.d("Shell install result code: $result")
            result == 0
        } catch (e: Exception) {
            Timber.e(e, "Shell install failed")
            false
        }
    }

    private suspend fun installRoot(path: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val command = "su -c pm install -r $path"
            val process = Runtime.getRuntime().exec(command)
            val result = process.waitFor()
            Timber.d("Root install result code: $result")
            result == 0
        } catch (e: Exception) {
            Timber.e(e, "Root install failed")
            false
        }
    }
}
