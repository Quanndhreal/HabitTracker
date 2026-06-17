package com.yourapp.habittracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val xpReward: Int = 25,             // Điểm XP nhận được (Ví dụ: +25XP)
    val imageUrl: String? = null,       // Đường dẫn ảnh nền minh họa cho thẻ
    val timeBlock: TimeBlock,           // Phân loại: DAY_START, THE_DAY, DAY_END
    val unitType: UnitType = UnitType.NONE, // Loại đơn vị để làm nút tương tác nhanh
    val targetValue: Float = 1f,        // Mục tiêu cần đạt (Ví dụ: 1.8 Lít, 1.2 Km)
    val currentValue: Float = 0f,       // Tiến trình hiện tại đã làm được trong ngày
    val createdAt: Long = System.currentTimeMillis()
)

enum class TimeBlock {
    DAY_START,
    THE_DAY,
    DAY_END
}

enum class UnitType {
    NONE,       // Chỉ bấm check hoàn thành thông thường
    WATER,      // Thói quen uống nước (hiện nút +0.3L)
    RUNNING     // Thói quen chạy bộ (hiện nút +0.2km)
}