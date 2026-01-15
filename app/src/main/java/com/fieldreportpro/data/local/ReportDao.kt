package com.fieldreportpro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports ORDER BY updatedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ReportEntity?>

    @Query("SELECT * FROM reports WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ReportEntity?

    @Query("SELECT COUNT(*) FROM reports")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM reports WHERE status = :status")
    fun countByStatus(status: ReportStatus): Flow<Int>

    @Query("SELECT * FROM reports WHERE status = 'PENDING_SYNC'")
    suspend fun getPendingForSync(): List<ReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: ReportEntity)

    @Update
    suspend fun update(report: ReportEntity)

    @Query(
        "UPDATE reports SET status = :status, syncState = :syncState, " +
            "syncProgress = :progress, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun updateStatus(
        id: String,
        status: ReportStatus,
        syncState: SyncState,
        progress: Int,
        updatedAt: Long
    )

    @Query(
        "UPDATE reports SET syncState = :syncState, syncProgress = :progress, " +
            "updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun updateSyncProgress(
        id: String,
        syncState: SyncState,
        progress: Int,
        updatedAt: Long
    )
}
