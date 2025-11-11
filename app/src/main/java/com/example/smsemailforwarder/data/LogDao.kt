package com.example.smsemailforwarder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert
    suspend fun insert(log: SmsEmailLog): Long

    @Query("UPDATE sms_email_log SET status = :status, error = :error WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, error: String?)

    @Query("DELETE FROM sms_email_log WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM sms_email_log")
    suspend fun deleteAll()

    @Query("SELECT * FROM sms_email_log ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SmsEmailLog>>

    @Query("SELECT * FROM sms_email_log WHERE (\"from\" LIKE :q OR body LIKE :q) ORDER BY timestamp DESC")
    fun searchAll(q: String): Flow<List<SmsEmailLog>>

    @Query("SELECT * FROM sms_email_log WHERE status = :status AND (\"from\" LIKE :q OR body LIKE :q) ORDER BY timestamp DESC")
    fun searchByStatus(status: String, q: String): Flow<List<SmsEmailLog>>

    @Query("SELECT * FROM sms_email_log WHERE id = :id")
    suspend fun getById(id: Long): SmsEmailLog?
}
