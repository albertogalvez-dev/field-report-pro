package com.fieldreportpro.domain

import com.fieldreportpro.domain.ui_models.HomeSummaryUi
import com.fieldreportpro.domain.ui_models.ReportDetailUi
import com.fieldreportpro.domain.ui_models.ReportFormData
import com.fieldreportpro.domain.ui_models.ReportUi
import com.fieldreportpro.domain.ui_models.SettingsUiState
import com.fieldreportpro.domain.ui_models.SyncUiState
import kotlinx.coroutines.flow.Flow
import android.net.Uri

interface ReportRepository {
    fun observeHomeSummary(): Flow<HomeSummaryUi>
    fun observeRecentActivity(): Flow<List<ReportUi>>
    fun observeReports(query: String): Flow<List<ReportUi>>
    fun observeReportDetail(id: String): Flow<ReportDetailUi>
    fun observeSettings(): Flow<SettingsUiState>
    fun observeSyncState(): Flow<SyncUiState>
    suspend fun createDraft(formData: ReportFormData): String
    suspend fun updateDraft(id: String, formData: ReportFormData)
    suspend fun addAttachmentFromUri(reportId: String, uri: Uri): Boolean
    suspend fun markAttachmentAnnotated(attachmentId: String, annotatedUri: Uri)
    suspend fun removeAttachment(attachmentId: String)
    suspend fun queueForSync(reportId: String)
    suspend fun setOfflineModeSimulated(enabled: Boolean)
    suspend fun setAutoSyncWifi(enabled: Boolean)
    suspend fun setCompressPhotos(enabled: Boolean)
    suspend fun syncNow()
    suspend fun seedIfEmpty()
}
