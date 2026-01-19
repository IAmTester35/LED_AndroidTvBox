package com.reecotech.androidtvbox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.reecotech.androidtvbox.MainActivity
import timber.log.Timber

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("BootCompletedReceiver onReceive: action=${intent.action}, data=${intent.dataString}")
        
        val actions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
        )

        if (intent.action in actions) {
            Timber.i("Boot event detected (${intent.action}). Starting services and MainActivity.")
            
            // Show a Toast to confirm receiver is triggered (helper for manual verification)
            try {
                android.widget.Toast.makeText(context, "App Boot detected: ${intent.action}", android.widget.Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Timber.w("Failed to show toast: ${e.message}")
            }
            
            // Start the foreground polling service FIRST (critical!)
            try {
                val serviceIntent = Intent(context, com.reecotech.androidtvbox.service.StationPollingService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
                Timber.i("StationPollingService start requested on boot")
            } catch (e: Exception) {
                Timber.e(e, "Failed to start StationPollingService on boot")
            }
            
            // Then start MainActivity
            val i = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(i)
            Timber.i("MainActivity start requested.")
        } else {
            Timber.d("Received intent action ${intent.action} which is not handled by this receiver.")
        }
    }
}
