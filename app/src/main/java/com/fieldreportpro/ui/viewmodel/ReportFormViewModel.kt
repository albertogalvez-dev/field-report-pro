package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldreportpro.domain.ReportRepository
import com.fieldreportpro.domain.ui_models.ReportFormData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class ReportFormViewModel(
    private val repository: ReportRepository
) : ViewModel() {
    private val reportIdState = MutableStateFlow<String?>(null)

    val attachments = reportIdState.flatMapLatest { reportId ->
        if (reportId == null) {
            flowOf(emptyList())
        } else {
            repository.observeReportDetail(reportId).map { detail -> detail.attachments }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun addAttachment(formData: ReportFormData, uri: android.net.Uri): Boolean {
        val reportId = ensureReportId(formData)
        return repository.addAttachmentFromUri(reportId, uri)
    }

    suspend fun saveDraft(formData: ReportFormData): String {
        val currentId = reportIdState.value
        return if (currentId == null) {
            val id = repository.createDraft(formData)
            reportIdState.value = id
            id
        } else {
            repository.updateDraft(currentId, formData)
            currentId
        }
    }

    suspend fun queueForSync(formData: ReportFormData): String {
        val id = saveDraft(formData)
        repository.queueForSync(id)
        return id
    }

    private suspend fun ensureReportId(formData: ReportFormData): String {
        val currentId = reportIdState.value
        if (currentId != null) {
            return currentId
        }
        val draftData = formData.copy(
            title = formData.title.ifBlank { "Untitled report" }
        )
        val id = repository.createDraft(draftData)
        reportIdState.value = id
        return id
    }
}
