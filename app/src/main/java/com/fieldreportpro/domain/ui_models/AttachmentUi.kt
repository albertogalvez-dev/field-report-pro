package com.fieldreportpro.domain.ui_models

data class AttachmentUi(
    val id: String,
    val reportId: String,
    val thumbnailResOrUrl: String,
    val annotated: Boolean
)
