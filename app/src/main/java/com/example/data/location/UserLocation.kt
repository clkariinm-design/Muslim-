package com.example.data.location

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val countryName: String,
    val timezoneId: String = java.util.TimeZone.getDefault().id,
    val isGpsDetected: Boolean = false
) {
    val displayTitle: String
        get() = if (countryName.isNotBlank() && countryName != cityName) {
            "$cityName, $countryName"
        } else {
            cityName
        }
}

data class PresetCity(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String
)

object PresetCities {
    val defaultCity = PresetCity("Mecca", "Saudi Arabia", 21.4225, 39.8262, "Asia/Riyadh")

    val popularCities = listOf(
        PresetCity("Mogadishu", "Somalia", 2.0469, 45.3182, "Africa/Mogadishu"),
        PresetCity("Hargeisa", "Somalia", 9.5600, 44.0650, "Africa/Mogadishu"),
        PresetCity("Mecca", "Saudi Arabia", 21.4225, 39.8262, "Asia/Riyadh"),
        PresetCity("Medina", "Saudi Arabia", 24.5247, 39.5692, "Asia/Riyadh"),
        PresetCity("Cairo", "Egypt", 30.0444, 31.2357, "Africa/Cairo"),
        PresetCity("Dubai", "United Arab Emirates", 25.2048, 55.2708, "Asia/Dubai"),
        PresetCity("Nairobi", "Kenya", -1.2921, 36.8219, "Africa/Nairobi"),
        PresetCity("Istanbul", "Turkey", 41.0082, 28.9784, "Europe/Istanbul"),
        PresetCity("London", "United Kingdom", 51.5074, -0.1278, "Europe/London"),
        PresetCity("New York", "United States", 40.7128, -74.0060, "America/New_York"),
        PresetCity("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
        PresetCity("Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
        PresetCity("Toronto", "Canada", 43.6532, -79.3832, "America/Toronto")
    )
}
