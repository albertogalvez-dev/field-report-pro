package com.fieldreportpro.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun toReportCategory(value: String): ReportCategory = ReportCategory.valueOf(value)

    @TypeConverter
    fun fromReportCategory(value: ReportCategory): String = value.name

    @TypeConverter
    fun toReportPriority(value: String): ReportPriority = ReportPriority.valueOf(value)

    @TypeConverter
    fun fromReportPriority(value: ReportPriority): String = value.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = ReportStatus.valueOf(value)

    @TypeConverter
    fun fromReportStatus(value: ReportStatus): String = value.name

    @TypeConverter
    fun toSyncState(value: String): SyncState = SyncState.valueOf(value)

    @TypeConverter
    fun fromSyncState(value: SyncState): String = value.name

    @TypeConverter
    fun toTimelineEventType(value: String): TimelineEventTypeEntity =
        TimelineEventTypeEntity.valueOf(value)

    @TypeConverter
    fun fromTimelineEventType(value: TimelineEventTypeEntity): String = value.name
}
