package com.fieldreportpro.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fieldreportpro.data.local.AppDatabase
import com.fieldreportpro.data.local.ReportStatus
import com.fieldreportpro.data.local.SyncState
import com.fieldreportpro.data.local.TimelineEventEntity
import com.fieldreportpro.data.local.TimelineEventTypeEntity
import com.fieldreportpro.data.settings.SettingsDataStore
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(applicationContext)
        val reportDao = database.reportDao()
        val timelineDao = database.timelineDao()
        val settingsStore = SettingsDataStore(applicationContext)
        val settings = settingsStore.settings.first()

        if (settings.offlineModeSimulated) {
            val pendingReports = reportDao.getPendingForSync()
            val now = System.currentTimeMillis()
            pendingReports.forEach { report ->
                reportDao.updateStatus(
                    id = report.id,
                    status = ReportStatus.PENDING_SYNC,
                    syncState = SyncState.WAITING,
                    progress = 0,
                    updatedAt = now
                )
            }
            return Result.success()
        }

        val pendingReports = reportDao.getPendingForSync()
        pendingReports.forEach { report ->
            val start = System.currentTimeMillis()
            reportDao.updateStatus(
                id = report.id,
                status = ReportStatus.PENDING_SYNC,
                syncState = SyncState.UPLOADING,
                progress = 0,
                updatedAt = start
            )
            val steps = listOf(20, 40, 60, 80, 100)
            steps.forEach { progress ->
                reportDao.updateSyncProgress(
                    id = report.id,
                    syncState = SyncState.UPLOADING,
                    progress = progress,
                    updatedAt = System.currentTimeMillis()
                )
                delay(250)
            }
            val doneAt = System.currentTimeMillis()
            reportDao.updateStatus(
                id = report.id,
                status = ReportStatus.SYNCED,
                syncState = SyncState.DONE,
                progress = 100,
                updatedAt = doneAt
            )
            timelineDao.insert(
                TimelineEventEntity(
                    id = UUID.randomUUID().toString(),
                    reportId = report.id,
                    type = TimelineEventTypeEntity.SYNCED,
                    message = "System Online",
                    timestamp = doneAt
                )
            )
        }

        if (pendingReports.isNotEmpty()) {
            settingsStore.setLastSyncAt(System.currentTimeMillis())
        }

        return Result.success()
    }
}
