package com.example.smsemailforwarder.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smsemailforwarder.R
import com.example.smsemailforwarder.smtp.EmailConfig
import com.example.smsemailforwarder.smtp.EmailSender
import com.example.smsemailforwarder.data.AppDatabase
import com.example.smsemailforwarder.data.SmsEmailLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmailForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val from = intent?.getStringExtra(EXTRA_FROM) ?: "Unknown"
        val body = intent?.getStringExtra(EXTRA_BODY) ?: ""
        Log.i(TAG, "Start email send: from='$from' bodyLen=${body.length}")

        val notification = buildNotification("Forwarding SMS from $from")
        startForeground(NOTIF_ID, notification)

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.get(this@EmailForegroundService).logDao()
            val logId = dao.insert(
                SmsEmailLog(
                    timestamp = System.currentTimeMillis(),
                    from = from,
                    body = body,
                    status = "PENDING",
                    error = null
                )
            )
            try {
                val subject = "SMS from $from"
                val host = EmailConfig.host(this@EmailForegroundService)
                val port = EmailConfig.port(this@EmailForegroundService)
                val username = EmailConfig.username(this@EmailForegroundService)
                val password = EmailConfig.password(this@EmailForegroundService)
                val fromEmail = EmailConfig.fromEmail(this@EmailForegroundService)
                val toEmail = EmailConfig.toEmail(this@EmailForegroundService)
                val useSsl = EmailConfig.useSsl(this@EmailForegroundService)

                if (username.isBlank() || password.isBlank() || toEmail.isBlank()) {
                    throw IllegalStateException("Email config incomplete. Open Settings to configure.")
                }

                EmailSender.send(
                    host = host,
                    port = port,
                    username = username,
                    password = password,
                    from = fromEmail,
                    to = toEmail,
                    subject = subject,
                    body = body,
                    useSsl = useSsl,
                )
                dao.updateStatus(logId, "SENT", null)
                Log.i(TAG, "Email sent OK")
            } catch (t: Throwable) {
                dao.updateStatus(logId, "FAILED", t.message ?: "Unknown error")
                Log.e(TAG, "Email send failed", t)
            } finally {
                stopForeground(STOP_FOREGROUND_DETACH)
                stopSelf(startId)
            }
        }

        return START_NOT_STICKY
    }

    private fun buildNotification(text: String): Notification {
        val channelId = "email_forward_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(channelId, "SMS Email Forwarding", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(ch)
        }
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_email_notify)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val EXTRA_FROM = "extra_from"
        const val EXTRA_BODY = "extra_body"
        private const val NOTIF_ID = 1001
        private const val TAG = "EmailFgService"
    }
}
