package com.example.gnm18.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
// table name of the category
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "user_id") val userId: Int
)
