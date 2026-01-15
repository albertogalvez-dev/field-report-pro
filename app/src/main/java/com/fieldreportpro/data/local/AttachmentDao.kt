package com.fieldreportpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE reportId = :reportId ORDER BY createdAt DESC")
    fun listByReportId(reportId: String): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AttachmentEntity?

    @Query("SELECT COUNT(*) FROM attachments WHERE reportId = :reportId")
    suspend fun countByReportId(reportId: String): Int

    @Query("SELECT reportId, COUNT(*) as count FROM attachments GROUP BY reportId")
    fun observeCounts(): Flow<List<AttachmentCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachment: AttachmentEntity)

    @Query("UPDATE attachments SET annotatedUri = :annotatedUri WHERE id = :id")
    suspend fun updateAnnotatedUri(id: String, annotatedUri: String)

    @Query("DELETE FROM attachments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM attachments WHERE reportId = :reportId")
    suspend fun deleteByReportId(reportId: String)
}
