package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldreportpro.domain.ReportRepository
import com.fieldreportpro.domain.ui_models.SettingsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: ReportRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = repository.observeSettings()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SettingsUiState(
                offlineModeSimulated = false,
                autoSyncWifi = true,
                compressPhotos = true,
                lastSyncLabel = "Last synced --"
            )
        )

    fun setOfflineMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setOfflineModeSimulated(enabled)
        }
    }

    fun setAutoSyncWifi(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAutoSyncWifi(enabled)
        }
    }

    fun setCompressPhotos(enabled: Boolean) {
        viewModelScope.launch {
            repository.setCompressPhotos(enabled)
        }
    }
}
