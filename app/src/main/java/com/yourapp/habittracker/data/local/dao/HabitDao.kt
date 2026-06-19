package com.yourapp.habittracker.data.local.dao

import androidx.room.*
import com.yourapp.habittracker.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isActive = 1 AND isArchived = 0 ORDER BY sortOrder ASC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE timeBlock = :timeBlock AND isActive = 1 AND isArchived = 0 ORDER BY sortOrder ASC")
    fun getHabitsByTimeBlock(timeBlock: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): HabitEntity?

    @Query("SELECT DISTINCT timeBlock FROM habits WHERE isActive = 1 AND isArchived = 0 ORDER BY sortOrder ASC")
    fun getDistinctTimeBlocks(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    // Thêm method này để insert nhiều habits cùng lúc
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET isArchived = 1, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun archiveHabit(habitId: Long, timestamp: Long = System.currentTimeMillis())
}