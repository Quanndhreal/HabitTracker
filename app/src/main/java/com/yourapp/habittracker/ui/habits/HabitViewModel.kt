package com.yourapp.habittracker.ui.habits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.habittracker.HabitApplication
import com.yourapp.habittracker.data.local.entity.HabitEntity
import com.yourapp.habittracker.data.local.entity.UserStatsEntity
import com.yourapp.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as HabitApplication).database
    private val repository = HabitRepository(
        database.habitDao(),
        database.habitLogDao(),
        database.userStatsDao()
    )

    // StateFlow cho danh sách habits
    val activeHabits: StateFlow<List<HabitEntity>> = repository.activeHabits
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // StateFlow cho time blocks
    val timeBlocks: StateFlow<List<String>> = repository.distinctTimeBlocks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // StateFlow cho user stats
    val userStats: StateFlow<UserStatsEntity?> = repository.getUserStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // State cho selected time block
    private val _selectedTimeBlock = MutableStateFlow<String?>(null)
    val selectedTimeBlock: StateFlow<String?> = _selectedTimeBlock.asStateFlow()

    fun selectTimeBlock(timeBlock: String) {
        _selectedTimeBlock.value = timeBlock
    }

    fun createHabit(
        name: String,
        description: String,
        timeBlock: String,
        xpReward: Int,
        icon: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            repository.createHabit(
                HabitEntity(
                    name = name,
                    description = description,
                    timeBlock = timeBlock,
                    xpReward = xpReward,
                    icon = icon,
                    colorHex = colorHex,
                    sortOrder = (activeHabits.value.size) + 1
                )
            )
        }
    }

    fun toggleCompletion(habitId: Long, xpReward: Int) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, xpReward)
        }
    }

    fun markPartial(habitId: Long, xpReward: Int) {
        viewModelScope.launch {
            repository.markPartial(habitId, xpReward)
        }
    }

    fun markSkipped(habitId: Long) {
        viewModelScope.launch {
            repository.markSkipped(habitId)
        }
    }

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
        }
    }
}