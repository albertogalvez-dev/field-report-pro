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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fieldreportpro.domain.ui_models.PriorityUi
import com.fieldreportpro.domain.ui_models.ReportFormData
import com.fieldreportpro.ui.components.AttachmentRow
import com.fieldreportpro.ui.components.OutlinePillButton
import com.fieldreportpro.ui.components.PrimaryPillButton
import com.fieldreportpro.ui.sample.DemoAssets
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.theme.PrimaryGreen

@Composable
fun ReportFormScreen(
    onCancel: () -> Unit,
    onSaveDraft: (ReportFormData) -> Unit,
    onQueueSync: (ReportFormData) -> Unit,
    modifier: Modifier = Modifier
) {
    var reportTitle by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf("Safety") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedPriority by rememberSaveable { mutableStateOf(PriorityUi.Med) }
    var description by rememberSaveable { mutableStateOf("") }

    Box(modifier = modifier) {
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
                        onValueChange = {
                            reportTitle = it
                            if (showError) showError = reportTitle.isBlank()
                        },
                        placeholder = { Text(text = "Enter report title") },
                        isError = showError && reportTitle.isBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            errorBorderColor = Color(0xFFD32F2F)
                        )
                    )
                    if (showError && reportTitle.isBlank()) {
                        Text(
                            text = "Report title is required.",
                            color = Color(0xFFD32F2F),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Category", style = AppTextStyles.Body, color = Color(0xFF6F6F6F))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF6F6F6), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .clickable { categoryExpanded = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedCategory, modifier = Modifier.weight(1f))
                            Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
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
                                        selectedCategory = option
                                        categoryExpanded = false
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
                            onClick = { selectedPriority = PriorityUi.Low },
                            modifier = Modifier.weight(1f)
                        )
                        PrioritySegment(
                            label = "Med",
                            selected = selectedPriority == PriorityUi.Med,
                            onClick = { selectedPriority = PriorityUi.Med },
                            modifier = Modifier.weight(1f)
                        )
                        PrioritySegment(
                            label = "High",
                            selected = selectedPriority == PriorityUi.High,
                            onClick = { selectedPriority = PriorityUi.High },
                            modifier = Modifier.weight(1f)
                        )
                        PrioritySegment(
                            label = "Crit",
                            selected = selectedPriority == PriorityUi.Crit,
                            onClick = { selectedPriority = PriorityUi.Crit },
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
                            .background(Color(0xFFEAEAEA), RoundedCornerShape(16.dp))
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
                        onValueChange = { description = it },
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
                        Text(text = "3 Files", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7A7A))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    AttachmentRow(attachments = DemoAssets.attachmentsPreview)
                }
            }
        }

        BottomActionBar(
            onSaveDraft = {
                showError = reportTitle.isBlank()
                if (!showError) {
                    onSaveDraft(
                        ReportFormData(
                            title = reportTitle.trim(),
                            category = selectedCategory,
                            priority = selectedPriority,
                            locationText = "Zone 4, Sector B",
                            unit = "Unit 4",
                            description = description.trim()
                        )
                    )
                }
            },
            onQueueSync = {
                showError = reportTitle.isBlank()
                if (!showError) {
                    onQueueSync(
                        ReportFormData(
                            title = reportTitle.trim(),
                            category = selectedCategory,
                            priority = selectedPriority,
                            locationText = "Zone 4, Sector B",
                            unit = "Unit 4",
                            description = description.trim()
                        )
                    )
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
    val background = if (selected) PrimaryGreen else Color(0xFFF4F4F4)
    val contentColor = if (selected) Color.White else Color(0xFF5A5A5A)
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
        color = Color.White.copy(alpha = 0.85f)
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
                        tint = Color.White,
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
        ReportFormScreen(
            onCancel = {},
            onSaveDraft = { _ -> },
            onQueueSync = { _ -> }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReportFormPreviewDark() {
    FieldReportTheme(darkTheme = true) {
        ReportFormScreen(
            onCancel = {},
            onSaveDraft = { _ -> },
            onQueueSync = { _ -> }
        )
    }
}
