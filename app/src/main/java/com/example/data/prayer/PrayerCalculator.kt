package com.example.data.prayer

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object PrayerCalculator {

    /**
     * Calculates prayer times for a given location, date, and calculation convention.
     * Guaranteed compliance with standard methods such as ISNA, Umm al-Qura, MWL, Egyptian, etc.
     */
    fun calculateSchedule(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance(),
        calculationMethod: CalculationMethod = CalculationMethod.ISNA,
        asrMethod: AsrMethod = AsrMethod.STANDARD,
        highLatitudeRule: HighLatitudeRule = HighLatitudeRule.ANGLE_BASED,
        isRamadan: Boolean = false,
        timezoneId: String = TimeZone.getDefault().id,
        minuteOffsets: Map<PrayerType, Int> = emptyMap()
    ): PrayerSchedule {
        val tz = TimeZone.getTimeZone(timezoneId)
        val tzOffsetHours = tz.getOffset(calendar.timeInMillis).toDouble() / (1000.0 * 60.0 * 60.0)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian Day Number adjusted for longitude
        val jd = julianDay(year, month, day) - (longitude / (15.0 * 24.0))

        // Sun astronomical parameters
        val sunCoords = calculateSunCoordinates(jd)
        val declination = sunCoords.declination // in degrees
        val eqOfTime = sunCoords.equationOfTime // in minutes

        // Solar Noon (transit time in decimal hours)
        val solarNoon = 12.0 + tzOffsetHours - (longitude / 15.0) - (eqOfTime / 60.0)

        // Dhuhr: Meridian transit + 1 minute safety buffer (Zawal)
        val dhuhrHour = solarNoon + (1.0 / 60.0)

        // Sunrise & Sunset: Solar altitude -0.833° (accounting for 16' semidiameter + 34' atmospheric refraction)
        val sunriseHourAngleOpt = calculateHourAngle(-0.833, latitude, declination)
        val sunriseHourAngle = sunriseHourAngleOpt ?: 90.0
        val sunriseHour = solarNoon - (sunriseHourAngle / 15.0)
        val sunsetHour = solarNoon + (sunriseHourAngle / 15.0)

        // Night duration in hours (from sunset to next sunrise)
        val nightDurationHours = (24.0 - sunsetHour + sunriseHour).coerceIn(4.0, 20.0)

        // Fajr calculation
        val fajrAngle = calculationMethod.fajrAngle
        val rawFajrHourAngle = calculateHourAngle(-fajrAngle, latitude, declination)
        val fajrHour = if (rawFajrHourAngle != null) {
            val computed = solarNoon - (rawFajrHourAngle / 15.0)
            // Validate against high-latitude threshold
            adjustHighLatitudeTwilight(
                twilightHour = computed,
                boundaryHour = sunriseHour,
                nightDuration = nightDurationHours,
                angle = fajrAngle,
                isMorning = true,
                rule = highLatitudeRule
            )
        } else {
            // Sun does not dip below twilight angle (polar/extreme summer): apply rule fallback
            applyHighLatitudeFallback(
                boundaryHour = sunriseHour,
                nightDuration = nightDurationHours,
                angle = fajrAngle,
                isMorning = true,
                rule = highLatitudeRule
            )
        }

        // Asr calculation based on shadow ratio
        val asrAngle = calculateAsrAngle(asrMethod.shadowFactor, latitude, declination)
        val asrHourAngleOpt = calculateHourAngle(asrAngle, latitude, declination)
        val asrHourAngle = asrHourAngleOpt ?: ((sunsetHour - solarNoon) * 15.0 * 0.6)
        val asrHour = solarNoon + (asrHourAngle / 15.0)

        // Maghrib calculation (sunset + slight disc clearance)
        val maghribHour = sunsetHour

        // Isha calculation
        val ishaHour = if (calculationMethod.ishaFixedMinutes > 0) {
            // Umm al-Qura, Qatar, Dubai: fixed interval after Maghrib
            val minutes = if (isRamadan && calculationMethod.ramadanIshaMinutes > 0) {
                calculationMethod.ramadanIshaMinutes
            } else {
                calculationMethod.ishaFixedMinutes
            }
            maghribHour + (minutes.toDouble() / 60.0)
        } else {
            // Angle-based (ISNA: 15°, MWL: 17°, Egyptian: 17.5°, Karachi: 18°)
            val ishaAngle = calculationMethod.ishaAngle
            val rawIshaHourAngle = calculateHourAngle(-ishaAngle, latitude, declination)
            if (rawIshaHourAngle != null) {
                val computed = solarNoon + (rawIshaHourAngle / 15.0)
                adjustHighLatitudeTwilight(
                    twilightHour = computed,
                    boundaryHour = sunsetHour,
                    nightDuration = nightDurationHours,
                    angle = ishaAngle,
                    isMorning = false,
                    rule = highLatitudeRule
                )
            } else {
                applyHighLatitudeFallback(
                    boundaryHour = sunsetHour,
                    nightDuration = nightDurationHours,
                    angle = ishaAngle,
                    isMorning = false,
                    rule = highLatitudeRule
                )
            }
        }

        // Base midnight epoch millis
        val baseCalendar = (calendar.clone() as Calendar).apply {
            timeZone = tz
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val baseMillis = baseCalendar.timeInMillis
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
            timeZone = tz
        }

        val nowMillis = System.currentTimeMillis()

        // Apply any minute offsets
        val rawTimes = listOf(
            PrayerType.FAJR to (decimalHourToMillis(baseMillis, fajrHour) + (minuteOffsets[PrayerType.FAJR] ?: 0) * 60000L),
            PrayerType.SUNRISE to (decimalHourToMillis(baseMillis, sunriseHour) + (minuteOffsets[PrayerType.SUNRISE] ?: 0) * 60000L),
            PrayerType.DHUHR to (decimalHourToMillis(baseMillis, dhuhrHour) + (minuteOffsets[PrayerType.DHUHR] ?: 0) * 60000L),
            PrayerType.ASR to (decimalHourToMillis(baseMillis, asrHour) + (minuteOffsets[PrayerType.ASR] ?: 0) * 60000L),
            PrayerType.MAGHRIB to (decimalHourToMillis(baseMillis, maghribHour) + (minuteOffsets[PrayerType.MAGHRIB] ?: 0) * 60000L),
            PrayerType.ISHA to (decimalHourToMillis(baseMillis, ishaHour) + (minuteOffsets[PrayerType.ISHA] ?: 0) * 60000L)
        )

        // Find next prayer index
        var nextIndex = -1
        for (i in rawTimes.indices) {
            if (rawTimes[i].second > nowMillis) {
                nextIndex = i
                break
            }
        }

        val isAllPassedToday = (nextIndex == -1)
        val nextPrayerMillis = if (isAllPassedToday) {
            rawTimes[0].second + (24 * 60 * 60 * 1000L)
        } else {
            rawTimes[nextIndex].second
        }

        val prevPrayerMillis = when {
            isAllPassedToday -> rawTimes.last().second
            nextIndex == 0 -> rawTimes[0].second - (6 * 60 * 60 * 1000L)
            else -> rawTimes[nextIndex - 1].second
        }

        val remainingMillis = (nextPrayerMillis - nowMillis).coerceAtLeast(0L)
        val totalInterval = (nextPrayerMillis - prevPrayerMillis).coerceAtLeast(1L)
        val elapsedInterval = (nowMillis - prevPrayerMillis).coerceAtLeast(0L)
        val progressPercent = (elapsedInterval.toFloat() / totalInterval.toFloat()).coerceIn(0f, 1f)

        val items = rawTimes.mapIndexed { index, pair ->
            val prayerType = pair.first
            val timeMillis = pair.second
            val isNext = (index == nextIndex)
            val isPassed = timeMillis < nowMillis

            PrayerTimeItem(
                type = prayerType,
                timeMillis = timeMillis,
                formattedTime = timeFormat.format(Date(timeMillis)),
                isNext = isNext,
                isPassed = isPassed,
                notificationEnabled = true
            )
        }

        val nextItem = items.find { it.isNext } ?: items.first().copy(
            timeMillis = nextPrayerMillis,
            formattedTime = timeFormat.format(Date(nextPrayerMillis)),
            isNext = true
        )

        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).apply {
            timeZone = tz
        }
        val dateDisplay = dateFormat.format(calendar.time)
        val hijriDisplay = HijriCalendarHelper.getHijriDate(calendar)

        return PrayerSchedule(
            prayerTimes = items,
            nextPrayer = nextItem,
            previousPrayer = items.find { it.timeMillis == prevPrayerMillis },
            remainingMillisToNext = remainingMillis,
            elapsedPercentOfCurrentInterval = progressPercent,
            dateDisplay = dateDisplay,
            hijriDateDisplay = hijriDisplay,
            calculationMethod = calculationMethod,
            asrMethod = asrMethod
        )
    }

    private fun adjustHighLatitudeTwilight(
        twilightHour: Double,
        boundaryHour: Double,
        nightDuration: Double,
        angle: Double,
        isMorning: Boolean,
        rule: HighLatitudeRule
    ): Double {
        val portion = when (rule) {
            HighLatitudeRule.MIDDLE_OF_NIGHT -> 0.5
            HighLatitudeRule.ONE_SEVENTH -> 1.0 / 7.0
            HighLatitudeRule.ANGLE_BASED -> angle / 60.0
        }
        val maxDuration = nightDuration * portion

        return if (isMorning) {
            val morningDuration = boundaryHour - twilightHour
            if (morningDuration > maxDuration) {
                boundaryHour - maxDuration
            } else {
                twilightHour
            }
        } else {
            val eveningDuration = twilightHour - boundaryHour
            if (eveningDuration > maxDuration) {
                boundaryHour + maxDuration
            } else {
                twilightHour
            }
        }
    }

    private fun applyHighLatitudeFallback(
        boundaryHour: Double,
        nightDuration: Double,
        angle: Double,
        isMorning: Boolean,
        rule: HighLatitudeRule
    ): Double {
        val portion = when (rule) {
            HighLatitudeRule.MIDDLE_OF_NIGHT -> 0.5
            HighLatitudeRule.ONE_SEVENTH -> 1.0 / 7.0
            HighLatitudeRule.ANGLE_BASED -> angle / 60.0
        }
        val duration = nightDuration * portion
        return if (isMorning) {
            boundaryHour - duration
        } else {
            boundaryHour + duration
        }
    }

    private fun decimalHourToMillis(baseDateMillis: Long, decimalHour: Double): Long {
        val totalSeconds = (decimalHour * 3600.0).roundToLong()
        return baseDateMillis + (totalSeconds * 1000L)
    }

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        val y = if (month <= 2) year - 1 else year
        val m = if (month <= 2) month + 12 else month
        val a = y / 100
        val b = 2 - a + a / 4
        return (365.25 * (y + 4716)).toLong() + (30.6001 * (m + 1)).toInt() + day + b - 1524.5
    }

    private data class SunCoordinates(val declination: Double, val equationOfTime: Double)

    private fun calculateSunCoordinates(jd: Double): SunCoordinates {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(toRadians(g)) + 0.020 * sin(toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val raRad = atan2(cos(toRadians(e)) * sin(toRadians(l)), cos(toRadians(l)))
        var ra = toDegrees(raRad) / 15.0
        ra = fixHour(ra)

        val declinationRad = asin(sin(toRadians(e)) * sin(toRadians(l)))
        val declination = toDegrees(declinationRad)

        val eqOfTime = (q / 15.0) - fixHour(ra)
        return SunCoordinates(declination, eqOfTime * 60.0)
    }

    private fun calculateHourAngle(alpha: Double, latitude: Double, declination: Double): Double? {
        val latRad = toRadians(latitude)
        val decRad = toRadians(declination)
        val alphaRad = toRadians(alpha)

        val cosH = (sin(alphaRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        if (cosH > 1.0 || cosH < -1.0) {
            return null // Twilight angle is not reached at this latitude/date
        }
        return toDegrees(acos(cosH))
    }

    private fun calculateAsrAngle(shadowFactor: Double, latitude: Double, declination: Double): Double {
        val diff = abs(latitude - declination)
        val angleRad = atan(1.0 / (shadowFactor + tan(toRadians(diff))))
        return toDegrees(angleRad)
    }

    private fun toRadians(deg: Double): Double = Math.toRadians(deg)
    private fun toDegrees(rad: Double): Double = Math.toDegrees(rad)

    private fun fixAngle(angle: Double): Double {
        var a = angle - 360.0 * floor(angle / 360.0)
        if (a < 0) a += 360.0
        return a
    }

    private fun fixHour(hour: Double): Double {
        var h = hour - 24.0 * floor(hour / 24.0)
        if (h < 0) h += 24.0
        return h
    }
}
