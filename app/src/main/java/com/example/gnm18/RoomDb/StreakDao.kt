
package com.example.gnm18.RoomDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
// Data Access Object (DAO) for streak entity operations
// Defines methods to interact with the streak table in the database
@Dao
interface StreakDao {
    @Insert
    suspend fun insert(streak: Streak)

    @Update
    suspend fun update(streak: Streak)

    @Query("SELECT * FROM streaks WHERE user_id = :userId AND type = :type LIMIT 1")
    suspend fun getStreak(userId: Int, type: String): Streak?

    @Query("SELECT * FROM streaks WHERE user_id = :userId")
    suspend fun getAllStreaksForUser(userId: Int): List<Streak>

    @Query("DELETE FROM streaks WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: Int)

    @Query("UPDATE streaks SET current_streak = 0 WHERE user_id = :userId AND type = :type")
    suspend fun resetStreak(userId: Int, type: String)
}