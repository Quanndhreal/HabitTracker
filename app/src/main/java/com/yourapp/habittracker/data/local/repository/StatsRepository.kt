package com.yourapp.habittracker.data.local.repository

import com.yourapp.habittracker.data.local.dao.HabitDao
import com.yourapp.habittracker.data.local.dao.HabitLogDao
import com.yourapp.habittracker.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

class StatsRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) {
    fun getAllHabits(): Flow<List<HabitEntity>> {
        return habitDao.getActiveHabits()
    }

    suspend fun getHabitById(habitId: Long): HabitEntity? {
        return habitDao.getHabitById(habitId)
    }

    suspend fun getCompletedCountInWeek(habitId: Long, startOfWeek: String, endOfWeek: String): Int {
        return habitLogDao.getCompletedCountInWeek(habitId, startOfWeek, endOfWeek)
    }

    fun getLogsForHeatmap(habitId: Long, startDate: String, endDate: String): Flow<List<com.yourapp.habittracker.data.local.entity.HabitLogEntity>> {
        return habitLogDao.getLogsForHeatmap(habitId, startDate, endDate)
    }

    suspend fun getWeeklyProgress(habitId: Long, weekDates: List<String>): List<Int> {
        val progress = mutableListOf<Int>()
        for (date in weekDates) {
            val count = habitLogDao.getCompletedCountInWeek(habitId, date, date)
            progress.add(count)
        }
        return progress
    }

    suspend fun getSuccessRate(habitId: Long): Float {
        // Tính tổng số ngày đã có log và số ngày completed
        // Đây là logic đơn giản, có thể mở rộng
        val allHabits = habitDao.getActiveHabits()
        // TODO: Implement proper success rate calculation
        return 85f // Placeholder
    }

    suspend fun getStreakForHabit(habitId: Long): Int {
        // Sử dụng query streak từ HabitLogDao nếu có
        return 12 // Placeholder
    }
}