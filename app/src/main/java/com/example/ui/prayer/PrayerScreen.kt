package com.example.ui.prayer

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import com.example.data.prayer.adhan.AdhanMelody
import android.os.Build
import androidx.compose.material.icons.outlined.Brightness5
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Switch
import com.example.data.location.PresetCity
import com.example.data.prayer.AsrMethod
import com.example.data.prayer.CalculationMethod
import com.example.data.prayer.HighLatitudeRule
import com.example.data.prayer.PrayerSchedule
import com.example.data.prayer.PrayerTimeItem
import com.example.data.prayer.PrayerType
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenLight
import com.example.ui.theme.IslamicGreenPrimary
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    viewModel: PrayerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Permission launcher for Location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val granted = fineGranted || coarseGranted
        viewModel.onLocationPermissionResult(granted)
    }

    // Function to check or request permissions
    val checkAndRequestLocation = {
        val fineCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineCheck || coarseCheck) {
            viewModel.detectCurrentLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Audible Adhan notifications enabled")
            }
        }
    }

    val requestNotificationPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Show error snackbar if any
    LaunchedEffect(uiState.locationErrorMessage) {
        uiState.locationErrorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IslamicGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "☪",
                                    fontSize = 20.sp,
                                    color = IslamicGold
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Athan",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Prayer Times & Qibla",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Settings for calculation methods
                    IconButton(
                        onClick = { viewModel.openMethodPicker(true) },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Calculation Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Location Bar Card
            item {
                LocationBarCard(
                    cityName = uiState.location.displayTitle,
                    isGps = uiState.location.isGpsDetected,
                    latitude = uiState.location.latitude,
                    longitude = uiState.location.longitude,
                    isLoading = uiState.isLoadingLocation,
                    onDetectClick = { checkAndRequestLocation() },
                    onChangeCityClick = { viewModel.openCityPicker(true) }
                )
            }

            // Next Prayer Hero Card
            item {
                uiState.schedule?.let { schedule ->
                    NextPrayerHeroCard(
                        schedule = schedule,
                        countdownText = uiState.countdownFormatted,
                        latitude = uiState.location.latitude,
                        longitude = uiState.location.longitude
                    )
                }
            }

            // Date & Hijri Calendar Card
            item {
                uiState.schedule?.let { schedule ->
                    DateCard(
                        gregorianDate = schedule.dateDisplay,
                        hijriDate = schedule.hijriDateDisplay
                    )
                }
            }

            // Audible Adhan Alerts & Test Card
            item {
                AdhanAudibleAlertCard(
                    isPlaying = uiState.isAdhanPlaying,
                    selectedMelody = uiState.selectedMelody,
                    onSelectMelody = { viewModel.setAdhanMelody(it) },
                    onToggleTest = {
                        requestNotificationPermission()
                        viewModel.toggleAudibleAdhanTest()
                    }
                )
            }

            // Quick Calculation Method Switcher (e.g. ISNA, Umm al-Qura)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Calculation Convention",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "More Settings",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { viewModel.openMethodPicker(true) }
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(listOf(
                            CalculationMethod.ISNA,
                            CalculationMethod.UMM_AL_QURA,
                            CalculationMethod.MUSLIM_WORLD_LEAGUE,
                            CalculationMethod.EGYPTIAN,
                            CalculationMethod.KARACHI
                        )) { method ->
                            val isSelected = method == uiState.calculationMethod
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setCalculationMethod(method) },
                                label = {
                                    val shortLabel = when (method) {
                                        CalculationMethod.ISNA -> "ISNA (15°/15°)"
                                        CalculationMethod.UMM_AL_QURA -> "Umm Al-Qura"
                                        CalculationMethod.MUSLIM_WORLD_LEAGUE -> "MWL (18°/17°)"
                                        CalculationMethod.EGYPTIAN -> "Egyptian (19.5°)"
                                        CalculationMethod.KARACHI -> "Karachi (18°)"
                                        else -> method.name
                                    }
                                    Text(
                                        text = shortLabel,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    // Calculation Compliance & Parameters Badge
                    CalculationComplianceCard(
                        method = uiState.calculationMethod,
                        asrMethod = uiState.asrMethod,
                        latitude = uiState.location.latitude,
                        longitude = uiState.location.longitude,
                        onOpenSettings = { viewModel.openMethodPicker(true) }
                    )
                }
            }

            // Header for Daily Prayer Times
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Prayer Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.calculationMethod.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { viewModel.openMethodPicker(true) }
                    )
                }
            }

            // Prayer items list
            uiState.schedule?.let { schedule ->
                items(schedule.prayerTimes) { item ->
                    val isNotificationOn = uiState.notifications[item.type] ?: true
                    PrayerTimeCard(
                        item = item,
                        isNotificationOn = isNotificationOn,
                        onToggleNotification = { viewModel.toggleNotification(item.type) }
                    )
                }
            }
        }
    }

    // City Picker Bottom Sheet
    if (uiState.showCityPicker) {
        CityPickerBottomSheet(
            searchQuery = uiState.citySearchQuery,
            cities = uiState.filteredCities,
            onSearchChange = { viewModel.updateCitySearch(it) },
            onSelectCity = { viewModel.selectPresetCity(it) },
            onDetectGps = {
                viewModel.openCityPicker(false)
                checkAndRequestLocation()
            },
            onDismiss = { viewModel.openCityPicker(false) }
        )
    }

    // Method Picker Bottom Sheet
    if (uiState.showMethodPicker) {
        MethodPickerBottomSheet(
            currentMethod = uiState.calculationMethod,
            currentAsr = uiState.asrMethod,
            currentRule = uiState.highLatitudeRule,
            isRamadan = uiState.isRamadan,
            onSelectMethod = { viewModel.setCalculationMethod(it) },
            onSelectAsr = { viewModel.setAsrMethod(it) },
            onSelectRule = { viewModel.setHighLatitudeRule(it) },
            onToggleRamadan = { viewModel.toggleRamadan(it) },
            onDismiss = { viewModel.openMethodPicker(false) }
        )
    }
}

@Composable
fun LocationBarCard(
    cityName: String,
    isGps: Boolean,
    latitude: Double,
    longitude: Double,
    isLoading: Boolean,
    onDetectClick: () -> Unit,
    onChangeCityClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("location_bar_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                modifier = Modifier
                    .weight(1f)
                    .clickable { onChangeCityClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cityName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isGps) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "GPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    val latDir = if (latitude >= 0) "N" else "S"
                    val lonDir = if (longitude >= 0) "E" else "W"
                    Text(
                        text = String.format(Locale.US, "%.2f°%s, %.2f°%s • Tap to change", Math.abs(latitude), latDir, Math.abs(longitude), lonDir),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // GPS Refresh Button
            IconButton(
                onClick = onDetectClick,
                enabled = !isLoading,
                modifier = Modifier.testTag("detect_gps_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Detect Location with GPS",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun NextPrayerHeroCard(
    schedule: PrayerSchedule,
    countdownText: String,
    latitude: Double,
    longitude: Double
) {
    val next = schedule.nextPrayer ?: return

    val gradient = Brush.linearGradient(
        colors = listOf(
            IslamicGreenDark,
            IslamicGreenPrimary,
            Color(0xFF135A36)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("next_prayer_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "NEXT PRAYER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGoldLight,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text = next.type.arabicName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "${next.type.englishName} (${next.type.somaliName})",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = next.formattedTime,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = countdownText,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { schedule.elapsedPercentOfCurrentInterval },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = IslamicGold,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun DateCard(gregorianDate: String, hijriDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gregorianDate,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = hijriDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = IslamicGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🕌",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CalculationComplianceCard(
    method: CalculationMethod,
    asrMethod: AsrMethod,
    latitude: Double,
    longitude: Double,
    onOpenSettings: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSettings() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = method.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "⚙ Settings",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val paramsText = when (method) {
                CalculationMethod.UMM_AL_QURA -> "Fajr: 18.5° • Isha: +90 min after Maghrib (Umm al-Qura)"
                CalculationMethod.ISNA -> "Fajr: 15.0° • Isha: 15.0° (ISNA Standard)"
                CalculationMethod.MUSLIM_WORLD_LEAGUE -> "Fajr: 18.0° • Isha: 17.0° (MWL Standard)"
                CalculationMethod.EGYPTIAN -> "Fajr: 19.5° • Isha: 17.5° (Egyptian Authority)"
                CalculationMethod.KARACHI -> "Fajr: 18.0° • Isha: 18.0° (Karachi)"
                CalculationMethod.DUBAI -> "Fajr: 18.2° • Isha: +90 min after Maghrib (Dubai)"
                CalculationMethod.KUWAIT -> "Fajr: 18.0° • Isha: 17.5° (Kuwait)"
                CalculationMethod.QATAR -> "Fajr: 18.0° • Isha: +90 min after Maghrib (Qatar)"
                CalculationMethod.MOONSIGHTING_COMMITTEE -> "Fajr: 18.0° • Isha: 18.0° (Moonsighting)"
            }

            Text(
                text = paramsText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val latDir = if (latitude >= 0) "N" else "S"
            val lonDir = if (longitude >= 0) "E" else "W"
            Text(
                text = "Asr: ${asrMethod.title} • Coordinates: ${String.format(Locale.US, "%.2f°%s, %.2f°%s", Math.abs(latitude), latDir, Math.abs(longitude), lonDir)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun PrayerTimeCard(
    item: PrayerTimeItem,
    isNotificationOn: Boolean,
    onToggleNotification: () -> Unit
) {
    val isNext = item.isNext
    val isPassed = item.isPassed

    val cardBorder = if (isNext) {
        BorderStroke(2.dp, IslamicGreenPrimary)
    } else null

    val containerColor = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        isPassed -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prayer_card_${item.type.id}")
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNext) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isNext) IslamicGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getPrayerIcon(item.type),
                            contentDescription = item.type.englishName,
                            tint = if (isNext) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.type.englishName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isPassed && !isNext) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                        )
                        if (isNext) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = IslamicGreenPrimary
                            ) {
                                Text(
                                    text = "NEXT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${item.type.somaliName} • ${item.type.arabicName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = item.formattedTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isNext) IslamicGreenPrimary else MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onToggleNotification,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("notification_toggle_${item.type.id}")
                ) {
                    Icon(
                        imageVector = if (isNotificationOn) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        contentDescription = "Toggle alert for ${item.type.englishName}",
                        tint = if (isNotificationOn) IslamicGold else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)

private fun getPrayerIcon(type: PrayerType): ImageVector {
    return when (type) {
        PrayerType.FAJR -> Icons.Outlined.WbTwilight
        PrayerType.SUNRISE -> Icons.Outlined.WbSunny
        PrayerType.DHUHR -> Icons.Outlined.Brightness5
        PrayerType.ASR -> Icons.Outlined.WbSunny
        PrayerType.MAGHRIB -> Icons.Outlined.WbTwilight
        PrayerType.ISHA -> Icons.Outlined.NightsStay
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerBottomSheet(
    searchQuery: String,
    cities: List<PresetCity>,
    onSearchChange: (String) -> Unit,
    onSelectCity: (PresetCity) -> Unit,
    onDetectGps: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Detect GPS Button
            Button(
                onClick = onDetectGps,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sheet_detect_gps_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Detect My Location Automatically (GPS)")
            }

            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("city_search_input"),
                placeholder = { Text("Search city or country…") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Cities List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cities) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCity(city) }
                            .testTag("city_item_${city.name.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = city.country,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = String.format(Locale.US, "%.1f°, %.1f°", city.latitude, city.longitude),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodPickerBottomSheet(
    currentMethod: CalculationMethod,
    currentAsr: AsrMethod,
    currentRule: HighLatitudeRule,
    isRamadan: Boolean,
    onSelectMethod: (CalculationMethod) -> Unit,
    onSelectAsr: (AsrMethod) -> Unit,
    onSelectRule: (HighLatitudeRule) -> Unit,
    onToggleRamadan: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calculation Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            item {
                Text(
                    text = "Calculation Method (Convention)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(CalculationMethod.entries) { method ->
                val selected = method == currentMethod
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectMethod(method) }
                        .testTag("method_${method.name.lowercase()}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = { onSelectMethod(method) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = method.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                            )
                            Text(
                                text = method.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (currentMethod == CalculationMethod.UMM_AL_QURA) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ramadan Isha Extension",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Umm al-Qura sets Isha 120 min after Maghrib during Ramadan (instead of 90 min).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isRamadan,
                                onCheckedChange = onToggleRamadan
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Asr Juristic Method (Madhab)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(AsrMethod.entries) { asr ->
                val selected = asr == currentAsr
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectAsr(asr) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = { onSelectAsr(asr) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = asr.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                            )
                            Text(
                                text = asr.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "High Latitude Rule",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(HighLatitudeRule.entries) { rule ->
                val selected = rule == currentRule
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectRule(rule) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = { onSelectRule(rule) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = rule.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                            )
                            Text(
                                text = rule.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdhanAudibleAlertCard(
    isPlaying: Boolean,
    selectedMelody: AdhanMelody,
    onSelectMelody: (AdhanMelody) -> Unit,
    onToggleTest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("adhan_audible_alert_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isPlaying) BorderStroke(1.5.dp, IslamicGold) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isPlaying) IslamicGold else IslamicGreenPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Default.NotificationsActive,
                                contentDescription = "Adhan Alerts",
                                tint = if (isPlaying) Color.Black else IslamicGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Audible Adhan Alerts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isPlaying) "Playing Adhan alert now…" else "Scheduled at exact prayer times",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isPlaying) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isPlaying) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IslamicGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "PLAYING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Melody selection chips
            Text(
                text = "Adhan Melody Style:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AdhanMelody.entries) { melody ->
                    val isSelected = melody == selectedMelody
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectMelody(melody) },
                        label = {
                            val label = when (melody) {
                                AdhanMelody.MAKKAH_MELODY -> "Makkah (Bayati)"
                                AdhanMelody.MADINAH_MELODY -> "Madinah (Hijaz)"
                                AdhanMelody.DEVICE_ALARM -> "Device Alarm"
                            }
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Test button
            Button(
                onClick = onToggleTest,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_adhan_alert_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) MaterialTheme.colorScheme.error else IslamicGreenPrimary
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPlaying) "Stop Adhan Audio" else "Test Audible Adhan Alert Now",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

