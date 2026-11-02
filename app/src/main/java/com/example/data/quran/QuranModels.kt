package com.example.data.quran

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameTransliteration: String,
    val nameEnglish: String,
    val nameSomali: String,
    val revelationType: String, // "Meccan" or "Medinan"
    val numberOfAyahs: Int
)

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val transliteration: String,
    val translationEnglish: String,
    val translationSomali: String,
    val juzNumber: Int
)
