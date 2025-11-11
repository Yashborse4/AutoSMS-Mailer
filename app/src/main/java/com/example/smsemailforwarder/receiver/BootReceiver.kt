package com.example.smsemailforwarder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.smsemailforwarder.service.BackgroundService
import com.example.smsemailforwarder.util.Prefs

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.i(TAG, "BootReceiver onReceive action=$action")
        val shouldStart = Prefs.isBackgroundServiceEnabled(context)
        if (!shouldStart) {
            Log.i(TAG, "BackgroundService disabled by user; not starting")
            return
        }
        val matches = action == Intent.ACTION_BOOT_COMPLETED ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && action == Intent.ACTION_LOCKED_BOOT_COMPLETED) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && action == Intent.ACTION_USER_UNLOCKED) ||
                action == Intent.ACTION_MY_PACKAGE_REPLACED
        if (matches) {
            val svc = Intent(context, BackgroundService::class.java)
            ContextCompat.startForegroundService(context, svc)
            Log.i(TAG, "Requested startForegroundService for BackgroundService")
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
