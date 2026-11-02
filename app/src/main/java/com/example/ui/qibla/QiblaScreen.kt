package com.example.ui.qibla

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    viewModel: QiblaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Register / unregister sensors with lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.startSensors()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.stopSensors()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        viewModel.startSensors()

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopSensors()
        }
    }

    val animatedDialRotation by animateFloatAsState(
        targetValue = -uiState.deviceHeading,
        animationSpec = spring(stiffness = 300f),
        label = "dial_rotation"
    )

    val animatedNeedleRotation by animateFloatAsState(
        targetValue = uiState.needleAngle,
        animationSpec = spring(stiffness = 300f),
        label = "needle_rotation"
    )

    val alignmentColor by animateColorAsState(
        targetValue = if (uiState.isFacingKaaba) IslamicGold else IslamicGreenPrimary,
        label = "alignment_color"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Alignment Status Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("qibla_alignment_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isFacingKaaba)
                    IslamicGreenDark
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (uiState.isFacingKaaba) IslamicGold else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (uiState.isFacingKaaba) Icons.Default.CheckCircle else Icons.Default.Explore,
                                contentDescription = null,
                                tint = if (uiState.isFacingKaaba) Color.Black else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (uiState.isFacingKaaba) "Facing the Kaaba!" else "Rotate Phone Towards Kaaba",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isFacingKaaba) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (uiState.isFacingKaaba) "Alhamdulillah • Qibla Aligned" else "Align the Kaaba pointer to the top marker",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.isFacingKaaba) IslamicGoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "${uiState.qiblaBearing.toInt()}°",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (uiState.isFacingKaaba) IslamicGold else MaterialTheme.colorScheme.primary
                )
            }
        }

        // Tilt warning if phone held vertically
        AnimatedVisibility(visible = !uiState.isDeviceFlat) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Hold Flat",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Hold your phone flat on your palm for best compass precision.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Circular Qibla Compass
        Box(
            modifier = Modifier
                .size(290.dp)
                .testTag("qibla_compass_dial"),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Compass Rose (North, East, South, West dial)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedDialRotation)
            ) {
                drawCompassDial(
                    primaryColor = IslamicGreenPrimary,
                    accentColor = IslamicGold,
                    isDark = true
                )
            }

            // Fixed Top Heading Guide marker
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = alignmentColor,
                    modifier = Modifier.size(width = 12.dp, height = 14.dp)
                ) {}
            }

            // Rotating Kaaba Pointer Needle
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedNeedleRotation)
            ) {
                drawKaabaNeedle(
                    isFacing = uiState.isFacingKaaba,
                    needleColor = alignmentColor
                )
            }

            // Center Display Hub
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(105.dp)
                    .border(
                        width = if (uiState.isFacingKaaba) 3.dp else 1.5.dp,
                        color = alignmentColor,
                        shape = CircleShape
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🕋",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "${uiState.deviceHeading.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = alignmentColor
                    )
                    Text(
                        text = uiState.cardinalDirection,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Location & Distance Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("qibla_stats_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = uiState.userLocation.displayTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            val latDir = if (uiState.userLocation.latitude >= 0) "N" else "S"
                            val lonDir = if (uiState.userLocation.longitude >= 0) "E" else "W"
                            Text(
                                text = String.format(Locale.US, "%.2f°%s, %.2f°%s", Math.abs(uiState.userLocation.latitude), latDir, Math.abs(uiState.userLocation.longitude), lonDir),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Distance to Kaaba",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = String.format(Locale.US, "%,.0f km", uiState.distanceKm),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = IslamicGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Qibla Bearing: ${uiState.qiblaBearing.toInt()}° from North",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Current Heading: ${uiState.deviceHeading.toInt()}°",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCompassDial(primaryColor: Color, accentColor: Color, isDark: Boolean) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = size.minDimension / 2f - 12f

    // Outer circle
    drawCircle(
        color = primaryColor.copy(alpha = 0.2f),
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    // Inner tick ring
    drawCircle(
        color = accentColor.copy(alpha = 0.15f),
        radius = radius - 16.dp.toPx(),
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )

    // Draw 36 tick marks (every 10 degrees)
    for (deg in 0 until 360 step 10) {
        val angleRad = Math.toRadians((deg - 90).toDouble())
        val isMajor = deg % 30 == 0
        val isCardinal = deg % 90 == 0

        val tickLength = when {
            isCardinal -> 14.dp.toPx()
            isMajor -> 9.dp.toPx()
            else -> 5.dp.toPx()
        }

        val strokeWidth = when {
            isCardinal -> 2.5.dp.toPx()
            isMajor -> 1.5.dp.toPx()
            else -> 1.dp.toPx()
        }

        val tickColor = when {
            deg == 0 -> Color.Red // North marker
            isCardinal -> accentColor
            isMajor -> primaryColor
            else -> primaryColor.copy(alpha = 0.35f)
        }

        val startX = center.x + ((radius - tickLength) * cos(angleRad)).toFloat()
        val startY = center.y + ((radius - tickLength) * sin(angleRad)).toFloat()
        val endX = center.x + (radius * cos(angleRad)).toFloat()
        val endY = center.y + (radius * sin(angleRad)).toFloat()

        drawLine(
            color = tickColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeWidth
        )
    }
}

private fun DrawScope.drawKaabaNeedle(isFacing: Boolean, needleColor: Color) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val needleLength = size.minDimension / 2f - 24f

    // Forward pointer pointing to Kaaba
    val path = Path().apply {
        moveTo(center.x, center.y - needleLength)
        lineTo(center.x - 14f, center.y - 70f)
        lineTo(center.x + 14f, center.y - 70f)
        close()
    }

    drawPath(
        path = path,
        color = needleColor
    )

    // Needle Stem
    drawLine(
        color = needleColor,
        start = Offset(center.x, center.y - 70f),
        end = Offset(center.x, center.y + 60f),
        strokeWidth = 3.dp.toPx()
    )

    // Kaaba cube silhouette icon atop needle
    val cubeSize = 22f
    val cubeTopY = center.y - needleLength - cubeSize - 4f
    drawRect(
        color = if (isFacing) Color(0xFF1B1B1B) else Color(0xFF2C2C2C),
        topLeft = Offset(center.x - cubeSize / 2f, cubeTopY),
        size = androidx.compose.ui.geometry.Size(cubeSize, cubeSize)
    )

    // Golden Kiswah band on the cube
    drawLine(
        color = IslamicGold,
        start = Offset(center.x - cubeSize / 2f, cubeTopY + 6f),
        end = Offset(center.x + cubeSize / 2f, cubeTopY + 6f),
        strokeWidth = 2.5f
    )
}
