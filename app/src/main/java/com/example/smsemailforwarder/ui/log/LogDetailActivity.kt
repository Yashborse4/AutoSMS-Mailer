package com.example.smsemailforwarder.ui.log

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smsemailforwarder.R
import com.example.smsemailforwarder.data.AppDatabase
import com.example.smsemailforwarder.data.SmsEmailLog
import com.example.smsemailforwarder.service.EmailForegroundService
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_detail)
        title = getString(R.string.detail_title)

        val id = intent.getLongExtra(EXTRA_ID, -1)
        if (id <= 0) { finish(); return }

        val tvFrom = findViewById<TextView>(R.id.tvFrom)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvError = findViewById<TextView>(R.id.tvError)
        val tvBody = findViewById<TextView>(R.id.tvBody)
        val btnResend = findViewById<MaterialButton>(R.id.btnResend)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.get(this@LogDetailActivity).logDao()
            val log = dao.getById(id) ?: return@launch
            val ts = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", log.timestamp)
            runOnUiThread {
                tvFrom.text = getString(R.string.label_from) + ": " + log.from
                tvTime.text = getString(R.string.label_time) + ": " + ts
                tvStatus.text = getString(R.string.label_status) + ": " + log.status
                tvError.text = getString(R.string.label_error) + ": " + (log.error ?: "-")
                tvBody.text = log.body

                btnResend.setOnClickListener {
                    val i = Intent(this@LogDetailActivity, EmailForegroundService::class.java).apply {
                        putExtra(EmailForegroundService.EXTRA_FROM, log.from)
                        putExtra(EmailForegroundService.EXTRA_BODY, log.body)
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) startForegroundService(i) else startService(i)
                }
                btnDelete.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteById(id)
                        runOnUiThread { finish() }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}
