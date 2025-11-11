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

    fun host(ctx: Context) = prefs(ctx).getString("host", "smtp.gmail.com") ?: "smtp.gmail.com"
    fun port(ctx: Context) = prefs(ctx).getInt("port", 587)
    fun username(ctx: Context) = prefs(ctx).getString("username", "") ?: ""
    fun password(ctx: Context) = prefs(ctx).getString("password", "") ?: ""
    fun fromEmail(ctx: Context) = prefs(ctx).getString("from", username(ctx)) ?: username(ctx)
    fun toEmail(ctx: Context) = prefs(ctx).getString("to", "") ?: ""
    fun useSsl(ctx: Context) = prefs(ctx).getBoolean("use_ssl", false)

    fun save(
        ctx: Context,
        host: String,
        port: Int,
        username: String,
        password: String,
        from: String,
        to: String,
        useSsl: Boolean,
    ) {
        prefs(ctx).edit()
            .putString("host", host.trim())
            .putInt("port", port)
            .putString("username", username.trim())
            .putString("password", password)
            .putString("from", from.trim())
            .putString("to", to.trim())
            .putBoolean("use_ssl", useSsl)
            .apply()
    }
}
