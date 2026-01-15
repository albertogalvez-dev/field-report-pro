package com.fieldreportpro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fieldreportpro.ui.navigation.BottomNavBar
import com.fieldreportpro.ui.navigation.Routes
import com.fieldreportpro.ui.navigation.TopLevelDestination
import com.fieldreportpro.ui.screens.HomeOverviewScreen
import com.fieldreportpro.ui.screens.PhotoAnnotationScreen
import com.fieldreportpro.ui.screens.ReportDetailScreen
import com.fieldreportpro.ui.screens.ReportFormScreen
import com.fieldreportpro.ui.screens.ReportsListScreen
import com.fieldreportpro.ui.screens.SettingsScreen
import com.fieldreportpro.ui.screens.SyncCenterScreen
import kotlinx.coroutines.launch

@Composable
fun FieldReportProApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelDestination.items.any { it.route == currentRoute }
    val app = LocalContext.current.applicationContext as FieldReportProApplication
    val repository = app.container.repository
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Routes.Home) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home
            ) {
                composable(Routes.Home) {
                    HomeOverviewScreen(
                        onCreateReport = { navController.navigate(Routes.ReportForm) },
                        onOpenReport = { reportId -> navController.navigate(Routes.reportDetail(reportId)) }
                    )
                }
                composable(Routes.Reports) {
                    ReportsListScreen(
                        onCreateReport = { navController.navigate(Routes.ReportForm) },
                        onOpenReport = { reportId -> navController.navigate(Routes.reportDetail(reportId)) }
                    )
                }
                composable(Routes.Sync) {
                    SyncCenterScreen(
                        onBack = { navController.navigateUp() }
                    )
                }
                composable(Routes.Settings) {
                    SettingsScreen(
                        onDone = { navController.navigateUp() }
                    )
                }
                composable(Routes.ReportForm) {
                    ReportFormScreen(
                        onCancel = { navController.navigateUp() },
                        onSaveDraft = { formData ->
                            scope.launch { repository.createDraft(formData) }
                            navController.navigateUp()
                        },
                        onQueueSync = { formData ->
                            scope.launch {
                                val reportId = repository.createDraft(formData)
                                repository.queueForSync(reportId)
                            }
                            navController.navigateUp()
                        }
                    )
                }
                composable(
                    route = Routes.ReportDetail,
                    arguments = listOf(navArgument("id") { defaultValue = "1" })
                ) {
                    val reportId = it.arguments?.getString("id") ?: "1"
                    ReportDetailScreen(
                        reportId = reportId,
                        onBack = { navController.navigateUp() },
                        onEdit = { navController.navigate(Routes.ReportForm) },
                        onAnnotate = { reportIdArg, attachmentId ->
                            navController.navigate(Routes.annotate(reportIdArg, attachmentId))
                        }
                    )
                }
                composable(
                    route = Routes.Annotate,
                    arguments = listOf(
                        navArgument("reportId") { defaultValue = "1" },
                        navArgument("attachmentId") { defaultValue = "att-1" }
                    )
                ) {
                    PhotoAnnotationScreen(
                        onBack = { navController.navigateUp() },
                        onSave = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
