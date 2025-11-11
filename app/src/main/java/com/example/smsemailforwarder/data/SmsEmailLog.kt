package com.example.smsemailforwarder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_email_log")
data class SmsEmailLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val from: String,
    val body: String,
    val status: String, // PENDING, SENT, FAILED
    val error: String? = null,
)
