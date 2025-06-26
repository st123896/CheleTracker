package com.example.gnm18.Models

import com.example.gnm18.RoomDb.BudgetGoal
import com.example.gnm18.RoomDb.BudgetGoalDao


/**
 * Repository for BudgetGoal operations
 * Mediates between ViewModel and data source
 */
class BudgetGoalRepository(private val budgetGoalDao: BudgetGoalDao) {
    // Insert new budget goal
    suspend fun insert(goal: BudgetGoal) = budgetGoalDao.insert(goal)
    // Get budget goal for specific user, month and year
    suspend fun getBudgetGoal(userId: Int, month: Int, year: Int) =
        budgetGoalDao.getBudgetGoal(userId, month, year)
    // Update existing budget goal
    suspend fun update(goal: BudgetGoal) = budgetGoalDao.update(goal)
}