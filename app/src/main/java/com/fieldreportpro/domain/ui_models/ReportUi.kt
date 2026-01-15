package com.fieldreportpro.domain.ui_models

data class ReportUi(
    val id: String,
    val refCode: String,
    val title: String,
    val location: String,
    val unit: String,
    val priority: PriorityUi,
    val status: StatusUi,
    val updatedLabel: String,
    val hasAttachments: Boolean,
    val attachmentsCount: Int
)
