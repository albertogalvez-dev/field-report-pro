package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.fieldreportpro.data.FakeReportRepository
import com.fieldreportpro.domain.ui_models.HomeSummaryUi
import com.fieldreportpro.domain.ui_models.ReportUi
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    val summary: HomeSummaryUi,
    val recentActivity: List<ReportUi>
)

class HomeViewModel : ViewModel() {
    private val repository = FakeReportRepository

    val uiState = HomeUiState(
        summary = repository.getHomeSummary(),
        recentActivity = repository.getRecentActivity()
    )

    val settingsState: StateFlow<com.fieldreportpro.domain.ui_models.SettingsUiState> =
        repository.settingsState
}
