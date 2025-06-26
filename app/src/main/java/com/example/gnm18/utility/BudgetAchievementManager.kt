package com.example.gnm18.utility


import com.example.gnm18.R
import com.example.gnm18.RoomDb.Achievement
import com.example.gnm18.RoomDb.AchievementDao
import com.example.gnm18.RoomDb.AchievementTypes
import com.example.gnm18.RoomDb.BudgetGoalDao
import com.example.gnm18.RoomDb.Streak
import com.example.gnm18.RoomDb.StreakDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetAchievementManager(
    private val budgetGoalDao: BudgetGoalDao,
    private val achievementDao: AchievementDao,
    private val streakDao: StreakDao
) {
    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    fun checkBudgetAchievements(userId: Int, actualSpending: Double, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val budgetGoal = budgetGoalDao.getBudgetGoal(userId, currentMonth, currentYear)
            budgetGoal?.let { goal ->
                val percentageUsed = (actualSpending / goal.maxAmount) * 100

                // Check for bronze achievement (single month 50% or less)
                if (percentageUsed <= 50) {
                    checkBronzeAchievement(userId)

                    // Check for silver streak (2 consecutive months)
                    updateBudgetStreak(userId, 2, AchievementTypes.BUDGET_SILVER)

                    // Check for gold streak (3 consecutive months at 80% or less)
                    if (percentageUsed <= 80) {
                        updateBudgetStreak(userId, 3, AchievementTypes.BUDGET_GOLD)
                    }
                }
            }
        }
    }

    private suspend fun checkBronzeAchievement(userId: Int) {
        val existingAchievement = achievementDao.getAchievementByType(
            userId,
            AchievementTypes.BUDGET_BRONZE
        )

        if (existingAchievement == null) {
            achievementDao.insert(
                Achievement(
                    userId = userId,
                    type = AchievementTypes.BUDGET_BRONZE,
                    title = "Budget Keeper (Bronze)",
                    description = "Stayed within 50% of budget for a month",
                    iconResource = R.drawable.ic_budget_bronze
                )
            )
        }
    }

    private suspend fun updateBudgetStreak(userId: Int, requiredStreak: Int, achievementType: String) {
        val streakType = when (achievementType) {
            AchievementTypes.BUDGET_SILVER -> "budget_silver_streak"
            AchievementTypes.BUDGET_GOLD -> "budget_gold_streak"
            else -> return
        }

        var streak = streakDao.getStreak(userId, streakType)
        val now = System.currentTimeMillis()

        if (streak == null) {
            streak = Streak(
                userId = userId,
                type = streakType,
                currentStreak = 1,
                lastUpdated = now
            )
            streakDao.insert(streak)
        } else {
            // Check if last update was in previous month
            val cal = Calendar.getInstance().apply { timeInMillis = streak.lastUpdated }
            val lastMonth = cal.get(Calendar.MONTH) + 1
            val lastYear = cal.get(Calendar.YEAR)

            if ((currentMonth == lastMonth + 1 && currentYear == lastYear) ||
                (currentMonth == 1 && lastMonth == 12 && currentYear == lastYear + 1)) {
                // Consecutive month
                streak = streak.copy(
                    currentStreak = streak.currentStreak + 1,
                    lastUpdated = now
                )
                streakDao.update(streak)
            } else if (currentMonth != lastMonth || currentYear != lastYear) {
                // Not consecutive, reset streak
                streakDao.resetStreak(userId, streakType)
                return
            }
        }

        // Check if streak meets requirement
        if (streak.currentStreak >= requiredStreak) {
            val existingAchievement = achievementDao.getAchievementByType(
                userId,
                achievementType
            )

            if (existingAchievement == null) {
                val (title, desc) = when (achievementType) {
                    AchievementTypes.BUDGET_SILVER -> Pair(
                        "Budget Keeper (Silver)",
                        "Stayed within 50% of budget for 2 consecutive months"
                    )
                    AchievementTypes.BUDGET_GOLD -> Pair(
                        "Budget Keeper (Gold)",
                        "Stayed within 80% of budget for 3 consecutive months"
                    )
                    else -> return
                }

                achievementDao.insert(
                    Achievement(
                        userId = userId,
                        type = achievementType,
                        title = title,
                        description = desc,
                        iconResource = when (achievementType) {
                            AchievementTypes.BUDGET_SILVER -> R.drawable.ic_budget_silver
                            AchievementTypes.BUDGET_GOLD -> R.drawable.ic_budget_gold
                            else -> return
                        }
                    )
                )
            }
        }
    }
}