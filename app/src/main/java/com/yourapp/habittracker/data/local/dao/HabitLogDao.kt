package com.yourapp.habittracker.data.local.dao

import androidx.room.*
import com.yourapp.habittracker.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getLogByDate(habitId: Long, date: String): HabitLogEntity?

    @Query("SELECT * FROM habit_logs WHERE date = :today")
    fun getTodayAllLogs(today: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getLogsForHeatmap(habitId: Long, startDate: String, endDate: String): Flow<List<HabitLogEntity>>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE habitId = :habitId AND status = 'completed' AND date BETWEEN :startOfWeek AND :endOfWeek")
    suspend fun getCompletedCountInWeek(habitId: Long, startOfWeek: String, endOfWeek: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: HabitLogEntity)

    @Query("UPDATE habit_logs SET status = :status, xpEarned = :xp, completedAt = :timestamp WHERE id = :logId")
    suspend fun updateLogStatus(logId: Long, status: String, xp: Int, timestamp: Long)
}