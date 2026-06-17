package com.yourapp.habittracker.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_summaries")
data class DaySummaryEntity(
    @PrimaryKey
    val date: String,                      // "2026-06-17"

    // Tổng quan ngày (dùng cho Journey)
    val title: String = "",                // "Day 1"
    val feeling: String? = null,           // "😄", "😐", "😢"
    val feelingNote: String? = null,

    // Thống kê
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val totalXpEarned: Int = 0,

    // Media (dùng cho Journey - ô "The visual journey")
    val mediaDescription: String? = null,
    val mediaUrls: String? = null,         // JSON array

    // Text note (dùng cho Journey - ô "Describe feeling")
    val journalEntry: String? = null,

    // Streak sau ngày này
    val streakCount: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)