package com.example.ui.prayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.LocationService
import com.example.data.location.PresetCities
import com.example.data.location.PresetCity
import com.example.data.location.UserLocation
import com.example.data.prayer.AsrMethod
import com.example.data.prayer.CalculationMethod
import com.example.data.prayer.HighLatitudeRule
import com.example.data.prayer.PrayerCalculator
import com.example.data.prayer.PrayerSchedule
import com.example.data.prayer.PrayerType
import com.example.data.prayer.adhan.AdhanAudioPlayer
import com.example.data.prayer.adhan.AdhanMelody
import com.example.data.prayer.adhan.AdhanScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class PrayerUiState(
    val isLoadingLocation: Boolean = false,
    val location: UserLocation = UserLocation(
        latitude = 21.4225,
        longitude = 39.8262,
        cityName = "Mecca",
        countryName = "Saudi Arabia",
        timezoneId = "Asia/Riyadh",
        isGpsDetected = false
    ),
    val schedule: PrayerSchedule? = null,
    val calculationMethod: CalculationMethod = CalculationMethod.ISNA,
    val asrMethod: AsrMethod = AsrMethod.STANDARD,
    val highLatitudeRule: HighLatitudeRule = HighLatitudeRule.ANGLE_BASED,
    val isRamadan: Boolean = false,
    val countdownFormatted: String = "00:00:00",
    val locationErrorMessage: String? = null,
    val showCityPicker: Boolean = false,
    val showMethodPicker: Boolean = false,
    val citySearchQuery: String = "",
    val notifications: Map<PrayerType, Boolean> = PrayerType.entries.associateWith { true },
    val selectedMelody: com.example.data.prayer.adhan.AdhanMelody = com.example.data.prayer.adhan.AdhanMelody.MAKKAH_MELODY,
    val isAdhanPlaying: Boolean = false
) {
    val filteredCities: List<PresetCity>
        get() = if (citySearchQuery.isBlank()) {
            PresetCities.popularCities
        } else {
            PresetCities.popularCities.filter {
                it.name.contains(citySearchQuery, ignoreCase = true) ||
                        it.country.contains(citySearchQuery, ignoreCase = true)
            }
        }
}

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val locationService = LocationService(application)
    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        recalculatePrayerTimes()
        startCountdownTicker()
        observeAdhanAudioState()
    }

    private fun observeAdhanAudioState() {
        viewModelScope.launch {
            com.example.data.prayer.adhan.AdhanAudioPlayer.isPlayingState.collect { playing ->
                _uiState.update { it.copy(isAdhanPlaying = playing) }
            }
        }
    }

    fun onLocationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            detectCurrentLocation()
        } else {
            _uiState.update {
                it.copy(
                    locationErrorMessage = "Location permission denied. Using selected city."
                )
            }
        }
    }

    fun detectCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true, locationErrorMessage = null) }
            val result = locationService.getCurrentLocation()
            result.onSuccess { detectedLocation ->
                _uiState.update {
                    it.copy(
                        isLoadingLocation = false,
                        location = detectedLocation,
                        locationErrorMessage = null
                    )
                }
                recalculatePrayerTimes()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingLocation = false,
                        locationErrorMessage = error.message ?: "Could not detect GPS location."
                    )
                }
            }
        }
    }

    fun selectPresetCity(city: PresetCity) {
        val newLocation = UserLocation(
            latitude = city.latitude,
            longitude = city.longitude,
            cityName = city.name,
            countryName = city.country,
            timezoneId = city.timezoneId,
            isGpsDetected = false
        )
        _uiState.update {
            it.copy(
                location = newLocation,
                showCityPicker = false,
                locationErrorMessage = null
            )
        }
        recalculatePrayerTimes()
    }

    fun setCalculationMethod(method: CalculationMethod) {
        _uiState.update { it.copy(calculationMethod = method) }
        recalculatePrayerTimes()
    }

    fun setAsrMethod(method: AsrMethod) {
        _uiState.update { it.copy(asrMethod = method) }
        recalculatePrayerTimes()
    }

    fun toggleNotification(prayerType: PrayerType) {
        _uiState.update { state ->
            val updated = state.notifications.toMutableMap()
            val current = updated[prayerType] ?: true
            updated[prayerType] = !current
            state.copy(notifications = updated)
        }
        syncAlarms()
    }

    fun setAdhanMelody(melody: AdhanMelody) {
        _uiState.update { it.copy(selectedMelody = melody) }
        syncAlarms()
    }

    fun toggleAudibleAdhanTest(prayerType: PrayerType = PrayerType.FAJR) {
        if (_uiState.value.isAdhanPlaying) {
            stopAudibleAdhan()
        } else {
            val type = _uiState.value.schedule?.nextPrayer?.type ?: prayerType
            AdhanScheduler.triggerInstantAdhanTest(
                context = getApplication(),
                prayerType = type,
                melody = _uiState.value.selectedMelody
            )
        }
    }

    fun stopAudibleAdhan() {
        AdhanScheduler.stopAdhanAlert(getApplication())
    }

    private fun syncAlarms(schedule: PrayerSchedule? = _uiState.value.schedule) {
        if (schedule != null) {
            AdhanScheduler.schedulePrayerAlarms(
                context = getApplication(),
                schedule = schedule,
                notificationsMap = _uiState.value.notifications,
                melody = _uiState.value.selectedMelody
            )
        }
    }

    fun openCityPicker(open: Boolean) {
        _uiState.update { it.copy(showCityPicker = open, citySearchQuery = "") }
    }

    fun openMethodPicker(open: Boolean) {
        _uiState.update { it.copy(showMethodPicker = open) }
    }

    fun updateCitySearch(query: String) {
        _uiState.update { it.copy(citySearchQuery = query) }
    }

    fun setHighLatitudeRule(rule: HighLatitudeRule) {
        _uiState.update { it.copy(highLatitudeRule = rule) }
        recalculatePrayerTimes()
    }

    fun toggleRamadan(isRamadan: Boolean) {
        _uiState.update { it.copy(isRamadan = isRamadan) }
        recalculatePrayerTimes()
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(locationErrorMessage = null) }
    }

    fun recalculatePrayerTimes() {
        val state = _uiState.value
        val schedule = PrayerCalculator.calculateSchedule(
            latitude = state.location.latitude,
            longitude = state.location.longitude,
            calendar = Calendar.getInstance(),
            calculationMethod = state.calculationMethod,
            asrMethod = state.asrMethod,
            highLatitudeRule = state.highLatitudeRule,
            isRamadan = state.isRamadan,
            timezoneId = state.location.timezoneId
        )
        _uiState.update { it.copy(schedule = schedule) }
        updateCountdownString(schedule.remainingMillisToNext)
        syncAlarms(schedule)
    }

    private fun startCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val schedule = _uiState.value.schedule
                if (schedule != null) {
                    val nextPrayerMillis = schedule.nextPrayer?.timeMillis ?: 0L
                    val now = System.currentTimeMillis()
                    val diff = (nextPrayerMillis - now).coerceAtLeast(0L)

                    if (diff <= 0L) {
                        // Current prayer has arrived! Recalculate schedule
                        recalculatePrayerTimes()
                    } else {
                        updateCountdownString(diff)
                    }
                }
            }
        }
    }

    private fun updateCountdownString(remainingMillis: Long) {
        val totalSeconds = remainingMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val formatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        _uiState.update { it.copy(countdownFormatted = formatted) }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        AdhanAudioPlayer.stop()
    }
}
