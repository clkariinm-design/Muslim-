package com.example

import com.example.data.qibla.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QiblaCalculatorTest {

    @Test
    fun qibla_fromMogadishu_calculatesAccurateBearingAndDistance() {
        // Mogadishu coordinates: 2.0469 N, 45.3182 E
        val qibla = QiblaCalculator.calculateQibla(2.0469, 45.3182)

        // Mogadishu to Makkah is NNW (~347 degrees)
        assertTrue(
            "Bearing from Mogadishu should be between 340 and 355 degrees (was ${qibla.qiblaBearing})",
            qibla.qiblaBearing in 340f..355f
        )

        // Distance is approx 2,220 km
        assertTrue(
            "Distance should be around 2,200 km (was ${qibla.distanceKm})",
            qibla.distanceKm in 2100.0..2350.0
        )
    }

    @Test
    fun qibla_fromLondon_calculatesAccurateBearingAndDistance() {
        // London coordinates: 51.5074 N, -0.1278 W
        val qibla = QiblaCalculator.calculateQibla(51.5074, -0.1278)

        // London to Makkah is ESE (~118-120 degrees)
        assertTrue(
            "Bearing from London should be between 115 and 125 degrees (was ${qibla.qiblaBearing})",
            qibla.qiblaBearing in 115f..125f
        )
        assertEquals("ESE", qibla.cardinalDirection)

        // Distance is approx 4,790 km
        assertTrue(
            "Distance should be around 4,790 km (was ${qibla.distanceKm})",
            qibla.distanceKm in 4600.0..4950.0
        )
    }

    @Test
    fun qibla_fromNewYork_calculatesGreatCircleBearing() {
        // New York coordinates: 40.7128 N, -74.0060 W
        val qibla = QiblaCalculator.calculateQibla(40.7128, -74.0060)

        // Great circle from NY to Makkah is ENE (~58 degrees)
        assertTrue(
            "Great-circle bearing from NY should be between 55 and 65 degrees (was ${qibla.qiblaBearing})",
            qibla.qiblaBearing in 55f..65f
        )
        assertEquals("ENE", qibla.cardinalDirection)
    }
}
