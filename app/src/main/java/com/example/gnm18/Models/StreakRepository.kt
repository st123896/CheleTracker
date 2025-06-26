package com.example.gnm18.Models

import com.example.gnm18.RoomDb.Streak
import com.example.gnm18.RoomDb.StreakDao
/**
 * Repository for Streak operations
 * Abstracts data source operations from ViewModel
 */
class StreakRepository(private val streakDao: StreakDao) {

    suspend fun insert(streak: Streak) = streakDao.insert(streak)

    suspend fun update(streak: Streak) = streakDao.update(streak)

    suspend fun getStreak(userId: Int, type: String): Streak? =
        streakDao.getStreak(userId, type)

    suspend fun getAllStreaksForUser(userId: Int): List<Streak> =
        streakDao.getAllStreaksForUser(userId)

    suspend fun resetStreak(userId: Int, type: String) =
        streakDao.resetStreak(userId, type)

    suspend fun deleteAllForUser(userId: Int) =
        streakDao.deleteAllForUser(userId)
}