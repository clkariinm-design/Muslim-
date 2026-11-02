package com.example.data.quran.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quran_bookmarks",
    indices = [Index(value = ["surahNumber", "ayahNumber"], unique = true)]
)
data class QuranBookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val surahNumber: Int,
    val surahName: String,
    val surahArabic: String,
    val ayahNumber: Int,
    val ayahArabicPreview: String,
    val ayahTranslationPreview: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
