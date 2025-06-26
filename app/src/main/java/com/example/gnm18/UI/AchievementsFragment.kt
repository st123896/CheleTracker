// AchievementsFragment.kt
package com.example.gnm18

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gnm18.Adapter.AchievementAdapter
import com.example.gnm18.Models.AchievementViewModel
import com.example.gnm18.databinding.FragmentAchievementsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

/**
 * Fragment for managing and displaying achievement
 * Allows users to set and view their monthly achievements
 */
class AchievementsFragment : Fragment() {
    private lateinit var binding: FragmentAchievementsBinding
    private lateinit var viewModel: AchievementViewModel
    private lateinit var adapter: AchievementAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)  // Added to enable options menu
        return binding.root
        // Initialize drawer layout and navigation view
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Set up navigation
        setupNavigation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AchievementViewModel::class.java]
        adapter = AchievementAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AchievementsFragment.adapter
        }

        val userId = requireActivity().getSharedPreferences("user_prefs", 0)
            .getInt("user_id", 0)

        viewModel.getAchievementsByUser(userId).observe(viewLifecycleOwner) { achievements ->
            adapter.submitList(achievements)
            binding.emptyView.visibility = if (achievements.isEmpty()) View.VISIBLE else View.GONE
        }
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
                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                        putExtra("open_fragment", "budget")
                    })
                }

                R.id.nav_graph -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                        putExtra("open_fragment", "graph")
                    })
                }

                R.id.nav_achievements -> {
                    // Already in achievements fragment
                    drawerLayout.closeDrawer(navView)
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
        @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.achievements_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                showAchievementsInfoDialog()
                true
            }
            // Remove or implement these actions in ViewModel
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAchievementsInfoDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.about_achievements)
            .setMessage(getString(R.string.achievements_info_message))  // Fixed string reference
            .setPositiveButton(getString(R.string.got_it), null)  // Fixed string reference
            .show()
    }
}