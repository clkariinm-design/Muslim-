package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.azkar.AzkarScreen
import com.example.ui.azkar.AzkarViewModel
import com.example.ui.prayer.PrayerScreen
import com.example.ui.prayer.PrayerViewModel
import com.example.ui.qibla.QiblaScreen
import com.example.ui.qibla.QiblaViewModel
import com.example.ui.quran.QuranScreen
import com.example.ui.quran.QuranViewModel
import com.example.ui.tasbih.TasbihScreen
import com.example.ui.tasbih.TasbihViewModel
import com.example.ui.theme.AthanTheme
import com.example.ui.theme.IslamicGreenPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AthanTheme {
                val prayerViewModel: PrayerViewModel = viewModel()
                val qiblaViewModel: QiblaViewModel = viewModel()
                val quranViewModel: QuranViewModel = viewModel()
                val tasbihViewModel: TasbihViewModel = viewModel()
                val azkarViewModel: AzkarViewModel = viewModel()

                // Keep Qibla location synchronized with prayer location
                val prayerUiState by prayerViewModel.uiState.collectAsStateWithLifecycle()
                LaunchedEffect(prayerUiState.location) {
                    qiblaViewModel.updateLocation(prayerUiState.location)
                }

                var selectedTab by rememberSaveable { mutableIntStateOf(0) }

                BackHandler(enabled = selectedTab != 0) {
                    selectedTab = 0
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("main_navigation_bar"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 0) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                                        contentDescription = "Prayer Times"
                                    )
                                },
                                label = { Text("Prayers") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IslamicGreenPrimary,
                                    selectedTextColor = IslamicGreenPrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_prayers")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                                        contentDescription = "Holy Quran"
                                    )
                                },
                                label = { Text("Quran") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IslamicGreenPrimary,
                                    selectedTextColor = IslamicGreenPrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_quran")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 2) Icons.Filled.Explore else Icons.Outlined.Explore,
                                        contentDescription = "Qibla Compass"
                                    )
                                },
                                label = { Text("Qibla") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IslamicGreenPrimary,
                                    selectedTextColor = IslamicGreenPrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_qibla")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 3) Icons.Filled.Fingerprint else Icons.Outlined.Fingerprint,
                                        contentDescription = "Digital Tasbih"
                                    )
                                },
                                label = { Text("Tasbih") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IslamicGreenPrimary,
                                    selectedTextColor = IslamicGreenPrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_tasbih")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 4,
                                onClick = { selectedTab = 4 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 4) Icons.Filled.Spa else Icons.Outlined.Spa,
                                        contentDescription = "Daily Azkar"
                                    )
                                },
                                label = { Text("Azkar") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IslamicGreenPrimary,
                                    selectedTextColor = IslamicGreenPrimary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_azkar")
                            )
                        }
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        0 -> PrayerScreen(
                            viewModel = prayerViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        1 -> QuranScreen(
                            viewModel = quranViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        2 -> QiblaScreen(
                            viewModel = qiblaViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        3 -> TasbihScreen(
                            viewModel = tasbihViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        4 -> AzkarScreen(
                            viewModel = azkarViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
