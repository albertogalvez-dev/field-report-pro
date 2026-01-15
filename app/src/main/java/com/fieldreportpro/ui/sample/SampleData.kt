package com.fieldreportpro.ui.sample

import com.fieldreportpro.domain.ui_models.AccentColorType
import com.fieldreportpro.domain.ui_models.AttachmentUi
import com.fieldreportpro.domain.ui_models.HomeSummaryUi
import com.fieldreportpro.domain.ui_models.PriorityUi
import com.fieldreportpro.domain.ui_models.ReportDetailUi
import com.fieldreportpro.domain.ui_models.ReportUi
import com.fieldreportpro.domain.ui_models.SettingsUiState
import com.fieldreportpro.domain.ui_models.StatusUi
import com.fieldreportpro.domain.ui_models.SyncItemStatus
import com.fieldreportpro.domain.ui_models.SyncQueueItemUi
import com.fieldreportpro.domain.ui_models.SyncUiState
import com.fieldreportpro.domain.ui_models.TimelineEventType
import com.fieldreportpro.domain.ui_models.TimelineEventUi
import com.fieldreportpro.domain.ui_models.TimelineIconType

object SampleData {
    val homeSummary = HomeSummaryUi(
        syncedCount = 128,
        draftCount = 3,
        pendingCount = 6
    )

    val reports: List<ReportUi> = listOf(
        ReportUi(
            id = "1",
            refCode = "REF-2023-89",
            title = "Cracked beam near intake valve",
            location = "Zone 4, Sector B",
            unit = "Unit 4",
            priority = PriorityUi.High,
            status = StatusUi.Pending,
            updatedLabel = "Updated 12m ago",
            hasAttachments = true,
            attachmentsCount = 5
        ),
        ReportUi(
            id = "2",
            refCode = "REF-2023-90",
            title = "Loose conduit bracket",
            location = "Plant North, Bay 2",
            unit = "Unit 2",
            priority = PriorityUi.Med,
            status = StatusUi.Draft,
            updatedLabel = "Updated 28m ago",
            hasAttachments = false,
            attachmentsCount = 0
        ),
        ReportUi(
            id = "3",
            refCode = "REF-2023-91",
            title = "Leak detected at pressure line",
            location = "Zone 2, Sector C",
            unit = "Unit 7",
            priority = PriorityUi.Crit,
            status = StatusUi.Pending,
            updatedLabel = "Updated 1h ago",
            hasAttachments = true,
            attachmentsCount = 2
        ),
        ReportUi(
            id = "4",
            refCode = "REF-2023-92",
            title = "Corrosion on intake gate",
            location = "Turbine Hall",
            unit = "Unit 1",
            priority = PriorityUi.Low,
            status = StatusUi.Synced,
            updatedLabel = "Updated 3h ago",
            hasAttachments = true,
            attachmentsCount = 3
        ),
        ReportUi(
            id = "5",
            refCode = "REF-2023-93",
            title = "Vibration spikes on pump",
            location = "Zone 1, Sector A",
            unit = "Unit 3",
            priority = PriorityUi.High,
            status = StatusUi.Error,
            updatedLabel = "Updated 6h ago",
            hasAttachments = true,
            attachmentsCount = 1
        )
    )

    val detailAttachments: List<AttachmentUi> = listOf(
        AttachmentUi(
            id = "att-1",
            reportId = "1",
            thumbnailResOrUrl = "android.resource://com.fieldreportpro/drawable/demo_photo_1",
            annotated = true
        ),
        AttachmentUi(
            id = "att-2",
            reportId = "1",
            thumbnailResOrUrl = "android.resource://com.fieldreportpro/drawable/demo_photo_2",
            annotated = false
        ),
        AttachmentUi(
            id = "att-3",
            reportId = "1",
            thumbnailResOrUrl = "android.resource://com.fieldreportpro/drawable/demo_photo_3",
            annotated = true
        ),
        AttachmentUi(
            id = "att-4",
            reportId = "1",
            thumbnailResOrUrl = "android.resource://com.fieldreportpro/drawable/demo_photo_1",
            annotated = false
        ),
        AttachmentUi(
            id = "att-5",
            reportId = "1",
            thumbnailResOrUrl = "android.resource://com.fieldreportpro/drawable/demo_photo_2",
            annotated = true
        )
    )

    val detailTimeline: List<TimelineEventUi> = listOf(
        TimelineEventUi(
            id = "t1",
            type = TimelineEventType.Synced,
            title = "Synced to cloud",
            subtitle = "System Online",
            timeLabel = "2m ago",
            iconType = TimelineIconType.Cloud,
            accentColorType = AccentColorType.Green
        ),
        TimelineEventUi(
            id = "t2",
            type = TimelineEventType.Queued,
            title = "Queued for sync",
            subtitle = "Waiting for Wi-Fi",
            timeLabel = "10m ago",
            iconType = TimelineIconType.Queue,
            accentColorType = AccentColorType.Blue
        ),
        TimelineEventUi(
            id = "t3",
            type = TimelineEventType.PhotosAdded,
            title = "Photos added",
            subtitle = "3 attachments",
            timeLabel = "22m ago",
            iconType = TimelineIconType.Photo,
            accentColorType = AccentColorType.Amber
        ),
        TimelineEventUi(
            id = "t4",
            type = TimelineEventType.DetailsUpdated,
            title = "Details updated",
            subtitle = "Priority set to High",
            timeLabel = "40m ago",
            iconType = TimelineIconType.Edit,
            accentColorType = AccentColorType.Gray
        ),
        TimelineEventUi(
            id = "t5",
            type = TimelineEventType.Created,
            title = "Report created",
            subtitle = "Draft saved",
            timeLabel = "1h ago",
            iconType = TimelineIconType.Create,
            accentColorType = AccentColorType.Gray
        )
    )

    val reportDetail = ReportDetailUi(
        report = reports.first(),
        description = "Inspection notes show stress fractures around the intake housing. Pressure dropped to 2 PSI during load testing.",
        locationLabel = "Zone 4, Sector B",
        attachments = detailAttachments,
        timeline = detailTimeline
    )

    val syncState = SyncUiState(
        systemOnline = true,
        lastSyncLabel = "Last sync: 2 mins ago",
        dataUsageLabel = "DATA USAGE 24.5 MB today",
        queuedRemainingLabel = "3 remaining",
        queuedItems = listOf(
            SyncQueueItemUi(
                id = "sync-1",
                title = "Uploading report REF-2023-89",
                subtitle = "60% complete",
                status = SyncItemStatus.Uploading,
                progress = 0.6f
            ),
            SyncQueueItemUi(
                id = "sync-2",
                title = "Failed to upload REF-2023-93",
                subtitle = "Tap to retry",
                status = SyncItemStatus.Failed,
                progress = null
            ),
            SyncQueueItemUi(
                id = "sync-3",
                title = "Waiting for network",
                subtitle = "Queued for Wi-Fi",
                status = SyncItemStatus.Waiting,
                progress = null
            )
        ),
        snackbarMessage = "Successfully synced 3 reports"
    )

    val settingsState = SettingsUiState(
        offlineModeSimulated = true,
        autoSyncWifi = true,
        compressPhotos = true,
        lastSyncLabel = "Last synced 2 mins ago"
    )
}
