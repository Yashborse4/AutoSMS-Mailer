package com.example.smsemailforwarder.util

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val PREFS_NAME = "settings"
    private const val KEY_BG_ENABLED = "background_enabled"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isBackgroundServiceEnabled(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_BG_ENABLED, true) // default enabled

    fun setBackgroundServiceEnabled(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_BG_ENABLED, enabled).apply()
    }
}
