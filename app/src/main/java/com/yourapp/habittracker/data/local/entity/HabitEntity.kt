package com.yourapp.habittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val timeBlock: String = "Morning",
    val xpReward: Int = 25,
    val icon: String = "💧",
    val colorHex: String = "#FF8A65",
    val backgroundImageRes: String = "",
    val isArchived: Boolean = false,
    val isActive: Boolean = true,
    val targetDaysPerWeek: Int = 7,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val category: String? = null,  // ← Thêm field này
    val isRecommended: Boolean = false  // ← Thêm field này
)