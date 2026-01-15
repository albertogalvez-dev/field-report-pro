package com.fieldreportpro.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timeline_events",
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["reportId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["reportId"])]
)
data class TimelineEventEntity(
    @PrimaryKey val id: String,
    val reportId: String,
    val type: TimelineEventTypeEntity,
    val message: String,
    val timestamp: Long
)
