package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fieldreportpro.domain.ReportRepository
import com.fieldreportpro.domain.ui_models.ReportUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi

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

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val repository: ReportRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val filters = MutableStateFlow(defaultFilters)
    private val demoEmpty = MutableStateFlow(false)

    private val reportsFlow = query.flatMapLatest { repository.observeReports(it) }

    val uiState: StateFlow<ReportsUiState> = combine(
        query,
        filters,
        demoEmpty,
        reportsFlow
    ) { queryValue, filterValue, demoValue, reports ->
        val visibleReports = if (filterValue.isActive || demoValue) emptyList() else reports
        ReportsUiState(
            query = queryValue,
            filters = filterValue,
            reports = visibleReports,
            demoEmptyState = demoValue
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ReportsUiState(
            query = "",
            filters = defaultFilters,
            reports = emptyList(),
            demoEmptyState = false
        )
    )

    fun updateQuery(query: String) {
        this.query.value = query
    }

    fun toggleFilter(filter: ReportFilterType) {
        val current = filters.value
        val updatedFilters = when (filter) {
            ReportFilterType.Category -> current.copy(category = !current.category)
            ReportFilterType.Priority -> current.copy(priority = !current.priority)
            ReportFilterType.Status -> current.copy(status = !current.status)
            ReportFilterType.Date -> current.copy(date = !current.date)
        }
        filters.value = updatedFilters
    }

    fun toggleDemoEmptyState(enabled: Boolean) {
        demoEmpty.value = enabled
    }
}

enum class ReportFilterType {
    Category,
    Priority,
    Status,
    Date
}
