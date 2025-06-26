
package com.example.gnm18.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
// table name of the streak
@Entity(tableName = "streaks")
data class Streak(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "type") val type: String, // "expense_logging", etc.
    @ColumnInfo(name = "current_streak") val currentStreak: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)
