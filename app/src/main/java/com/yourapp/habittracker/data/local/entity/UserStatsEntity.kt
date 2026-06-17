package com.yourapp.habittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalXp: Int = 0,
    val level: Int = 1,
    val xpToNextLevel: Int = 100,
    val challengeDay: Int = 1,
    val challengeTotal: Int = 66,
    val streakFreezesAvailable: Int = 1,
    val achievementPoints: Int = 0
)