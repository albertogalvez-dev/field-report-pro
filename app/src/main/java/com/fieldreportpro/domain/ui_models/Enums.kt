package com.fieldreportpro.domain.ui_models

enum class StatusUi {
    Draft,
    Pending,
    Synced,
    Error
}

enum class PriorityUi {
    Low,
    Med,
    High,
    Crit
}

enum class TimelineEventType {
    Synced,
    Queued,
    PhotosAdded,
    DetailsUpdated,
    Created
}

enum class TimelineIconType {
    Cloud,
    Queue,
    Photo,
    Edit,
    Create
}

enum class AccentColorType {
    Green,
    Blue,
    Amber,
    Red,
    Gray
}

enum class SyncItemStatus {
    Uploading,
    Failed,
    Waiting
}
