package com.fieldreportpro.data.local

enum class ReportCategory(val label: String) {
    Safety("Safety"),
    Maintenance("Maintenance"),
    Observation("Observation"),
    Quality("Quality");

    companion object {
        fun fromLabel(label: String): ReportCategory {
            return values().firstOrNull { it.label.equals(label, ignoreCase = true) } ?: Safety
        }
    }
}

enum class ReportPriority {
    Low,
    Med,
    High,
    Crit
}

enum class ReportStatus {
    DRAFT,
    PENDING_SYNC,
    SYNCED,
    ERROR
}

enum class SyncState {
    WAITING,
    UPLOADING,
    DONE,
    FAILED
}

enum class TimelineEventTypeEntity {
    CREATED,
    EDITED,
    PHOTOS_ADDED,
    QUEUED,
    SYNCED,
    FAILED
}
