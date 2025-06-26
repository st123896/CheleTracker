package com.example.gnm18.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gnm18.Adapter.ExpenseAdapter
import com.example.gnm18.AddExpenseActivity
import com.example.gnm18.Models.CategoryViewModel
import com.example.gnm18.Models.ExpenseViewModel
import com.example.gnm18.R
import com.example.gnm18.databinding.FragmentExpensesBinding

class ExpensesFragment : Fragment() {
    /**
     * Fragment for displaying and managing expenses
     * Shows list of expenses and allows adding new ones
     */

    // View binding variables
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    // ViewModels for expense and category data

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var categoryViewModel: CategoryViewModel


    // RecyclerView adapter
    private lateinit var adapter: ExpenseAdapter
    private var userId: Int = 0

    // Current user ID from shared preferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("user_id", 0)

        // Initialize ViewModels
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        adapter = ExpenseAdapter { expense ->
            // Handle expense click (show details)
        }
        // Configure RecyclerView

        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        // Setup add expense button
        binding.btnAddExpense.setOnClickListener {
            // Launch AddExpenseActivity
            val intent = Intent(requireContext(), AddExpenseActivity::class.java)
            startActivity(intent)
        }

        loadExpenses()
    }
    /**
     * Loads expenses for the current user
     */
    private fun loadExpenses() {
        expenseViewModel.getExpensesWithCategoryByUser(userId).observe(viewLifecycleOwner) { expenses ->
            adapter.submitList(expenses)
        }
    }
    // Clean up view binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}