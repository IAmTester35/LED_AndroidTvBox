package com.reecotech.androidtvbox.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.reecotech.androidtvbox.MainActivity
import timber.log.Timber

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("BootCompletedReceiver onReceive: ${intent.action}")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            Timber.d("Boot completed, starting MainActivity")
            // Toast to verify receiver is working even if Activity doesn't start
            android.widget.Toast.makeText(context, "Boot detected, starting app...", android.widget.Toast.LENGTH_LONG).show()
            
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }
}
