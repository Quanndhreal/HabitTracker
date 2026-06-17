package com.yourapp.habittracker.data.local.dao

import androidx.room.*
import com.yourapp.habittracker.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET currentStreak = :streak, totalXp = totalXp + :xp WHERE id = 1")
    suspend fun updateStreakAndXp(streak: Int, xp: Int)
}