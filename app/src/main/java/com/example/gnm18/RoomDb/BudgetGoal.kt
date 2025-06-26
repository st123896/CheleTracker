package com.example.gnm18.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// table name of the budgetgoals
@Entity(tableName = "budget_goals")
data class BudgetGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "min_amount") var minAmount: Double,
    @ColumnInfo(name = "max_amount") var maxAmount: Double,
    @ColumnInfo(name = "month") val month: Int,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "user_id") val userId: Int
)
