package com.reecotech.androidtvbox.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.reecotech.androidtvbox.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
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

    fun downloadAndInstallApk(url: String) {
        if (url.isEmpty()) return

        Timber.d("Starting download from: $url")

        // clean old apk if exists
        val fileName = "update.apk"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) {
            file.delete()
        }

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Update")
            .setDescription("Downloading new version of the app...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)

        // Register receiver to listen for download complete
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    Timber.d("Download complete. Starting install...")
                    installApk(ctxt, file)
                    try {
                        context.unregisterReceiver(this)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
        }, filter, Context.RECEIVER_EXPORTED)
    }

    private fun installApk(context: Context, file: File) {
        if (!file.exists()) {
            Timber.e("APK file not found at ${file.absolutePath}")
            return
        }

        Timber.d("Attempting SILENT INSTALL via Shell...")
        val success = installSilent(file.absolutePath)
        
        if (success) {
            Timber.i("Silent install command sent successfully. App should restart.")
            // App might be killed here by the OS during install
        } else {
            Timber.e("Silent install failed. Trying root method...")
            val rootSuccess = installRoot(file.absolutePath)
            if (!rootSuccess) {
                 Timber.e("All silent install methods failed.")
                 // Fallback to normal install if needed, or just log error for Kiosk
            }
        }
    }

    private fun installSilent(path: String): Boolean {
        return try {
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

    private fun installRoot(path: String): Boolean {
        return try {
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
