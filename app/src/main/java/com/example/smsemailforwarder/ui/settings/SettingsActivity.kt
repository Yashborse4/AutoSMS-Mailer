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

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAppPassword = findViewById<EditText>(R.id.etAppPassword)
        val etToEmail = findViewById<EditText>(R.id.etToEmail)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnTest = findViewById<Button>(R.id.btnTest)

        etEmail.setText(PrefsEmail.email(this))
        etAppPassword.setText(PrefsEmail.appPassword(this))
        etAppPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        etToEmail.setText(PrefsEmail.toEmail(this))

        btnSave.setOnClickListener {
            PrefsEmail.saveSimple(
                this,
                etEmail.text.toString(),
                etAppPassword.text.toString(),
                etToEmail.text.toString(),
            )
            finish()
        }

        btnTest.setOnClickListener {
            PrefsEmail.saveSimple(
                this,
                etEmail.text.toString(),
                etAppPassword.text.toString(),
                etToEmail.text.toString(),
            )
            // Optionally, you can return to main and use the Test button there.
        }
    }
}
