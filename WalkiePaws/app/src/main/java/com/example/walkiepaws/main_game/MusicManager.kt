package com.example.walkiepaws.main_game

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.walkiepaws.R

class MusicManager private constructor(context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentVolume = 0.5f
    private var isMusicEnabled = true
    private val prefs: SharedPreferences = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)
    private val appContext: Context = context.applicationContext

    companion object {
        @Volatile
        private var instance: MusicManager? = null

        fun getInstance(context: Context): MusicManager {
            return instance ?: synchronized(this) {
                instance ?: MusicManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun initialize(context: Context) {
        loadSettings()
        if (isMusicEnabled && mediaPlayer == null) {
            createMediaPlayer()
        }
    }

    private fun createMediaPlayer() {
        mediaPlayer?.release() // Освобождаем старый экземпляр, если он есть
        mediaPlayer = MediaPlayer.create(appContext, R.raw.song_main_game)?.apply {
            isLooping = true
            setVolume(currentVolume, currentVolume)
            if (isMusicEnabled) start()
        }
    }

    fun play() {
        if (isMusicEnabled) {
            if (mediaPlayer == null) {
                createMediaPlayer()
            } else if (!mediaPlayer!!.isPlaying) {
                mediaPlayer?.start()
            }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setVolume(volume: Float) {
        currentVolume = volume
        mediaPlayer?.setVolume(volume, volume)
        saveSettings()
    }

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            play()
        } else {
            pause()
        }
        saveSettings()
    }

    fun getCurrentVolume(): Float = currentVolume
    fun isMusicEnabled(): Boolean = isMusicEnabled

    private fun saveSettings() {
        prefs.edit()
            .putFloat("volume", currentVolume)
            .putBoolean("music_enabled", isMusicEnabled)
            .apply()
    }

    private fun loadSettings() {
        currentVolume = prefs.getFloat("volume", 0.5f)
        isMusicEnabled = prefs.getBoolean("music_enabled", true)
    }
}