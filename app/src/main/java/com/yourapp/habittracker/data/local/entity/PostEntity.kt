package com.yourapp.habittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String = "TheSteelShadow",
    val countryEmoji: String = "🇺🇸",
    val habitName: String = "",
    val streakDay: Int = 1,
    val totalDays: Int = 66,
    val description: String = "",
    val location: String? = null,
    val imageUrl: String? = null,
    val reactions: String = "{}",
    val visibility: String = "public",
    val createdAt: Long = System.currentTimeMillis(),
    val timeAgo: String = "now"
)