package com.example.gnm18.RoomDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Data Access Object (DAO) for User entity operations
// Defines methods to interact with the user table in the database

@Dao// Marks this interface as a Room DAO
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun checkUsernameExists(username: String): User?
}