package com.example.smsemailforwarder.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object PrefsEmail {
    private const val PREFS_NAME = "email_config"

    private fun prefs(ctx: Context) = EncryptedSharedPreferences.create(
        ctx,
        PREFS_NAME,
        MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Simple config: just these three
    fun email(ctx: Context) = prefs(ctx).getString("email", "") ?: ""
    fun appPassword(ctx: Context) = prefs(ctx).getString("app_password", "") ?: ""
    fun toEmail(ctx: Context) = prefs(ctx).getString("to_email", "") ?: ""

    fun saveSimple(
        ctx: Context,
        email: String,
        appPassword: String,
        toEmail: String,
    ) {
        prefs(ctx).edit()
            .putString("email", email.trim())
            .putString("app_password", appPassword)
            .putString("to_email", toEmail.trim())
            .apply()
    }
}
