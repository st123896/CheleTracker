package com.example.gnm18.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gnm18.RoomDb.Expense
import com.example.gnm18.RoomDb.ExpenseWithCategory
import com.example.gnm18.databinding.ItemExpenseBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * RecyclerView adapter for displaying Expense items
 * Features:
 * - DiffUtil for efficient updates
 * - Glide for image loading
 * - Click listeners for item interaction
 * - Date formatting for display
 * - Category name display
 */
class ExpenseAdapter(
    //private val onClick: (Expense) -> Unit,
    //private val getCategoryName: (Int) -> String? // Function to fetch category name by ID
    private val onClick: (ExpenseWithCategory) -> Unit
) : ListAdapter<ExpenseWithCategory, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ExpenseViewHolder(
        private val binding: ItemExpenseBinding,
        private val onClick: (ExpenseWithCategory) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expenseWithCategory: ExpenseWithCategory) {
            val expense = expenseWithCategory.expense
            binding.tvAmount.text = "R${expense.amount}"
            binding.tvDescription.text = expense.description

            binding.tvDate.text = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                .format(Date(expense.date))

            // Use the categoryName directly from the joined result
            binding.tvCategory.text = expenseWithCategory.categoryName ?: "Uncategorized"

            expense.photoPath?.let { path ->
                binding.ivPhoto.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(File(path))
                    .into(binding.ivPhoto)
            } ?: run {
                binding.ivPhoto.visibility = View.GONE
            }

            binding.root.setOnClickListener { onClick(expenseWithCategory) }
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseWithCategory>() {
        override fun areItemsTheSame(oldItem: ExpenseWithCategory, newItem: ExpenseWithCategory): Boolean {
            return oldItem.expense.id == newItem.expense.id
        }

        override fun areContentsTheSame(oldItem: ExpenseWithCategory, newItem: ExpenseWithCategory): Boolean {
            return oldItem == newItem
        }
    }
}