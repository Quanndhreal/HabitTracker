package com.yourapp.habittracker

import android.app.Application
import androidx.room.Room
import com.yourapp.habittracker.data.local.HabitDatabase

class HabitApplication : Application() {

    // Singleton database - KHÔNG dùng by lazy vì cần khởi tạo ngay
    lateinit var database: HabitDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Khởi tạo database
        database = Room.databaseBuilder(
            applicationContext,
            HabitDatabase::class.java,
            "habit_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    companion object {
        lateinit var instance: HabitApplication
            private set
    }
}