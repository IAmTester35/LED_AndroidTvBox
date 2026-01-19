package com.reecotech.androidtvbox.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.reecotech.androidtvbox.MainActivity
import timber.log.Timber

class AutoStartService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Timber.i("AutoStartService Connected")
        
        // Try to start MainActivity when service connects (usually after boot)
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
            Timber.i("AutoStartService: MainActivity start requested on connection")
        } catch (e: Exception) {
            Timber.e(e, "AutoStartService: Failed to start MainActivity")
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We can listen for specific events to ensure app stays in foreground if needed
    }

    override fun onInterrupt() {
        Timber.w("AutoStartService Interrupted")
    }
}
