package com.example.data.azkar

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object AzkarNotificationHelper {

    const val CHANNEL_ID = "daily_azkar_channel"
    private const val CHANNEL_NAME = "Daily Azkar & Remembrances"
    private const val CHANNEL_DESC = "Authentic daily morning, evening, and night Azkar reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showAzkarNotification(context: Context, azkar: AzkarItem, customTitle: String? = null) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            azkar.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = customTitle ?: "${azkar.category.icon} ${azkar.category.titleEnglish} • ${azkar.category.titleSomali}"

        val bigText = buildString {
            append(azkar.arabicText)
            append("\n\n")
            append(azkar.translationSomali)
            append("\n\n")
            append("— [${azkar.reference}]")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(azkar.arabicText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setSummaryText(azkar.category.titleEnglish)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

class AzkarAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule all daily alarms upon reboot
                AzkarScheduler.scheduleAllDailyAzkar(context)
            }
            AzkarScheduler.ACTION_MORNING_AZKAR -> {
                val azkar = AzkarCollection.getRandomAzkarForCategory(AzkarCategory.MORNING)
                AzkarNotificationHelper.showAzkarNotification(
                    context,
                    azkar,
                    "🌅 Morning Azkar • Adkaarta Subaxda"
                )
            }
            AzkarScheduler.ACTION_MIDDAY_AZKAR -> {
                val azkar = AzkarCollection.getRandomAzkarForCategory(AzkarCategory.GENERAL)
                AzkarNotificationHelper.showAzkarNotification(
                    context,
                    azkar,
                    "☀️ Midday Remembrance • Xuska Alle"
                )
            }
            AzkarScheduler.ACTION_EVENING_AZKAR -> {
                val azkar = AzkarCollection.getRandomAzkarForCategory(AzkarCategory.EVENING)
                AzkarNotificationHelper.showAzkarNotification(
                    context,
                    azkar,
                    "🌇 Evening Azkar • Adkaarta Galabta"
                )
            }
            AzkarScheduler.ACTION_NIGHT_AZKAR -> {
                val azkar = AzkarCollection.getRandomAzkarForCategory(AzkarCategory.BEFORE_SLEEP)
                AzkarNotificationHelper.showAzkarNotification(
                    context,
                    azkar,
                    "🌙 Night Azkar • Adkaarta Hurdada"
                )
            }
        }
    }
}

object AzkarScheduler {

    const val ACTION_MORNING_AZKAR = "com.example.action.MORNING_AZKAR"
    const val ACTION_MIDDAY_AZKAR = "com.example.action.MIDDAY_AZKAR"
    const val ACTION_EVENING_AZKAR = "com.example.action.EVENING_AZKAR"
    const val ACTION_NIGHT_AZKAR = "com.example.action.NIGHT_AZKAR"

    fun scheduleAllDailyAzkar(context: Context) {
        // Morning Azkar at 07:00 AM
        scheduleDailyAlarm(context, 7, 0, ACTION_MORNING_AZKAR, 101)

        // Midday Azkar at 01:30 PM
        scheduleDailyAlarm(context, 13, 30, ACTION_MIDDAY_AZKAR, 102)

        // Evening Azkar at 05:30 PM
        scheduleDailyAlarm(context, 17, 30, ACTION_EVENING_AZKAR, 103)

        // Night / Sleep Azkar at 09:30 PM
        scheduleDailyAlarm(context, 21, 30, ACTION_NIGHT_AZKAR, 104)
    }

    private fun scheduleDailyAlarm(
        context: Context,
        hour: Int,
        minute: Int,
        action: String,
        requestCode: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, AzkarAlarmReceiver::class.java).apply {
            this.action = action
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If time already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback to non-exact alarm if exact alarm permission is restricted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun triggerInstantTestNotification(context: Context) {
        val randomAzkar = AzkarCollection.getRandomDailyAzkar()
        AzkarNotificationHelper.showAzkarNotification(
            context,
            randomAzkar,
            "${randomAzkar.category.icon} Daily Remembrance • Adkaar"
        )
    }
}
