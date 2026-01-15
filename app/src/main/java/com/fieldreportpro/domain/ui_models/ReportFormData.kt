package com.fieldreportpro.domain.ui_models

data class ReportFormData(
    val title: String,
    val category: String,
    val priority: PriorityUi,
    val locationText: String,
    val unit: String,
    val description: String
)
