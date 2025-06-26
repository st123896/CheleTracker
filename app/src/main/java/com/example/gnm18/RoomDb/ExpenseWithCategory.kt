package com.example.gnm18.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Embedded
// table name of the expensewithcategory
data class ExpenseWithCategory(
    @Embedded val expense: Expense,
    @ColumnInfo(name = "categoryName") val categoryName: String?

)