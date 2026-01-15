package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.fieldreportpro.data.FakeReportRepository
import com.fieldreportpro.domain.ui_models.SyncUiState

class SyncViewModel : ViewModel() {
    private val repository = FakeReportRepository

    val uiState: SyncUiState = repository.getSyncState()
}
