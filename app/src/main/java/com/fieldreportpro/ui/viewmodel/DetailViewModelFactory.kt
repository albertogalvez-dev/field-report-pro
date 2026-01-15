package com.fieldreportpro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fieldreportpro.domain.ReportRepository

class DetailViewModelFactory(
    private val repository: ReportRepository,
    private val reportId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(reportId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
