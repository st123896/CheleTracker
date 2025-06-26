package com.example.gnm18.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.gnm18.RoomDb.AppDatabase
import com.example.gnm18.RoomDb.Category
import kotlinx.coroutines.launch

/**
 * ViewModel for Category operations
 * Manages category data for UI
 */

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository


    init {
        // Initialize repository with CategoryDao from database
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
    }

    // Insert new category in background thread
    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }
    // Get all categories for a user as LiveData
    fun getCategoriesByUser(userId: Int) = liveData {
        emit(repository.getCategoriesByUser(userId))
    }
    // Delete category in background thread
    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }
}