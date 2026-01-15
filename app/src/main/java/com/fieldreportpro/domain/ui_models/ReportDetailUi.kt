package com.fieldreportpro.domain.ui_models

data class ReportDetailUi(
    val report: ReportUi,
    val description: String,
    val locationLabel: String,
    val attachments: List<AttachmentUi>,
    val timeline: List<TimelineEventUi>
) {
    companion object {
        val Empty = ReportDetailUi(
            report = ReportUi.Empty,
            description = "",
            locationLabel = "",
            attachments = emptyList(),
            timeline = emptyList()
        )
    }
}
