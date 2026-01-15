package com.fieldreportpro

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fieldreportpro.ui.viewmodel.HomeViewModel
import com.fieldreportpro.ui.viewmodel.ReportsViewModel
import com.fieldreportpro.ui.viewmodel.SettingsViewModel
import com.fieldreportpro.ui.viewmodel.SyncViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application = this.app()
            HomeViewModel(application.container.repository)
        }
        initializer {
            val application = this.app()
            ReportsViewModel(application.container.repository)
        }
        initializer {
            val application = this.app()
            SettingsViewModel(application.container.repository)
        }
        initializer {
            val application = this.app()
            SyncViewModel(application.container.repository)
        }
    }
}

private fun CreationExtras.app(): FieldReportProApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FieldReportProApplication
}
