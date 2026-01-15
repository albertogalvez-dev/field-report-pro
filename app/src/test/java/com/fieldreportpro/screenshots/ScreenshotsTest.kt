package com.fieldreportpro.screenshots

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.fieldreportpro.ui.screens.HomeOverviewContent
import androidx.compose.material3.SnackbarHostState
import com.fieldreportpro.ui.screens.PhotoAnnotationContent
import com.fieldreportpro.ui.screens.ReportDetailContent
import com.fieldreportpro.ui.screens.ReportFormContent
import com.fieldreportpro.ui.screens.ReportsListContent
import com.fieldreportpro.ui.screens.SettingsContent
import com.fieldreportpro.ui.screens.SyncCenterContent
import com.fieldreportpro.ui.sample.DemoAssets
import com.fieldreportpro.ui.sample.SampleData
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.viewmodel.ReportsFilters
import com.fieldreportpro.ui.viewmodel.ReportsUiState
import org.junit.Rule
import org.junit.Test

class ScreenshotsTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(softButtons = false)
    )

    @Test
    fun homeLight() {
        paparazzi.snapshot(name = "home_light") {
            FieldReportTheme {
                HomeOverviewContent(
                    summaryCount = SampleData.homeSummary.syncedCount.toString(),
                    draftCount = SampleData.homeSummary.draftCount.toString(),
                    pendingCount = SampleData.homeSummary.pendingCount.toString(),
                    recentActivity = SampleData.reports,
                    showOfflineBanner = false,
                    onCreateReport = {},
                    onOpenReport = {}
                )
            }
        }
    }

    @Test
    fun homeDark() {
        paparazzi.snapshot(name = "home_dark") {
            FieldReportTheme(darkTheme = true) {
                HomeOverviewContent(
                    summaryCount = SampleData.homeSummary.syncedCount.toString(),
                    draftCount = SampleData.homeSummary.draftCount.toString(),
                    pendingCount = SampleData.homeSummary.pendingCount.toString(),
                    recentActivity = SampleData.reports,
                    showOfflineBanner = true,
                    onCreateReport = {},
                    onOpenReport = {}
                )
            }
        }
    }

    @Test
    fun reportsEmptyState() {
        paparazzi.snapshot(name = "empty_state") {
            FieldReportTheme {
                ReportsListContent(
                    onCreateReport = {},
                    onOpenReport = {},
                    showDemoToggle = false,
                    onQueryChange = {},
                    onToggleFilter = {},
                    onToggleDemoEmptyState = {},
                    uiState = ReportsUiState(
                        query = "",
                        filters = ReportsFilters(
                            category = false,
                            priority = false,
                            status = false,
                            date = false
                        ),
                        reports = emptyList(),
                        demoEmptyState = true
                    )
                )
            }
        }
    }

    @Test
    fun reportFormLight() {
        paparazzi.snapshot(name = "form") {
            FieldReportTheme {
                ReportFormContent(
                    reportTitle = "",
                    onReportTitleChange = {},
                    showError = false,
                    selectedCategory = "Safety",
                    onCategoryChange = {},
                    categoryExpanded = false,
                    onCategoryExpandedChange = {},
                    selectedPriority = SampleData.reports.first().priority,
                    onPriorityChange = {},
                    description = "",
                    onDescriptionChange = {},
                    attachments = DemoAssets.attachmentsPreview,
                    attachmentsCount = DemoAssets.attachmentsPreview.size,
                    onAddAttachment = {},
                    onCancel = {},
                    onSaveDraft = {},
                    onQueueSync = {},
                    snackbarHostState = SnackbarHostState()
                )
            }
        }
    }

    @Test
    fun reportDetailLight() {
        paparazzi.snapshot(name = "detail") {
            FieldReportTheme {
                ReportDetailContent(
                    detail = SampleData.reportDetail,
                    onBack = {},
                    onEdit = {},
                    onAnnotate = { _, _ -> },
                    onViewAll = {},
                    snackbarHostState = SnackbarHostState()
                )
            }
        }
    }

    @Test
    fun syncCenterLight() {
        paparazzi.snapshot(name = "sync") {
            FieldReportTheme {
                SyncCenterContent(
                    uiState = SampleData.syncState,
                    onBack = {},
                    onSyncNow = {},
                    onRetry = {}
                )
            }
        }
    }

    @Test
    fun settingsDark() {
        paparazzi.snapshot(name = "settings_dark") {
            FieldReportTheme(darkTheme = true) {
                SettingsContent(
                    uiState = SampleData.settingsState,
                    onDone = {},
                    onOfflineModeChange = {},
                    onAutoSyncChange = {},
                    onCompressChange = {}
                )
            }
        }
    }

    @Test
    fun annotateDark() {
        paparazzi.snapshot(name = "annotate_dark") {
            FieldReportTheme(darkTheme = true) {
                PhotoAnnotationContent(
                    title = "OBSERVATION #142",
                    subtitle = "Zone 4, Sector B",
                    imageUri = DemoAssets.attachmentsPreview.first().thumbnailResOrUrl,
                    attachments = SampleData.detailAttachments,
                    selectedAttachmentId = SampleData.detailAttachments.first().id,
                    showDemoOverlay = true,
                    onBack = {},
                    onSave = { _, _ -> },
                    onAddPhoto = {},
                    onSelectAttachment = {}
                )
            }
        }
    }
}
