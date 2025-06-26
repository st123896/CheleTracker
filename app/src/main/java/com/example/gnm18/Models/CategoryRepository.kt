package com.example.gnm18.Models

import com.example.gnm18.RoomDb.Category
import com.example.gnm18.RoomDb.CategoryDao

class CategoryRepository(private val categoryDao: CategoryDao) {
    /**
     * Repository for Category operations
     * Single source of truth for category data
     */

    // Insert new category
    suspend fun insert(category: Category) = categoryDao.insert(category)

    // Get all categories for a user
    suspend fun getCategoriesByUser(userId: Int) = categoryDao.getCategoriesByUser(userId)
    // Delete a category
    suspend fun delete(category: Category) = categoryDao.delete(category)


}