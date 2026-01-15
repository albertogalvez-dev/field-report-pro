package com.fieldreportpro.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attachments",
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
data class AttachmentEntity(
    @PrimaryKey val id: String,
    val reportId: String,
    val uri: String,
    val annotatedUri: String?,
    val createdAt: Long
)
