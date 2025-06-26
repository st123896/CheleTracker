package com.example.gnm18.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gnm18.RoomDb.Category
import com.example.gnm18.databinding.ItemCategoryBinding

/**
 * RecyclerView adapter for category items
 * - Implements ListAdapter with DiffUtil for efficient updates
 * - Handles category display and delete actions
 * - Includes view holder pattern for performance
 * - Supports click listeners for delete operations
 */
class CategoryAdapter(private val onDeleteClick: (Category) -> Unit) :
    ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        private val onDeleteClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.tvName.text = category.name
            binding.btnDelete.setOnClickListener {
                onDeleteClick(category)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}