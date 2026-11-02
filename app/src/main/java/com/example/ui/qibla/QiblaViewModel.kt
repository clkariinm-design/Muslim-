package com.example.ui.qibla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.UserLocation
import com.example.data.qibla.CompassOrientation
import com.example.data.qibla.QiblaCalculator
import com.example.data.qibla.QiblaSensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

data class QiblaUiState(
    val userLocation: UserLocation = UserLocation(
        latitude = 21.4225,
        longitude = 39.8262,
        cityName = "Mecca",
        countryName = "Saudi Arabia"
    ),
    val qiblaBearing: Float = 0f,
    val distanceKm: Double = 0.0,
    val cardinalDirection: String = "N",
    val deviceHeading: Float = 0f, // Azimuth
    val needleAngle: Float = 0f,    // Relative rotation for Kaaba needle
    val isFacingKaaba: Boolean = false,
    val isDeviceFlat: Boolean = true,
    val sensorAccuracy: Int = 3
)

class QiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorManager = QiblaSensorManager(application)
    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    init {
        updateLocation(_uiState.value.userLocation)
        viewModelScope.launch {
            sensorManager.orientationState.collect { orientation ->
                onOrientationUpdated(orientation)
            }
        }
    }

    fun startSensors() {
        sensorManager.startListening()
    }

    fun stopSensors() {
        sensorManager.stopListening()
    }

    fun updateLocation(location: UserLocation) {
        val qiblaData = QiblaCalculator.calculateQibla(location.latitude, location.longitude)
        _uiState.update { current ->
            val relativeNeedle = calculateNeedleAngle(qiblaData.qiblaBearing, current.deviceHeading)
            val facing = isAngleFacingKaaba(relativeNeedle)
            current.copy(
                userLocation = location,
                qiblaBearing = qiblaData.qiblaBearing,
                distanceKm = qiblaData.distanceKm,
                cardinalDirection = qiblaData.cardinalDirection,
                needleAngle = relativeNeedle,
                isFacingKaaba = facing
            )
        }
    }

    private fun onOrientationUpdated(orientation: CompassOrientation) {
        val qibla = _uiState.value.qiblaBearing
        val needle = calculateNeedleAngle(qibla, orientation.azimuth)
        val facing = isAngleFacingKaaba(needle)

        if (facing) {
            sensorManager.triggerQiblaAlignedHaptic()
        }

        _uiState.update { current ->
            current.copy(
                deviceHeading = orientation.azimuth,
                needleAngle = needle,
                isFacingKaaba = facing,
                isDeviceFlat = orientation.isFlat,
                sensorAccuracy = orientation.accuracy
            )
        }
    }

    private fun calculateNeedleAngle(qiblaBearing: Float, deviceHeading: Float): Float {
        var diff = qiblaBearing - deviceHeading
        while (diff < 0f) diff += 360f
        return diff % 360f
    }

    private fun isAngleFacingKaaba(needleAngle: Float): Boolean {
        val diff = if (needleAngle > 180f) 360f - needleAngle else needleAngle
        return diff <= 3.0f
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopListening()
    }
}
