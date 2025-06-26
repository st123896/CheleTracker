package com.example.gnm18.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.gnm18.RoomDb.AppDatabase
import com.example.gnm18.RoomDb.User
import kotlinx.coroutines.launch


/**
 * ViewModel for User operations
 * Manages user-related data for UI
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    // Initialize repository with UserDao from database
    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }
    // Insert new user in background thread
    fun insert(user: User) = viewModelScope.launch {
        repository.insert(user)
    }
    // Get user by credentials as LiveData
    fun getUser(username: String, password: String) = liveData {
        emit(repository.getUser(username, password))
    }
    // Check username existence as LiveData
    fun checkUsernameExists(username: String) = liveData {
        emit(repository.checkUsernameExists(username))
    }
}