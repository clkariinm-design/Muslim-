package com.example

import com.example.data.prayer.PrayerType
import com.example.data.prayer.adhan.AdhanAudioPlayer
import com.example.data.prayer.adhan.AdhanMelody
import com.example.data.prayer.adhan.AdhanNotificationManager
import com.example.data.prayer.adhan.AdhanScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdhanAlertTest {

    @Test
    fun testAdhanMelodyOptions() {
        val melodies = AdhanMelody.entries
        assertEquals(3, melodies.size)
        assertTrue(melodies.contains(AdhanMelody.MAKKAH_MELODY))
        assertTrue(melodies.contains(AdhanMelody.MADINAH_MELODY))
        assertTrue(melodies.contains(AdhanMelody.DEVICE_ALARM))

        melodies.forEach { melody ->
            assertNotNull(melody.title)
            assertNotNull(melody.description)
            assertTrue(melody.title.isNotBlank())
        }
    }

    @Test
    fun testAdhanSchedulerConstants() {
        assertEquals("com.example.action.PRAYER_ADHAN", AdhanScheduler.ACTION_PRAYER_ADHAN)
        assertEquals("com.example.action.STOP_ADHAN", AdhanScheduler.ACTION_STOP_ADHAN)
        assertEquals("EXTRA_PRAYER_TYPE", AdhanScheduler.EXTRA_PRAYER_TYPE)
        assertEquals("EXTRA_FORMATTED_TIME", AdhanScheduler.EXTRA_FORMATTED_TIME)
        assertEquals("EXTRA_MELODY", AdhanScheduler.EXTRA_MELODY)
    }

    @Test
    fun testAdhanNotificationChannelConfig() {
        assertEquals("adhan_prayer_alerts_channel", AdhanNotificationManager.CHANNEL_ID)
        assertEquals(1001, AdhanNotificationManager.NOTIFICATION_ID)
    }

    @Test
    fun testAdhanAudioPlayerInitialStateAndStop() {
        assertFalse(AdhanAudioPlayer.isPlayingState.value)
        assertEquals(null, AdhanAudioPlayer.currentPrayerPlaying.value)

        // Stopping when idle should be completely safe
        AdhanAudioPlayer.stop()
        assertFalse(AdhanAudioPlayer.isPlayingState.value)
    }

    @Test
    fun testPrayerTypesSubjectToAdhan() {
        val fardPrayers = PrayerType.entries.filter { it.isFard }
        // 5 daily obligatory prayers (Fajr, Dhuhr, Asr, Maghrib, Isha)
        assertEquals(5, fardPrayers.size)

        // Sunrise is marked non-fard (so Adhan is skipped for sunrise)
        assertFalse(PrayerType.SUNRISE.isFard)
        assertTrue(PrayerType.FAJR.isFard)
        assertTrue(PrayerType.DHUHR.isFard)
        assertTrue(PrayerType.ASR.isFard)
        assertTrue(PrayerType.MAGHRIB.isFard)
        assertTrue(PrayerType.ISHA.isFard)
    }
}
