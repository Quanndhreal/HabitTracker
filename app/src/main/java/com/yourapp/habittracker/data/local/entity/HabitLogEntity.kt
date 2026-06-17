package com.yourapp.habittracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId", "date"], unique = true)]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String,                      // "2026-06-17"

    // Trạng thái (dùng cho Home, Detail, Item)
    val status: String = "completed",      // "completed", "partial", "skipped", "missed"

    // XP thực tế nhận được (có thể thấp hơn nếu partial)
    val xpEarned: Int = 0,

    val note: String = "",
    val completedAt: Long? = null,

    // Dùng cho Journey
    val feeling: String? = null,           // "😄", "😐", "😢"
    val feelingNote: String? = null,       // Ghi chú cảm xúc
    val mediaUrls: String? = null          // JSON array các URL ảnh/video
)