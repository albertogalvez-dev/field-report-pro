package com.fieldreportpro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fieldreportpro.ui.viewmodel.ReportFilterType
import com.fieldreportpro.ui.viewmodel.ReportsFilters

@Composable
fun FilterChipRow(
    filters: ReportsFilters,
    onToggle: (ReportFilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(label = "Category", selected = filters.category, onClick = { onToggle(ReportFilterType.Category) })
        FilterChip(label = "Priority", selected = filters.priority, onClick = { onToggle(ReportFilterType.Priority) })
        FilterChip(label = "Status", selected = filters.status, onClick = { onToggle(ReportFilterType.Status) })
        FilterChip(label = "Date", selected = filters.date, onClick = { onToggle(ReportFilterType.Date) })
    }
}
