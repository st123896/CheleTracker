package com.example.gnm18.Models

import android.Manifest
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.gnm18.ExpenseNotificationActivity
import com.example.gnm18.R
import com.example.gnm18.RoomDb.Achievement
import com.example.gnm18.RoomDb.AchievementTypes
import com.example.gnm18.RoomDb.AppDatabase
import com.example.gnm18.RoomDb.Expense
import com.example.gnm18.RoomDb.ExpenseWithCategory
import com.example.gnm18.RoomDb.Streak
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ViewModel for Expense operations
 * Provides data to UI and survives configuration changes
 */
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository

    init {
        // Initialize repository with ExpenseDao from database
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
        // Removed checkLoggingStreak call from init as it needs to be called from coroutine
    }

    // Insert a new expense in background thread
    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
        checkLoggingStreak(expense.userId)  // 2. Moved checkLoggingStreak call here

    }

    // Get all expenses for a specific user as LiveData
    fun getExpensesByUser(userId: Int) = liveData {
        emit(repository.getExpensesByUser(userId))
    }

    // Get expenses within a date range for a specific user
    fun getExpensesByDateRange(userId: Int, startDate: Long, endDate: Long) = liveData {
        emit(repository.getExpensesByDateRange(userId, startDate, endDate))
    }

    // Get total amount spent in a category within date range
    fun getCategoryTotal(userId: Int, categoryId: Int, startDate: Long, endDate: Long) = liveData {
        emit(repository.getCategoryTotal(userId, categoryId, startDate, endDate))
    }

    // Delete an expense in background thread
    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    private val sharedPrefs by lazy {
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)  // 3. Fixed getApplication reference
    }

    fun getMonthlyBudget(userId: Int): Float {
        return sharedPrefs.getFloat("monthly_budget_$userId", 0f)
    }

    fun setMonthlyBudget(userId: Int, amount: Float) {
        sharedPrefs.edit().putFloat("monthly_budget_$userId", amount).apply()
    }

    fun getExpensesWithCategoryByUser(userId: Int) = liveData {
        emit(repository.getExpensesWithCategoryByUser(userId))
    }

    fun getExpensesWithCategoryByDateRange(userId: Int, startDate: Long, endDate: Long) = liveData {
        emit(repository.getExpensesWithCategoryByDateRange(userId, startDate, endDate))
    }

    private suspend fun checkLoggingStreak(userId: Int) {  // 4. Fixed suspend function call
        val streakDao = AppDatabase.getDatabase(getApplication()).streakDao()  // 5. Fixed getApplication reference
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        var streak = streakDao.getStreak(userId, "expense_logging")

        if (streak == null) {
            streak = Streak(
                userId = userId,
                type = "expense_logging",
                currentStreak = 1,
                lastUpdated = today
            )
            streakDao.insert(streak)
        } else {
            val lastUpdated = Calendar.getInstance().apply {
                timeInMillis = streak.lastUpdated
            }

            val todayCal = Calendar.getInstance()

            // Check if logged yesterday (maintains streak)
            lastUpdated.add(Calendar.DAY_OF_YEAR, 1)
            if (lastUpdated.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                lastUpdated.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)) {
                streak = streak.copy(  // 6. Fixed val reassignment by using copy
                    currentStreak = streak.currentStreak + 1,
                    lastUpdated = today
                )
                streakDao.update(streak)
            }
            // Check if logged today (no change)
            else if (streak.lastUpdated == today) {
                // Do nothing
            }
            // Otherwise reset streak
            else {
                streak = streak.copy(  // 7. Fixed val reassignment by using copy
                    currentStreak = 1,
                    lastUpdated = today
                )
                streakDao.update(streak)
            }
        }

        // Check for streak achievements
        checkStreakAchievements(userId, streak.currentStreak)
    }

    private suspend fun checkStreakAchievements(userId: Int, currentStreak: Int) {
        val achievementDao = AppDatabase.getDatabase(getApplication()).achievementDao()

        when {
            currentStreak >= 7 -> {
                val existing = achievementDao.getAchievementByType(
                    userId,
                    AchievementTypes.STREAK_MASTER
                )

                if (existing == null) {
                    val achievement = Achievement(
                        userId = userId,
                        type = AchievementTypes.STREAK_MASTER,
                        title = "Streak Master",
                        description = "Logged expenses for 7 days in a row",
                        iconResource = R.drawable.ic_streak_master  // 8. Fixed R reference
                    )
                    achievementDao.insert(achievement)
                }
            }
        }
    }
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        private fun sendExpenseNotification(context: Context, expense: ExpenseWithCategory) {
            val intent = Intent(context, ExpenseNotificationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateStr = dateFormat.format(Date(expense.expense.date))
            val timeStr = timeFormat.format(Date(expense.expense.date))

            val notification = NotificationCompat.Builder(context, "expense_notifications")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Expense: ${expense.categoryName}")
                .setContentText("R${"%.2f".format(expense.expense.amount)}")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Category: ${expense.categoryName}\nAmount: R${"%.2f".format(expense.expense.amount)}\nDate: $dateStr\nTime: $timeStr"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(expense.expense.id, notification)
        }
    }

