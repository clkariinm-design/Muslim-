package com.example.ui.azkar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.azkar.AzkarCategory
import com.example.data.azkar.AzkarCollection
import com.example.data.azkar.AzkarItem
import com.example.data.azkar.AzkarScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AzkarUiState(
    val selectedCategory: AzkarCategory = AzkarCategory.MORNING,
    val items: List<AzkarItem> = AzkarCollection.allAzkar.filter { it.category == AzkarCategory.MORNING },
    val completionCounters: Map<String, Int> = emptyMap(),
    val morningEnabled: Boolean = true,
    val middayEnabled: Boolean = true,
    val eveningEnabled: Boolean = true,
    val nightEnabled: Boolean = true,
    val testNotificationMessage: String? = null
)

class AzkarViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AzkarUiState())
    val uiState: StateFlow<AzkarUiState> = _uiState.asStateFlow()

    init {
        // Automatically schedule daily alarms upon launch
        AzkarScheduler.scheduleAllDailyAzkar(application)
    }

    fun selectCategory(category: AzkarCategory) {
        val filtered = AzkarCollection.allAzkar.filter { it.category == category }
        _uiState.update {
            it.copy(
                selectedCategory = category,
                items = filtered
            )
        }
    }

    fun incrementCounter(azkarId: String, maxReps: Int) {
        _uiState.update { current ->
            val counters = current.completionCounters.toMutableMap()
            val existing = counters[azkarId] ?: 0
            if (existing < maxReps) {
                counters[azkarId] = existing + 1
            } else {
                counters[azkarId] = 0 // Reset on full completion
            }
            current.copy(completionCounters = counters)
        }
    }

    fun triggerTestNotification() {
        AzkarScheduler.triggerInstantTestNotification(getApplication())
        _uiState.update {
            it.copy(testNotificationMessage = "Remembrance notification sent! Check your notification shade.")
        }
    }

    fun clearTestMessage() {
        _uiState.update { it.copy(testNotificationMessage = null) }
    }

    fun toggleReminder(type: String, enabled: Boolean) {
        _uiState.update { current ->
            when (type) {
                "morning" -> current.copy(morningEnabled = enabled)
                "midday" -> current.copy(middayEnabled = enabled)
                "evening" -> current.copy(eveningEnabled = enabled)
                "night" -> current.copy(nightEnabled = enabled)
                else -> current
            }
        }
        AzkarScheduler.scheduleAllDailyAzkar(getApplication())
    }
}
