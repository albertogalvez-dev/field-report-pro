package com.fieldreportpro.ui.sample

import com.fieldreportpro.domain.ui_models.AttachmentUi

object DemoAssets {
    private const val packageName = "com.fieldreportpro"

    val mapPreviewUri: String = "android.resource://$packageName/drawable/demo_map"

    val attachmentsPreview: List<AttachmentUi> = listOf(
        AttachmentUi(
            id = "demo-1",
            reportId = "demo",
            thumbnailResOrUrl = "android.resource://$packageName/drawable/demo_photo_1",
            annotated = false,
            sourceUri = "android.resource://$packageName/drawable/demo_photo_1",
            annotatedUri = null
        ),
        AttachmentUi(
            id = "demo-2",
            reportId = "demo",
            thumbnailResOrUrl = "android.resource://$packageName/drawable/demo_photo_2",
            annotated = false,
            sourceUri = "android.resource://$packageName/drawable/demo_photo_2",
            annotatedUri = null
        ),
        AttachmentUi(
            id = "demo-3",
            reportId = "demo",
            thumbnailResOrUrl = "android.resource://$packageName/drawable/demo_photo_3",
            annotated = false,
            sourceUri = "android.resource://$packageName/drawable/demo_photo_3",
            annotatedUri = null
        )
    )
}
