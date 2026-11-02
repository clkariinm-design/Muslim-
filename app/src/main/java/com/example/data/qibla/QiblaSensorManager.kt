package com.example.data.qibla

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class CompassOrientation(
    val azimuth: Float = 0f, // 0 to 360 degrees
    val pitch: Float = 0f,   // tilt forward/back
    val roll: Float = 0f,    // tilt left/right
    val isFlat: Boolean = true,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
)

class QiblaSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _orientationState = MutableStateFlow(CompassOrientation())
    val orientationState: StateFlow<CompassOrientation> = _orientationState.asStateFlow()

    private var rotationVectorSensor: Sensor? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    // Accelerometer + Magnetometer fallback arrays
    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false

    // Exponential moving average for smooth display
    private var smoothedAzimuth = 0f
    private var lastVibrateTimestamp = 0L

    init {
        rotationVectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor == null) {
            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }
    }

    fun startListening() {
        val sm = sensorManager ?: return
        if (rotationVectorSensor != null) {
            sm.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometer?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            magnetometer?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
        lastAccelerometerSet = false
        lastMagnetometerSet = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                computeAngles(rotationMatrix, event.accuracy)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                lastAccelerometerSet = true
                if (lastAccelerometerSet && lastMagnetometerSet) {
                    if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
                        computeAngles(rotationMatrix, event.accuracy)
                    }
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                lastMagnetometerSet = true
                if (lastAccelerometerSet && lastMagnetometerSet) {
                    if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
                        computeAngles(rotationMatrix, event.accuracy)
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val current = _orientationState.value
        _orientationState.value = current.copy(accuracy = accuracy)
    }

    private fun computeAngles(rMatrix: FloatArray, accuracy: Int) {
        SensorManager.getOrientation(rMatrix, orientationAngles)

        // Azimuth in degrees [0, 360)
        var rawAzimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        rawAzimuth = (rawAzimuth + 360f) % 360f

        val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
        val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
        val isFlat = abs(pitch) < 25f && abs(roll) < 25f

        // Smooth azimuth using circular interpolation
        smoothedAzimuth = smoothAngle(smoothedAzimuth, rawAzimuth, 0.2f)

        _orientationState.value = CompassOrientation(
            azimuth = smoothedAzimuth,
            pitch = pitch,
            roll = roll,
            isFlat = isFlat,
            accuracy = accuracy
        )
    }

    /**
     * Smooths an angle transition across the 0°/360° boundary.
     */
    private fun smoothAngle(current: Float, target: Float, factor: Float): Float {
        var diff = target - current
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        return ((current + diff * factor) + 360f) % 360f
    }

    /**
     * Triggers a subtle tactile pulse when user aligns directly with the Qibla.
     */
    fun triggerQiblaAlignedHaptic() {
        val now = System.currentTimeMillis()
        if (now - lastVibrateTimestamp > 2500L) { // Debounce by 2.5 seconds
            lastVibrateTimestamp = now
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(40L, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(40L)
                }
            } catch (ignored: Exception) {
            }
        }
    }
}
