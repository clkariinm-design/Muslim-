package com.example.data.prayer.adhan

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Melody style for offline synthesized Adhan playback
 */
enum class AdhanMelody(val title: String, val description: String) {
    MAKKAH_MELODY("Makkah Style (Bayati)", "Traditional resonant melodic call of the Grand Mosque"),
    MADINAH_MELODY("Madinah Style (Hijaz)", "Serene melodic chanting of the Prophet's Mosque"),
    DEVICE_ALARM("Device Alarm Ringtone", "Standard high-priority device alert tone")
}

/**
 * Offline Audible Adhan Audio Engine.
 * Provides high-fidelity synthesized musical harmonics of the Adhan notes (Maqam Bayati / Hijaz)
 * generated purely via PCM AudioTrack, ensuring 100% offline audio playback on any Android device
 * or emulator without requiring network downloads. Also supports system ringtone playback.
 */
object AdhanAudioPlayer {

    private const val TAG = "AdhanAudioPlayer"
    private const val SAMPLE_RATE = 22050

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var systemRingtone: Ringtone? = null

    private val _isPlayingState = MutableStateFlow(false)
    val isPlayingState: StateFlow<Boolean> = _isPlayingState.asStateFlow()

    private val _currentPrayerPlaying = MutableStateFlow<String?>(null)
    val currentPrayerPlaying: StateFlow<String?> = _currentPrayerPlaying.asStateFlow()

    @Synchronized
    fun playAdhan(
        context: Context,
        prayerName: String = "Adhan",
        melody: AdhanMelody = AdhanMelody.MAKKAH_MELODY,
        durationSeconds: Int = 18
    ) {
        stop()

        _currentPrayerPlaying.value = prayerName
        _isPlayingState.value = true

        if (melody == AdhanMelody.DEVICE_ALARM) {
            playSystemAlarm(context)
            return
        }

        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                val notes = when (melody) {
                    AdhanMelody.MAKKAH_MELODY -> getMakkahAdhanNotes()
                    AdhanMelody.MADINAH_MELODY -> getMadinahAdhanNotes()
                    else -> getMakkahAdhanNotes()
                }

                val bufferSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(SAMPLE_RATE / 2)

                val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(SAMPLE_RATE)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(bufferSize)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    AudioTrack(
                        AudioManager.STREAM_ALARM,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize,
                        AudioTrack.MODE_STREAM
                    )
                }

                audioTrack = track
                track.play()

                val startTime = System.currentTimeMillis()
                val maxDurationMs = durationSeconds * 1000L

                // Loop through melodic notes
                while (isActive && (System.currentTimeMillis() - startTime) < maxDurationMs) {
                    for (note in notes) {
                        if (!isActive) break

                        val pcmData = generateChantTone(
                            frequency = note.frequency,
                            durationMs = note.durationMs,
                            hasVibrato = note.hasVibrato
                        )

                        track.write(pcmData, 0, pcmData.size)
                        delay(25L) // Inter-tone breath pause
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "AudioTrack Adhan synthesis error", e)
            } finally {
                stop()
            }
        }
    }

    private fun playSystemAlarm(context: Context) {
        try {
            var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            systemRingtone = RingtoneManager.getRingtone(context.applicationContext, alarmUri)?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                }
                play()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play system alarm", e)
        }
    }

    @Synchronized
    fun stop() {
        playbackJob?.cancel()
        playbackJob = null

        try {
            audioTrack?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioTrack", e)
        } finally {
            audioTrack = null
        }

        try {
            systemRingtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping system ringtone", e)
        } finally {
            systemRingtone = null
        }

        _isPlayingState.value = false
        _currentPrayerPlaying.value = null
    }

    /**
     * Synthesizes warm vocal/horn harmonic timbre using additive harmonic synthesis
     * with attack, decay, sustain, release (ADSR) envelope and slight pitch vibrato.
     */
    private fun generateChantTone(
        frequency: Double,
        durationMs: Int,
        hasVibrato: Boolean
    ): ShortArray {
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val pcm = ShortArray(totalSamples)

        val attackSamples = (SAMPLE_RATE * 0.08).toInt().coerceAtMost(totalSamples / 4)
        val releaseSamples = (SAMPLE_RATE * 0.12).toInt().coerceAtMost(totalSamples / 3)
        val sustainSamples = (totalSamples - attackSamples - releaseSamples).coerceAtLeast(0)

        for (i in 0 until totalSamples) {
            // ADSR Envelope
            val envelope = when {
                i < attackSamples -> (i.toDouble() / attackSamples)
                i < attackSamples + sustainSamples -> 1.0 - (0.15 * ((i - attackSamples).toDouble() / sustainSamples))
                else -> {
                    val relProgress = (i - attackSamples - sustainSamples).toDouble() / releaseSamples
                    (1.0 - relProgress).coerceAtLeast(0.0)
                }
            }

            // Vibrato (pitch flutter common in Adhan recitation)
            val vibrato = if (hasVibrato) {
                1.0 + 0.015 * sin(2.0 * PI * 5.2 * (i.toDouble() / SAMPLE_RATE))
            } else {
                1.0
            }

            val currentFreq = frequency * vibrato
            val t = i.toDouble() / SAMPLE_RATE

            // Harmonic Overtones (Fundamental + 2nd + 3rd harmonic for warm voice chime)
            val fundamental = sin(2.0 * PI * currentFreq * t)
            val harmonic2 = 0.45 * sin(2.0 * PI * (currentFreq * 2.0) * t)
            val harmonic3 = 0.20 * sin(2.0 * PI * (currentFreq * 3.0) * t)
            val harmonic4 = 0.08 * sin(2.0 * PI * (currentFreq * 4.0) * t)

            val rawSample = (fundamental + harmonic2 + harmonic3 + harmonic4) * envelope * 0.70
            pcm[i] = (rawSample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        return pcm
    }

    private data class Note(val frequency: Double, val durationMs: Int, val hasVibrato: Boolean = true)

    // Musical Notes: D4 = 293.66, E4 = 329.63, F4 = 349.23, G4 = 392.00, A4 = 440.00, Bb4 = 466.16, C5 = 523.25, D5 = 587.33
    private fun getMakkahAdhanNotes(): List<Note> = listOf(
        // Allahu Akbar, Allahu Akbar (Opening Takbeer)
        Note(293.66, 600, false), // Al- (D4)
        Note(392.00, 1100, true), // laahu (G4)
        Note(440.00, 500, false), // Ak- (A4)
        Note(392.00, 1200, true), // bar (G4)
        Note(293.66, 250, false), // breath

        Note(293.66, 600, false), // Al- (D4)
        Note(392.00, 1100, true), // laahu (G4)
        Note(349.23, 500, false), // Ak- (F4)
        Note(293.66, 1400, true), // bar (D4)
        Note(293.66, 350, false), // pause

        // Ash-hadu an la ilaha illallah
        Note(349.23, 600, false), // Ash-hadu (F4)
        Note(392.00, 700, false), // al-laa (G4)
        Note(440.00, 1300, true), // ilaaha (A4)
        Note(392.00, 600, false), // il-lal- (G4)
        Note(349.23, 1400, true), // laah (F4)
        Note(293.66, 400, false), // pause

        // Hayya 'alas-Salah
        Note(392.00, 650, false), // Hayya (G4)
        Note(440.00, 800, true),  // 'alas- (A4)
        Note(523.25, 1200, true), // Salaah (C5)
        Note(440.00, 700, false), // (A4)
        Note(392.00, 1300, true)  // (G4)
    )

    private fun getMadinahAdhanNotes(): List<Note> = listOf(
        // Hijaz Melody: D4, Eb4, F#4, G4, A4, Bb4, C5, D5
        Note(293.66, 700, false), // D4
        Note(311.13, 500, false), // Eb4
        Note(369.99, 1300, true), // F#4
        Note(392.00, 1000, true), // G4
        Note(293.66, 300, false),

        Note(392.00, 600, false), // G4
        Note(440.00, 1200, true), // A4
        Note(369.99, 700, false), // F#4
        Note(293.66, 1500, true), // D4
        Note(293.66, 350, false),

        Note(369.99, 700, false), // F#4
        Note(392.00, 800, false), // G4
        Note(466.16, 1400, true), // Bb4
        Note(392.00, 600, false), // G4
        Note(293.66, 1600, true)  // D4
    )
}
