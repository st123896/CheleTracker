package com.example.gnm18.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gnm18.Adapter.CategoryAdapter
import com.example.gnm18.AddExpenseActivity
import com.example.gnm18.ExpenseListActivity
import com.example.gnm18.ExpenseNotificationActivity
import com.example.gnm18.HomeActivity
import com.example.gnm18.LoginActivity
import com.example.gnm18.MainActivity
import com.example.gnm18.Models.CategoryViewModel
import com.example.gnm18.R
import com.example.gnm18.ReportActivity
import com.example.gnm18.RoomDb.Category
import com.example.gnm18.databinding.FragmentCategoriesBinding
import com.google.android.material.navigation.NavigationView

/**
 * Fragment for managing expense categories
 * Allows adding, viewing and deleting categories
 */
class CategoriesFragment : Fragment() {
    // View binding variables
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    // ViewModel for category data
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    // RecyclerView adapter
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
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


        // Initialize ViewModel
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]


        // Setup RecyclerView adapter with delete click handler
        adapter = CategoryAdapter { category ->
            showDeleteDialog(category)
        }
// Configure RecyclerView
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        // Setup add category button
        binding.btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        loadCategories()
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
    // Load categories
    private fun loadCategories() {
        categoryViewModel.getCategoriesByUser(userId).observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }
    }
    /**
     * Loads categories for the current user
     */
    private fun showAddCategoryDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Category name"

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Add New Category")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotEmpty()) {
                    val category = Category(name = name, userId = userId)
                    categoryViewModel.insert(category)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    /**
     * Shows confirmation dialog before deleting a category
     * @param category The category to be deleted
     */
    private fun showDeleteDialog(category: Category) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete ${category.name}?")
            .setPositiveButton("Delete") { _, _ ->
                categoryViewModel.delete(category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}