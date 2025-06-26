package com.example.gnm18.RoomDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// Data Access Object (DAO) for category entity operations
// Defines methods to interact with the categories table in the database
@Dao// Marks this interface as a Room DAO
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories WHERE user_id = :userId")
    suspend fun getCategoriesByUser(userId: Int): List<Category>

    @Delete
    suspend fun delete(category: Category)


}
