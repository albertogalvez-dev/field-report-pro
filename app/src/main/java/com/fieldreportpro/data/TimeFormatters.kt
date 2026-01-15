package com.fieldreportpro.data

import java.util.concurrent.TimeUnit

object TimeFormatters {
    fun updatedLabel(timestamp: Long, now: Long = System.currentTimeMillis()): String {
        return "Updated ${shortRelative(timestamp, now)}"
    }

    fun lastSyncLabel(timestamp: Long, now: Long = System.currentTimeMillis()): String {
        return if (timestamp <= 0L) {
            "Last sync: --"
        } else {
            "Last sync: ${longRelative(timestamp, now)}"
        }
    }

    fun lastSyncedLabel(timestamp: Long, now: Long = System.currentTimeMillis()): String {
        return if (timestamp <= 0L) {
            "Last synced --"
        } else {
            "Last synced ${longRelative(timestamp, now)}"
        }
    }

    fun shortRelative(timestamp: Long, now: Long = System.currentTimeMillis()): String {
        val diff = (now - timestamp).coerceAtLeast(0L)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            else -> "${minutes / 1440}d ago"
        }
    }

    private fun longRelative(timestamp: Long, now: Long): String {
        val diff = (now - timestamp).coerceAtLeast(0L)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes} mins ago"
            minutes < 1440 -> "${minutes / 60} hrs ago"
            else -> "${minutes / 1440} days ago"
        }
    }
}
