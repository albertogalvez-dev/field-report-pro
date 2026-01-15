package com.fieldreportpro.data

import android.content.Context
import com.fieldreportpro.data.local.AttachmentDao
import com.fieldreportpro.data.local.AttachmentEntity
import com.fieldreportpro.data.local.ReportCategory
import com.fieldreportpro.data.local.ReportDao
import com.fieldreportpro.data.local.ReportEntity
import com.fieldreportpro.data.local.ReportPriority
import com.fieldreportpro.data.local.ReportStatus
import com.fieldreportpro.data.local.SyncState
import com.fieldreportpro.data.local.TimelineDao
import com.fieldreportpro.data.local.TimelineEventEntity
import com.fieldreportpro.data.local.TimelineEventTypeEntity
import com.fieldreportpro.data.settings.SettingsDataStore
import java.util.UUID
import kotlinx.coroutines.flow.first

object SeedData {
    suspend fun seedIfEmpty(
        context: Context,
        reportDao: ReportDao,
        attachmentDao: AttachmentDao,
        timelineDao: TimelineDao,
        settingsDataStore: SettingsDataStore
    ) {
        if (reportDao.countAll() > 0) {
            return
        }

        val now = System.currentTimeMillis()
        val minute = 60_000L
        val hour = 60 * minute
        val baseUri = "android.resource://${context.packageName}/drawable"

        val reports = listOf(
            ReportEntity(
                id = "1",
                refCode = "REF-2023-89",
                title = "Cracked beam near intake valve",
                category = ReportCategory.Observation,
                priority = ReportPriority.High,
                locationText = "Zone 4, Sector B",
                unit = "Unit 4",
                description = "Inspection notes show stress fractures around the intake housing.",
                status = ReportStatus.PENDING_SYNC,
                syncState = SyncState.UPLOADING,
                syncProgress = 60,
                createdAt = now - 5 * hour,
                updatedAt = now - 12 * minute
            ),
            ReportEntity(
                id = "2",
                refCode = "REF-2023-90",
                title = "Loose conduit bracket",
                category = ReportCategory.Maintenance,
                priority = ReportPriority.Med,
                locationText = "Plant North, Bay 2",
                unit = "Unit 2",
                description = "Conduit bracket shows wear and needs tightening.",
                status = ReportStatus.DRAFT,
                syncState = SyncState.WAITING,
                syncProgress = 0,
                createdAt = now - 6 * hour,
                updatedAt = now - 28 * minute
            ),
            ReportEntity(
                id = "3",
                refCode = "REF-2023-91",
                title = "Leak detected at pressure line",
                category = ReportCategory.Safety,
                priority = ReportPriority.Crit,
                locationText = "Zone 2, Sector C",
                unit = "Unit 7",
                description = "Pressure line leak detected during routine inspection.",
                status = ReportStatus.PENDING_SYNC,
                syncState = SyncState.WAITING,
                syncProgress = 0,
                createdAt = now - 8 * hour,
                updatedAt = now - 60 * minute
            ),
            ReportEntity(
                id = "4",
                refCode = "REF-2023-92",
                title = "Corrosion on intake gate",
                category = ReportCategory.Quality,
                priority = ReportPriority.Low,
                locationText = "Turbine Hall",
                unit = "Unit 1",
                description = "Surface corrosion spotted on the intake gate hinge.",
                status = ReportStatus.SYNCED,
                syncState = SyncState.DONE,
                syncProgress = 100,
                createdAt = now - 12 * hour,
                updatedAt = now - 3 * hour
            ),
            ReportEntity(
                id = "5",
                refCode = "REF-2023-93",
                title = "Vibration spikes on pump",
                category = ReportCategory.Maintenance,
                priority = ReportPriority.High,
                locationText = "Zone 1, Sector A",
                unit = "Unit 3",
                description = "Pump vibration exceeds threshold during peak load.",
                status = ReportStatus.DRAFT,
                syncState = SyncState.FAILED,
                syncProgress = 0,
                createdAt = now - 16 * hour,
                updatedAt = now - 6 * hour
            )
        )

        reports.forEach { reportDao.upsert(it) }

        val photoUris = listOf(
            "$baseUri/demo_photo_1",
            "$baseUri/demo_photo_2",
            "$baseUri/demo_photo_3",
            "$baseUri/demo_photo_1",
            "$baseUri/demo_photo_2"
        )
        photoUris.forEachIndexed { index, uri ->
            attachmentDao.insert(
                AttachmentEntity(
                    id = "att-${index + 1}",
                    reportId = "1",
                    uri = uri,
                    annotatedUri = if (index % 2 == 0) uri else null,
                    createdAt = now - (index + 1) * minute
                )
            )
        }
        attachmentDao.insert(
            AttachmentEntity(
                id = "att-6",
                reportId = "4",
                uri = "$baseUri/demo_photo_3",
                annotatedUri = null,
                createdAt = now - 2 * hour
            )
        )

        val timelineEvents = listOf(
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = "1",
                type = TimelineEventTypeEntity.SYNCED,
                message = "System Online",
                timestamp = now - 2 * minute
            ),
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = "1",
                type = TimelineEventTypeEntity.QUEUED,
                message = "Waiting for Wi-Fi",
                timestamp = now - 10 * minute
            ),
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = "1",
                type = TimelineEventTypeEntity.PHOTOS_ADDED,
                message = "3 attachments",
                timestamp = now - 22 * minute
            ),
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = "1",
                type = TimelineEventTypeEntity.EDITED,
                message = "Priority set to High",
                timestamp = now - 40 * minute
            ),
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = "1",
                type = TimelineEventTypeEntity.CREATED,
                message = "Draft saved",
                timestamp = now - 60 * minute
            )
        )
        timelineDao.insertAll(timelineEvents)

        val settings = settingsDataStore.settings
        val lastSyncAt = settings.first().lastSyncAt
        if (lastSyncAt == 0L) {
            settingsDataStore.setLastSyncAt(now - 2 * minute)
        }
    }
}
