package com.example.gnm18.RoomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// In AppDatabase.kt - add migration handling
@Database(
    entities = [User::class, Category::class, Expense::class, BudgetGoal::class ,  Achievement::class, Streak::class],
    version = 3, // Incremented version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao// DAO for User entity operations
    abstract fun categoryDao(): CategoryDao// DAO for Category entity operations
    abstract fun expenseDao(): ExpenseDao  // DAO for Expense entity operations
    abstract fun budgetGoalDao(): BudgetGoalDao // DAO for BudgetGoal entity operations
    abstract fun achievementDao(): AchievementDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gmn18_db"
                )
                    .addMigrations(MIGRATION_1_2,MIGRATION_2_3) // Add migration
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add any schema changes here if needed
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add schema changes from version 2 to 3
                // For example, if you added new tables or columns:
                // database.execSQL("ALTER TABLE expenses ADD COLUMN new_column INTEGER DEFAULT 0")
            }
        }
    }
}