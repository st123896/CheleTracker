package com.example.gnm18.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gnm18.RoomDb.AppDatabase
import com.example.gnm18.RoomDb.Streak
import kotlinx.coroutines.launch
import java.util.Calendar
/**
 * ViewModel for Streak operations
 * Provides budget goal data to UI
 */
class StreakViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StreakRepository
    private val _currentStreak = MutableLiveData<Streak?>()
    val currentStreak: LiveData<Streak?> = _currentStreak

    init {
        val streakDao = AppDatabase.getDatabase(application).streakDao()
        repository = StreakRepository(streakDao)
    }

    fun getStreak(userId: Int, type: String) = viewModelScope.launch {
        _currentStreak.value = repository.getStreak(userId, type)
    }

    fun getAllStreaksForUser(userId: Int) = viewModelScope.launch {
        _currentStreak.value = repository.getAllStreaksForUser(userId).firstOrNull()
    }

    fun updateStreak(streak: Streak) = viewModelScope.launch {
        repository.update(streak)
        _currentStreak.value = streak
    }

    fun incrementStreak(userId: Int, type: String) = viewModelScope.launch {
        val current = repository.getStreak(userId, type)
        if (current == null) {
            val newStreak = Streak(
                userId = userId,
                type = type,
                currentStreak = 1,
                lastUpdated = System.currentTimeMillis()
            )
            repository.insert(newStreak)
            _currentStreak.value = newStreak
        } else {
            val updated = current.copy(
                currentStreak = current.currentStreak + 1,
                lastUpdated = System.currentTimeMillis()
            )
            repository.update(updated)
            _currentStreak.value = updated
        }
    }

    fun resetStreak(userId: Int, type: String) = viewModelScope.launch {
        repository.resetStreak(userId, type)
        _currentStreak.value = null
    }

    fun checkAndUpdateStreak(userId: Int, type: String) = viewModelScope.launch {
        val currentStreak = repository.getStreak(userId, type)
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }

        currentStreak?.let { streak ->
            val lastUpdated = Calendar.getInstance().apply {
                timeInMillis = streak.lastUpdated
            }

            // Check if streak should be reset (more than 1 day gap)
            if (calendar.get(Calendar.DAY_OF_YEAR) - lastUpdated.get(Calendar.DAY_OF_YEAR) > 1 ||
                calendar.get(Calendar.YEAR) > lastUpdated.get(Calendar.YEAR)) {
                resetStreak(userId, type)
            }
            // Check if we should increment (new day)
            else if (calendar.get(Calendar.DAY_OF_YEAR) != lastUpdated.get(Calendar.DAY_OF_YEAR)) {
                incrementStreak(userId, type)
            }
        } ?: run {
            // No existing streak - create new one
            val newStreak = Streak(
                userId = userId,
                type = type,
                currentStreak = 1,
                lastUpdated = now
            )
            repository.insert(newStreak)
            _currentStreak.value = newStreak
        }
    }
}