package com.example.gnm18.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.gnm18.RoomDb.Achievement
import com.example.gnm18.RoomDb.AppDatabase
import kotlinx.coroutines.launch

/**
 * ViewModel for Achievement operations
 * Provides budget goal data to UI
 */
class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AchievementRepository

    init {
        val achievementDao = AppDatabase.getDatabase(application).achievementDao()
        repository = AchievementRepository(achievementDao)
    }

    fun insert(achievement: Achievement) = viewModelScope.launch {
        repository.insert(achievement)
    }

    fun getAchievementsByUser(userId: Int) = liveData {
        emit(repository.getAchievementsByUser(userId))
    }

    fun getAchievementByType(userId: Int, type: String) = liveData {
        emit(repository.getAchievementByType(userId, type))
    }

}