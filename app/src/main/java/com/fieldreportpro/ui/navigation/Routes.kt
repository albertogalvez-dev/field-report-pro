package com.fieldreportpro.ui.navigation

object Routes {
    const val Home = "home"
    const val Reports = "reports"
    const val Sync = "sync"
    const val Settings = "settings"
    const val ReportForm = "reportForm"
    const val ReportDetail = "reportDetail/{id}"
    const val Annotate = "annotate/{reportId}/{attachmentId}"

    fun reportDetail(id: String): String = "reportDetail/$id"
    fun annotate(reportId: String, attachmentId: String): String =
        "annotate/$reportId/$attachmentId"
}
