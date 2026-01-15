package com.fieldreportpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fieldreportpro.AppViewModelProvider
import com.fieldreportpro.domain.ui_models.ReportUi
import com.fieldreportpro.ui.components.DraftSummaryCard
import com.fieldreportpro.ui.components.OfflineBannerCard
import com.fieldreportpro.ui.components.PendingSummaryCard
import com.fieldreportpro.ui.components.ReportActivityCard
import com.fieldreportpro.ui.components.SummaryCardLarge
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.theme.PrimaryGreen
import com.fieldreportpro.ui.viewmodel.HomeViewModel
import com.fieldreportpro.ui.viewmodel.SettingsViewModel

@Composable
fun HomeOverviewScreen(
    onCreateReport: () -> Unit,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    HomeOverviewContent(
        summaryCount = uiState.summary.syncedCount.toString(),
        draftCount = uiState.summary.draftCount.toString(),
        pendingCount = uiState.summary.pendingCount.toString(),
        recentActivity = uiState.recentActivity,
        showOfflineBanner = settingsState.offlineModeSimulated,
        onCreateReport = onCreateReport,
        onOpenReport = onOpenReport,
        modifier = modifier
    )
}

@Composable
internal fun HomeOverviewContent(
    summaryCount: String,
    draftCount: String,
    pendingCount: String,
    recentActivity: List<ReportUi>,
    showOfflineBanner: Boolean,
    onCreateReport: () -> Unit,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = AppDimens.Spacing16,
                    end = AppDimens.Spacing16,
                    top = AppDimens.Spacing16,
                    bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reports",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { }) {
                                Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                            }
                            Box {
                                IconButton(onClick = { }) {
                                    Icon(imageVector = Icons.Outlined.FilterList, contentDescription = null)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(PrimaryGreen)
                                )
                            }
                        }
                    }
                }
                if (showOfflineBanner) {
                    item {
                        OfflineBannerCard(onSyncNow = { })
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)) {
                        SummaryCardLarge(
                            title = "Synced Reports",
                            count = summaryCount
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)) {
                            DraftSummaryCard(modifier = Modifier.weight(1f), count = draftCount)
                            PendingSummaryCard(modifier = Modifier.weight(1f), count = pendingCount)
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Recent Activity", style = MaterialTheme.typography.titleMedium)
                        Text(text = "View All", color = PrimaryGreen, style = MaterialTheme.typography.labelLarge)
                    }
                }
                items(recentActivity) { report ->
                    ReportActivityCard(
                        report = report,
                        onClick = { onOpenReport(report.id) }
                    )
                }
            }

            FloatingActionButton(
                onClick = onCreateReport,
                containerColor = PrimaryGreen,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeOverviewPreview() {
    FieldReportTheme {
        HomeOverviewContent(
            summaryCount = "128",
            draftCount = "3",
            pendingCount = "6",
            recentActivity = listOf(
                ReportUi(
                    id = "1",
                    refCode = "REF-2023-89",
                    title = "Cracked beam near intake valve",
                    location = "Zone 4, Sector B",
                    unit = "Unit 4",
                    priority = com.fieldreportpro.domain.ui_models.PriorityUi.High,
                    status = com.fieldreportpro.domain.ui_models.StatusUi.Pending,
                    updatedLabel = "Updated 12m ago",
                    hasAttachments = true,
                    attachmentsCount = 5
                ),
                ReportUi(
                    id = "2",
                    refCode = "REF-2023-90",
                    title = "Loose conduit bracket",
                    location = "Plant North, Bay 2",
                    unit = "Unit 2",
                    priority = com.fieldreportpro.domain.ui_models.PriorityUi.Med,
                    status = com.fieldreportpro.domain.ui_models.StatusUi.Draft,
                    updatedLabel = "Updated 28m ago",
                    hasAttachments = false,
                    attachmentsCount = 0
                )
            ),
            showOfflineBanner = true,
            onCreateReport = {},
            onOpenReport = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeOverviewPreviewDark() {
    FieldReportTheme(darkTheme = true) {
        HomeOverviewContent(
            summaryCount = "128",
            draftCount = "3",
            pendingCount = "6",
            recentActivity = listOf(
                ReportUi(
                    id = "1",
                    refCode = "REF-2023-89",
                    title = "Cracked beam near intake valve",
                    location = "Zone 4, Sector B",
                    unit = "Unit 4",
                    priority = com.fieldreportpro.domain.ui_models.PriorityUi.High,
                    status = com.fieldreportpro.domain.ui_models.StatusUi.Pending,
                    updatedLabel = "Updated 12m ago",
                    hasAttachments = true,
                    attachmentsCount = 5
                )
            ),
            showOfflineBanner = false,
            onCreateReport = {},
            onOpenReport = {}
        )
    }
}
