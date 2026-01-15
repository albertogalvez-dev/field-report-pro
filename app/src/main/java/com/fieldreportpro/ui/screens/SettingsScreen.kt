package com.fieldreportpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fieldreportpro.AppViewModelProvider
import com.fieldreportpro.domain.ui_models.SettingsUiState
import com.fieldreportpro.ui.components.OutlinePillButton
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.theme.PrimaryGreen
import com.fieldreportpro.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FieldReportTheme(darkTheme = true) {
        SettingsContent(
            uiState = uiState,
            onDone = onDone,
            onOfflineModeChange = viewModel::setOfflineMode,
            onAutoSyncChange = viewModel::setAutoSyncWifi,
            onCompressChange = viewModel::setCompressPhotos,
            modifier = modifier
        )
    }
}

@Composable
internal fun SettingsContent(
    uiState: SettingsUiState,
    onDone: () -> Unit,
    onOfflineModeChange: (Boolean) -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    onCompressChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = AppDimens.Spacing16),
        contentPadding = PaddingValues(top = AppDimens.Spacing16, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(48.dp))
                Text(text = "Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onDone) {
                    Text(text = "Done", color = PrimaryGreen)
                }
            }
        }
        item {
            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.Corner20),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(AppDimens.Spacing16),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF3C4650), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "VR", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Victoria Reed", style = MaterialTheme.typography.titleSmall)
                        Text(text = "North Ridge Energy", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9AA1A8))
                        Text(text = "Active Status", style = MaterialTheme.typography.bodySmall, color = PrimaryGreen)
                    }
                }
            }
        }
        item {
            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.Corner20),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(AppDimens.Spacing16),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Preferences", style = MaterialTheme.typography.titleSmall)
                    PreferenceToggle(
                        title = "Offline mode (simulate)",
                        checked = uiState.offlineModeSimulated,
                        onCheckedChange = onOfflineModeChange
                    )
                    PreferenceToggle(
                        title = "Auto-sync on Wi-Fi",
                        checked = uiState.autoSyncWifi,
                        onCheckedChange = onAutoSyncChange
                    )
                    PreferenceToggle(
                        title = "Compress photos",
                        checked = uiState.compressPhotos,
                        onCheckedChange = onCompressChange
                    )
                }
            }
        }
        item {
            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.Corner20),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(AppDimens.Spacing16),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "About", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Field Report Pro", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Build v1.0.2 (Production)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9AA1A8))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AboutChip(label = "Kotlin")
                        AboutChip(label = "Jetpack Compose")
                        AboutChip(label = "Room DB")
                        AboutChip(label = "WorkManager")
                    }
                }
            }
        }
        item {
            OutlinePillButton(
                text = "Sign Out",
                borderColor = Color(0xFFEF5350),
                onClick = {}
            )
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Device ID: FRP-20489", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9AA1A8))
                Text(text = uiState.lastSyncLabel, style = MaterialTheme.typography.bodySmall, color = Color(0xFF9AA1A8))
            }
        }
    }
}

@Composable
private fun PreferenceToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun AboutChip(label: String) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = Color(0xFF2F373F)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFBFC7CE)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    FieldReportTheme(darkTheme = true) {
        SettingsContent(
            uiState = SettingsUiState(
                offlineModeSimulated = false,
                autoSyncWifi = true,
                compressPhotos = true,
                lastSyncLabel = "Last synced 2 mins ago"
            ),
            onDone = {},
            onOfflineModeChange = {},
            onAutoSyncChange = {},
            onCompressChange = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsPreviewDark() {
    FieldReportTheme(darkTheme = true) {
        SettingsContent(
            uiState = SettingsUiState(
                offlineModeSimulated = true,
                autoSyncWifi = true,
                compressPhotos = true,
                lastSyncLabel = "Last synced 2 mins ago"
            ),
            onDone = {},
            onOfflineModeChange = {},
            onAutoSyncChange = {},
            onCompressChange = {}
        )
    }
}
