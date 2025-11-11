package com.example.smsemailforwarder.smtp

import android.content.Context
import com.example.smsemailforwarder.util.PrefsEmail

object EmailConfig {
    // Fixed Gmail SMTP; user provides only email, app password, and destination
    fun host(@Suppress("UNUSED_PARAMETER") ctx: Context) = "smtp.gmail.com"
    fun port(@Suppress("UNUSED_PARAMETER") ctx: Context) = 587 // STARTTLS
    fun useSsl(@Suppress("UNUSED_PARAMETER") ctx: Context) = false

    fun username(ctx: Context) = PrefsEmail.email(ctx)
    fun password(ctx: Context) = PrefsEmail.appPassword(ctx)
    fun fromEmail(ctx: Context) = PrefsEmail.email(ctx)
    fun toEmail(ctx: Context) = PrefsEmail.toEmail(ctx)
}
