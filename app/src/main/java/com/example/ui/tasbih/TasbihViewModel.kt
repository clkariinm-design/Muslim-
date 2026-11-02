package com.example.ui.tasbih

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import com.example.data.tasbih.DhikrItem
import com.example.data.tasbih.TasbihPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TasbihUiState(
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val totalCount: Int = 0,
    val roundsCompleted: Int = 0,
    val selectedDhikr: DhikrItem = TasbihPresets.defaultDhikrList[0],
    val availableDhikrList: List<DhikrItem> = TasbihPresets.defaultDhikrList,
    val isHapticEnabled: Boolean = true,
    val showDhikrPicker: Boolean = false
) {
    val progress: Float
        get() = if (targetCount > 0) {
            (currentCount % targetCount).toFloat() / targetCount.toFloat()
        } else {
            0f
        }
}

class TasbihViewModel(application: Application) : AndroidViewModel(application) {

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _uiState = MutableStateFlow(TasbihUiState())
    val uiState: StateFlow<TasbihUiState> = _uiState.asStateFlow()

    fun increment() {
        val state = _uiState.value
        val newCount = state.currentCount + 1
        val newTotal = state.totalCount + 1
        val target = state.targetCount

        val completedTarget = (target > 0 && newCount % target == 0)
        val newRounds = if (completedTarget) state.roundsCompleted + 1 else state.roundsCompleted

        _uiState.update {
            it.copy(
                currentCount = newCount,
                totalCount = newTotal,
                roundsCompleted = newRounds
            )
        }

        // Haptic feedback
        if (state.isHapticEnabled) {
            if (completedTarget) {
                triggerTargetReachedVibration()
            } else {
                triggerTapVibration()
            }
        }
    }

    fun reset() {
        _uiState.update { it.copy(currentCount = 0) }
        if (_uiState.value.isHapticEnabled) {
            triggerTapVibration()
        }
    }

    fun resetTotal() {
        _uiState.update { it.copy(totalCount = 0, roundsCompleted = 0, currentCount = 0) }
    }

    fun selectDhikr(dhikr: DhikrItem) {
        _uiState.update {
            it.copy(
                selectedDhikr = dhikr,
                targetCount = dhikr.defaultTarget,
                currentCount = 0,
                showDhikrPicker = false
            )
        }
    }

    fun setTarget(target: Int) {
        _uiState.update { it.copy(targetCount = target) }
    }

    fun toggleHaptic() {
        _uiState.update { it.copy(isHapticEnabled = !it.isHapticEnabled) }
    }

    fun openDhikrPicker(open: Boolean) {
        _uiState.update { it.copy(showDhikrPicker = open) }
    }

    private fun triggerTapVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30L)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun triggerTargetReachedVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 80, 80, 140)
                val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(150L)
            }
        } catch (ignored: Exception) {
        }
    }
}
