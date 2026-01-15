package com.fieldreportpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.fieldreportpro.FieldReportProApplication
import com.fieldreportpro.domain.ui_models.PriorityUi
import com.fieldreportpro.domain.ui_models.ReportDetailUi
import com.fieldreportpro.domain.ui_models.ReportUi
import com.fieldreportpro.domain.ui_models.StatusUi
import com.fieldreportpro.ui.components.AttachmentRow
import com.fieldreportpro.ui.components.OutlinePillButton
import com.fieldreportpro.ui.components.PrimaryPillButton
import com.fieldreportpro.ui.components.PriorityChip
import com.fieldreportpro.ui.components.StatusChip
import com.fieldreportpro.ui.components.TimelineCard
import com.fieldreportpro.ui.components.AddPhotoBottomSheet
import com.fieldreportpro.ui.components.rememberAttachmentPicker
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.theme.HighPriority
import com.fieldreportpro.ui.theme.PrimaryGreen
import com.fieldreportpro.ui.viewmodel.DetailViewModel
import com.fieldreportpro.ui.viewmodel.DetailViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun ReportDetailScreen(
    reportId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onAnnotate: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as FieldReportProApplication
    val viewModel: DetailViewModel =
        viewModel(factory = DetailViewModelFactory(app.container.repository, reportId))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddPhotoSheet by remember { mutableStateOf(false) }
    val attachmentPicker = rememberAttachmentPicker { uri ->
        scope.launch {
            val added = viewModel.addAttachment(uri)
            if (!added) {
                snackbarHostState.showSnackbar("Maximum 3 photos per report")
            }
        }
    }

    AddPhotoBottomSheet(
        show = showAddPhotoSheet,
        onDismiss = { showAddPhotoSheet = false },
        onTakePhoto = {
            showAddPhotoSheet = false
            attachmentPicker.launchCamera()
        },
        onPickPhoto = {
            showAddPhotoSheet = false
            attachmentPicker.launchGallery()
        }
    )

    ReportDetailContent(
        detail = uiState.reportDetail,
        onBack = onBack,
        onEdit = onEdit,
        onAnnotate = onAnnotate,
        onViewAll = { showAddPhotoSheet = true },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
internal fun ReportDetailContent(
    detail: ReportDetailUi,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onAnnotate: (String, String) -> Unit,
    onViewAll: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onSyncNow: () -> Unit = {}
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
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                    Text(
                        text = "Report Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                    }
                }
            }
            item {
                DetailHeaderCard(detail.report)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Photos", style = AppTextStyles.SectionTitle)
                    Text(
                        text = "View All",
                        color = PrimaryGreen,
                        modifier = Modifier.clickable { onViewAll() }
                    )
                }
            }
            item {
                AttachmentRow(
                    attachments = detail.attachments,
                    extraCount = (detail.report.attachmentsCount - 3).coerceAtLeast(0),
                    showAddTile = false,
                    onAttachmentClick = { attachment -> onAnnotate(detail.report.id, attachment.id) }
                )
            }
            item {
                DescriptionCard(description = detail.description)
            }
            item {
                TimelineCard(title = "Activity Log", events = detail.timeline)
            }
        }

        BottomActionBar(
            onEdit = onEdit,
            onSyncNow = onSyncNow,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 104.dp)
        )
    }
}

@Composable
private fun DetailHeaderCard(report: ReportUi) {
    Card(
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .background(HighPriority)
            )
            Column(
                modifier = Modifier.padding(AppDimens.Spacing16),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = report.refCode, style = MaterialTheme.typography.labelLarge)
                    Text(text = report.unit, style = MaterialTheme.typography.labelLarge, color = Color(0xFF8A8A8A))
                }
                Text(text = report.title, style = MaterialTheme.typography.titleLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF8A8A8A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Updated today at 10:24 AM", style = MaterialTheme.typography.bodySmall, color = Color(0xFF8A8A8A))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityChip(priority = report.priority)
                    StatusChip(status = report.status, label = statusLabel(report.status))
                }
            }
        }
    }
}

private fun statusLabel(status: StatusUi): String {
    return when (status) {
        StatusUi.Pending -> "PENDING REVIEW"
        StatusUi.Draft -> "DRAFT"
        StatusUi.Synced -> "SYNCED"
        StatusUi.Error -> "ERROR"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DescriptionCard(description: String) {
    Card(
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppDimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Description", style = AppTextStyles.SectionTitle)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Inspection notes show stress fractures around the intake housing. Pressure dropped to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF4E5)
                ) {
                    Text(
                        text = "2 PSI",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = AppTextStyles.ChipText,
                        color = Color(0xFFB36B00)
                    )
                }
                Text(
                    text = "during load testing.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A8A8A)
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    onEdit: () -> Unit,
    onSyncNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(AppDimens.Spacing16)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinePillButton(
                text = "Edit",
                modifier = Modifier.weight(1f),
                onClick = onEdit
            )
            PrimaryPillButton(
                text = "Sync Now",
                modifier = Modifier.weight(1f),
                onClick = onSyncNow
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportDetailPreview() {
    FieldReportTheme {
        ReportDetailContent(
            detail = ReportDetailUi(
                report = ReportUi(
                    id = "1",
                    refCode = "REF-2023-89",
                    title = "Cracked beam near intake valve",
                    location = "Zone 4, Sector B",
                    unit = "Unit 4",
                    priority = PriorityUi.High,
                    status = StatusUi.Pending,
                    updatedLabel = "Updated 12m ago",
                    hasAttachments = true,
                    attachmentsCount = 5
                ),
                description = "Inspection notes show stress fractures around the intake housing.",
                locationLabel = "Zone 4, Sector B",
                attachments = emptyList(),
                timeline = emptyList()
            ),
            onBack = {},
            onEdit = {},
            onAnnotate = { _, _ -> },
            onViewAll = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReportDetailPreviewDark() {
    FieldReportTheme(darkTheme = true) {
        ReportDetailContent(
            detail = ReportDetailUi(
                report = ReportUi(
                    id = "1",
                    refCode = "REF-2023-89",
                    title = "Cracked beam near intake valve",
                    location = "Zone 4, Sector B",
                    unit = "Unit 4",
                    priority = PriorityUi.High,
                    status = StatusUi.Pending,
                    updatedLabel = "Updated 12m ago",
                    hasAttachments = true,
                    attachmentsCount = 5
                ),
                description = "Inspection notes show stress fractures around the intake housing.",
                locationLabel = "Zone 4, Sector B",
                attachments = emptyList(),
                timeline = emptyList()
            ),
            onBack = {},
            onEdit = {},
            onAnnotate = { _, _ -> },
            onViewAll = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
