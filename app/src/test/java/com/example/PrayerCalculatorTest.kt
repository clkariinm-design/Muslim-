package com.example

import com.example.data.prayer.AsrMethod
import com.example.data.prayer.CalculationMethod
import com.example.data.prayer.HighLatitudeRule
import com.example.data.prayer.PrayerCalculator
import com.example.data.prayer.PrayerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class PrayerCalculatorTest {

    @Test
    fun ummAlQura_makkah_regularAndRamadanIsha() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.MAY, 15, 12, 0, 0)
        }

        // Makkah: 21.4225 N, 39.8262 E
        val regularSchedule = PrayerCalculator.calculateSchedule(
            latitude = 21.4225,
            longitude = 39.8262,
            calendar = calendar,
            calculationMethod = CalculationMethod.UMM_AL_QURA,
            isRamadan = false,
            timezoneId = "Asia/Riyadh"
        )

        val maghribRegular = regularSchedule.prayerTimes.first { it.type == PrayerType.MAGHRIB }
        val ishaRegular = regularSchedule.prayerTimes.first { it.type == PrayerType.ISHA }

        // In Umm al-Qura standard, Isha is 90 minutes after Maghrib (allow +/- 1 minute due to round-off)
        val diffMinutesRegular = (ishaRegular.timeMillis - maghribRegular.timeMillis) / 60000L
        assertEquals(90L, diffMinutesRegular)

        // In Ramadan, Isha is 120 minutes after Maghrib
        val ramadanSchedule = PrayerCalculator.calculateSchedule(
            latitude = 21.4225,
            longitude = 39.8262,
            calendar = calendar,
            calculationMethod = CalculationMethod.UMM_AL_QURA,
            isRamadan = true,
            timezoneId = "Asia/Riyadh"
        )
        val maghribRamadan = ramadanSchedule.prayerTimes.first { it.type == PrayerType.MAGHRIB }
        val ishaRamadan = ramadanSchedule.prayerTimes.first { it.type == PrayerType.ISHA }
        val diffMinutesRamadan = (ishaRamadan.timeMillis - maghribRamadan.timeMillis) / 60000L
        assertEquals(120L, diffMinutesRamadan)
    }

    @Test
    fun isna_northAmerica_chronologicalOrdering() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 10, 12, 0, 0)
        }

        // New York: 40.7128 N, -74.0060 W
        val schedule = PrayerCalculator.calculateSchedule(
            latitude = 40.7128,
            longitude = -74.0060,
            calendar = calendar,
            calculationMethod = CalculationMethod.ISNA,
            timezoneId = "America/New_York"
        )

        assertNotNull(schedule)
        assertEquals(6, schedule.prayerTimes.size)

        // Verify strictly chronological
        for (i in 0 until schedule.prayerTimes.size - 1) {
            val curr = schedule.prayerTimes[i]
            val next = schedule.prayerTimes[i + 1]
            assertTrue(
                "Prayer ${curr.type.englishName} (${curr.formattedTime}) must be before ${next.type.englishName} (${next.formattedTime})",
                curr.timeMillis < next.timeMillis
            )
        }
    }

    @Test
    fun asrHanafi_isLaterThanStandard() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 21, 12, 0, 0)
        }

        val standardSchedule = PrayerCalculator.calculateSchedule(
            latitude = 30.0444,
            longitude = 31.2357,
            calendar = calendar,
            calculationMethod = CalculationMethod.EGYPTIAN,
            asrMethod = AsrMethod.STANDARD,
            timezoneId = "Africa/Cairo"
        )

        val hanafiSchedule = PrayerCalculator.calculateSchedule(
            latitude = 30.0444,
            longitude = 31.2357,
            calendar = calendar,
            calculationMethod = CalculationMethod.EGYPTIAN,
            asrMethod = AsrMethod.HANAFI,
            timezoneId = "Africa/Cairo"
        )

        val asrStandard = standardSchedule.prayerTimes.first { it.type == PrayerType.ASR }.timeMillis
        val asrHanafi = hanafiSchedule.prayerTimes.first { it.type == PrayerType.ASR }.timeMillis

        assertTrue("Hanafi Asr must be later than Standard Asr", asrHanafi > asrStandard)
    }

    @Test
    fun highLatitude_summerSolsticeLondon_handlesWithoutCrashing() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 21, 12, 0, 0)
        }

        // London: 51.5074 N
        val schedule = PrayerCalculator.calculateSchedule(
            latitude = 51.5074,
            longitude = -0.1278,
            calendar = calendar,
            calculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
            highLatitudeRule = HighLatitudeRule.ANGLE_BASED,
            timezoneId = "Europe/London"
        )

        assertNotNull(schedule)
        assertEquals(6, schedule.prayerTimes.size)

        for (item in schedule.prayerTimes) {
            assertTrue(item.formattedTime.isNotBlank())
            assertTrue(item.timeMillis > 0L)
        }
    }
}
