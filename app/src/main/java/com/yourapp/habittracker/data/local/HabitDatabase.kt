package com.yourapp.habittracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yourapp.habittracker.data.local.dao.*
import com.yourapp.habittracker.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        HabitEntity::class,
        HabitLogEntity::class,
        DaySummaryEntity::class,
        PostEntity::class,
        AchievementEntity::class,
        UserStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun daySummaryDao(): DaySummaryDao
    abstract fun postDao(): PostDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: HabitDatabase) {
            val habitDao = db.habitDao()
            val achievementDao = db.achievementDao()
            val userStatsDao = db.userStatsDao()
            val postDao = db.postDao()

            // Insert habits mẫu
            val habits = listOf(
                HabitEntity(name = "Wake up at 7:30 AM", description = "Start your day fresh", timeBlock = "Day Start till 10AM", xpReward = 25, icon = "⏰", colorHex = "#FF8A65", sortOrder = 1),
                HabitEntity(name = "Drink 1.8L Water", description = "Hydration is key to daily energy.", timeBlock = "The Day", xpReward = 25, icon = "💧", colorHex = "#4FC3F7", sortOrder = 2),
                HabitEntity(name = "Read 30 minutes", description = "Feed your mind daily.", timeBlock = "The Day", xpReward = 30, icon = "📖", colorHex = "#81C784", sortOrder = 3),
                HabitEntity(name = "No more than 4 hours screen time", description = "Stop doom scrolling.", timeBlock = "The Day", xpReward = 40, icon = "📵", colorHex = "#E57373", sortOrder = 4),
                HabitEntity(name = "Evening meditation", description = "Clear your mind before sleep", timeBlock = "Evening", xpReward = 35, icon = "🧘", colorHex = "#CE93D8", sortOrder = 5)
            )
            habitDao.insertHabit(habits[0])
            habitDao.insertHabit(habits[1])
            habitDao.insertHabit(habits[2])
            habitDao.insertHabit(habits[3])
            habitDao.insertHabit(habits[4])

            // Insert achievements
            val achievements = listOf(
                AchievementEntity(name = "7-Day Streak", icon = "⚔️", requiredStreak = 7, rewardXp = 100, rewardTitle = "Streak Warrior"),
                AchievementEntity(name = "14-Day Streak", icon = "🐺", requiredStreak = 14, rewardXp = 250, rewardTitle = "Streak Hunter"),
                AchievementEntity(name = "30-Day Streak", icon = "🦅", requiredStreak = 30, rewardXp = 500, rewardTitle = "Streak Master"),
                AchievementEntity(name = "66-Day Streak", icon = "👑", requiredStreak = 66, rewardXp = 1000, rewardTitle = "Habit Legend")
            )
            achievementDao.insertAchievements(achievements)

            // Insert user stats
            userStatsDao.insertOrUpdateStats(
                UserStatsEntity(
                    currentStreak = 3,
                    longestStreak = 12,
                    totalXp = 450,
                    level = 5,
                    challengeDay = 3,
                    streakFreezesAvailable = 1,
                    achievementPoints = 2
                )
            )

            // Insert posts mẫu
            val posts = listOf(
                PostEntity(
                    username = "TheSteelShadow",
                    countryEmoji = "🇺🇸",
                    habitName = "2 Deep Breaths",
                    streakDay = 7,
                    totalDays = 66,
                    description = "Taking in some fresh air",
                    location = "TROTTERS PARK",
                    imageUrl = "park_image",
                    reactions = """{"👏":1,"🔥":1,"💪":1,"❤️":1}""",
                    visibility = "public",
                    timeAgo = "7h ago"
                ),
                PostEntity(
                    username = "HabitMaster",
                    countryEmoji = "🇻🇳",
                    habitName = "Morning Run",
                    streakDay = 15,
                    totalDays = 66,
                    description = "Beautiful sunrise today!",
                    location = "Central Park",
                    imageUrl = "sunrise_image",
                    reactions = """{"👏":2,"🔥":3,"💪":1,"❤️":2}""",
                    visibility = "public",
                    timeAgo = "2h ago"
                )
            )
            postDao.insertPost(posts[0])
            postDao.insertPost(posts[1])
        }
    }
}