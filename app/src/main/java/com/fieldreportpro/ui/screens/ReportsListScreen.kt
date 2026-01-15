package com.fieldreportpro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fieldreportpro.BuildConfig
import com.fieldreportpro.ui.components.EmptyState
import com.fieldreportpro.ui.components.FilterChipRow
import com.fieldreportpro.ui.components.ReportActivityCard
import com.fieldreportpro.ui.components.SearchBar
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.viewmodel.ReportsViewModel

@Composable
fun ReportsListScreen(
    onCreateReport: () -> Unit,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    showDemoToggle: Boolean = BuildConfig.DEBUG,
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppDimens.Spacing16),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)
    ) {
        SearchBar(
            query = uiState.query,
            placeholder = "Search reports, IDs, or tags...",
            onQueryChange = viewModel::updateQuery
        )
        FilterChipRow(
            filters = uiState.filters,
            onToggle = viewModel::toggleFilter
        )
        if (uiState.reports.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            EmptyState(
                modifier = Modifier.fillMaxSize(),
                onCreateReport = onCreateReport
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing12)
            ) {
                items(uiState.reports) { report ->
                    ReportActivityCard(
                        report = report,
                        onClick = { onOpenReport(report.id) }
                    )
                }
            }
        }
        if (showDemoToggle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Demo mode: show empty list")
                Switch(
                    checked = uiState.demoEmptyState,
                    onCheckedChange = viewModel::toggleDemoEmptyState
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportsListPreview() {
    FieldReportTheme {
        ReportsListScreen(
            onCreateReport = {},
            onOpenReport = {},
            showDemoToggle = false,
            viewModel = ReportsViewModel().apply { toggleDemoEmptyState(true) }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReportsListPreviewDark() {
    FieldReportTheme(darkTheme = true) {
        ReportsListScreen(
            onCreateReport = {},
            onOpenReport = {},
            showDemoToggle = false,
            viewModel = ReportsViewModel().apply { toggleDemoEmptyState(true) }
        )
    }
}
