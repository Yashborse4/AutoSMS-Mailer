package com.example.smsemailforwarder.ui.settings

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.smsemailforwarder.R
import com.example.smsemailforwarder.util.PrefsEmail

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.settings_title)

        val etHost = findViewById<EditText>(R.id.etHost)
        val etPort = findViewById<EditText>(R.id.etPort)
        val etUser = findViewById<EditText>(R.id.etUser)
        val etPass = findViewById<EditText>(R.id.etPass)
        val etFrom = findViewById<EditText>(R.id.etFrom)
        val etTo = findViewById<EditText>(R.id.etTo)
        val swSsl = findViewById<Switch>(R.id.swSsl)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnTest = findViewById<Button>(R.id.btnTest)

        etHost.setText(PrefsEmail.host(this))
        etPort.setText(PrefsEmail.port(this).toString())
        etUser.setText(PrefsEmail.username(this))
        etPass.setText(PrefsEmail.password(this))
        etPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        etFrom.setText(PrefsEmail.fromEmail(this))
        etTo.setText(PrefsEmail.toEmail(this))
        swSsl.isChecked = PrefsEmail.useSsl(this)

        btnSave.setOnClickListener {
            val host = etHost.text.toString()
            val port = etPort.text.toString().toIntOrNull() ?: 587
            val user = etUser.text.toString()
            val pass = etPass.text.toString()
            val from = etFrom.text.toString().ifBlank { user }
            val to = etTo.text.toString()
            val useSsl = swSsl.isChecked
            PrefsEmail.save(this, host, port, user, pass, from, to, useSsl)
            finish()
        }

        btnTest.setOnClickListener {
            // Start email service with a test body using current form values
            PrefsEmail.save(
                this,
                etHost.text.toString(),
                etPort.text.toString().toIntOrNull() ?: 587,
                etUser.text.toString(),
                etPass.text.toString(),
                etFrom.text.toString().ifBlank { etUser.text.toString() },
                etTo.text.toString(),
                swSsl.isChecked,
            )
            // Reuse existing test button flow in MainActivity by starting the service directly would be redundant here
            // Let the user go back and use "Send test email" or we could replicate that intent here if desired.
        }
    }
}
