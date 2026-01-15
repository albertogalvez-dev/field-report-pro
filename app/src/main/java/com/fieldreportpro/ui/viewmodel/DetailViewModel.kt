package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldreportpro.domain.ReportRepository
import com.fieldreportpro.domain.ui_models.ReportDetailUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DetailUiState(
    val reportDetail: ReportDetailUi
)

class DetailViewModel(
    reportId: String,
    repository: ReportRepository
) : ViewModel() {
    val uiState: StateFlow<DetailUiState> = repository.observeReportDetail(reportId)
        .map { detail -> DetailUiState(reportDetail = detail) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            DetailUiState(ReportDetailUi.Empty)
        )
}
