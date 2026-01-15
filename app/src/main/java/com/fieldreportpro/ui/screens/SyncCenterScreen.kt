package com.fieldreportpro.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.fieldreportpro.domain.ui_models.SyncItemStatus
import com.fieldreportpro.domain.ui_models.SyncQueueItemUi
import com.fieldreportpro.domain.ui_models.SyncUiState
import com.fieldreportpro.ui.components.PrimaryPillButton
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.theme.PrimaryGreen
import com.fieldreportpro.ui.viewmodel.SyncViewModel

@Composable
fun SyncCenterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SyncViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SyncCenterContent(
        uiState = uiState,
        onBack = onBack,
        onSyncNow = viewModel::syncNow,
        onRetry = viewModel::retrySync,
        modifier = modifier
    )
}

@Composable
internal fun SyncCenterContent(
    uiState: SyncUiState,
    onBack: () -> Unit,
    onSyncNow: () -> Unit,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = AppDimens.Spacing16,
                end = AppDimens.Spacing16,
                top = AppDimens.Spacing16,
                bottom = 140.dp
            ),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
                    }
                    Text(
                        text = "Sync Center",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.size(48.dp))
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (uiState.systemOnline) PrimaryGreen else Color(0xFFD32F2F),
                                        androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = if (uiState.systemOnline) "System Online" else "System Offline",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Text(text = uiState.lastSyncLabel, style = MaterialTheme.typography.bodySmall)
                        Text(text = uiState.dataUsageLabel, style = MaterialTheme.typography.bodySmall)
                        PrimaryPillButton(text = "Sync now", onClick = onSyncNow)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Queued items", style = MaterialTheme.typography.titleMedium)
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        color = PrimaryGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = uiState.queuedRemainingLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = PrimaryGreen,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            items(uiState.queuedItems) { item ->
                SyncQueueItemCard(item = item, onRetry = onRetry)
            }
        }

        SnackbarCard(
            message = uiState.snackbarMessage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun SyncQueueItemCard(
    item: SyncQueueItemUi,
    onRetry: (String) -> Unit
) {
    val borderColor = when (item.status) {
        SyncItemStatus.Failed -> Color(0xFFFFCDD2)
        else -> Color.Transparent
    }
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(AppDimens.Spacing16)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Icon(
                    imageVector = when (item.status) {
                        SyncItemStatus.Uploading -> Icons.Outlined.CloudSync
                        SyncItemStatus.Failed -> Icons.Outlined.ErrorOutline
                        SyncItemStatus.Waiting -> Icons.Outlined.Schedule
                    },
                    contentDescription = null,
                    tint = when (item.status) {
                        SyncItemStatus.Uploading -> PrimaryGreen
                        SyncItemStatus.Failed -> Color(0xFFD32F2F)
                        SyncItemStatus.Waiting -> Color(0xFF7A7A7A)
                    }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = item.subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF8A8A8A))
            if (item.status == SyncItemStatus.Uploading && item.progress != null) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = item.progress,
                    color = PrimaryGreen,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (item.status == SyncItemStatus.Failed) {
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryPillButton(text = "Retry", onClick = { onRetry(item.id) })
            }
        }
    }
}

@Composable
private fun SnackbarCard(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = Color(0xFF2E2E2E)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = message, color = Color.White, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncCenterPreview() {
    FieldReportTheme {
        SyncCenterContent(
            uiState = SyncUiState(
                systemOnline = true,
                lastSyncLabel = "Last sync: 2 mins ago",
                dataUsageLabel = "DATA USAGE 24.5 MB today",
                queuedRemainingLabel = "3 remaining",
                queuedItems = listOf(
                    SyncQueueItemUi(
                        id = "1",
                        title = "Uploading report REF-2023-89",
                        subtitle = "60% complete",
                        status = SyncItemStatus.Uploading,
                        progress = 0.6f
                    )
                ),
                snackbarMessage = "Successfully synced 3 reports"
            ),
            onBack = {},
            onSyncNow = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SyncCenterPreviewDark() {
    FieldReportTheme(darkTheme = true) {
        SyncCenterContent(
            uiState = SyncUiState(
                systemOnline = true,
                lastSyncLabel = "Last sync: 2 mins ago",
                dataUsageLabel = "DATA USAGE 24.5 MB today",
                queuedRemainingLabel = "3 remaining",
                queuedItems = listOf(
                    SyncQueueItemUi(
                        id = "1",
                        title = "Uploading report REF-2023-89",
                        subtitle = "60% complete",
                        status = SyncItemStatus.Uploading,
                        progress = 0.6f
                    )
                ),
                snackbarMessage = "Successfully synced 3 reports"
            ),
            onBack = {},
            onSyncNow = {},
            onRetry = {}
        )
    }
}
