package com.fieldreportpro.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fieldreportpro.data.FakeReportRepository
import com.fieldreportpro.domain.ui_models.ReportUi

private val defaultFilters = ReportsFilters(
    category = false,
    priority = false,
    status = false,
    date = false
)

data class ReportsFilters(
    val category: Boolean,
    val priority: Boolean,
    val status: Boolean,
    val date: Boolean
) {
    val isActive: Boolean
        get() = category || priority || status || date
}

data class ReportsUiState(
    val query: String,
    val filters: ReportsFilters,
    val reports: List<ReportUi>,
    val demoEmptyState: Boolean
)

class ReportsViewModel : ViewModel() {
    private val repository = FakeReportRepository

    var uiState by mutableStateOf(
        ReportsUiState(
            query = "",
            filters = defaultFilters,
            reports = repository.getReportsList(),
            demoEmptyState = false
        )
    )
        private set

    fun updateQuery(query: String) {
        uiState = uiState.copy(query = query)
    }

    fun toggleFilter(filter: ReportFilterType) {
        val updatedFilters = when (filter) {
            ReportFilterType.Category -> uiState.filters.copy(category = !uiState.filters.category)
            ReportFilterType.Priority -> uiState.filters.copy(priority = !uiState.filters.priority)
            ReportFilterType.Status -> uiState.filters.copy(status = !uiState.filters.status)
            ReportFilterType.Date -> uiState.filters.copy(date = !uiState.filters.date)
        }
        refreshReports(updatedFilters, uiState.demoEmptyState)
    }

    fun toggleDemoEmptyState(enabled: Boolean) {
        refreshReports(uiState.filters, enabled)
    }

    private fun refreshReports(filters: ReportsFilters, demoEmpty: Boolean) {
        val reports = if (filters.isActive || demoEmpty) emptyList() else repository.getReportsList()
        uiState = uiState.copy(filters = filters, reports = reports, demoEmptyState = demoEmpty)
    }
}

enum class ReportFilterType {
    Category,
    Priority,
    Status,
    Date
}
