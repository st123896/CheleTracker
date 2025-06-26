package com.example.gnm18

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp

import com.example.gnm18.UI.BudgetFragment
import com.example.gnm18.UI.CategoriesFragment
import com.example.gnm18.UI.ExpensesFragment
import com.example.gnm18.UI.GraphFragment
import com.example.gnm18.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

/**
 * Main container activity with navigation drawer
 * - Hosts fragments for different features (Expenses, Categories, Budget)
 * - Manages navigation between fragments
 * - Handles logout functionality
 * - Supports deep linking to specific fragments
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_expenses, R.id.nav_categories, R.id.nav_budget, R.id.nav_graph,R.id.nav_achievements),
            drawerLayout
        )

        setupNavigation()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExpensesFragment())
                .commit()
        }
        // Check if we need to open a specific fragment
        when (intent?.getStringExtra("open_fragment")) {
            "categories" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, CategoriesFragment())
                    .commit()
            }

            "budget" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, BudgetFragment())
                    .commit()
            }
            "graph" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, GraphFragment())
                    .commit()
            }
            "achievements" -> {  // Add this case
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, AchievementsFragment())
                    .commit()
            }
                else -> {
                // Default fragment
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ExpensesFragment())
                        .commit()
                }
            }
        }
    }

    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_expenses -> {
                    // Navigate to AddExpenseActivity
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                }
                R.id.nav_list -> {
                    // Navigate to AddExpenseActivity
                    startActivity(Intent(this, ExpenseListActivity::class.java))
                }
                R.id.nav_categories -> {
                    // Replace with CategoriesFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CategoriesFragment())
                        .addToBackStack(null) // Optional: Add to back stack
                        .commit()
                }
                R.id.nav_budget -> {
                    // Replace with BudgetFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, BudgetFragment())
                        .addToBackStack(null) // Optional: Add to back stack
                        .commit()
                }
                R.id.nav_graph -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, GraphFragment())
                        .commit()
                }
                R.id.nav_achievements -> {  // Add this case
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AchievementsFragment())
                        .commit()
                }
                R.id.nav_logout -> {
                    // Clear shared preferences
                    getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_report -> {
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)

                }
                R.id.nav_notification -> {
                    val intent = Intent(this, ExpenseNotificationActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // Optional: Handle back button with navigation drawer
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}