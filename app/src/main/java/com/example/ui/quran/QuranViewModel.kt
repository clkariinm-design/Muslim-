package com.example.ui.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.quran.Ayah
import com.example.data.quran.QuranRepository
import com.example.data.quran.Surah
import com.example.data.quran.db.QuranBookmarkEntity
import com.example.data.quran.db.QuranDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuranUiState(
    val surahs: List<Surah> = emptyList(),
    val filteredSurahs: List<Surah> = emptyList(),
    val selectedSurah: Surah? = null,
    val currentAyahs: List<Ayah> = emptyList(),
    val bookmarks: List<QuranBookmarkEntity> = emptyList(),
    val bookmarkedAyahKeys: Set<Pair<Int, Int>> = emptySet(),
    val searchQuery: String = "",
    val activeTab: Int = 0, // 0: Surahs, 1: Bookmarks
    val arabicFontSize: Float = 24f,
    val showTransliteration: Boolean = true,
    val showSomaliTranslation: Boolean = true,
    val showEnglishTranslation: Boolean = true,
    val initialAyahToScroll: Int? = null
)

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val database = QuranDatabase.getDatabase(application)
    private val repository = QuranRepository(database.quranBookmarkDao())

    private val _uiState = MutableStateFlow(
        QuranUiState(
            surahs = repository.getAllSurahs(),
            filteredSurahs = repository.getAllSurahs()
        )
    )
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        // Collect reactive bookmarks from Room Database
        viewModelScope.launch {
            repository.allBookmarks.collect { bookmarkList ->
                val keys = bookmarkList.map { Pair(it.surahNumber, it.ayahNumber) }.toSet()
                _uiState.update { current ->
                    current.copy(
                        bookmarks = bookmarkList,
                        bookmarkedAyahKeys = keys
                    )
                }
            }
        }
    }

    fun openSurah(surah: Surah, targetAyahNumber: Int? = null) {
        val ayahs = repository.getAyahs(surah.number)
        _uiState.update {
            it.copy(
                selectedSurah = surah,
                currentAyahs = ayahs,
                initialAyahToScroll = targetAyahNumber
            )
        }
    }

    fun closeSurahReader() {
        _uiState.update {
            it.copy(
                selectedSurah = null,
                currentAyahs = emptyList(),
                initialAyahToScroll = null
            )
        }
    }

    fun toggleBookmark(ayah: Ayah, surah: Surah) {
        viewModelScope.launch {
            repository.toggleBookmark(ayah, surah)
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            repository.deleteBookmarkById(id)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { current ->
            val filtered = if (query.isBlank()) {
                current.surahs
            } else {
                current.surahs.filter { surah ->
                    surah.nameTransliteration.contains(query, ignoreCase = true) ||
                            surah.nameEnglish.contains(query, ignoreCase = true) ||
                            surah.nameSomali.contains(query, ignoreCase = true) ||
                            surah.nameArabic.contains(query) ||
                            surah.number.toString() == query.trim()
                }
            }
            current.copy(searchQuery = query, filteredSurahs = filtered)
        }
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun setArabicFontSize(sizeSp: Float) {
        _uiState.update { it.copy(arabicFontSize = sizeSp.coerceIn(18f, 38f)) }
    }

    fun toggleTransliteration() {
        _uiState.update { it.copy(showTransliteration = !it.showTransliteration) }
    }

    fun toggleSomaliTranslation() {
        _uiState.update { it.copy(showSomaliTranslation = !it.showSomaliTranslation) }
    }

    fun toggleEnglishTranslation() {
        _uiState.update { it.copy(showEnglishTranslation = !it.showEnglishTranslation) }
    }
}
