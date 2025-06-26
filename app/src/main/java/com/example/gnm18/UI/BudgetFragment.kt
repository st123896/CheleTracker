package com.example.gnm18.UI

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gnm18.AddExpenseActivity
import com.example.gnm18.ExpenseListActivity
import com.example.gnm18.ExpenseNotificationActivity
import com.example.gnm18.HomeActivity
import com.example.gnm18.LoginActivity
import com.example.gnm18.MainActivity
import com.example.gnm18.Models.AchievementViewModel
import com.example.gnm18.Models.BudgetGoalViewModel
import com.example.gnm18.Models.ExpenseViewModel
import com.example.gnm18.R
import com.example.gnm18.ReportActivity
import com.example.gnm18.RoomDb.Achievement
import com.example.gnm18.RoomDb.AchievementTypes
import com.example.gnm18.RoomDb.BudgetGoal
import com.example.gnm18.databinding.FragmentBudgetBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.util.Calendar


/**
 * Fragment for managing and displaying budget goals
 * Allows users to set and view their monthly budget limits
 */

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class BudgetFragment : Fragment() {
    // View binding variables
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    // ViewModels for budget and expense data

    private lateinit var budgetGoalViewModel: BudgetGoalViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    // Current user ID from shared preferences
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        // Initialize drawer layout and navigation view
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Set up navigation
        setupNavigation()

        // Change toolbar navigation icon to menu
        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navView)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Get user ID from shared preferences
        userId = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("user_id", 0)

        // Initialize ViewModels
        budgetGoalViewModel = ViewModelProvider(this)[BudgetGoalViewModel::class.java]
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Load current budget and setup UI
        loadCurrentBudget()
        setupSaveButton()
    }
    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                }
                R.id.nav_expenses -> {
                    startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
                }
                R.id.nav_list -> {
                    startActivity(Intent(requireContext(), ExpenseListActivity::class.java))
                }
                R.id.nav_categories -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                        putExtra("open_fragment", "categories")
                    })
                }
                R.id.nav_budget -> {
                    // Already in budget fragment
                    drawerLayout.closeDrawer(navView)
                }
                R.id.nav_graph -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                        putExtra("open_fragment", "graph")
                    })
                }
                R.id.nav_achievements -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                        putExtra("open_fragment", "achievements")
                    })
                }
                R.id.nav_logout -> {
                    requireActivity().getSharedPreferences("user_prefs", 0).edit().clear().apply()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                R.id.nav_report -> {
                    startActivity(Intent(requireContext(), ReportActivity::class.java))
                }
                R.id.nav_notification -> {
                    startActivity(Intent(requireContext(), ExpenseNotificationActivity::class.java))
                }
            }
            true
        }
    }
    /**
     * Loads the current month's budget goal for the user
     * Updates UI with the budget values if they exist
     */
    private fun loadCurrentBudget() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        // Observe budget goal LiveData
        budgetGoalViewModel.getBudgetGoal(userId, month, year).observe(viewLifecycleOwner) { goal ->
            goal?.let {
                // Update UI with existing budget values
                binding.etMinBudget.setText(it.minAmount.toString())
                binding.etMaxBudget.setText(it.maxAmount.toString())
                updateSavedBudgetDisplay(it.minAmount, it.maxAmount)
                calculateBudgetProgress(it)
            } ?: run {
                // Handle case where no budget is set
                binding.tvSavedBudget.text = "Your Budget: Not set"
            }
        }
    }

    /**
     * Updates the display with the saved budget values
     * @param minAmount The minimum budget amount
     * @param maxAmount The maximum budget amount
     */
    private fun updateSavedBudgetDisplay(minAmount: Double, maxAmount: Double) {
        binding.tvSavedBudget.text =
            "Your Budget: R${"%.2f".format(minAmount)} - R${"%.2f".format(maxAmount)}"
    }

    /**
     * Calculates and displays budget progress based on current expenses
     * @param goal The budget goal containing min/max amounts
     */
    // In BudgetFragment.kt - update calculateBudgetProgress
    private fun calculateBudgetProgress(goal: BudgetGoal) {
        val calendar = Calendar.getInstance()
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

        expenseViewModel.getExpensesByDateRange(userId, startOfMonth, endOfMonth)
            .observe(viewLifecycleOwner) { expenses ->
                val totalSpent = expenses.sumOf { it.amount }
                val percentage = if (goal.maxAmount > 0) {
                    (totalSpent / goal.maxAmount * 100).toInt()
                } else {
                    0
                }

                val statusText = "Current spending: R${"%.2f".format(totalSpent)} ($percentage% of budget)"
                binding.tvBudgetStatus.text = statusText
                binding.seekBarBudget.value = percentage.coerceAtMost(100).toFloat()

                // Check for achievements
                checkBudgetAchievements(userId, totalSpent, goal)

                when {
                    totalSpent < goal.minAmount -> {
                        binding.tvBudgetStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.under_budget))
                        binding.seekBarBudget.trackActiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.under_budget)
                    }
                    totalSpent > goal.maxAmount -> {
                        binding.tvBudgetStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.over_budget))
                        binding.seekBarBudget.trackActiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.over_budget)
                    }
                    else -> {
                        binding.tvBudgetStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_budget))
                        binding.seekBarBudget.trackActiveTintList = ContextCompat.getColorStateList(requireContext(), R.color.on_budget)
                    }
                }
            }
    }

    /**
     * Sets up the save button click listener
     * Validates input and saves budget goals
     */
    private fun setupSaveButton() {
        binding.btnSaveBudget.setOnClickListener {
            val minText = binding.etMinBudget.text.toString()
            val maxText = binding.etMaxBudget.text.toString()

            if (minText.isEmpty() || maxText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both values", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val minAmount = minText.toDouble()
            val maxAmount = maxText.toDouble()

            if (minAmount >= maxAmount) {
                Toast.makeText(requireContext(), "Max must be greater than min", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)


            budgetGoalViewModel.getBudgetGoal(userId, month, year)
                .observe(viewLifecycleOwner) { existingGoal ->
                    if (existingGoal != null) {
                        existingGoal.minAmount = minAmount
                        existingGoal.maxAmount = maxAmount

                        budgetGoalViewModel.update(existingGoal)
                    } else {
                        val newGoal = BudgetGoal(
                            minAmount = minAmount,
                            maxAmount = maxAmount,
                            month = month,
                            year = year,
                            userId = userId,
                        )
                        budgetGoalViewModel.insert(newGoal)
                    }

                    // Update display and clear fields
                    updateSavedBudgetDisplay(minAmount, maxAmount)
                    binding.etMinBudget.text.toString()
                    binding.etMaxBudget.text.toString()
                    Toast.makeText(requireContext(), "Budget goals saved", Toast.LENGTH_SHORT)
                        .show()
                }
        }


    }
    private fun checkForAchievements(userId: Int, totalSpent: Double, goal: BudgetGoal) {
        viewLifecycleOwner.lifecycleScope.launch {
            val achievementViewModel = ViewModelProvider(this@BudgetFragment)[AchievementViewModel::class.java]

            if (totalSpent >= goal.minAmount && totalSpent <= goal.maxAmount) {
                val existing = achievementViewModel.getAchievementByType(userId, "budget_success")
                if (existing == null) {
                    val achievement = Achievement(
                        userId = userId,
                        type = "budget_success",
                        title = getString(R.string.achievement_budget_title),
                        description = getString(R.string.achievement_budget_desc),
                        iconResource = R.drawable.ic_achievement_budget
                    )
                    achievementViewModel.insert(achievement)
                    showAchievementPopup(achievement)
                }
            }
        }
    }

    private fun showAchievementPopup(achievement: Achievement) {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_achievement)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            findViewById<TextView>(R.id.tv_achievement_title).text = achievement.title
            findViewById<TextView>(R.id.tv_achievement_desc).text = achievement.description
            findViewById<ImageView>(R.id.iv_achievement_icon).setImageResource(achievement.iconResource)

            findViewById<Button>(R.id.btn_close).setOnClickListener { dismiss() }
        }
        dialog.show()
    }


    private fun checkBudgetAchievements(userId: Int, totalSpent: Double, goal: BudgetGoal) {
        viewLifecycleOwner.lifecycleScope.launch {
            val achievementViewModel = ViewModelProvider(this@BudgetFragment)[AchievementViewModel::class.java]
            val percentageUsed = (totalSpent / goal.maxAmount) * 100

            // Bronze achievement (50% or less in one month)
            if (percentageUsed <= 50) {
                val existing = achievementViewModel.getAchievementByType(
                    userId,
                    AchievementTypes.BUDGET_BRONZE
                )
                if (existing == null) {
                    val achievement = Achievement(
                        userId = userId,
                        type = AchievementTypes.BUDGET_BRONZE,
                        title = "Budget Keeper (Bronze)",
                        description = "Stayed within 50% of budget for a month",
                        iconResource = R.drawable.ic_budget_bronze
                    )
                    achievementViewModel.insert(achievement)
                    showAchievementPopup(achievement)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}