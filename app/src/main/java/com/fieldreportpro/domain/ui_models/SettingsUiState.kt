package com.fieldreportpro.domain.ui_models

data class SettingsUiState(
    val offlineModeSimulated: Boolean,
    val autoSyncWifi: Boolean,
    val compressPhotos: Boolean,
    val lastSyncLabel: String
)
