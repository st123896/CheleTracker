package com.example.gnm18.RoomDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
// Data Access Object (DAO) for BudgetGoal entity operations
// Defines methods to interact with the budget_goals table in the database

@Dao // Marks this interface as a Room DAO
interface BudgetGoalDao {
    @Insert
    suspend fun insert(goal: BudgetGoal)

    @Query("SELECT * FROM budget_goals WHERE user_id = :userId AND month = :month AND year = :year")
    suspend fun getBudgetGoal(userId: Int, month: Int, year: Int): BudgetGoal?

    @Update
    suspend fun update(goal: BudgetGoal)
}