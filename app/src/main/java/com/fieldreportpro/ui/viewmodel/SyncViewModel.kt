package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldreportpro.domain.ReportRepository
import com.fieldreportpro.domain.ui_models.SyncUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SyncViewModel(
    private val repository: ReportRepository
) : ViewModel() {
    val uiState: StateFlow<SyncUiState> = repository.observeSyncState()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SyncUiState(
                systemOnline = true,
                lastSyncLabel = "Last sync: --",
                dataUsageLabel = "DATA USAGE 24.5 MB today",
                queuedRemainingLabel = "0 remaining",
                queuedItems = emptyList(),
                snackbarMessage = "Successfully synced 3 reports"
            )
        )

    fun syncNow() {
        viewModelScope.launch {
            repository.syncNow()
        }
    }

    fun retrySync(reportId: String) {
        viewModelScope.launch {
            repository.queueForSync(reportId)
            repository.syncNow()
        }
    }
}
