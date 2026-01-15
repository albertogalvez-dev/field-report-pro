package com.fieldreportpro.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

data class AttachmentPicker(
    val launchCamera: () -> Unit,
    val launchGallery: () -> Unit
)

@Composable
fun rememberAttachmentPicker(
    onUriPicked: (Uri) -> Unit
): AttachmentPicker {
    val context = LocalContext.current
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let(onUriPicked)
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(onUriPicked)
    }

    return AttachmentPicker(
        launchCamera = {
            val uri = createTempImageUri(context)
            pendingCameraUri = uri
            takePictureLauncher.launch(uri)
        },
        launchGallery = {
            pickMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    )
}

private fun createTempImageUri(context: Context): Uri {
    val directory = File(context.cacheDir, "attachments").apply { mkdirs() }
    val file = File.createTempFile("report_photo_", ".jpg", directory)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
