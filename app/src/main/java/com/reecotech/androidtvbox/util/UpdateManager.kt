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

    fun downloadAndInstallApk(url: String, versionCode: Int) {
        if (url.isEmpty()) return

        val fileName = "update_$versionCode.apk"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

        // Check if file already exists
        if (file.exists() && file.length() > 0) {
            Timber.d("APK file already exists. Skipping download and starting install...")
            android.widget.Toast.makeText(context, "Update file ready. Installing...", android.widget.Toast.LENGTH_SHORT).show()
            installApk(context, file)
            return
        }

        Timber.d("Starting download from: $url")

        // Clean up old APKs
        cleanUpOldApks()

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading Update")
            .setDescription("Downloading version $versionCode...")
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
                    android.widget.Toast.makeText(ctxt, "Download complete. Installing...", android.widget.Toast.LENGTH_LONG).show()
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

    private fun cleanUpOldApks() {
        try {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            dir?.listFiles()?.forEach { file ->
                if (file.name.startsWith("update_") && file.name.endsWith(".apk")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clean up old APKs")
        }
    }

    private fun installApk(context: Context, file: File) {
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
        installViaIntent(context, file)
    }

    private fun installViaPackageInstaller(context: Context, apkFile: File) {
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
            // Fallback to intent if this fails?
             installViaIntent(context, apkFile)
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

    private fun installSilent(path: String): Boolean {
        return try {
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
