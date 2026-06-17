package com.yourapp.habittracker.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.habittracker.HabitApplication
import com.yourapp.habittracker.data.local.entity.HabitEntity
import com.yourapp.habittracker.data.local.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as HabitApplication).database
    private val repository = StatsRepository(database.habitDao(), database.habitLogDao())

    // Habit được chọn
    private val _selectedHabit = MutableStateFlow<HabitEntity?>(null)
    val selectedHabit: StateFlow<HabitEntity?> = _selectedHabit.asStateFlow()

    // Tất cả habits
    val allHabits: StateFlow<List<HabitEntity>> = repository.getAllHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Weekly progress (7 ngày)
    private val _weeklyProgress = MutableStateFlow<List<Int>>(List(7) { 0 })
    val weeklyProgress: StateFlow<List<Int>> = _weeklyProgress.asStateFlow()

    fun selectHabit(habit: HabitEntity) {
        _selectedHabit.value = habit
        loadWeeklyProgress(habit.id)
    }

    private fun loadWeeklyProgress(habitId: Long) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val progress = mutableListOf<Int>()

            for (i in 0..6) {
                val date = startOfWeek.plusDays(i.toLong())
                val count = repository.getCompletedCountInWeek(
                    habitId,
                    date.format(formatter),
                    date.format(formatter)
                )
                progress.add(count)
            }
            _weeklyProgress.value = progress
        }
    }
}