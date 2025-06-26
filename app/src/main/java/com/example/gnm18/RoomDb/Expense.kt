package com.example.gnm18.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// table name of the expense
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "date") val date: Long, // Stored as timestamp
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "photo_path") val photoPath: String? = null
)
