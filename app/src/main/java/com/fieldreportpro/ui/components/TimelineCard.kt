package com.fieldreportpro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.unit.dp
import com.fieldreportpro.domain.ui_models.AccentColorType
import com.fieldreportpro.domain.ui_models.TimelineEventUi
import com.fieldreportpro.domain.ui_models.TimelineIconType
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.PrimaryGreen

@Composable
fun TimelineCard(
    title: String,
    events: List<TimelineEventUi>,
    modifier: Modifier = Modifier
) {
    val cardColors = standardCardColors()
    val cardElevation = standardCardElevation()
    val cardBorder = standardCardBorder()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.Corner20),
        colors = cardColors,
        elevation = cardElevation,
        border = cardBorder
    ) {
        Column(modifier = Modifier.padding(AppDimens.Spacing16)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = AppTextStyles.SectionTitle)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "LIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = AppTextStyles.ChipText,
                        color = PrimaryGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                events.forEachIndexed { index, event ->
                    TimelineEventRow(
                        event = event,
                        isLast = index == events.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineEventRow(
    event: TimelineEventUi,
    isLast: Boolean
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val accentColor = accentColor(event.accentColorType)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconFor(event.iconType),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (!isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = event.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = event.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = event.timeLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun iconFor(iconType: TimelineIconType) = when (iconType) {
    TimelineIconType.Cloud -> Icons.Outlined.CloudDone
    TimelineIconType.Queue -> Icons.Outlined.Schedule
    TimelineIconType.Photo -> Icons.Outlined.PhotoCamera
    TimelineIconType.Edit -> Icons.Outlined.Edit
    TimelineIconType.Create -> Icons.Outlined.AddCircleOutline
}

private fun accentColor(type: AccentColorType): Color = when (type) {
    AccentColorType.Green -> PrimaryGreen
    AccentColorType.Blue -> Color(0xFF1565C0)
    AccentColorType.Amber -> Color(0xFFF9A825)
    AccentColorType.Red -> Color(0xFFC62828)
    AccentColorType.Gray -> Color(0xFF7A7A7A)
}
