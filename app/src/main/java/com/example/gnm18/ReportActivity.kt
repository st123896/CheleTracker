package com.example.gnm18

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.gnm18.Models.AchievementViewModel
import com.example.gnm18.Models.BudgetGoalViewModel
import com.example.gnm18.Models.ExpenseViewModel
import com.example.gnm18.databinding.ActivityReportBinding
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var budgetGoalViewModel: BudgetGoalViewModel
    private lateinit var achievementViewModel: AchievementViewModel
    private var userId: Int = 0
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
            .getInt("user_id", 0)


        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Setup toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        setupNavigation()

        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        budgetGoalViewModel = ViewModelProvider(this)[BudgetGoalViewModel::class.java]
        achievementViewModel = ViewModelProvider(this)[AchievementViewModel::class.java]

        // Set default to current month
        updateSelectedMonthText()

        setupMonthSelector()
        loadReport()

        binding.btnGeneratePdf.setOnClickListener {
            generatePdfReport(binding.tvSelectedMonth.text.toString())
        }

    }

    @SuppressLint("UseKtx")
    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_expenses -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                }
                R.id.nav_list -> {
                    startActivity(Intent(this, ExpenseListActivity::class.java))
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "categories")
                    })
                }
                R.id.nav_budget -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "budget")
                    })
                }
                R.id.nav_graph -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "graph")
                    })
                }
                R.id.nav_achievements -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "achievements")
                    })
                }
                R.id.nav_logout -> {
                    getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_report -> {
                    // Already in report activity
                    drawerLayout.closeDrawer(navView)
                }
            }
            true
        }
    }

    private fun updateSelectedMonthText() {
        binding.tvSelectedMonth.text = dateFormat.format(calendar.time)
    }

    private fun setupMonthSelector() {
        binding.btnSelectMonth.setOnClickListener {
            DatePickerDialog(this, { _, year, month, _ ->
                calendar.set(year, month, 1)
                updateSelectedMonthText()
                loadReport()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
                .show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadReport() {
        try {
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            val startOfMonth = calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfMonth = calendar.apply {
                set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            // Change this to use getExpensesWithCategoryByDateRange instead
            expenseViewModel.getExpensesWithCategoryByDateRange(userId, startOfMonth, endOfMonth)
                .observe(this) { expensesWithCategory ->
                    if (expensesWithCategory.isEmpty()) {
                        showEmptyState("No expenses for selected month")
                        return@observe
                    }

                    val totalSpent = expensesWithCategory.sumOf { it.expense.amount }
                    binding.tvTotalSpent.text = "Total Spent: R${"%.2f".format(totalSpent)}"

                    // Group by category name instead of category ID
                    val categories = expensesWithCategory.groupBy { it.categoryName }
                    val categorySummary = categories.map { (categoryName, expenses) ->
                        "$categoryName: R${"%.2f".format(expenses.sumOf { it.expense.amount })}"
                    }.joinToString("\n")

                    binding.tvCategorySummary.text = categorySummary
                }

            budgetGoalViewModel.getBudgetGoal(userId, month, year).observe(this) { goal ->
                goal?.let {
                    binding.tvBudgetGoal.text = "Budget Goal: R${"%.2f".format(it.minAmount)} - R${"%.2f".format(it.maxAmount)}"
                } ?: run {
                    binding.tvBudgetGoal.text = "No budget set for this month"
                }
            }

            achievementViewModel.getAchievementsByUser(userId).observe(this) { achievements ->
                val monthAchievements = achievements.filter {
                    val achieveDate = Calendar.getInstance().apply { timeInMillis = it.dateEarned }
                    achieveDate.get(Calendar.MONTH) + 1 == month &&
                            achieveDate.get(Calendar.YEAR) == year
                }

                if (monthAchievements.isNotEmpty()) {
                    binding.tvAchievements.text = monthAchievements.joinToString("\n") { it.title }
                } else {
                    binding.tvAchievements.text = "No achievements this month"
                }
            }

        } catch (e: Exception) {
            showEmptyState("Error loading report")
            e.printStackTrace()
        }
    }
    private fun showEmptyState(message: String = "No data available") {
        binding.tvTotalSpent.text = message
        binding.tvCategorySummary.text = ""
        binding.tvBudgetGoal.text = ""
        binding.tvAchievements.text = ""
    }

    private fun generatePdfReport(month: String) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas
            val paint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.BLACK
            }

            var yPos = 50f
            canvas.drawText("Monthly Financial Report - $month", 50f, yPos, paint)
            yPos += 30f
            canvas.drawText(binding.tvTotalSpent.text.toString(), 50f, yPos, paint)
            yPos += 20f
            canvas.drawText(binding.tvBudgetGoal.text.toString(), 50f, yPos, paint)
            yPos += 30f
            canvas.drawText("Category Summary:", 50f, yPos, paint)
            yPos += 20f

            binding.tvCategorySummary.text.toString().split("\n").forEach { line ->
                canvas.drawText(line, 50f, yPos, paint)
                yPos += 20f
            }

            yPos += 20f
            canvas.drawText("Achievements:", 50f, yPos, paint)
            yPos += 20f
            binding.tvAchievements.text.toString().split("\n").forEach { line ->
                canvas.drawText(line, 50f, yPos, paint)
                yPos += 20f
            }

            pdfDocument.finishPage(page)

            // Use getExternalFilesDir() for app-specific storage (no permissions needed)
            val downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "ExpenseReport_${month.replace(" ", "_")}.pdf"
            val file = File(downloadsDir, fileName)

            try {
                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(this, "Report saved to $fileName", Toast.LENGTH_LONG).show()

                    // Optionally share the file
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            this@ReportActivity,
                            "${packageName}.fileprovider",
                            file
                        ))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share Report"))
                }
            } catch (e: IOException) {
                Toast.makeText(this, "Error saving report: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                pdfDocument.close()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}