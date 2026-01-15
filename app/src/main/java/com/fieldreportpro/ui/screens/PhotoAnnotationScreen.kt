package com.fieldreportpro.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PointF
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fieldreportpro.FieldReportProApplication
import com.fieldreportpro.domain.ui_models.AttachmentUi
import com.fieldreportpro.domain.ui_models.ReportDetailUi
import com.fieldreportpro.ui.sample.DemoAssets
import com.fieldreportpro.ui.theme.AppDimens
import com.fieldreportpro.ui.theme.BackgroundDark
import com.fieldreportpro.ui.theme.DarkOutline
import com.fieldreportpro.ui.theme.DarkSurfaceElevated
import com.fieldreportpro.ui.theme.PrimaryGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

enum class AnnotationTool {
    Circle,
    Arrow,
    Rect,
    Text
}

data class AnnotationShape(
    val id: String = UUID.randomUUID().toString(),
    val type: AnnotationTool,
    val color: Color,
    val start: Offset,
    val end: Offset
)

@Composable
fun PhotoAnnotationScreen(
    reportId: String,
    attachmentId: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as FieldReportProApplication
    val inspection = LocalInspectionMode.current
    val detail by app.container.repository.observeReportDetail(reportId)
        .collectAsStateWithLifecycle(initialValue = ReportDetailUi.Empty)
    val attachments = if (detail.attachments.isNotEmpty()) {
        detail.attachments
    } else if (inspection) {
        DemoAssets.attachmentsPreview
    } else {
        emptyList()
    }
    var selectedAttachmentId by remember(attachmentId) { mutableStateOf(attachmentId) }
    val selectedAttachment = attachments.firstOrNull { it.id == selectedAttachmentId }
        ?: attachments.firstOrNull()
    val fallbackImage = DemoAssets.attachmentsPreview.firstOrNull()?.thumbnailResOrUrl
    val imageUri = selectedAttachment?.annotatedUri
        ?: selectedAttachment?.sourceUri
        ?: selectedAttachment?.thumbnailResOrUrl
        ?: fallbackImage
    val scope = rememberCoroutineScope()
    var savePending by remember { mutableStateOf(false) }

    PhotoAnnotationContent(
        title = "OBSERVATION #142",
        subtitle = detail.locationLabel.ifBlank { "Zone 4, Sector B" },
        imageUri = imageUri,
        attachments = attachments,
        selectedAttachmentId = selectedAttachment?.id,
        showDemoOverlay = inspection,
        onBack = onBack,
        onSave = { shapes, canvasSize ->
            val targetAttachment = selectedAttachment
            if (savePending || targetAttachment == null || imageUri == null) {
                onSave()
                return@PhotoAnnotationContent
            }
            savePending = true
            scope.launch {
                val annotatedUri = saveAnnotatedImage(
                    context = context,
                    sourceUri = Uri.parse(targetAttachment.sourceUri.ifBlank { imageUri }),
                    shapes = shapes,
                    canvasSize = canvasSize,
                    fileName = "annotated_${targetAttachment.id}.png"
                )
                if (annotatedUri != null) {
                    app.container.repository.markAttachmentAnnotated(targetAttachment.id, annotatedUri)
                }
                savePending = false
                onSave()
            }
        },
        onAddPhoto = {},
        onSelectAttachment = { selectedAttachmentId = it },
        modifier = modifier
    )
}

@Composable
internal fun PhotoAnnotationContent(
    title: String,
    subtitle: String,
    imageUri: String?,
    attachments: List<AttachmentUi>,
    selectedAttachmentId: String?,
    showDemoOverlay: Boolean,
    onBack: () -> Unit,
    onSave: (List<AnnotationShape>, IntSize) -> Unit,
    onAddPhoto: () -> Unit,
    onSelectAttachment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTool by remember { mutableStateOf(AnnotationTool.Circle) }
    val colorOptions = listOf(
        Color(0xFFEF4444),
        Color(0xFFFACC15),
        Color(0xFF22C55E)
    )
    var selectedColor by remember { mutableStateOf(colorOptions.first()) }
    val shapes = remember { mutableStateListOf<AnnotationShape>() }
    val redoStack = remember { mutableStateListOf<AnnotationShape>() }
    var currentShape by remember { mutableStateOf<AnnotationShape?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val canUndo = shapes.isNotEmpty()
    val canRedo = redoStack.isNotEmpty()
    val gestureModifier = if (showDemoOverlay) {
        Modifier
    } else {
        Modifier.pointerInput(selectedTool, selectedColor) {
            if (selectedTool == AnnotationTool.Text) return@pointerInput
            detectDragGestures(
                onDragStart = { offset ->
                    currentShape = AnnotationShape(
                        type = selectedTool,
                        color = selectedColor,
                        start = offset,
                        end = offset
                    )
                },
                onDrag = { change, _ ->
                    val shape = currentShape ?: return@detectDragGestures
                    currentShape = shape.copy(end = change.position)
                },
                onDragEnd = {
                    currentShape?.let { shape ->
                        shapes.add(shape)
                        redoStack.clear()
                    }
                    currentShape = null
                },
                onDragCancel = { currentShape = null }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAnnotationBar(
            title = title,
            subtitle = subtitle,
            onBack = onBack,
            onSave = {
                val shapesToSave = buildList {
                    addAll(shapes)
                    currentShape?.let { add(it) }
                }
                onSave(shapesToSave, canvasSize)
            }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = AppDimens.Spacing16, vertical = AppDimens.Spacing12)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(DarkSurfaceElevated)
                    .onSizeChanged { canvasSize = it }
                    .then(gestureModifier)
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                AnnotationCanvas(
                    shapes = shapes,
                    currentShape = currentShape,
                    showDemoOverlay = showDemoOverlay
                )
                if (showDemoOverlay) {
                    DemoLabel(modifier = Modifier.align(Alignment.Center).padding(top = 80.dp))
                }
            }

            ToolBar(
                selectedTool = selectedTool,
                onToolSelected = { selectedTool = it },
                canUndo = canUndo,
                canRedo = canRedo,
                onUndo = {
                    if (shapes.isNotEmpty()) {
                        redoStack.add(shapes.removeAt(shapes.lastIndex))
                    }
                },
                onRedo = {
                    if (redoStack.isNotEmpty()) {
                        shapes.add(redoStack.removeAt(redoStack.lastIndex))
                    }
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
            ColorPicker(
                colors = colorOptions,
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it },
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        BottomMediaStrip(
            attachments = attachments,
            selectedAttachmentId = selectedAttachmentId,
            onAddPhoto = onAddPhoto,
            onSelectAttachment = onSelectAttachment
        )
    }
}

@Composable
private fun TopAnnotationBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Spacing16, vertical = AppDimens.Spacing12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.2.sp,
                color = Color(0xFFE6E6E6)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9AA3AA)
            )
        }
        Surface(
            onClick = onSave,
            shape = RoundedCornerShape(18.dp),
            color = PrimaryGreen.copy(alpha = 0.12f)
        ) {
            Text(
                text = "SAVE",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = PrimaryGreen,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ToolBar(
    selectedTool: AnnotationTool,
    onToolSelected: (AnnotationTool) -> Unit,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = Color(0xFF2A3138),
        modifier = modifier.padding(top = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolButton(
                icon = Icons.Outlined.Edit,
                selected = selectedTool == AnnotationTool.Circle,
                onClick = { onToolSelected(AnnotationTool.Circle) }
            )
            ToolButton(
                icon = Icons.Outlined.ArrowOutward,
                selected = selectedTool == AnnotationTool.Arrow,
                onClick = { onToolSelected(AnnotationTool.Arrow) }
            )
            ToolButton(
                icon = Icons.Outlined.CheckBoxOutlineBlank,
                selected = selectedTool == AnnotationTool.Rect,
                onClick = { onToolSelected(AnnotationTool.Rect) }
            )
            ToolButton(
                icon = Icons.Outlined.Title,
                selected = selectedTool == AnnotationTool.Text,
                onClick = { onToolSelected(AnnotationTool.Text) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color(0xFF3A434B))
            )
            Spacer(modifier = Modifier.width(10.dp))
            ToolIcon(
                icon = Icons.AutoMirrored.Outlined.Undo,
                enabled = canUndo,
                onClick = onUndo
            )
            ToolIcon(
                icon = Icons.AutoMirrored.Outlined.Redo,
                enabled = canRedo,
                onClick = onRedo
            )
        }
    }
}

@Composable
private fun ToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) PrimaryGreen else Color.Transparent
    val contentColor = if (selected) BackgroundDark else Color(0xFFB0B8BE)
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun ToolIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.4f
    Surface(
        onClick = { if (enabled) onClick() },
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFB0B8BE).copy(alpha = alpha),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun ColorPicker(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(start = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2A3138))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selectedColor == color) 2.dp else 1.dp,
                        color = if (selectedColor == color) Color.White else Color(0xFF3F4952),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .drawBehind {
                    val stroke = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                    drawCircle(
                        color = Color(0xFF67707A),
                        style = stroke
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = Color(0xFF67707A),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun AnnotationCanvas(
    shapes: List<AnnotationShape>,
    currentShape: AnnotationShape?,
    showDemoOverlay: Boolean
) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 3.dp.toPx()
        shapes.forEach { shape ->
            drawShape(shape, strokeWidth)
        }
        currentShape?.let { drawShape(it, strokeWidth) }
        if (showDemoOverlay) {
            val demoColor = Color(0xFFEF4444)
            val radius = size.minDimension * 0.14f
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                color = demoColor,
                center = center,
                radius = radius,
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            )
        }
    }
}

private fun DrawScope.drawShape(shape: AnnotationShape, strokeWidth: Float) {
    when (shape.type) {
        AnnotationTool.Circle -> {
            val radius = hypot(shape.end.x - shape.start.x, shape.end.y - shape.start.y)
            drawCircle(
                color = shape.color,
                center = shape.start,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )
        }
        AnnotationTool.Rect -> {
            val rect = Rect(shape.start, shape.end)
            drawRect(
                color = shape.color,
                topLeft = Offset(
                    x = min(rect.left, rect.right),
                    y = min(rect.top, rect.bottom)
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = kotlin.math.abs(rect.right - rect.left),
                    height = kotlin.math.abs(rect.bottom - rect.top)
                ),
                style = Stroke(width = strokeWidth)
            )
        }
        AnnotationTool.Arrow -> {
            drawArrow(shape.start, shape.end, shape.color, strokeWidth)
        }
        AnnotationTool.Text -> Unit
    }
}

private fun DrawScope.drawArrow(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float
) {
    drawLine(color = color, start = start, end = end, strokeWidth = strokeWidth)
    val angle = atan2(end.y - start.y, end.x - start.x)
    val arrowLength = 14.dp.toPx()
    val arrowAngle = 0.52f
    val left = Offset(
        x = end.x - arrowLength * cos(angle - arrowAngle),
        y = end.y - arrowLength * sin(angle - arrowAngle)
    )
    val right = Offset(
        x = end.x - arrowLength * cos(angle + arrowAngle),
        y = end.y - arrowLength * sin(angle + arrowAngle)
    )
    drawLine(color = color, start = end, end = left, strokeWidth = strokeWidth)
    drawLine(color = color, start = end, end = right, strokeWidth = strokeWidth)
}

@Composable
private fun DemoLabel(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEF4444)
    ) {
        Text(
            text = "CRACK 2mm",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BottomMediaStrip(
    attachments: List<AttachmentUi>,
    selectedAttachmentId: String?,
    onAddPhoto: () -> Unit,
    onSelectAttachment: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Spacing16, vertical = AppDimens.Spacing12),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF232A31)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(AppDimens.Spacing12)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .background(Color(0xFF39424B), RoundedCornerShape(12.dp))
                )
            }
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onAddPhoto,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddAPhoto,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ADD PHOTO",
                            color = PrimaryGreen,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                attachments.take(6).forEach { attachment ->
                    AttachmentThumbnail(
                        attachment = attachment,
                        selected = attachment.id == selectedAttachmentId,
                        onClick = { onSelectAttachment(attachment.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentThumbnail(
    attachment: AttachmentUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) PrimaryGreen else DarkOutline,
                shape = RoundedCornerShape(18.dp)
            )
            .background(Color(0xFF2A3138))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = attachment.thumbnailResOrUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (attachment.annotated) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(6.dp)
                    .background(PrimaryGreen, CircleShape)
            )
        }
    }
}

private suspend fun saveAnnotatedImage(
    context: Context,
    sourceUri: Uri,
    shapes: List<AnnotationShape>,
    canvasSize: IntSize,
    fileName: String
): Uri? = withContext(Dispatchers.IO) {
    if (canvasSize.width == 0 || canvasSize.height == 0) return@withContext null
    val resolver = context.contentResolver
    val inputStream = resolver.openInputStream(sourceUri) ?: return@withContext null
    val sourceBitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null
    inputStream.close()
    val outputBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = android.graphics.Canvas(outputBitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = maxOf(4f, outputBitmap.width * 0.004f)
    }

    val scale = min(
        canvasSize.width.toFloat() / sourceBitmap.width,
        canvasSize.height.toFloat() / sourceBitmap.height
    )
    val offsetX = (canvasSize.width - sourceBitmap.width * scale) / 2f
    val offsetY = (canvasSize.height - sourceBitmap.height * scale) / 2f

    fun mapPoint(offset: Offset): PointF {
        val x = ((offset.x - offsetX) / scale).coerceIn(0f, sourceBitmap.width.toFloat())
        val y = ((offset.y - offsetY) / scale).coerceIn(0f, sourceBitmap.height.toFloat())
        return PointF(x, y)
    }

    shapes.forEach { shape ->
        paint.color = shape.color.toArgb()
        val start = mapPoint(shape.start)
        val end = mapPoint(shape.end)
        when (shape.type) {
            AnnotationTool.Circle -> {
                val radius = hypot(end.x - start.x, end.y - start.y)
                paint.pathEffect = DashPathEffect(floatArrayOf(10f, 6f), 0f)
                canvas.drawCircle(start.x, start.y, radius, paint)
            }
            AnnotationTool.Rect -> {
                paint.pathEffect = null
                canvas.drawRect(
                    min(start.x, end.x),
                    min(start.y, end.y),
                    kotlin.math.max(start.x, end.x),
                    kotlin.math.max(start.y, end.y),
                    paint
                )
            }
            AnnotationTool.Arrow -> {
                paint.pathEffect = null
                drawArrow(canvas, paint, start, end)
            }
            AnnotationTool.Text -> Unit
        }
    }

    val directory = File(context.cacheDir, "annotations").apply { mkdirs() }
    val file = File(directory, fileName)
    FileOutputStream(file).use { output ->
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun drawArrow(
    canvas: android.graphics.Canvas,
    paint: Paint,
    start: PointF,
    end: PointF
) {
    canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    val angle = atan2(end.y - start.y, end.x - start.x)
    val arrowLength = 24f
    val arrowAngle = 0.52f
    val leftX = end.x - arrowLength * cos(angle - arrowAngle)
    val leftY = end.y - arrowLength * sin(angle - arrowAngle)
    val rightX = end.x - arrowLength * cos(angle + arrowAngle)
    val rightY = end.y - arrowLength * sin(angle + arrowAngle)
    canvas.drawLine(end.x, end.y, leftX, leftY, paint)
    canvas.drawLine(end.x, end.y, rightX, rightY, paint)
}
