package com.example.gnm18.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// table name Achievement entity
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "type") val type: String, // "budget", "streak", etc.
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date_earned") val dateEarned: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "icon_resource") val iconResource: Int
)
