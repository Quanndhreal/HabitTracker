package com.yourapp.habittracker.data.local.dao

import androidx.room.*
import com.yourapp.habittracker.data.local.entity.DaySummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DaySummaryDao {
    @Query("SELECT * FROM day_summaries ORDER BY date DESC")
    fun getAllSummaries(): Flow<List<DaySummaryEntity>>

    @Query("SELECT * FROM day_summaries WHERE date = :date LIMIT 1")
    suspend fun getSummaryByDate(date: String): DaySummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: DaySummaryEntity)
}