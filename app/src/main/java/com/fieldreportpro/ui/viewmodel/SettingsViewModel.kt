package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.fieldreportpro.data.FakeReportRepository
import com.fieldreportpro.domain.ui_models.SettingsUiState
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val repository = FakeReportRepository
    val uiState: StateFlow<SettingsUiState> = repository.settingsState

    fun setOfflineMode(enabled: Boolean) {
        repository.updateSettings { it.copy(offlineModeSimulated = enabled) }
    }

    fun setAutoSyncWifi(enabled: Boolean) {
        repository.updateSettings { it.copy(autoSyncWifi = enabled) }
    }

    fun setCompressPhotos(enabled: Boolean) {
        repository.updateSettings { it.copy(compressPhotos = enabled) }
    }
}
