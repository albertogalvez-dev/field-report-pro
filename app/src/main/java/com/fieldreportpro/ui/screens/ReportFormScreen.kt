package com.fieldreportpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fieldreportpro.AppViewModelProvider
import com.fieldreportpro.domain.ui_models.PriorityUi
import com.fieldreportpro.domain.ui_models.ReportFormData
import com.fieldreportpro.domain.ui_models.AttachmentUi
import com.fieldreportpro.ui.components.AddPhotoBottomSheet
import com.fieldreportpro.ui.components.AttachmentRow
import com.fieldreportpro.ui.components.OutlinePillButton
import com.fieldreportpro.ui.components.PrimaryPillButton
import com.fieldreportpro.ui.components.standardCardBorder
import com.fieldreportpro.ui.components.standardCardColors
import com.fieldreportpro.ui.components.standardCardElevation
import com.fieldreportpro.ui.components.rememberAttachmentPicker
import com.fieldreportpro.ui.sample.DemoAssets
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.theme.PrimaryGreen
import com.fieldreportpro.ui.viewmodel.ReportFormViewModel
import kotlinx.coroutines.launch

@Composable
fun ReportFormScreen(
    onCancel: () -> Unit,
    onSaveDraft: (ReportFormData) -> Unit,
    onQueueSync: (ReportFormData) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var reportTitle by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf("Safety") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedPriority by rememberSaveable { mutableStateOf(PriorityUi.Med) }
    var description by rememberSaveable { mutableStateOf("") }
    var showAddPhotoSheet by remember { mutableStateOf(false) }
    val attachments by viewModel.attachments.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val attachmentPicker = rememberAttachmentPicker { uri ->
        scope.launch {
            val added = viewModel.addAttachment(currentFormData(
                reportTitle = reportTitle,
                selectedCategory = selectedCategory,
                selectedPriority = selectedPriority,
                description = description
            ), uri)
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

    ReportFormContent(
        reportTitle = reportTitle,
        onReportTitleChange = {
            reportTitle = it
            if (showError) showError = reportTitle.isBlank()
        },
        showError = showError,
        selectedCategory = selectedCategory,
        onCategoryChange = { selectedCategory = it },
        categoryExpanded = categoryExpanded,
        onCategoryExpandedChange = { categoryExpanded = it },
        selectedPriority = selectedPriority,
        onPriorityChange = { selectedPriority = it },
        description = description,
        onDescriptionChange = { description = it },
        attachments = attachments,
        attachmentsCount = attachments.size,
        onAddAttachment = { showAddPhotoSheet = true },
        onCancel = onCancel,
        onSaveDraft = {
            showError = reportTitle.isBlank()
            if (!showError) {
                val formData = currentFormData(
                    reportTitle = reportTitle,
                    selectedCategory = selectedCategory,
                    selectedPriority = selectedPriority,
                    description = description
                )
                scope.launch {
                    viewModel.saveDraft(formData)
                    onSaveDraft(formData)
                }
            }
        },
        onQueueSync = {
            showError = reportTitle.isBlank()
            if (!showError) {
                val formData = currentFormData(
                    reportTitle = reportTitle,
                    selectedCategory = selectedCategory,
                    selectedPriority = selectedPriority,
                    description = description
                )
                scope.launch {
                    viewModel.queueForSync(formData)
                    onQueueSync(formData)
                }
            }
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

private fun currentFormData(
    reportTitle: String,
    selectedCategory: String,
    selectedPriority: PriorityUi,
    description: String
): ReportFormData {
    return ReportFormData(
        title = reportTitle.trim(),
        category = selectedCategory,
        priority = selectedPriority,
        locationText = "Zone 4, Sector B",
        unit = "Unit 4",
        description = description.trim()
    )
}

@Composable
internal fun ReportFormContent(
    reportTitle: String,
    onReportTitleChange: (String) -> Unit,
    showError: Boolean,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    categoryExpanded: Boolean,
    onCategoryExpandedChange: (Boolean) -> Unit,
    selectedPriority: PriorityUi,
    onPriorityChange: (PriorityUi) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    attachments: List<AttachmentUi>,
    attachmentsCount: Int,
    onAddAttachment: () -> Unit,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
    onQueueSync: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = AppDimens.Spacing16),
                contentPadding = PaddingValues(top = AppDimens.Spacing16, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onCancel) {
                            Text(text = "Cancel", color = PrimaryGreen)
                        }
                        Text(
                            text = "New Field Report",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(modifier = Modifier.width(64.dp))
                    }
                }
                item {
                    FormCard {
                        Text(text = "Report Title", style = AppTextStyles.CardTitle)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reportTitle,
                            onValueChange = onReportTitleChange,
                            placeholder = { Text(text = "Enter report title") },
                            isError = showError && reportTitle.isBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )
                        )
                        if (showError && reportTitle.isBlank()) {
                            Text(
                                text = "Report title is required.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Category",
                            style = AppTextStyles.Body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .clickable { onCategoryExpandedChange(true) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = selectedCategory, modifier = Modifier.weight(1f))
                                Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { onCategoryExpandedChange(false) }
                            ) {
                                listOf("Safety", "Maintenance", "Observation", "Quality").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(text = option) },
                                        leadingIcon = {
                                            if (option == selectedCategory) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = PrimaryGreen
                                                )
                                            }
                                        },
                                        onClick = {
                                            onCategoryChange(option)
                                            onCategoryExpandedChange(false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    FormCard {
                        Text(text = "Priority Level", style = AppTextStyles.CardTitle)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrioritySegment(
                                label = "Low",
                                selected = selectedPriority == PriorityUi.Low,
                                onClick = { onPriorityChange(PriorityUi.Low) },
                                modifier = Modifier.weight(1f)
                            )
                            PrioritySegment(
                                label = "Med",
                                selected = selectedPriority == PriorityUi.Med,
                                onClick = { onPriorityChange(PriorityUi.Med) },
                                modifier = Modifier.weight(1f)
                            )
                            PrioritySegment(
                                label = "High",
                                selected = selectedPriority == PriorityUi.High,
                                onClick = { onPriorityChange(PriorityUi.High) },
                                modifier = Modifier.weight(1f)
                            )
                            PrioritySegment(
                                label = "Crit",
                                selected = selectedPriority == PriorityUi.Crit,
                                onClick = { onPriorityChange(PriorityUi.Crit) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                item {
                    FormCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Location", style = AppTextStyles.CardTitle)
                            TextButton(onClick = { }) {
                                Text(text = "USE CURRENT", color = PrimaryGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = "34.0522 N, 118.2437 W",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            AsyncImage(
                                model = DemoAssets.mapPreviewUri,
                                contentDescription = null,
                                modifier = Modifier.matchParentSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            SurfaceLabel(text = "Zone 4, Sector B")
                        }
                    }
                }
                item {
                    FormCard {
                        Text(text = "Description", style = AppTextStyles.CardTitle)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = onDescriptionChange,
                            placeholder = { Text(text = "Add a detailed description...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                }
                item {
                    FormCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Attachments", style = AppTextStyles.CardTitle)
                            Text(
                                text = "$attachmentsCount Files",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        AttachmentRow(
                            attachments = attachments,
                            onAddClick = onAddAttachment
                        )
                    }
                }
            }

            BottomActionBar(
                onSaveDraft = onSaveDraft,
                onQueueSync = onQueueSync,
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
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    val cardColors = standardCardColors()
    val cardElevation = standardCardElevation()
    val cardBorder = standardCardBorder()
    Card(
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = cardColors,
        elevation = cardElevation,
        border = cardBorder,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(AppDimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content
        )
    }
}

@Composable
private fun PrioritySegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = contentColor, style = AppTextStyles.ChipText)
    }
}

@Composable
private fun BoxScope.SurfaceLabel(text: String) {
    Surface(
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.BottomStart),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = AppTextStyles.ChipText
        )
    }
}

@Composable
private fun BottomActionBar(
    onSaveDraft: () -> Unit,
    onQueueSync: () -> Unit,
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
                text = "Save Draft",
                modifier = Modifier.weight(1f),
                onClick = onSaveDraft
            )
            PrimaryPillButton(
                text = "Queue for Sync",
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = onQueueSync
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportFormPreview() {
    FieldReportTheme {
        ReportFormContent(
            reportTitle = "",
            onReportTitleChange = {},
            showError = false,
            selectedCategory = "Safety",
            onCategoryChange = {},
            categoryExpanded = false,
            onCategoryExpandedChange = {},
            selectedPriority = PriorityUi.Med,
            onPriorityChange = {},
            description = "",
            onDescriptionChange = {},
            attachments = DemoAssets.attachmentsPreview,
            attachmentsCount = 3,
            onAddAttachment = {},
            onCancel = {},
            onSaveDraft = {},
            onQueueSync = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReportFormPreviewDark() {
    FieldReportTheme(darkTheme = true) {
        ReportFormContent(
            reportTitle = "",
            onReportTitleChange = {},
            showError = false,
            selectedCategory = "Safety",
            onCategoryChange = {},
            categoryExpanded = false,
            onCategoryExpandedChange = {},
            selectedPriority = PriorityUi.Med,
            onPriorityChange = {},
            description = "",
            onDescriptionChange = {},
            attachments = DemoAssets.attachmentsPreview,
            attachmentsCount = 3,
            onAddAttachment = {},
            onCancel = {},
            onSaveDraft = {},
            onQueueSync = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
