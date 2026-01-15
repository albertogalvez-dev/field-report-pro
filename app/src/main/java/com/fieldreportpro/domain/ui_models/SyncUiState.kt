package com.fieldreportpro.domain.ui_models

data class SyncQueueItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val status: SyncItemStatus,
    val progress: Float? = null
)

data class SyncUiState(
    val systemOnline: Boolean,
    val lastSyncLabel: String,
    val dataUsageLabel: String,
    val queuedRemainingLabel: String,
    val queuedItems: List<SyncQueueItemUi>,
    val snackbarMessage: String
)
