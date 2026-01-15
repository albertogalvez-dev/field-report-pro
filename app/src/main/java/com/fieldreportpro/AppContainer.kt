package com.fieldreportpro

import android.content.Context
import com.fieldreportpro.data.ReportRepositoryImpl
import com.fieldreportpro.data.local.AppDatabase
import com.fieldreportpro.data.settings.SettingsDataStore
import com.fieldreportpro.domain.ReportRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.work.WorkManager

class AppContainer(context: Context) {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val database = AppDatabase.getInstance(context)
    private val settingsDataStore = SettingsDataStore(context)
    private val workManager = WorkManager.getInstance(context)

    val repository: ReportRepository = ReportRepositoryImpl(
        appContext = context,
        reportDao = database.reportDao(),
        attachmentDao = database.attachmentDao(),
        timelineDao = database.timelineDao(),
        settingsDataStore = settingsDataStore,
        workManager = workManager
    )

    init {
        applicationScope.launch {
            repository.seedIfEmpty()
        }
    }
}
