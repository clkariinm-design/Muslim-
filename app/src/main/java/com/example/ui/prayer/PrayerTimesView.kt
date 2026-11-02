package com.example.ui.prayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Brightness5
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.location.UserLocation
import com.example.data.prayer.AsrMethod
import com.example.data.prayer.CalculationMethod
import com.example.data.prayer.PrayerSchedule
import com.example.data.prayer.PrayerTimeItem
import com.example.data.prayer.PrayerType
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * PrayerTimesView Composable
 *
 * Displays daily Islamic prayer times fetched via location services and styled
 * using polished Material 3 cards with upcoming prayer countdown, Islamic/Gregorian dates,
 * location detection, and calculation convention settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesView(
    modifier: Modifier = Modifier,
    viewModel: PrayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Location Permission Request Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onLocationPermissionResult(fineGranted || coarseGranted)
    }

    // Function to check and trigger location detection
    val triggerLocationDetection = {
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
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Notification Permission Launcher (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Adhan audible notifications enabled")
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

    LaunchedEffect(uiState.locationErrorMessage) {
        uiState.locationErrorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_times_view"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IslamicGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
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
                                text = "Daily Prayer Times • Salaadaha",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
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
        PrayerTimesContent(
            uiState = uiState,
            onRequestLocation = triggerLocationDetection,
            onChangeCity = { viewModel.openCityPicker(true) },
            onOpenSettings = { viewModel.openMethodPicker(true) },
            onSelectCalculationMethod = { viewModel.setCalculationMethod(it) },
            onToggleNotification = { prayerType ->
                requestNotificationPermission()
                viewModel.toggleNotification(prayerType)
            },
            onPlayAdhanTest = {
                requestNotificationPermission()
                viewModel.toggleAudibleAdhanTest()
            },
            onSelectAdhanMelody = { viewModel.setAdhanMelody(it) },
            modifier = Modifier.padding(innerPadding)
        )
    }

    // Bottom sheets
    if (uiState.showCityPicker) {
        CityPickerBottomSheet(
            searchQuery = uiState.citySearchQuery,
            cities = uiState.filteredCities,
            onSearchChange = { viewModel.updateCitySearch(it) },
            onSelectCity = { viewModel.selectPresetCity(it) },
            onDetectGps = {
                viewModel.openCityPicker(false)
                triggerLocationDetection()
            },
            onDismiss = { viewModel.openCityPicker(false) }
        )
    }

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

/**
 * Stateful/Stateless Content layout rendering all Material 3 prayer cards
 */
@Composable
fun PrayerTimesContent(
    uiState: PrayerUiState,
    onRequestLocation: () -> Unit,
    onChangeCity: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectCalculationMethod: (CalculationMethod) -> Unit,
    onToggleNotification: (PrayerType) -> Unit,
    onPlayAdhanTest: () -> Unit,
    onSelectAdhanMelody: (com.example.data.prayer.adhan.AdhanMelody) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Location Services Card (M3 Elevated Card)
        item {
            LocationServicesCard(
                location = uiState.location,
                isLoading = uiState.isLoadingLocation,
                onRefreshClick = onRequestLocation,
                onChangeCityClick = onChangeCity
            )
        }

        // 2. Next Prayer Spotlight Hero Card (Rich M3 Gradient Card)
        item {
            uiState.schedule?.let { schedule ->
                NextPrayerCard(
                    schedule = schedule,
                    countdownFormatted = uiState.countdownFormatted
                )
            }
        }

        // 3. Hijri & Gregorian Calendar Card (M3 Card)
        item {
            uiState.schedule?.let { schedule ->
                DateInfoCard(
                    gregorianDate = schedule.dateDisplay,
                    hijriDate = schedule.hijriDateDisplay
                )
            }
        }

        // 4. Quick Method Selector & Compliance Card
        item {
            CalculationSelectorCard(
                selectedMethod = uiState.calculationMethod,
                asrMethod = uiState.asrMethod,
                latitude = uiState.location.latitude,
                longitude = uiState.location.longitude,
                onSelectMethod = onSelectCalculationMethod,
                onOpenFullSettings = onOpenSettings
            )
        }

        // 5. Header for Daily Times
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Prayer Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = uiState.calculationMethod.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onOpenSettings() }
                )
            }
        }

        // 6. Material 3 Cards for each of the 6 Daily Prayer Times
        uiState.schedule?.let { schedule ->
            items(schedule.prayerTimes) { prayerItem ->
                val isNotifActive = uiState.notifications[prayerItem.type] ?: true
                DailyPrayerTimeCard(
                    item = prayerItem,
                    isNotificationActive = isNotifActive,
                    onToggleNotification = { onToggleNotification(prayerItem.type) }
                )
            }
        }

        // 7. Audible Adhan / Call to Prayer Test Card
        item {
            AdhanAudibleAlertCard(
                isPlaying = uiState.isAdhanPlaying,
                selectedMelody = uiState.selectedMelody,
                onSelectMelody = onSelectAdhanMelody,
                onToggleTest = onPlayAdhanTest
            )
        }
    }
}

/**
 * Material 3 Location Card with real-time GPS state and manual override
 */
@Composable
fun LocationServicesCard(
    location: UserLocation,
    isLoading: Boolean,
    onRefreshClick: () -> Unit,
    onChangeCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_location_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onChangeCityClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Pin",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = location.displayTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (location.isGpsDetected) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "GPS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    val latHemisphere = if (location.latitude >= 0) "N" else "S"
                    val lonHemisphere = if (location.longitude >= 0) "E" else "W"
                    Text(
                        text = String.format(
                            Locale.US,
                            "%.2f°%s, %.2f°%s • Tap to change city",
                            Math.abs(location.latitude),
                            latHemisphere,
                            Math.abs(location.longitude),
                            lonHemisphere
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onRefreshClick,
                enabled = !isLoading,
                modifier = Modifier.testTag("refresh_gps_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Detect Current Location via GPS",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Next Prayer Spotlight Hero Card
 */
@Composable
fun NextPrayerCard(
    schedule: PrayerSchedule,
    countdownFormatted: String,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .fillMaxWidth()
            .testTag("next_prayer_card"),
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            text = "UPCOMING PRAYER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGoldLight,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Text(
                        text = next.type.arabicName,
                        fontSize = 26.sp,
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
                            text = "${next.type.englishName} • ${next.type.somaliName}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = next.formattedTime,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Text(
                            text = countdownFormatted,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGold
                        )
                    }
                }

                // Progress Bar toward next prayer time
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

/**
 * Gregorian & Hijri Date Information Card
 */
@Composable
fun DateInfoCard(
    gregorianDate: String,
    hijriDate: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_date_info_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🕌", fontSize = 22.sp)
                }
            }
        }
    }
}

/**
 * Material 3 Card representing an individual prayer time (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha)
 */
@Composable
fun DailyPrayerTimeCard(
    item: PrayerTimeItem,
    isNotificationActive: Boolean,
    onToggleNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNext = item.isNext
    val isPassed = item.isPassed

    val cardBorder = if (isNext) {
        BorderStroke(2.dp, IslamicGreenPrimary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }

    val containerColor = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        isPassed -> MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_card_${item.type.id}")
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
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
            // Left: Icon + Names
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isNext) IslamicGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getPrayerIconVector(item.type),
                            contentDescription = item.type.englishName,
                            tint = if (isNext) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.type.englishName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isPassed && !isNext) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
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

            // Right: Time + Notification Toggle
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
                        .size(40.dp)
                        .testTag("notification_toggle_${item.type.id}")
                ) {
                    Icon(
                        imageVector = if (isNotificationActive) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        contentDescription = "Notification toggle for ${item.type.englishName}",
                        tint = if (isNotificationActive) IslamicGold else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/**
 * Calculation Method Quick Selector & Compliance Details
 */
@Composable
fun CalculationSelectorCard(
    selectedMethod: CalculationMethod,
    asrMethod: AsrMethod,
    latitude: Double,
    longitude: Double,
    onSelectMethod: (CalculationMethod) -> Unit,
    onOpenFullSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("calculation_selector_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculation Method",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onOpenFullSettings() }
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    listOf(
                        CalculationMethod.ISNA,
                        CalculationMethod.UMM_AL_QURA,
                        CalculationMethod.MUSLIM_WORLD_LEAGUE,
                        CalculationMethod.EGYPTIAN,
                        CalculationMethod.KARACHI
                    )
                ) { method ->
                    val isSelected = method == selectedMethod
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectMethod(method) },
                        label = {
                            val shortLabel = when (method) {
                                CalculationMethod.ISNA -> "ISNA"
                                CalculationMethod.UMM_AL_QURA -> "Umm Al-Qura"
                                CalculationMethod.MUSLIM_WORLD_LEAGUE -> "MWL"
                                CalculationMethod.EGYPTIAN -> "Egyptian"
                                CalculationMethod.KARACHI -> "Karachi"
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

            val latDir = if (latitude >= 0) "N" else "S"
            val lonDir = if (longitude >= 0) "E" else "W"
            Text(
                text = "${selectedMethod.description} • Asr: ${asrMethod.title}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getPrayerIconVector(type: PrayerType): ImageVector {
    return when (type) {
        PrayerType.FAJR -> Icons.Outlined.WbTwilight
        PrayerType.SUNRISE -> Icons.Outlined.WbSunny
        PrayerType.DHUHR -> Icons.Outlined.Brightness5
        PrayerType.ASR -> Icons.Outlined.WbSunny
        PrayerType.MAGHRIB -> Icons.Outlined.WbTwilight
        PrayerType.ISHA -> Icons.Outlined.NightsStay
    }
}
