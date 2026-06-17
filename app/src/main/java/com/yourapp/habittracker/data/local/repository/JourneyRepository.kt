package com.yourapp.habittracker.data.local.repository

import com.yourapp.habittracker.data.local.dao.DaySummaryDao
import com.yourapp.habittracker.data.local.entity.DaySummaryEntity
import kotlinx.coroutines.flow.Flow

class JourneyRepository(private val daySummaryDao: DaySummaryDao) {

    fun getAllSummaries(): Flow<List<DaySummaryEntity>> {
        return daySummaryDao.getAllSummaries()
    }

    suspend fun getSummaryByDate(date: String): DaySummaryEntity? {
        return daySummaryDao.getSummaryByDate(date)
    }

    suspend fun updateFeeling(date: String, feeling: String) {
        val existing = daySummaryDao.getSummaryByDate(date)
        if (existing != null) {
            daySummaryDao.insertOrUpdateSummary(
                existing.copy(feeling = feeling)
            )
        } else {
            daySummaryDao.insertOrUpdateSummary(
                DaySummaryEntity(
                    date = date,
                    feeling = feeling
                )
            )
        }
    }

    suspend fun addJournalEntry(date: String, entry: String) {
        val existing = daySummaryDao.getSummaryByDate(date)
        if (existing != null) {
            daySummaryDao.insertOrUpdateSummary(
                existing.copy(journalEntry = entry)
            )
        } else {
            daySummaryDao.insertOrUpdateSummary(
                DaySummaryEntity(
                    date = date,
                    journalEntry = entry
                )
            )
        }
    }

    suspend fun addMedia(date: String, mediaUrls: String, description: String) {
        val existing = daySummaryDao.getSummaryByDate(date)
        if (existing != null) {
            daySummaryDao.insertOrUpdateSummary(
                existing.copy(
                    mediaUrls = mediaUrls,
                    mediaDescription = description
                )
            )
        } else {
            daySummaryDao.insertOrUpdateSummary(
                DaySummaryEntity(
                    date = date,
                    mediaUrls = mediaUrls,
                    mediaDescription = description
                )
            )
        }
    }

    suspend fun createDaySummary(
        date: String,
        title: String,
        totalTasks: Int,
        completedTasks: Int,
        totalXpEarned: Int,
        streakCount: Int,
        feeling: String? = null,
        taskEmojis: String? = null
    ) {
        daySummaryDao.insertOrUpdateSummary(
            DaySummaryEntity(
                date = date,
                title = title,
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                totalXpEarned = totalXpEarned,
                streakCount = streakCount,
                feeling = feeling,
                journalEntry = null,
                mediaUrls = null,
                mediaDescription = null
            )
        )
    }
}