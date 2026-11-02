package com.example.data.qibla

import kotlin.math.*

object QiblaCalculator {

    // Sacred Kaaba in Makkah coordinates
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0

    data class QiblaData(
        val qiblaBearing: Float, // 0 to 360 degrees from true North
        val distanceKm: Double,
        val cardinalDirection: String
    )

    /**
     * Calculates the great-circle bearing (direction) towards the Kaaba
     * from any latitude and longitude on Earth, along with the distance in km.
     */
    fun calculateQibla(userLat: Double, userLon: Double): QiblaData {
        val lat1Rad = Math.toRadians(userLat)
        val lon1Rad = Math.toRadians(userLon)
        val lat2Rad = Math.toRadians(KAABA_LATITUDE)
        val lon2Rad = Math.toRadians(KAABA_LONGITUDE)

        val deltaLon = lon2Rad - lon1Rad

        val y = sin(deltaLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)

        var bearingRad = atan2(y, x)
        var bearingDeg = Math.toDegrees(bearingRad)
        bearingDeg = (bearingDeg + 360.0) % 360.0

        // Calculate Great-Circle Distance (Haversine formula)
        val deltaLat = lat2Rad - lat1Rad
        val a = sin(deltaLat / 2.0).pow(2.0) +
                cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2.0).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        val distanceKm = EARTH_RADIUS_KM * c

        val cardinal = getCardinalDirection(bearingDeg.toFloat())

        return QiblaData(
            qiblaBearing = bearingDeg.toFloat(),
            distanceKm = distanceKm,
            cardinalDirection = cardinal
        )
    }

    private val cardinalPoints = listOf(
        "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
        "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    )

    private fun getCardinalDirection(bearing: Float): String {
        val index = (((bearing + 11.25f) % 360f) / 22.5f).toInt()
        return cardinalPoints.getOrElse(index % 16) { "N" }
    }
}
