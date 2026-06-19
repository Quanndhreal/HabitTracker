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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as HabitApplication).database
    private val repository = HabitRepository(
        database.habitDao(),
        database.habitLogDao(),
        database.userStatsDao()
    )

    val activeHabits: StateFlow<List<HabitEntity>> = repository.activeHabits
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val timeBlocks: StateFlow<List<String>> = repository.distinctTimeBlocks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userStats: StateFlow<UserStatsEntity?> = repository.getUserStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _selectedTimeBlock = MutableStateFlow<String?>(null)
    val selectedTimeBlock: StateFlow<String?> = _selectedTimeBlock.asStateFlow()

    fun selectTimeBlock(timeBlock: String) {
        _selectedTimeBlock.value = timeBlock
    }

    fun createHabitFromBottomSheet(habit: HabitEntity) {
        viewModelScope.launch {
            try {
                android.util.Log.d("HabitViewModel", "Creating habit: ${habit.name}")

                val currentCount = activeHabits.value.size
                val newHabit = habit.copy(
                    sortOrder = currentCount + 1,
                    isActive = true,
                    isArchived = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                android.util.Log.d("HabitViewModel", "New habit: $newHabit")

                val habitId = repository.createHabit(newHabit)

                android.util.Log.d("HabitViewModel", "Habit created with ID: $habitId")

                // Tạo log cho hôm nay
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                database.habitLogDao().insertOrUpdateLog(
                    com.yourapp.habittracker.data.local.entity.HabitLogEntity(
                        habitId = habitId,
                        date = today,
                        status = "pending",
                        xpEarned = 0,
                        completedAt = null
                    )
                )

                android.util.Log.d("HabitViewModel", "Log created for today: $today")

            } catch (e: Exception) {
                android.util.Log.e("HabitViewModel", "Error: ${e.message}", e)
                throw e
            }
        }
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