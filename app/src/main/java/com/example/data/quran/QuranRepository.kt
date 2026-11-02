package com.example.data.quran

import com.example.data.quran.db.QuranBookmarkDao
import com.example.data.quran.db.QuranBookmarkEntity
import kotlinx.coroutines.flow.Flow

class QuranRepository(private val bookmarkDao: QuranBookmarkDao) {

    val allBookmarks: Flow<List<QuranBookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarkedFlow(surahNumber: Int, ayahNumber: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarkedFlow(surahNumber, ayahNumber)
    }

    suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return bookmarkDao.isBookmarked(surahNumber, ayahNumber)
    }

    suspend fun toggleBookmark(ayah: Ayah, surah: Surah, note: String = "") {
        val exists = bookmarkDao.isBookmarked(ayah.surahNumber, ayah.ayahNumber)
        if (exists) {
            bookmarkDao.deleteBookmark(ayah.surahNumber, ayah.ayahNumber)
        } else {
            val entity = QuranBookmarkEntity(
                surahNumber = surah.number,
                surahName = surah.nameTransliteration,
                surahArabic = surah.nameArabic,
                ayahNumber = ayah.ayahNumber,
                ayahArabicPreview = ayah.arabicText.take(60),
                ayahTranslationPreview = ayah.translationEnglish.take(80),
                note = note
            )
            bookmarkDao.insertBookmark(entity)
        }
    }

    suspend fun deleteBookmarkById(id: Long) {
        bookmarkDao.deleteBookmarkById(id)
    }

    fun getAllSurahs(): List<Surah> {
        return QuranDataSource.allSurahs
    }

    fun getAyahs(surahNumber: Int): List<Ayah> {
        return QuranDataSource.getAyahsForSurah(surahNumber)
    }
}
