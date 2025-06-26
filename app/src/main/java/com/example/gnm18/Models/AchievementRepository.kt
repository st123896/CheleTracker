package com.example.gnm18.Models

import com.example.gnm18.RoomDb.Achievement
import com.example.gnm18.RoomDb.AchievementDao

//Showing Achievements
class AchievementRepository(private val achievementDao: AchievementDao) {
    suspend fun insert(achievement: Achievement) = achievementDao.insert(achievement)
    suspend fun getAchievementsByUser(userId: Int) = achievementDao.getAchievementsByUser(userId)
    suspend fun getAchievementByType(userId: Int, type: String) = achievementDao.getAchievementByType(userId, type)


}
