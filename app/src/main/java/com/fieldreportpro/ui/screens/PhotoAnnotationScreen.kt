package com.fieldreportpro.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.Redo
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fieldreportpro.data.FakeReportRepository
import com.fieldreportpro.ui.components.AttachmentRow
import com.fieldreportpro.ui.components.OutlinePillButton
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.FieldReportTheme
import com.fieldreportpro.ui.theme.PrimaryGreen

@Composable
fun PhotoAnnotationScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    FieldReportTheme(darkTheme = true) {
        PhotoAnnotationContent(onBack = onBack, onSave = onSave, modifier = modifier)
    }
}

@Composable
private fun PhotoAnnotationContent(
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTool by remember { mutableStateOf("Pencil") }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(AppDimens.Spacing16)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Spacing16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "OBSERVATION #142", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Zone 4, Sector B", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9AA1A8))
                }
                TextButton(onClick = onSave) {
                    Text(text = "SAVE", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                AnnotationCanvas()
                ToolPalette(
                    selectedTool = selectedTool,
                    onToolSelected = { selectedTool = it },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                ColorStrip(modifier = Modifier.align(Alignment.CenterStart))
            }

            BottomStrip()
        }
    }
}

@Composable
private fun AnnotationCanvas() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .background(Color(0xFF1F262C), RoundedCornerShape(AppDimens.Corner20))
    ) {
        AsyncImage(
            model = FakeReportRepository.mapPreviewUrl(),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color(0xFFD32F2F),
                radius = size.minDimension / 6f,
                center = center,
                style = Stroke(
                    width = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 120.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFD32F2F)
        ) {
            Text(
                text = "CRACK 2mm",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ToolPalette(
    selectedTool: String,
    onToolSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        color = Color(0xFF2C343B)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToolButton(label = "Pencil", selected = selectedTool == "Pencil", onClick = { onToolSelected("Pencil") }, icon = Icons.Outlined.Brush)
            ToolButton(label = "Arrow", selected = selectedTool == "Arrow", onClick = { onToolSelected("Arrow") }, icon = Icons.Outlined.ArrowForward)
            ToolButton(label = "Rect", selected = selectedTool == "Rect", onClick = { onToolSelected("Rect") }, icon = Icons.Outlined.CropSquare)
            ToolButton(label = "Text", selected = selectedTool == "Text", onClick = { onToolSelected("Text") }, icon = Icons.Outlined.TextFields)
            ToolButton(label = "Undo", selected = false, onClick = { }, icon = Icons.Outlined.Undo)
            ToolButton(label = "Redo", selected = false, onClick = { }, icon = Icons.Outlined.Redo)
        }
    }
}

@Composable
private fun ToolButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val background = if (selected) PrimaryGreen else Color.Transparent
    val contentColor = if (selected) Color.White else Color(0xFFBFC7CE)
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ColorStrip(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(start = 6.dp)
            .background(Color(0xFF2C343B), RoundedCornerShape(20.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColorDot(color = Color(0xFFD32F2F))
        ColorDot(color = Color(0xFFF9A825))
        ColorDot(color = Color(0xFF2E7D32))
        Surface(
            shape = CircleShape,
            color = Color(0xFF3A444C)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}

@Composable
private fun ColorDot(color: Color) {
    Surface(shape = CircleShape, color = color) {
        Spacer(modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun BottomStrip() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF2C343B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinePillButton(
                text = "ADD PHOTO",
                borderColor = PrimaryGreen,
                onClick = {}
            )
            AttachmentRow(
                attachments = FakeReportRepository.getAttachmentsPreview(),
                showAddTile = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoAnnotationPreview() {
    PhotoAnnotationScreen(onBack = {}, onSave = {})
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PhotoAnnotationPreviewDark() {
    PhotoAnnotationScreen(onBack = {}, onSave = {})
}
