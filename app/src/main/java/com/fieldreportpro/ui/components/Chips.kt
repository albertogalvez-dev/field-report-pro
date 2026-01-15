package com.fieldreportpro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fieldreportpro.domain.ui_models.PriorityUi
import com.fieldreportpro.domain.ui_models.StatusUi
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.CritPriority
import com.fieldreportpro.ui.theme.DraftBg
import com.fieldreportpro.ui.theme.DraftText
import com.fieldreportpro.ui.theme.ErrorBg
import com.fieldreportpro.ui.theme.ErrorText
import com.fieldreportpro.ui.theme.HighPriority
import com.fieldreportpro.ui.theme.LowPriority
import com.fieldreportpro.ui.theme.MedPriority
import com.fieldreportpro.ui.theme.PendingBg
import com.fieldreportpro.ui.theme.PendingText
import com.fieldreportpro.ui.theme.SyncedBg
import com.fieldreportpro.ui.theme.SyncedText

private data class ChipColors(
    val container: Color,
    val content: Color,
    val border: Color
)

@Composable
fun StatusChip(
    status: StatusUi,
    modifier: Modifier = Modifier,
    label: String = status.name.uppercase()
) {
    val colors = statusChipColors(status)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = colors.container,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = AppTextStyles.ChipText,
            color = colors.content
        )
    }
}

@Composable
fun PriorityChip(priority: PriorityUi, modifier: Modifier = Modifier) {
    val colors = priorityChipColors(priority)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = colors.container,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Text(
            text = "${priority.name.uppercase()} PRIORITY",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = AppTextStyles.ChipText,
            color = colors.content
        )
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = if (selected) SyncedBg else Color.Transparent
    val content = if (selected) SyncedText else Color(0xFF4A4A4A)
    val border = if (selected) SyncedText else Color(0xFFDADADA)
    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = container,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = content,
                style = AppTextStyles.ChipText.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun statusChipColors(status: StatusUi): ChipColors {
    return when (status) {
        StatusUi.Draft -> ChipColors(DraftBg, DraftText, DraftText.copy(alpha = 0.4f))
        StatusUi.Pending -> ChipColors(PendingBg, PendingText, PendingText.copy(alpha = 0.4f))
        StatusUi.Synced -> ChipColors(SyncedBg, SyncedText, SyncedText.copy(alpha = 0.4f))
        StatusUi.Error -> ChipColors(ErrorBg, ErrorText, ErrorText.copy(alpha = 0.4f))
    }
}

private fun priorityChipColors(priority: PriorityUi): ChipColors {
    return when (priority) {
        PriorityUi.Low -> ChipColors(LowPriority.copy(alpha = 0.15f), LowPriority, LowPriority)
        PriorityUi.Med -> ChipColors(MedPriority.copy(alpha = 0.2f), MedPriority, MedPriority)
        PriorityUi.High -> ChipColors(HighPriority.copy(alpha = 0.2f), HighPriority, HighPriority)
        PriorityUi.Crit -> ChipColors(CritPriority.copy(alpha = 0.2f), CritPriority, CritPriority)
    }
}
