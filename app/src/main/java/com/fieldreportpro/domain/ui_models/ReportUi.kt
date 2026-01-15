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
) {
    companion object {
        val Empty = ReportUi(
            id = "",
            refCode = "",
            title = "",
            location = "",
            unit = "",
            priority = PriorityUi.Med,
            status = StatusUi.Draft,
            updatedLabel = "",
            hasAttachments = false,
            attachmentsCount = 0
        )
    }
}
