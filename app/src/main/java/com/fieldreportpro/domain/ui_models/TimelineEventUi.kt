package com.fieldreportpro.domain.ui_models

data class TimelineEventUi(
    val id: String,
    val type: TimelineEventType,
    val title: String,
    val subtitle: String,
    val timeLabel: String,
    val iconType: TimelineIconType,
    val accentColorType: AccentColorType
)
