package com.example.smsemailforwarder.smtp

import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    fun send(
        host: String,
        port: Int,
        username: String,
        password: String,
        from: String,
        to: String,
        subject: String,
        body: String,
        useSsl: Boolean,
    ) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            if (useSsl) {
                put("mail.smtp.socketFactory.port", port.toString())
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.port", port.toString())
            } else {
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.port", port.toString())
            }
            put("mail.smtp.host", host)
        }

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication =
                PasswordAuthentication(username, password)
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(from))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            setSubject(subject)
            setText(body)
        }

        Transport.send(message)
    }
}
