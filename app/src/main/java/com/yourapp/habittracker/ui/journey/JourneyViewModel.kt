package com.yourapp.habittracker.ui.journey

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.habittracker.HabitApplication
import com.yourapp.habittracker.data.local.entity.DaySummaryEntity
import com.yourapp.habittracker.data.local.repository.JourneyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as HabitApplication).database
    private val repository = JourneyRepository(database.daySummaryDao())

    val daySummaries: StateFlow<List<DaySummaryEntity>> = repository.getAllSummaries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateFeeling(date: String, feeling: String) {
        viewModelScope.launch {
            repository.updateFeeling(date, feeling)
        }
    }

    fun addJournalEntry(date: String, entry: String) {
        viewModelScope.launch {
            repository.addJournalEntry(date, entry)
        }
    }
}