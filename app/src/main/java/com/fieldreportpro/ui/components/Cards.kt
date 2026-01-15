package com.fieldreportpro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.fieldreportpro.domain.ui_models.ReportUi
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.DraftBg
import com.fieldreportpro.ui.theme.DraftText
import com.fieldreportpro.ui.theme.OfflineBg
import com.fieldreportpro.ui.theme.PendingBg
import com.fieldreportpro.ui.theme.PendingText
import com.fieldreportpro.ui.theme.PrimaryGreen
import com.fieldreportpro.ui.theme.SyncedBg
import com.fieldreportpro.ui.theme.TextSub

@Composable
fun OfflineBannerCard(
    modifier: Modifier = Modifier,
    onSyncNow: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = OfflineBg.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, Color(0xFFE8D88A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(AppDimens.Spacing16),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Spacing12)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFFF5CC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.WifiOff,
                    contentDescription = null,
                    tint = Color(0xFF7A5B1E)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Offline Mode",
                    style = AppTextStyles.CardTitle,
                    color = Color(0xFF5D4037)
                )
                Text(
                    text = "Changes will sync automatically when connection is restored.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF795548)
                )
            }
            Text(
                text = "Sync Now",
                modifier = Modifier
                    .clickable { onSyncNow() },
                style = MaterialTheme.typography.labelSmall.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF5D4037)
            )
        }
    }
}

@Composable
fun SummaryCardLarge(
    title: String,
    count: String,
    modifier: Modifier = Modifier,
    iconBackground: Color = SyncedBg,
    iconTint: Color = PrimaryGreen
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(AppDimens.Spacing16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextSub)
                Text(text = count, style = MaterialTheme.typography.headlineMedium)
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudDone,
                    contentDescription = null,
                    tint = iconTint
                )
            }
        }
    }
}

@Composable
fun SummaryCardSmall(
    title: String,
    count: String,
    chipText: String,
    modifier: Modifier = Modifier,
    chipBackground: Color,
    chipTextColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppDimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing12)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextSub)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = count, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = chipBackground
                ) {
                    Text(
                        text = chipText,
                        style = AppTextStyles.ChipText,
                        color = chipTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReportActivityCard(
    report: ReportUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppDimens.Spacing16),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(priority = report.priority)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = report.location, style = MaterialTheme.typography.bodySmall, color = TextSub)
                Text(text = report.updatedLabel, style = MaterialTheme.typography.bodySmall, color = TextSub)
            }
            StatusChip(status = report.status)
        }
    }
}

@Composable
fun DraftSummaryCard(modifier: Modifier = Modifier, count: String) {
    SummaryCardSmall(
        title = "Drafts",
        count = count,
        chipText = "Action",
        modifier = modifier,
        chipBackground = DraftBg,
        chipTextColor = DraftText
    )
}

@Composable
fun PendingSummaryCard(modifier: Modifier = Modifier, count: String) {
    SummaryCardSmall(
        title = "Pending",
        count = count,
        chipText = "Wait",
        modifier = modifier,
        chipBackground = PendingBg,
        chipTextColor = PendingText
    )
}
