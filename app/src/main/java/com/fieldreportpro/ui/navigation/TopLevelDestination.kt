package com.fieldreportpro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TopLevelDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Home : TopLevelDestination(Routes.Home, "Home", Icons.Outlined.Home)
    data object Reports : TopLevelDestination(Routes.Reports, "Reports", Icons.Outlined.Description)
    data object Sync : TopLevelDestination(Routes.Sync, "Sync", Icons.Outlined.CloudSync)
    data object Settings : TopLevelDestination(Routes.Settings, "Settings", Icons.Outlined.AccountCircle)

    companion object {
        val items = listOf(Home, Reports, Sync, Settings)
    }
}
