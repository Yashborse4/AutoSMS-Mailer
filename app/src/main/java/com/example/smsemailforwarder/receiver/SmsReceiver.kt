package com.example.smsemailforwarder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.smsemailforwarder.service.EmailForegroundService

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val from = messages.firstOrNull()?.displayOriginatingAddress ?: "Unknown"
            val body = messages.joinToString(separator = "") { it.displayMessageBody }
            Log.i(TAG, "SMS received from=$from len=${body.length}")
            startEmailService(context, from, body)
        }
        // Optional: handle MMS via WAP_PUSH_RECEIVED if needed
    }

    private fun startEmailService(context: Context, from: String, body: String) {
        val svc = Intent(context, EmailForegroundService::class.java).apply {
            putExtra(EmailForegroundService.EXTRA_FROM, from)
            putExtra(EmailForegroundService.EXTRA_BODY, body)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, svc)
        } else {
            context.startService(svc)
        }
        Log.i(TAG, "Requested EmailForegroundService")
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
