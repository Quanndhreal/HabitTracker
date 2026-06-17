package com.yourapp.habittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val requiredStreak: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val rewardXp: Int = 0,
    val rewardTitle: String? = null
)