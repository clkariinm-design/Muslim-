package com.example.data.prayer.adhan

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.prayer.PrayerSchedule
import com.example.data.prayer.PrayerType
import java.util.Calendar

object AdhanNotificationManager {

    private const val TAG = "AdhanNotification"
    const val CHANNEL_ID = "adhan_prayer_alerts_channel"
    private const val CHANNEL_NAME = "Adhan & Prayer Times Alerts"
    private const val CHANNEL_DESC = "Audible Adhan calls and prayer time notifications"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val defaultAlarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 800)
                setSound(defaultAlarmUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showAdhanAlert(
        context: Context,
        prayerType: PrayerType,
        formattedTime: String,
        playMelodicAudio: Boolean = true,
        melody: AdhanMelody = AdhanMelody.MAKKAH_MELODY
    ) {
        createNotificationChannel(context)

        // Stop any previously playing adhan
        AdhanAudioPlayer.stop()

        // 1. Content Intent to launch the app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_PRAYER_TYPE", prayerType.name)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            201,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Action Intent to stop / dismiss audio
        val stopIntent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            action = AdhanScheduler.ACTION_STOP_ADHAN
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            202,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "🕌 Hayya 'alas-Salah • ${prayerType.englishName} (${prayerType.somaliName})"
        val arabicChant = "الله أكبر، الله أكبر • حي على الصلاة، حي على الفلاح"
        val somaliTranslation = "Waa waqtigii salaadda ${prayerType.somaliName} ($formattedTime). Kaalaya liibaanta!"

        val bigText = buildString {
            append(arabicChant)
            append("\n\n")
            append(somaliTranslation)
            append("\n\n")
            append("• ${prayerType.arabicName} | Time: $formattedTime")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("$arabicChant ($formattedTime)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setSummaryText("Waqtiga Salaadda")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "⏹ Stop Adhan",
                stopPendingIntent
            )
            .addAction(
                android.R.drawable.ic_dialog_map,
                "🧭 Open Qibla & Prayer",
                contentPendingIntent
            )
            .setOngoing(playMelodicAudio)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notification)

        // Play the audible offline Adhan audio
        if (playMelodicAudio) {
            AdhanAudioPlayer.playAdhan(
                context = context,
                prayerName = prayerType.englishName,
                melody = melody,
                durationSeconds = 24
            )
        }
    }

    fun dismissAdhan(context: Context) {
        AdhanAudioPlayer.stop()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}

class AdhanAlarmReceiver : BroadcastReceiver() {

    private val TAG = "AdhanAlarmReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d(TAG, "AdhanAlarmReceiver received action: $action")

        when (action) {
            AdhanScheduler.ACTION_PRAYER_ADHAN -> {
                val prayerTypeName = intent.getStringExtra(AdhanScheduler.EXTRA_PRAYER_TYPE)
                val formattedTime = intent.getStringExtra(AdhanScheduler.EXTRA_FORMATTED_TIME) ?: ""
                val melodyName = intent.getStringExtra(AdhanScheduler.EXTRA_MELODY)

                val prayerType = try {
                    PrayerType.valueOf(prayerTypeName ?: PrayerType.FAJR.name)
                } catch (e: Exception) {
                    PrayerType.FAJR
                }

                val melody = try {
                    AdhanMelody.valueOf(melodyName ?: AdhanMelody.MAKKAH_MELODY.name)
                } catch (e: Exception) {
                    AdhanMelody.MAKKAH_MELODY
                }

                // Acquire temporary partial wake lock to guarantee execution while device is sleeping
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                val wakeLock = powerManager?.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "Athan:AdhanWakeLock"
                )
                wakeLock?.acquire(30000L) // 30 seconds max

                try {
                    AdhanNotificationManager.showAdhanAlert(
                        context = context,
                        prayerType = prayerType,
                        formattedTime = formattedTime,
                        playMelodicAudio = true,
                        melody = melody
                    )
                } finally {
                    if (wakeLock?.isHeld == true) {
                        try {
                            wakeLock.release()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error releasing wakelock", e)
                        }
                    }
                }
            }

            AdhanScheduler.ACTION_STOP_ADHAN -> {
                AdhanNotificationManager.dismissAdhan(context)
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                Log.i(TAG, "Device rebooted: re-initializing prayer alerts")
                // Note: when app is opened or location is updated, AdhanScheduler will resync
            }
        }
    }
}

object AdhanScheduler {

    const val ACTION_PRAYER_ADHAN = "com.example.action.PRAYER_ADHAN"
    const val ACTION_STOP_ADHAN = "com.example.action.STOP_ADHAN"
    const val EXTRA_PRAYER_TYPE = "EXTRA_PRAYER_TYPE"
    const val EXTRA_FORMATTED_TIME = "EXTRA_FORMATTED_TIME"
    const val EXTRA_MELODY = "EXTRA_MELODY"

    private const val REQUEST_CODE_BASE = 500

    /**
     * Schedules exact audible Adhan alarms for all upcoming prayers in the schedule
     */
    fun schedulePrayerAlarms(
        context: Context,
        schedule: PrayerSchedule,
        notificationsMap: Map<PrayerType, Boolean>,
        melody: AdhanMelody = AdhanMelody.MAKKAH_MELODY
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = System.currentTimeMillis()

        schedule.prayerTimes.forEachIndexed { index, item ->
            // Skip Sunrise as it is not a prayer requiring Adhan call
            if (item.type == PrayerType.SUNRISE) return@forEachIndexed

            val isEnabled = notificationsMap[item.type] ?: true
            val requestCode = REQUEST_CODE_BASE + index

            val intent = Intent(context, AdhanAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ADHAN
                putExtra(EXTRA_PRAYER_TYPE, item.type.name)
                putExtra(EXTRA_FORMATTED_TIME, item.formattedTime)
                putExtra(EXTRA_MELODY, melody.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!isEnabled || item.timeMillis <= now) {
                // Cancel past or disabled alarms
                alarmManager.cancel(pendingIntent)
            } else {
                // Schedule exact alarm for upcoming prayer time
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            item.timeMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            item.timeMillis,
                            pendingIntent
                        )
                    }
                    Log.d("AdhanScheduler", "Scheduled Adhan for ${item.type.englishName} at ${item.formattedTime}")
                } catch (e: SecurityException) {
                    Log.w("AdhanScheduler", "Exact alarm permission restricted; falling back to inexact alarm", e)
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        item.timeMillis,
                        pendingIntent
                    )
                }
            }
        }
    }

    /**
     * Instantly triggers a test audible Adhan alert so the user can test their volume and notification
     */
    fun triggerInstantAdhanTest(
        context: Context,
        prayerType: PrayerType = PrayerType.FAJR,
        melody: AdhanMelody = AdhanMelody.MAKKAH_MELODY
    ) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        val amPm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val timeString = String.format(java.util.Locale.US, "%02d:%02d %s", if (hour == 0) 12 else hour, min, amPm)

        AdhanNotificationManager.showAdhanAlert(
            context = context,
            prayerType = prayerType,
            formattedTime = timeString,
            playMelodicAudio = true,
            melody = melody
        )
    }

    fun stopAdhanAlert(context: Context) {
        AdhanNotificationManager.dismissAdhan(context)
    }
}
