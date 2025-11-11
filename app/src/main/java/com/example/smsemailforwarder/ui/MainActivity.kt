package com.example.smsemailforwarder.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smsemailforwarder.R
import com.example.smsemailforwarder.service.EmailForegroundService
import com.example.smsemailforwarder.service.BackgroundService
import com.example.smsemailforwarder.ui.log.LogActivity
import com.example.smsemailforwarder.ui.settings.SettingsActivity
import com.example.smsemailforwarder.util.AutoStartSettings
import com.example.smsemailforwarder.util.Prefs
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val requestPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bgSwitch = findViewById<SwitchCompat>(R.id.switchBackgroundService)
        val tvStatus = findViewById<TextView>(R.id.tvServiceStatus)
        val enabled = Prefs.isBackgroundServiceEnabled(this)
        bgSwitch.isChecked = enabled
        updateServiceStatus(tvStatus, enabled)
        if (enabled) {
            startForegroundServiceCompat(Intent(this, BackgroundService::class.java))
        }

        bgSwitch.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setBackgroundServiceEnabled(this, isChecked)
            updateServiceStatus(tvStatus, isChecked)
            if (isChecked) {
                startForegroundServiceCompat(Intent(this, BackgroundService::class.java))
            } else {
                stopService(Intent(this, BackgroundService::class.java))
            }
        }

        findViewById<Button>(R.id.btnViewHistory).setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }

        findViewById<Button>(R.id.btnRequestPerms).setOnClickListener {
            val perms = mutableListOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
            )
            if (Build.VERSION.SDK_INT >= 33) {
                perms += Manifest.permission.POST_NOTIFICATIONS
            }
            requestPerms.launch(perms.toTypedArray())
        }

        findViewById<Button>(R.id.btnBatteryOpt).setOnClickListener {
            requestIgnoreBatteryOptimizationsIfNeeded(this)
        }

        findViewById<Button>(R.id.btnAutostart).setOnClickListener {
            AutoStartSettings.openAutoStartSettings(this)
        }

        findViewById<Button>(R.id.btnTestEmail).setOnClickListener {
            // Fire a foreground service with a sample payload to test email sending
            val i = Intent(this, EmailForegroundService::class.java).apply {
                putExtra(EmailForegroundService.EXTRA_FROM, "Test")
                putExtra(EmailForegroundService.EXTRA_BODY, "This is a test email from SmsEmailForwarder.")
            }
            startForegroundServiceCompat(i)
        }

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun startForegroundServiceCompat(i: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
        else startService(i)
    }

    private fun updateServiceStatus(tv: TextView, enabled: Boolean) {
        tv.text = if (enabled) getString(R.string.status_running) else getString(R.string.status_stopped)
        val color = if (enabled) 0xFF2E7D32.toInt() else 0xFFC62828.toInt() // green/red
        tv.setTextColor(color)
    }

    private fun requestIgnoreBatteryOptimizationsIfNeeded(context: Context) {
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            val pkg = context.packageName
            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$pkg")
                }
                context.startActivity(intent)
            }
        } catch (_: Throwable) { /* best effort */ }
    }
}
