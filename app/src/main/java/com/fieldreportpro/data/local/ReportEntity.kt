package com.fieldreportpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val refCode: String,
    val title: String,
    val category: ReportCategory,
    val priority: ReportPriority,
    val locationText: String,
    val unit: String,
    val description: String,
    val status: ReportStatus,
    val syncState: SyncState,
    val syncProgress: Int,
    val createdAt: Long,
    val updatedAt: Long
)
