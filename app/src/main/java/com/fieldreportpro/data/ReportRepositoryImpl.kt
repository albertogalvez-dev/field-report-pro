package com.fieldreportpro.data

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
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
import com.fieldreportpro.domain.ReportRepository
import com.fieldreportpro.domain.ui_models.AccentColorType
import com.fieldreportpro.domain.ui_models.AttachmentUi
import com.fieldreportpro.domain.ui_models.HomeSummaryUi
import com.fieldreportpro.domain.ui_models.PriorityUi
import com.fieldreportpro.domain.ui_models.ReportDetailUi
import com.fieldreportpro.domain.ui_models.ReportFormData
import com.fieldreportpro.domain.ui_models.ReportUi
import com.fieldreportpro.domain.ui_models.SettingsUiState
import com.fieldreportpro.domain.ui_models.StatusUi
import com.fieldreportpro.domain.ui_models.SyncItemStatus
import com.fieldreportpro.domain.ui_models.SyncQueueItemUi
import com.fieldreportpro.domain.ui_models.SyncUiState
import com.fieldreportpro.domain.ui_models.TimelineEventType
import com.fieldreportpro.domain.ui_models.TimelineEventUi
import com.fieldreportpro.domain.ui_models.TimelineIconType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

class ReportRepositoryImpl(
    private val appContext: Context,
    private val reportDao: ReportDao,
    private val attachmentDao: AttachmentDao,
    private val timelineDao: TimelineDao,
    private val settingsDataStore: SettingsDataStore,
    private val workManager: WorkManager
) : ReportRepository {
    override fun observeHomeSummary(): Flow<HomeSummaryUi> {
        return combine(
            reportDao.countByStatus(ReportStatus.SYNCED),
            reportDao.countByStatus(ReportStatus.DRAFT),
            reportDao.countByStatus(ReportStatus.PENDING_SYNC)
        ) { synced, draft, pending ->
            HomeSummaryUi(
                syncedCount = synced,
                draftCount = draft,
                pendingCount = pending
            )
        }
    }

    override fun observeRecentActivity(): Flow<List<ReportUi>> {
        return combine(
            reportDao.observeRecent(5),
            attachmentDao.observeCounts()
        ) { reports, counts ->
            val countMap = counts.associateBy({ it.reportId }, { it.count })
            reports.map { report ->
                report.toUi(attachmentCount = countMap[report.id] ?: 0)
            }
        }
    }

    override fun observeReports(query: String): Flow<List<ReportUi>> {
        val trimmed = query.trim()
        return combine(
            reportDao.observeAll(),
            attachmentDao.observeCounts()
        ) { reports, counts ->
            val countMap = counts.associateBy({ it.reportId }, { it.count })
            reports
                .filter { report ->
                    trimmed.isBlank() ||
                        report.title.contains(trimmed, ignoreCase = true) ||
                        report.refCode.contains(trimmed, ignoreCase = true)
                }
                .map { report ->
                    report.toUi(attachmentCount = countMap[report.id] ?: 0)
                }
        }
    }

    override fun observeReportDetail(id: String): Flow<ReportDetailUi> {
        return combine(
            reportDao.observeById(id),
            attachmentDao.listByReportId(id),
            timelineDao.listByReportId(id)
        ) { report, attachments, timeline ->
            if (report == null) {
                ReportDetailUi.Empty
            } else {
                val attachmentUi = attachments.map { it.toUi() }
                ReportDetailUi(
                    report = report.toUi(attachmentCount = attachmentUi.size),
                    description = report.description,
                    locationLabel = report.locationText,
                    attachments = attachmentUi,
                    timeline = timeline.map { it.toUi() }
                )
            }
        }
    }

    override fun observeSettings(): Flow<SettingsUiState> {
        return settingsDataStore.settings.map { settings ->
            SettingsUiState(
                offlineModeSimulated = settings.offlineModeSimulated,
                autoSyncWifi = settings.autoSyncWifi,
                compressPhotos = settings.compressPhotos,
                lastSyncLabel = TimeFormatters.lastSyncedLabel(settings.lastSyncAt)
            )
        }
    }

    override fun observeSyncState(): Flow<SyncUiState> {
        return combine(
            settingsDataStore.settings,
            reportDao.observeAll()
        ) { settings, reports ->
            val queuedReports = reports.filter {
                it.status == ReportStatus.PENDING_SYNC || it.syncState == SyncState.FAILED
            }
            val queueItems = queuedReports.mapNotNull { report -> report.toSyncQueueItem() }
            SyncUiState(
                systemOnline = !settings.offlineModeSimulated,
                lastSyncLabel = TimeFormatters.lastSyncLabel(settings.lastSyncAt),
                dataUsageLabel = "DATA USAGE 24.5 MB today",
                queuedRemainingLabel = "${queueItems.size} remaining",
                queuedItems = queueItems,
                snackbarMessage = "Successfully synced 3 reports"
            )
        }
    }

    override suspend fun createDraft(formData: ReportFormData): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val report = ReportEntity(
            id = id,
            refCode = generateRefCode(),
            title = formData.title,
            category = ReportCategory.fromLabel(formData.category),
            priority = formData.priority.toEntity(),
            locationText = formData.locationText,
            unit = formData.unit,
            description = formData.description,
            status = ReportStatus.DRAFT,
            syncState = SyncState.WAITING,
            syncProgress = 0,
            createdAt = now,
            updatedAt = now
        )
        reportDao.upsert(report)
        timelineDao.insert(
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = id,
                type = TimelineEventTypeEntity.CREATED,
                message = "Draft saved",
                timestamp = now
            )
        )
        return id
    }

    override suspend fun updateDraft(id: String, formData: ReportFormData) {
        val existing = reportDao.getById(id) ?: return
        val now = System.currentTimeMillis()
        reportDao.update(
            existing.copy(
                title = formData.title,
                category = ReportCategory.fromLabel(formData.category),
                priority = formData.priority.toEntity(),
                locationText = formData.locationText,
                unit = formData.unit,
                description = formData.description,
                updatedAt = now
            )
        )
        timelineDao.insert(
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = id,
                type = TimelineEventTypeEntity.EDITED,
                message = "Details updated",
                timestamp = now
            )
        )
    }

    override suspend fun addAttachment(reportId: String, uri: String) {
        if (attachmentDao.countByReportId(reportId) >= 3) {
            return
        }
        val now = System.currentTimeMillis()
        attachmentDao.insert(
            AttachmentEntity(
                id = UUID.randomUUID().toString(),
                reportId = reportId,
                uri = uri,
                annotatedUri = null,
                createdAt = now
            )
        )
        val report = reportDao.getById(reportId)
        if (report != null) {
            reportDao.update(report.copy(updatedAt = now))
        }
        timelineDao.insert(
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = reportId,
                type = TimelineEventTypeEntity.PHOTOS_ADDED,
                message = "Attachment added",
                timestamp = now
            )
        )
    }

    override suspend fun queueForSync(reportId: String) {
        val now = System.currentTimeMillis()
        reportDao.updateStatus(
            id = reportId,
            status = ReportStatus.PENDING_SYNC,
            syncState = SyncState.WAITING,
            progress = 0,
            updatedAt = now
        )
        timelineDao.insert(
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                reportId = reportId,
                type = TimelineEventTypeEntity.QUEUED,
                message = "Waiting for Wi-Fi",
                timestamp = now
            )
        )
    }

    override suspend fun setOfflineModeSimulated(enabled: Boolean) {
        settingsDataStore.setOfflineModeSimulated(enabled)
    }

    override suspend fun setAutoSyncWifi(enabled: Boolean) {
        settingsDataStore.setAutoSyncWifi(enabled)
    }

    override suspend fun setCompressPhotos(enabled: Boolean) {
        settingsDataStore.setCompressPhotos(enabled)
    }

    override suspend fun syncNow() {
        val workRequest = OneTimeWorkRequestBuilder<com.fieldreportpro.data.sync.SyncWorker>().build()
        workManager.enqueueUniqueWork("sync_now", ExistingWorkPolicy.REPLACE, workRequest)
    }

    override suspend fun seedIfEmpty() {
        SeedData.seedIfEmpty(
            context = appContext,
            reportDao = reportDao,
            attachmentDao = attachmentDao,
            timelineDao = timelineDao,
            settingsDataStore = settingsDataStore
        )
    }

    private fun generateRefCode(): String {
        val year = LocalDate.now().year
        val suffix = (100..999).random()
        return "REF-$year-$suffix"
    }
}

private fun ReportEntity.toUi(attachmentCount: Int): ReportUi {
    return ReportUi(
        id = id,
        refCode = refCode,
        title = title,
        location = locationText,
        unit = unit,
        priority = priority.toUi(),
        status = status.toUi(),
        updatedLabel = TimeFormatters.updatedLabel(updatedAt),
        hasAttachments = attachmentCount > 0,
        attachmentsCount = attachmentCount
    )
}

private fun AttachmentEntity.toUi(): AttachmentUi {
    return AttachmentUi(
        id = id,
        reportId = reportId,
        thumbnailResOrUrl = uri,
        annotated = annotatedUri != null
    )
}

private fun TimelineEventEntity.toUi(): TimelineEventUi {
    val (title, iconType, accentColorType, type) = when (type) {
        TimelineEventTypeEntity.SYNCED -> Quad(
            "Synced to cloud",
            TimelineIconType.Cloud,
            AccentColorType.Green,
            TimelineEventType.Synced
        )
        TimelineEventTypeEntity.QUEUED -> Quad(
            "Queued for sync",
            TimelineIconType.Queue,
            AccentColorType.Blue,
            TimelineEventType.Queued
        )
        TimelineEventTypeEntity.PHOTOS_ADDED -> Quad(
            "Photos added",
            TimelineIconType.Photo,
            AccentColorType.Amber,
            TimelineEventType.PhotosAdded
        )
        TimelineEventTypeEntity.EDITED -> Quad(
            "Details updated",
            TimelineIconType.Edit,
            AccentColorType.Gray,
            TimelineEventType.DetailsUpdated
        )
        TimelineEventTypeEntity.CREATED -> Quad(
            "Report created",
            TimelineIconType.Create,
            AccentColorType.Gray,
            TimelineEventType.Created
        )
        TimelineEventTypeEntity.FAILED -> Quad(
            "Sync failed",
            TimelineIconType.Cloud,
            AccentColorType.Red,
            TimelineEventType.Queued
        )
    }
    return TimelineEventUi(
        id = id,
        type = type,
        title = title,
        subtitle = message,
        timeLabel = TimeFormatters.shortRelative(timestamp),
        iconType = iconType,
        accentColorType = accentColorType
    )
}

private fun ReportEntity.toSyncQueueItem(): SyncQueueItemUi? {
    return when (syncState) {
        SyncState.UPLOADING -> SyncQueueItemUi(
            id = id,
            title = "Uploading report $refCode",
            subtitle = "${syncProgress}% complete",
            status = SyncItemStatus.Uploading,
            progress = (syncProgress / 100f).coerceIn(0f, 1f)
        )
        SyncState.FAILED -> SyncQueueItemUi(
            id = id,
            title = "Failed to upload $refCode",
            subtitle = "Tap to retry",
            status = SyncItemStatus.Failed
        )
        SyncState.WAITING -> SyncQueueItemUi(
            id = id,
            title = "Waiting for network",
            subtitle = "Queued for Wi-Fi",
            status = SyncItemStatus.Waiting
        )
        SyncState.DONE -> null
    }
}

private fun ReportPriority.toUi(): PriorityUi = when (this) {
    ReportPriority.Low -> PriorityUi.Low
    ReportPriority.Med -> PriorityUi.Med
    ReportPriority.High -> PriorityUi.High
    ReportPriority.Crit -> PriorityUi.Crit
}

private fun PriorityUi.toEntity(): ReportPriority = when (this) {
    PriorityUi.Low -> ReportPriority.Low
    PriorityUi.Med -> ReportPriority.Med
    PriorityUi.High -> ReportPriority.High
    PriorityUi.Crit -> ReportPriority.Crit
}

private fun ReportStatus.toUi(): StatusUi = when (this) {
    ReportStatus.DRAFT -> StatusUi.Draft
    ReportStatus.PENDING_SYNC -> StatusUi.Pending
    ReportStatus.SYNCED -> StatusUi.Synced
    ReportStatus.ERROR -> StatusUi.Error
}

private data class Quad<T, U, V, W>(val first: T, val second: U, val third: V, val fourth: W)
