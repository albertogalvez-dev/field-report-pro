package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.fieldreportpro.data.FakeReportRepository
import com.fieldreportpro.domain.ui_models.ReportDetailUi


data class DetailUiState(
    val reportDetail: ReportDetailUi
)

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository = FakeReportRepository
    private val reportId = savedStateHandle.get<String>("id") ?: "1"

    val uiState = DetailUiState(
        reportDetail = repository.getReportDetail(reportId)
    )
}
