package com.fieldreportpro.data

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FakeReportRepository {
    private val attachmentUrls = listOf(
        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=500&q=60"
    )

    private val reports = listOf(
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

    private val detailAttachments = attachmentUrls.mapIndexed { index, url ->
        AttachmentUi(
            id = "att-${index + 1}",
            reportId = "1",
            thumbnailResOrUrl = url,
            annotated = index % 2 == 0
        )
    }

    private val detailTimeline = listOf(
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

    private val _settingsState = MutableStateFlow(
        SettingsUiState(
            offlineModeSimulated = false,
            autoSyncWifi = true,
            compressPhotos = true
        )
    )
    val settingsState = _settingsState.asStateFlow()

    fun updateSettings(transform: (SettingsUiState) -> SettingsUiState) {
        _settingsState.value = transform(_settingsState.value)
    }

    fun getHomeSummary(): HomeSummaryUi {
        return HomeSummaryUi(
            syncedCount = 128,
            draftCount = 3,
            pendingCount = 6
        )
    }

    fun getRecentActivity(): List<ReportUi> = reports

    fun getReportsList(): List<ReportUi> = reports

    fun getReportDetail(reportId: String): ReportDetailUi {
        val report = reports.firstOrNull { it.id == reportId } ?: reports.first()
        return ReportDetailUi(
            report = report,
            description = "Inspection notes show stress fractures around the intake housing. Pressure dropped to 2 PSI during load testing.",
            locationLabel = "Zone 4, Sector B",
            attachments = detailAttachments,
            timeline = detailTimeline
        )
    }

    fun getAttachmentsPreview(): List<AttachmentUi> = detailAttachments.take(3)

    fun getSyncState(): SyncUiState {
        return SyncUiState(
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
    }

    fun saveDraft(title: String) {
        // Demo-only: no persistence yet.
    }

    fun queueReport(title: String) {
        // Demo-only: no persistence yet.
    }

    fun mapPreviewUrl(): String =
        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=800&q=60"
}
