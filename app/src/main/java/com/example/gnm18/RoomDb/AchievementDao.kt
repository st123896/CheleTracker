package com.example.gnm18.RoomDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
// Data Access Object (DAO) forAchievementDao entity operations
// Defines methods to interact with the achievement table in the database

@Dao
interface AchievementDao {
    @Insert
    suspend fun insert(achievement: Achievement)

    @Query("SELECT * FROM achievements WHERE user_id = :userId")
    suspend fun getAchievementsByUser(userId: Int): List<Achievement>

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND type = :type")
    suspend fun getAchievementByType(userId: Int, type: String): Achievement?

}