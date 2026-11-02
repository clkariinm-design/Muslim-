package com.example.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.quran.Ayah
import com.example.data.quran.Surah
import com.example.data.quran.db.QuranBookmarkEntity
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuranScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.selectedSurah != null) {
        // Verse-by-verse Surah reader
        SurahReaderView(
            surah = uiState.selectedSurah!!,
            ayahs = uiState.currentAyahs,
            bookmarkedAyahs = uiState.bookmarkedAyahKeys,
            arabicFontSize = uiState.arabicFontSize,
            showTransliteration = uiState.showTransliteration,
            showSomali = uiState.showSomaliTranslation,
            showEnglish = uiState.showEnglishTranslation,
            initialScrollAyah = uiState.initialAyahToScroll,
            onBackClick = { viewModel.closeSurahReader() },
            onToggleBookmark = { ayah -> viewModel.toggleBookmark(ayah, uiState.selectedSurah!!) },
            onIncreaseFontSize = { viewModel.setArabicFontSize(uiState.arabicFontSize + 2f) },
            onDecreaseFontSize = { viewModel.setArabicFontSize(uiState.arabicFontSize - 2f) },
            onToggleTransliteration = { viewModel.toggleTransliteration() },
            onToggleSomali = { viewModel.toggleSomaliTranslation() },
            onToggleEnglish = { viewModel.toggleEnglishTranslation() },
            modifier = modifier
        )
    } else {
        // Surah list and bookmarks directory
        SurahListView(
            uiState = uiState,
            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
            onSelectTab = { viewModel.setActiveTab(it) },
            onOpenSurah = { surah -> viewModel.openSurah(surah) },
            onOpenBookmark = { bookmark ->
                val surah = uiState.surahs.find { it.number == bookmark.surahNumber }
                    ?: Surah(bookmark.surahNumber, bookmark.surahArabic, bookmark.surahName, "", "", "Meccan", 30)
                viewModel.openSurah(surah, bookmark.ayahNumber)
            },
            onDeleteBookmark = { bookmarkId -> viewModel.deleteBookmark(bookmarkId) },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahListView(
    uiState: QuranUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectTab: (Int) -> Unit,
    onOpenSurah: (Surah) -> Unit,
    onOpenBookmark: (QuranBookmarkEntity) -> Unit,
    onDeleteBookmark: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IslamicGreenPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "📖", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Holy Quran",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Quraanka Kariimka",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quran_search_field"),
                placeholder = { Text("Search Surah by name, number, or Somali…") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Tabs: Surahs vs Bookmarks
            TabRow(
                selectedTabIndex = uiState.activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = uiState.activeTab == 0,
                    onClick = { onSelectTab(0) },
                    text = { Text("Surahs (${uiState.filteredSurahs.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_surahs")
                )
                Tab(
                    selected = uiState.activeTab == 1,
                    onClick = { onSelectTab(1) },
                    text = { Text("Bookmarks (${uiState.bookmarks.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_bookmarks")
                )
            }

            // Quick Last Read Banner (if bookmarks exist)
            if (uiState.activeTab == 0 && uiState.bookmarks.isNotEmpty() && uiState.searchQuery.isBlank()) {
                val lastBookmark = uiState.bookmarks.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenBookmark(lastBookmark) }
                        .testTag("last_read_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = IslamicGreenDark
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = IslamicGold,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "CONTINUE READING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IslamicGoldLight,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${lastBookmark.surahName} • Ayah ${lastBookmark.ayahNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Text(
                            text = lastBookmark.surahArabic,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight
                        )
                    }
                }
            }

            // Content List based on Tab
            if (uiState.activeTab == 0) {
                // Surah Directory List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("surah_list"),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredSurahs, key = { it.number }) { surah ->
                        SurahItemCard(surah = surah, onClick = { onOpenSurah(surah) })
                    }
                }
            } else {
                // Room Bookmarks List
                if (uiState.bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "🔖", fontSize = 48.sp)
                            Text(
                                text = "No Bookmarks Saved Yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap the bookmark ribbon icon beside any Ayah in the Quran reader to save it here in your Room Database.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("bookmarks_list"),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.bookmarks, key = { it.id }) { bookmark ->
                            BookmarkItemCard(
                                bookmark = bookmark,
                                onClick = { onOpenBookmark(bookmark) },
                                onDelete = { onDeleteBookmark(bookmark.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahItemCard(surah: Surah, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("surah_item_${surah.number}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Surah Number Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = surah.number.toString(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = surah.nameTransliteration,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (surah.revelationType == "Meccan")
                                IslamicGold.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (surah.revelationType == "Meccan") "🕋 Makki" else "🕌 Madani",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (surah.revelationType == "Meccan") IslamicGold else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${surah.nameEnglish} • ${surah.nameSomali} • ${surah.numberOfAyahs} Ayahs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = surah.nameArabic,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGreenPrimary
            )
        }
    }
}

@Composable
fun BookmarkItemCard(
    bookmark: QuranBookmarkEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(bookmark.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("bookmark_item_${bookmark.surahNumber}_${bookmark.ayahNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${bookmark.surahName} (${bookmark.surahArabic}) • Ayah ${bookmark.ayahNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Bookmark",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (bookmark.ayahArabicPreview.isNotBlank()) {
                Text(
                    text = bookmark.ayahArabicPreview,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreenPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (bookmark.ayahTranslationPreview.isNotBlank()) {
                Text(
                    text = bookmark.ayahTranslationPreview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "Saved $dateString",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderView(
    surah: Surah,
    ayahs: List<Ayah>,
    bookmarkedAyahs: Set<Pair<Int, Int>>,
    arabicFontSize: Float,
    showTransliteration: Boolean,
    showSomali: Boolean,
    showEnglish: Boolean,
    initialScrollAyah: Int?,
    onBackClick: () -> Unit,
    onToggleBookmark: (Ayah) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleTransliteration: () -> Unit,
    onToggleSomali: () -> Unit,
    onToggleEnglish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler {
        onBackClick()
    }

    // Scroll to specific bookmark if navigated from bookmark list
    LaunchedEffect(initialScrollAyah) {
        if (initialScrollAyah != null && initialScrollAyah > 1) {
            val targetIndex = (initialScrollAyah - 1).coerceIn(0, (ayahs.size - 1).coerceAtLeast(0))
            // +1 offset due to Surah Header at index 0
            listState.animateScrollToItem(targetIndex + 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${surah.number}. ${surah.nameTransliteration}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${surah.nameArabic} • ${surah.numberOfAyahs} Ayahs",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Surahs"
                        )
                    }
                },
                actions = {
                    // Font size adjustments
                    IconButton(onClick = onDecreaseFontSize) {
                        Text(text = "A-", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onIncreaseFontSize) {
                        Text(text = "A+", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Surah Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = IslamicGreenDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = surah.nameArabic,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight
                        )
                        Text(
                            text = "${surah.nameEnglish} • ${surah.nameSomali}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "${surah.revelationType} • ${surah.numberOfAyahs} Verses",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        // Bismillah banner (for all surahs except Surah At-Tawbah 9)
                        if (surah.number != 9) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGold
                            )
                        }
                    }
                }
            }

            // Reader Display Toggles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = showSomali,
                        onClick = onToggleSomali,
                        label = { Text("Somali", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    FilterChip(
                        selected = showEnglish,
                        onClick = onToggleEnglish,
                        label = { Text("English", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    FilterChip(
                        selected = showTransliteration,
                        onClick = onToggleTransliteration,
                        label = { Text("Transliteration", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            // Verses List
            items(ayahs, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                val isBookmarked = bookmarkedAyahs.contains(Pair(ayah.surahNumber, ayah.ayahNumber))
                VerseCard(
                    ayah = ayah,
                    arabicFontSize = arabicFontSize,
                    showTransliteration = showTransliteration,
                    showSomali = showSomali,
                    showEnglish = showEnglish,
                    isBookmarked = isBookmarked,
                    onToggleBookmark = { onToggleBookmark(ayah) },
                    onCopyAyah = {
                        val textToCopy = "${ayah.arabicText}\n\n${ayah.translationSomali}\n\n[Surah ${surah.nameTransliteration} ${ayah.surahNumber}:${ayah.ayahNumber}]"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Ayah", textToCopy))
                        Toast.makeText(context, "Ayah copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun VerseCard(
    ayah: Ayah,
    arabicFontSize: Float,
    showTransliteration: Boolean,
    showSomali: Boolean,
    showEnglish: Boolean,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onCopyAyah: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_${ayah.surahNumber}_${ayah.ayahNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBookmarked)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Verse Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verse Number Badge
                Surface(
                    shape = CircleShape,
                    color = if (isBookmarked) IslamicGold else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${ayah.ayahNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isBookmarked) Color.Black else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onCopyAyah,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Ayah",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_button_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Ayah",
                            tint = if (isBookmarked) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Arabic Quranic Text (RTL)
            Text(
                text = ayah.arabicText,
                fontSize = arabicFontSize.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                lineHeight = (arabicFontSize * 1.7f).sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Transliteration (if enabled)
            if (showTransliteration && ayah.transliteration.isNotBlank()) {
                Text(
                    text = ayah.transliteration,
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicGold,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Somali Translation (if enabled)
            if (showSomali && ayah.translationSomali.isNotBlank()) {
                Text(
                    text = ayah.translationSomali,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // English Translation (if enabled)
            if (showEnglish && ayah.translationEnglish.isNotBlank()) {
                Text(
                    text = ayah.translationEnglish,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
