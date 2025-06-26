package com.example.gnm18.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// table name of the user
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String
)