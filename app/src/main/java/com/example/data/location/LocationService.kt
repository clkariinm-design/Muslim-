package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

class LocationService(private val context: Context) {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    /**
     * Attempts to retrieve current location using Google Play Services FusedLocationProviderClient
     * with fallback to standard Android LocationManager.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<UserLocation> = withContext(Dispatchers.IO) {
        try {
            // First attempt: Fused Location Provider (high accuracy or last known)
            var androidLocation: Location? = tryGetFusedLocation()

            // Second attempt: Fallback to Android LocationManager
            if (androidLocation == null) {
                androidLocation = tryGetSystemLocation()
            }

            if (androidLocation != null) {
                val geocoded = reverseGeocode(androidLocation.latitude, androidLocation.longitude)
                Result.success(
                    UserLocation(
                        latitude = androidLocation.latitude,
                        longitude = androidLocation.longitude,
                        cityName = geocoded.first,
                        countryName = geocoded.second,
                        timezoneId = TimeZone.getDefault().id,
                        isGpsDetected = true
                    )
                )
            } else {
                Result.failure(Exception("Unable to obtain location fix from GPS/Network."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun tryGetFusedLocation(): Location? {
        return try {
            val cts = CancellationTokenSource()
            suspendCancellableCoroutine { cont ->
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            cont.resume(loc)
                        } else {
                            // If immediate current is null, try lastLocation
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { lastLoc -> cont.resume(lastLoc) }
                                .addOnFailureListener { cont.resume(null) }
                        }
                    }
                    .addOnFailureListener {
                        cont.resume(null)
                    }

                cont.invokeOnCancellation {
                    cts.cancel()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun tryGetSystemLocation(): Location? {
        return try {
            val lm = locationManager ?: return null
            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            )
            var bestLocation: Location? = null
            for (provider in providers) {
                if (lm.isProviderEnabled(provider)) {
                    val loc = lm.getLastKnownLocation(provider)
                    if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                        bestLocation = loc
                    }
                }
            }
            bestLocation
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("DEPRECATION")
    private fun reverseGeocode(latitude: Double, longitude: Double): Pair<String, String> {
        return try {
            if (!Geocoder.isPresent()) {
                return Pair(formatCoordinates(latitude, longitude), "")
            }
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val city = address.locality
                    ?: address.subAdminArea
                    ?: address.adminArea
                    ?: formatCoordinates(latitude, longitude)
                val country = address.countryName ?: ""
                Pair(city, country)
            } else {
                Pair(formatCoordinates(latitude, longitude), "")
            }
        } catch (e: Exception) {
            Pair(formatCoordinates(latitude, longitude), "")
        }
    }

    private fun formatCoordinates(lat: Double, lon: Double): String {
        val latDir = if (lat >= 0) "N" else "S"
        val lonDir = if (lon >= 0) "E" else "W"
        return String.format(Locale.US, "%.2f°%s, %.2f°%s", Math.abs(lat), latDir, Math.abs(lon), lonDir)
    }
}
