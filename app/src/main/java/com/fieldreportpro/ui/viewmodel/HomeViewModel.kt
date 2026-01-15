package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldreportpro.domain.ReportRepository
import com.fieldreportpro.domain.ui_models.HomeSummaryUi
import com.fieldreportpro.domain.ui_models.ReportUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val summary: HomeSummaryUi,
    val recentActivity: List<ReportUi>
)

class HomeViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeHomeSummary(),
        repository.observeRecentActivity()
    ) { summary, recentActivity ->
        HomeUiState(summary = summary, recentActivity = recentActivity)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState(
            summary = HomeSummaryUi(0, 0, 0),
            recentActivity = emptyList()
        )
    )
}
