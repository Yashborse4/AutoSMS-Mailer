package com.example.smsemailforwarder.smtp

import android.content.Context
import com.example.smsemailforwarder.util.PrefsEmail

object EmailConfig {
    fun host(ctx: Context) = PrefsEmail.host(ctx)
    fun port(ctx: Context) = PrefsEmail.port(ctx)
    fun username(ctx: Context) = PrefsEmail.username(ctx)
    fun password(ctx: Context) = PrefsEmail.password(ctx)
    fun fromEmail(ctx: Context) = PrefsEmail.fromEmail(ctx)
    fun toEmail(ctx: Context) = PrefsEmail.toEmail(ctx)
    fun useSsl(ctx: Context) = PrefsEmail.useSsl(ctx)
}
