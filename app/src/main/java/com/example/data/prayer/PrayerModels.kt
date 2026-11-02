package com.example.data.prayer

enum class CalculationMethod(
    val title: String,
    val description: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaFixedMinutes: Int = 0,
    val ramadanIshaMinutes: Int = 0
) {
    ISNA(
        title = "Islamic Society of North America (ISNA)",
        description = "Fajr: 15°, Isha: 15° — Standard across North America (USA & Canada)",
        fajrAngle = 15.0,
        ishaAngle = 15.0
    ),
    UMM_AL_QURA(
        title = "Umm Al-Qura (Makkah)",
        description = "Fajr: 18.5°, Isha: 90 min after Maghrib (120 min in Ramadan) — Saudi Arabia & Gulf",
        fajrAngle = 18.5,
        ishaAngle = 0.0,
        ishaFixedMinutes = 90,
        ramadanIshaMinutes = 120
    ),
    MUSLIM_WORLD_LEAGUE(
        title = "Muslim World League (MWL)",
        description = "Fajr: 18°, Isha: 17° — Europe, Far East, and parts of the Americas",
        fajrAngle = 18.0,
        ishaAngle = 17.0
    ),
    EGYPTIAN(
        title = "Egyptian General Authority of Survey",
        description = "Fajr: 19.5°, Isha: 17.5° — Africa, Middle East, and parts of Asia",
        fajrAngle = 19.5,
        ishaAngle = 17.5
    ),
    KARACHI(
        title = "Univ. of Islamic Sciences, Karachi",
        description = "Fajr: 18°, Isha: 18° — Pakistan, Bangladesh, India, Afghanistan",
        fajrAngle = 18.0,
        ishaAngle = 18.0
    ),
    DUBAI(
        title = "Dubai / UAE General Authority",
        description = "Fajr: 18.2°, Isha: 90 min after Maghrib — United Arab Emirates",
        fajrAngle = 18.2,
        ishaAngle = 0.0,
        ishaFixedMinutes = 90
    ),
    KUWAIT(
        title = "Kuwait Ministry of Awqaf",
        description = "Fajr: 18°, Isha: 17.5° — Kuwait",
        fajrAngle = 18.0,
        ishaAngle = 17.5
    ),
    QATAR(
        title = "Qatar Ministry of Endowments",
        description = "Fajr: 18°, Isha: 90 min after Maghrib — Qatar",
        fajrAngle = 18.0,
        ishaAngle = 0.0,
        ishaFixedMinutes = 90
    ),
    MOONSIGHTING_COMMITTEE(
        title = "Moonsighting Committee Worldwide",
        description = "Fajr: 18°, Isha: 18° — Verified observational criteria",
        fajrAngle = 18.0,
        ishaAngle = 18.0
    )
}

enum class AsrMethod(val title: String, val description: String, val shadowFactor: Double) {
    STANDARD(
        title = "Standard (Shafi'i, Maliki, Hanbali)",
        description = "Shadow of object equals its length plus noon shadow (1x)",
        shadowFactor = 1.0
    ),
    HANAFI(
        title = "Hanafi",
        description = "Shadow of object equals twice its length plus noon shadow (2x)",
        shadowFactor = 2.0
    )
}

enum class HighLatitudeRule(val title: String, val description: String) {
    MIDDLE_OF_NIGHT("Middle of the Night", "Fajr/Isha twilight capped at half of night length"),
    ONE_SEVENTH("One-Seventh Rule", "Fajr/Isha twilight capped at 1/7 of night length"),
    ANGLE_BASED("Angle-Based Rule", "Fajr/Isha portion based on angle / 60")
}

enum class PrayerType(
    val id: String,
    val englishName: String,
    val arabicName: String,
    val somaliName: String,
    val isFard: Boolean
) {
    FAJR("fajr", "Fajr", "الفجر", "Subax", true),
    SUNRISE("sunrise", "Sunrise", "الشروق", "Shuruuq", false),
    DHUHR("dhuhr", "Dhuhr", "الظهر", "Duhur", true),
    ASR("asr", "Asr", "العصر", "Casar", true),
    MAGHRIB("maghrib", "Maghrib", "المغرب", "Maqrib", true),
    ISHA("isha", "Isha", "العشاء", "Cishe", true)
}

data class PrayerTimeItem(
    val type: PrayerType,
    val timeMillis: Long,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false,
    val notificationEnabled: Boolean = true
)

data class PrayerSchedule(
    val prayerTimes: List<PrayerTimeItem>,
    val nextPrayer: PrayerTimeItem?,
    val previousPrayer: PrayerTimeItem?,
    val remainingMillisToNext: Long,
    val elapsedPercentOfCurrentInterval: Float,
    val dateDisplay: String,
    val hijriDateDisplay: String,
    val calculationMethod: CalculationMethod,
    val asrMethod: AsrMethod
)
