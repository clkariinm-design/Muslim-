package com.example

import com.example.data.quran.QuranDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranDataSourceTest {

    @Test
    fun quranDataSource_containsAll114Surahs() {
        val surahs = QuranDataSource.allSurahs
        assertEquals(114, surahs.size)

        // First surah is Al-Fatihah
        val fatihah = surahs[0]
        assertEquals(1, fatihah.number)
        assertEquals("Al-Fatihah", fatihah.nameTransliteration)
        assertEquals("الفاتحة", fatihah.nameArabic)
        assertEquals(7, fatihah.numberOfAyahs)

        // Last surah is An-Nas
        val nas = surahs[113]
        assertEquals(114, nas.number)
        assertEquals("An-Nas", nas.nameTransliteration)
        assertEquals("الناس", nas.nameArabic)
        assertEquals(6, nas.numberOfAyahs)
    }

    @Test
    fun quranDataSource_retrievesAuthenticAyahsForAlFatihah() {
        val ayahs = QuranDataSource.getAyahsForSurah(1)
        assertEquals(7, ayahs.size)

        // Bismillah is Ayah 1 of Al-Fatihah
        assertTrue(ayahs[0].arabicText.contains("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"))
        assertTrue(ayahs[0].translationEnglish.isNotBlank())
        assertTrue(ayahs[0].translationSomali.isNotBlank())

        // Last Ayah of Al-Fatihah
        assertEquals(7, ayahs[6].ayahNumber)
        assertTrue(ayahs[6].arabicText.contains("صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ"))
    }

    @Test
    fun quranDataSource_retrievesAyahsForAlIkhlas() {
        val ayahs = QuranDataSource.getAyahsForSurah(112)
        assertEquals(4, ayahs.size)
        assertTrue(ayahs[0].arabicText.contains("قُلْ هُوَ اللَّهُ أَحَدٌ"))
        assertTrue(ayahs[3].arabicText.contains("وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ"))
    }
}
