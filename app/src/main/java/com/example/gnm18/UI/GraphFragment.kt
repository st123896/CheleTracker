package com.example.gnm18.UI

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.gnm18.AddExpenseActivity
import com.example.gnm18.ExpenseListActivity
import com.example.gnm18.ExpenseNotificationActivity
import com.example.gnm18.HomeActivity
import com.example.gnm18.LoginActivity
import com.example.gnm18.MainActivity
import com.example.gnm18.Models.BudgetGoalViewModel
import com.example.gnm18.Models.ExpenseViewModel
import com.example.gnm18.R
import com.example.gnm18.ReportActivity
import com.example.gnm18.RoomDb.Expense
import com.example.gnm18.RoomDb.ExpenseWithCategory
import com.example.gnm18.databinding.FragmentGraphBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt
/**
 * Fragment for managing and displaying budget goals via a graph
 * Allows users to set and view their monthly budget limits
 */
class GraphFragment : Fragment() {
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var budgetGoalViewModel: BudgetGoalViewModel
    private var userId: Int = 0
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
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

        userId = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("user_id", 0)

        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        budgetGoalViewModel = ViewModelProvider(this)[BudgetGoalViewModel::class.java]

        setupCharts()
        setupDateRangeSelector()
        setupQuickFilters()
        loadData()
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
    private fun setupCharts() {
        // Bar Chart Setup
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = 12
                valueFormatter = IndexAxisValueFormatter(getMonthLabels())
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                granularity = 1000f
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
        }

        // Pie Chart Setup
        binding.pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            isDrawHoleEnabled = true
            holeRadius = 45f
            transparentCircleRadius = 50f
            setHoleColor(Color.WHITE)

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                formSize = 12f
                textSize = 12f
                xEntrySpace = 8f
            }

            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            animateXY(1000, 1000)
        }
    }

    private fun getMonthLabels(): List<String> {
        return listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    }

    private fun setupDateRangeSelector() {
        // Initialize with current month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        binding.tvStartDate.setText(dateFormat.format(calendar.time))
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        binding.tvEndDate.setText(dateFormat.format(calendar.time))

        binding.tvStartDate.setOnClickListener { showDatePicker(true) }
        binding.tvEndDate.setOnClickListener { showDatePicker(false) }
        binding.btnApplyFilter.setOnClickListener { loadData() }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val currentDate = Calendar.getInstance()
        try {
            val text = if (isStartDate) binding.tvStartDate.text.toString()
            else binding.tvEndDate.text.toString()
            dateFormat.parse(text)?.let { currentDate.time = it }
        } catch (e: Exception) {
            // Use default if parsing fails
        }

        DatePickerDialog(requireContext(), { _, year, month, day ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, day)
            }
            if (isStartDate) {
                binding.tvStartDate.setText(dateFormat.format(selectedDate.time))
            } else {
                binding.tvEndDate.setText(dateFormat.format(selectedDate.time))
            }
        },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun setupQuickFilters() {
        binding.chipToday.setOnClickListener { setQuickFilter(QuickFilter.TODAY) }
        binding.chipWeek.setOnClickListener { setQuickFilter(QuickFilter.WEEK) }
        binding.chipMonth.setOnClickListener { setQuickFilter(QuickFilter.MONTH) }
        binding.chipYear.setOnClickListener { setQuickFilter(QuickFilter.YEAR) }
    }

    private enum class QuickFilter { TODAY, WEEK, MONTH, YEAR }

    private fun setQuickFilter(filter: QuickFilter) {
        val today = Calendar.getInstance()
        when (filter) {
            QuickFilter.TODAY -> {
                binding.tvStartDate.setText(dateFormat.format(today.time))
                binding.tvEndDate.setText(dateFormat.format(today.time))
            }
            QuickFilter.WEEK -> {
                today.add(Calendar.DAY_OF_YEAR, -7)
                binding.tvStartDate.setText(dateFormat.format(today.time))
                today.add(Calendar.DAY_OF_YEAR, 7)
                binding.tvEndDate.setText(dateFormat.format(today.time))
            }
            QuickFilter.MONTH -> {
                today.set(Calendar.DAY_OF_MONTH, 1)
                binding.tvStartDate.setText(dateFormat.format(today.time))
                today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DAY_OF_MONTH))
                binding.tvEndDate.setText(dateFormat.format(today.time))
            }
            QuickFilter.YEAR -> {
                today.set(Calendar.DAY_OF_YEAR, 1)
                binding.tvStartDate.setText(dateFormat.format(today.time))
                today.set(Calendar.MONTH, 11)
                today.set(Calendar.DAY_OF_MONTH, 31)
                binding.tvEndDate.setText(dateFormat.format(today.time))
            }
        }
        loadData()
    }

    private fun loadData() {
        try {
            val startDate = dateFormat.parse(binding.tvStartDate.text.toString())?.time ?: return
            val endDate = dateFormat.parse(binding.tvEndDate.text.toString())?.time ?: return

            // Use the correct function with date range parameters
            expenseViewModel.getExpensesWithCategoryByDateRange(userId, startDate, endDate)
                .observe(viewLifecycleOwner) { expenses ->
                    if (expenses.isEmpty()) {
                        binding.barChart.visibility = View.GONE
                        binding.pieChart.visibility = View.GONE
                        //binding.tvNoBarData.visibility = View.VISIBLE
                        binding.tvNoPieData.visibility = View.VISIBLE
                        return@observe
                    }

                    binding.barChart.visibility = View.VISIBLE
                    binding.pieChart.visibility = View.VISIBLE
                   // binding.tvNoBarData.visibility = View.GONE
                    binding.tvNoPieData.visibility = View.GONE

                    // Process data for charts
                    val categories = expenses.groupBy { it.expense.categoryId }

                    // Bar Chart Data (Monthly)
                    val monthlyData = expenses.groupBy {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it.expense.date
                        cal.get(Calendar.MONTH)
                    }.map { (month, monthExpenses) ->
                        // Fix: Sum the amounts, not category IDs
                        BarEntry(month.toFloat(), monthExpenses.sumOf { it.expense.amount }.toFloat())
                    }

                    val barDataSet = BarDataSet(monthlyData, "Monthly Spending").apply {
                        colors = ColorTemplate.MATERIAL_COLORS.toList()
                        valueTextColor = Color.BLACK
                        valueTextSize = 10f
                    }

                    binding.barChart.apply {
                        data = BarData(barDataSet)
                        xAxis.valueFormatter = IndexAxisValueFormatter(getMonthLabels())
                        invalidate()
                    }

                    // Pie Chart Data (Categories)
                    val pieEntries = categories.map { (categoryId, categoryExpenses) ->
                        val value = categoryExpenses.sumOf { it.expense.amount }.toFloat()
                        // Use the actual category name from ExpenseWithCategory
                        PieEntry(value, categoryExpenses.first().categoryName ?: "Category $categoryId")
                    }

                    val pieDataSet = PieDataSet(pieEntries, "").apply {
                        colors = ColorTemplate.MATERIAL_COLORS.toList()
                        valueFormatter = PercentFormatter(binding.pieChart)
                        valueTextSize = 12f
                        valueTextColor = Color.WHITE
                        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                    }

                    binding.pieChart.apply {
                        data = PieData(pieDataSet)
                        invalidate()
                    }

                    // Category Breakdown - use actual category names
                    updateCategoryBreakdown(expenses)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateCategoryBreakdown(expenses: List<ExpenseWithCategory>) {
        binding.categoryBreakdownContainer.removeAllViews()

        val categories = expenses.groupBy { it.expense.categoryId }
        val total = expenses.sumOf { it.expense.amount }.toFloat()

        categories.forEach { (_, categoryExpenses) ->
            val amount = categoryExpenses.sumOf { it.expense.amount }.toFloat()
            val percentage = (amount / total * 100).roundToInt()
            val categoryName = categoryExpenses.first().categoryName ?: "Uncategorized"

            val view = layoutInflater.inflate(
                R.layout.item_category_breakdown,
                binding.categoryBreakdownContainer,
                false
            )

            val color = ColorTemplate.MATERIAL_COLORS[categoryExpenses.first().expense.categoryId % ColorTemplate.MATERIAL_COLORS.size]

            view.findViewById<View>(R.id.colorIndicator).setBackgroundColor(color)
            view.findViewById<TextView>(R.id.tvCategoryName).text = categoryName
            view.findViewById<TextView>(R.id.tvPercentage).text = "$percentage%"
            view.findViewById<TextView>(R.id.tvAmount).text = "R${"%.2f".format(amount)}"

            binding.categoryBreakdownContainer.addView(view)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}