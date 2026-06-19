package com.yourapp.habittracker.data.repository

import com.yourapp.habittracker.data.local.dao.HabitDao
import com.yourapp.habittracker.data.local.dao.HabitLogDao
import com.yourapp.habittracker.data.local.dao.UserStatsDao
import com.yourapp.habittracker.data.local.entity.HabitEntity
import com.yourapp.habittracker.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val userStatsDao: UserStatsDao
) {
    val activeHabits: Flow<List<HabitEntity>> = habitDao.getActiveHabits()

    fun getHabitsByTimeBlock(timeBlock: String): Flow<List<HabitEntity>> =
        habitDao.getHabitsByTimeBlock(timeBlock)

    val distinctTimeBlocks: Flow<List<String>> = habitDao.getDistinctTimeBlocks()

    suspend fun getHabitById(id: Long): HabitEntity? = habitDao.getHabitById(id)

    suspend fun createHabit(habit: HabitEntity): Long {
        return try {
            android.util.Log.d("HabitRepository", "Inserting habit: ${habit.name}")
            val id = habitDao.insertHabit(habit)
            android.util.Log.d("HabitRepository", "Inserted with ID: $id")
            id
        } catch (e: Exception) {
            android.util.Log.e("HabitRepository", "Error inserting habit: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: HabitEntity) = habitDao.deleteHabit(habit)

    suspend fun archiveHabit(habitId: Long) = habitDao.archiveHabit(habitId)

    fun getTodayLogs(today: String): Flow<List<HabitLogEntity>> =
        habitLogDao.getTodayAllLogs(today)

    suspend fun getLogByDate(habitId: Long, date: String): HabitLogEntity? =
        habitLogDao.getLogByDate(habitId, date)

    suspend fun toggleHabitCompletion(habitId: Long, xpReward: Int) {
        try {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val existingLog = habitLogDao.getLogByDate(habitId, today)

            if (existingLog != null) {
                val newStatus = if (existingLog.status == "completed") "missed" else "completed"
                val xpEarned = if (newStatus == "completed") xpReward else 0
                habitLogDao.updateLogStatus(existingLog.id, newStatus, xpEarned, System.currentTimeMillis())
            } else {
                habitLogDao.insertOrUpdateLog(
                    HabitLogEntity(
                        habitId = habitId,
                        date = today,
                        status = "completed",
                        xpEarned = xpReward,
                        completedAt = System.currentTimeMillis()
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun markPartial(habitId: Long, xpReward: Int) {
        try {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val partialXp = (xpReward * 0.5).toInt()
            habitLogDao.insertOrUpdateLog(
                HabitLogEntity(
                    habitId = habitId,
                    date = today,
                    status = "partial",
                    xpEarned = partialXp,
                    completedAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun markSkipped(habitId: Long) {
        try {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            habitLogDao.insertOrUpdateLog(
                HabitLogEntity(
                    habitId = habitId,
                    date = today,
                    status = "skipped",
                    xpEarned = 0,
                    completedAt = null
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun getUserStats() = userStatsDao.getUserStats()

    fun getLogsForHeatmap(habitId: Long, startDate: String, endDate: String): Flow<List<HabitLogEntity>> =
        habitLogDao.getLogsForHeatmap(habitId, startDate, endDate)

    suspend fun getCompletedInWeek(habitId: Long, startOfWeek: String, endOfWeek: String): Int =
        habitLogDao.getCompletedCountInWeek(habitId, startOfWeek, endOfWeek)
}