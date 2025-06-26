package com.example.gnm18.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.gnm18.R
import com.example.gnm18.RoomDb.Achievement
import com.example.gnm18.RoomDb.AchievementTypes
import com.example.gnm18.RoomDb.AppDatabase
import com.example.gnm18.RoomDb.BudgetGoal
import com.example.gnm18.RoomDb.ExpenseDao_Impl
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for BudgetGoal operations
 * Provides budget goal data to UI
 */
class BudgetGoalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BudgetGoalRepository
    private val achievementRepository: AchievementRepository
    // Initialize repository with BudgetGoalDao from database
    init {
        val budgetGoalDao = AppDatabase.getDatabase(application).budgetGoalDao()
        repository = BudgetGoalRepository(budgetGoalDao)
        val achievementDao = AppDatabase.getDatabase(application).achievementDao()
        achievementRepository = AchievementRepository(achievementDao)
    }
    // Insert new budget goal in background thread
    fun insert(goal: BudgetGoal) = viewModelScope.launch {
        repository.insert(goal)
        checkBudgetAchievements(goal.userId, goal.month, goal.year)
    }
    // Get budget goal as LiveData
    fun getBudgetGoal(userId: Int, month: Int, year: Int) = liveData {
        emit(repository.getBudgetGoal(userId, month, year))
    }
    // Update budget goal in background thread
    fun update(goal: BudgetGoal) = viewModelScope.launch {
        repository.update(goal)
    }

    private suspend fun checkBudgetAchievements(userId: Int, month: Int, year: Int) {
        val budgetGoal = repository.getBudgetGoal(userId, month, year)
        budgetGoal?.let {
            val expenseDao = AppDatabase.getDatabase(getApplication()).expenseDao()
            val expenseRepo = ExpenseRepository(expenseDao)

            // Get start and end dates for the month
            val calendar = Calendar.getInstance().apply {
                set(year, month - 1, 1)
            }
            val startDate = calendar.timeInMillis
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endDate = calendar.timeInMillis

            val expenses = expenseRepo.getExpensesByDateRange(userId, startDate, endDate)
            val totalSpent = expenses.sumOf { expense -> expense.amount }

            if (totalSpent <= it.maxAmount) {
                val existing = achievementRepository.getAchievementByType(
                    userId,
                    AchievementTypes.BUDGET_KEEPER
                )

                if (existing == null) {
                    val achievement = Achievement(
                        userId = userId,
                        type = AchievementTypes.BUDGET_KEEPER,
                        title = "Budget Keeper",
                        description = "Stayed within your budget for a month",
                        iconResource = R.drawable.ic_budget_keeper
                    )
                    achievementRepository.insert(achievement)
                }
            }
        }
    }
}

