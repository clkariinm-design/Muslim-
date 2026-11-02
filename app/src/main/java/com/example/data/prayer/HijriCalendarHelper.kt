package com.example.data.prayer

import java.util.Calendar

object HijriCalendarHelper {

    private val islamicMonths = listOf(
        "Muharram",
        "Safar",
        "Rabi' al-Awwal",
        "Rabi' al-Thani",
        "Jumada al-Awwal",
        "Jumada al-Thani",
        "Rajab",
        "Sha'ban",
        "Ramadan",
        "Shawwal",
        "Dhu al-Qi'dah",
        "Dhu al-Hijjah"
    )

    private val islamicMonthsArabic = listOf(
        "محرم",
        "صفر",
        "ربيع الأول",
        "ربيع الثاني",
        "جمادى الأولى",
        "جمادى الثانية",
        "رجب",
        "شعبان",
        "رمضان",
        "شوال",
        "ذو القعدة",
        "ذو الحجة"
    )

    /**
     * Converts a Gregorian date (calendar) to approximate Hijri date.
     */
    fun getHijriDate(calendar: Calendar = Calendar.getInstance(), dayAdjustment: Int = 0): String {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian Day calculation
        val a = if (m <= 2) (y - 1) / 100 else y / 100
        val b = 2 - a + a / 4
        val adjustedYear = if (m <= 2) y - 1 else y
        val adjustedMonth = if (m <= 2) m + 12 else m

        val jd = (365.25 * (adjustedYear + 4716)).toLong() +
                (30.6001 * (adjustedMonth + 1)).toInt() +
                d + b - 1524.5 + dayAdjustment

        // Islamic calendar calculation from JD
        val l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val lPrime = l - 10631 * n + 354
        val j = (((10985 - lPrime) / 5316).toInt()) * ((50 * lPrime / 17719).toInt()) +
                ((lPrime / 5670).toInt()) * ((43 * lPrime / 15238).toInt())
        val lSecond = lPrime - (((30 - j) / 15).toInt()) * ((17719 * j / 50).toInt()) -
                ((j / 16).toInt()) * ((15238 * j / 43).toInt()) + 29
        val islamicMonthIndex = ((24 * lSecond / 709).toInt()).coerceIn(1, 12)
        val islamicDay = ((lSecond - (709 * islamicMonthIndex / 24)).toInt()).coerceIn(1, 30)
        val islamicYear = 30 * n + j - 30

        val monthName = islamicMonths.getOrElse(islamicMonthIndex - 1) { "Ramadan" }
        val monthArabic = islamicMonthsArabic.getOrElse(islamicMonthIndex - 1) { "رمضان" }

        return "$islamicDay $monthName $islamicYear AH ($monthArabic)"
    }
}
