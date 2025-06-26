package com.example.gnm18

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gnm18.Adapter.ExpenseAdapter
import com.example.gnm18.Models.ExpenseViewModel
import com.example.gnm18.databinding.ActivityExpenseListBinding
import com.google.android.material.navigation.NavigationView

/**
 * Displays list of user expenses
 * - Shows expense items in RecyclerView
 * - Observes expense data changes
 * - Supports potential item click actions
 * - Debug logging for expense loading
 */
class ExpenseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var adapter: ExpenseAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Setup toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        setupNavigation()
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        setupRecyclerView()
        loadExpenses()
    }
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
                    // Already in ExpenseListActivity
                    drawerLayout.closeDrawer(navView)
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
                    startActivity(Intent(this, ReportActivity::class.java))
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, ExpenseNotificationActivity::class.java))
                    true
                }
            }
            true
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expense ->

        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ExpenseListActivity)
            adapter = this@ExpenseListActivity.adapter
            setHasFixedSize(false) // Important if items have variable heights
        }
    }

    private fun loadExpenses() {
        val userId = 1
        expenseViewModel.getExpensesWithCategoryByUser(userId).observe(this) { expenses ->
            Log.d("ExpenseList", "Loaded expenses: ${expenses.size}") // Check this log
            if (expenses.size > 1) {
                Log.d("ExpenseList", "First expense: ${expenses[0]}")
                Log.d("ExpenseList", "Second expense: ${expenses[1]}")
            }
            adapter.submitList(expenses)
            adapter.submitList(expenses.toList()) // Create a new list instance
        }

    }

}