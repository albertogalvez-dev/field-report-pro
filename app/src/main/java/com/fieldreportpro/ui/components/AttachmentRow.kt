package com.fieldreportpro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fieldreportpro.domain.ui_models.AttachmentUi
import com.fieldreportpro.ui.theme.AppTextStyles
import com.fieldreportpro.ui.theme.PrimaryGreen

@Composable
fun AttachmentRow(
    attachments: List<AttachmentUi>,
    modifier: Modifier = Modifier,
    extraCount: Int = 0,
    showAddTile: Boolean = true,
    onAttachmentClick: ((AttachmentUi) -> Unit)? = null,
    onAddClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        attachments.take(3).forEachIndexed { index, attachment ->
            AttachmentThumbnail(
                attachment = attachment,
                showOverlay = extraCount > 0 && index == 2,
                overlayText = "+$extraCount",
                onClick = { onAttachmentClick?.invoke(attachment) }
            )
        }
        if (showAddTile) {
            AttachmentAddTile(onClick = { onAddClick?.invoke() })
        }
    }
}

@Composable
private fun AttachmentThumbnail(
    attachment: AttachmentUi,
    showOverlay: Boolean,
    overlayText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(74.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = attachment.thumbnailResOrUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        if (showOverlay) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = overlayText,
                    style = AppTextStyles.CardTitle,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AttachmentAddTile(onClick: () -> Unit) {
    val outline = MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .size(74.dp)
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )
                drawRoundRect(
                    color = outline,
                    style = stroke,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.AddAPhoto,
            contentDescription = null,
            tint = PrimaryGreen
        )
    }
}
