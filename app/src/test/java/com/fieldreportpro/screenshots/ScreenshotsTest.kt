package com.fieldreportpro.screenshots

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.fieldreportpro.data.FakeReportRepository
import com.fieldreportpro.domain.ui_models.SettingsUiState
import com.fieldreportpro.ui.screens.HomeOverviewContent
import com.fieldreportpro.ui.screens.PhotoAnnotationScreen
import com.fieldreportpro.ui.screens.ReportDetailContent
import com.fieldreportpro.ui.screens.ReportFormScreen
import com.fieldreportpro.ui.screens.ReportsListScreen
import com.fieldreportpro.ui.screens.SettingsContent
import com.fieldreportpro.ui.screens.SyncCenterContent
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.viewmodel.ReportsViewModel
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
                    summaryCount = "128",
                    draftCount = "3",
                    pendingCount = "6",
                    recentActivity = FakeReportRepository.getRecentActivity(),
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
                    summaryCount = "128",
                    draftCount = "3",
                    pendingCount = "6",
                    recentActivity = FakeReportRepository.getRecentActivity(),
                    showOfflineBanner = true,
                    onCreateReport = {},
                    onOpenReport = {}
                )
            }
        }
    }

    @Test
    fun reportsEmptyState() {
        val viewModel = ReportsViewModel().apply { toggleDemoEmptyState(true) }
        paparazzi.snapshot(name = "empty_state") {
            FieldReportTheme {
                ReportsListScreen(
                    onCreateReport = {},
                    onOpenReport = {},
                    showDemoToggle = false,
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun reportFormLight() {
        paparazzi.snapshot(name = "form") {
            FieldReportTheme {
                ReportFormScreen(
                    onCancel = {},
                    onSaveDraft = {},
                    onQueueSync = {}
                )
            }
        }
    }

    @Test
    fun reportDetailLight() {
        paparazzi.snapshot(name = "detail") {
            FieldReportTheme {
                ReportDetailContent(
                    detail = FakeReportRepository.getReportDetail("1"),
                    onBack = {},
                    onEdit = {},
                    onAnnotate = { _, _ -> }
                )
            }
        }
    }

    @Test
    fun syncCenterLight() {
        paparazzi.snapshot(name = "sync") {
            FieldReportTheme {
                SyncCenterContent(
                    uiState = FakeReportRepository.getSyncState(),
                    onBack = {}
                )
            }
        }
    }

    @Test
    fun settingsDark() {
        paparazzi.snapshot(name = "settings_dark") {
            FieldReportTheme(darkTheme = true) {
                SettingsContent(
                    uiState = SettingsUiState(
                        offlineModeSimulated = true,
                        autoSyncWifi = true,
                        compressPhotos = true
                    ),
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
            PhotoAnnotationScreen(
                onBack = {},
                onSave = {}
            )
        }
    }
}
