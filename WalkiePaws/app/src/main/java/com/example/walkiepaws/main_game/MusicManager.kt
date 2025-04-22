package com.example.walkiepaws.main_game

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.walkiepaws.R

class MusicManager private constructor(context: Context) {
    // Основной плеер для главного меню
    private var mainPlayer: MediaPlayer? = null
    // Плеер для игр
    private var gamePlayer: MediaPlayer? = null
    // Плеер для звуков
    private var soundPlayer: MediaPlayer? = null

    private var currentVolume = 0.5f
    private var isMusicEnabled = true
    private var areSoundsEnabled = true

    private val prefs: SharedPreferences =
        context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)
    private val appContext: Context = context.applicationContext

    companion object {
        @Volatile private var instance: MusicManager? = null

        fun getInstance(context: Context): MusicManager {
            return instance ?: synchronized(this) {
                instance ?: MusicManager(context.applicationContext).also { instance = it }
            }
        }
    }

    // Инициализация главной музыки
    fun initialize(context: Context) {
        loadSettings()
        if (isMusicEnabled) {
            createMainPlayer()
        }
    }

    private fun createMainPlayer() {
        mainPlayer?.release()
        mainPlayer = MediaPlayer.create(appContext, R.raw.song_main_game).apply {
            isLooping = true
            setVolume(currentVolume, currentVolume)
            if (isMusicEnabled) start()
        }
    }

    // Обратная совместимость для кастомизации
    @Deprecated("Используйте playMainMusic() для главного меню или playGameMusic() для игр")
    fun play() {
        playMainMusic()
    }

    @Deprecated("Используйте pauseMainMusic() для главного меню или pauseGameMusic() для игр")
    fun pause() {
        pauseMainMusic()
    }

    @Deprecated("Используйте stopMainMusic() для главного меню или stopGameMusic() для игр")
    fun stop() {
        stopMainMusic()
    }

    // Управление основной музыкой
    fun playMainMusic() {
        if (isMusicEnabled) {
            if (mainPlayer == null) {
                createMainPlayer()
            } else if (!mainPlayer!!.isPlaying) {
                mainPlayer?.start()
            }
        }
    }

    fun pauseMainMusic() {
        mainPlayer?.pause()
    }

    fun stopMainMusic() {
        mainPlayer?.stop()
        mainPlayer?.release()
        mainPlayer = null
    }

    // Управление игровой музыкой
    fun playGameMusic(resId: Int) {
        if (!isMusicEnabled) return

        gamePlayer?.release()
        gamePlayer = MediaPlayer.create(appContext, resId).apply {
            isLooping = true
            setVolume(currentVolume, currentVolume)
            start()
        }
    }

    fun pauseGameMusic() {
        gamePlayer?.pause()
    }

    fun stopGameMusic() {
        gamePlayer?.stop()
        gamePlayer?.release()
        gamePlayer = null
    }

    // Звуковые эффекты
    fun playSleepSound() = playSoundEffect(R.raw.sleep)
    fun playEatSound() = playSoundEffect(R.raw.eat)
    fun playHappySound() = playSoundEffect(R.raw.happy)
    fun playThanksSound() = playSoundEffect(R.raw.thanks)
    fun playGapeSound() = playSoundEffect(R.raw.gape)
    fun playSadnessSound() = playSoundEffect(R.raw.sadness)
    fun playJumpSound() = playSoundEffect(R.raw.jump_cr)
    fun playLoseSound() = playSoundEffect(R.raw.lose_cr)

    private fun playSoundEffect(resId: Int) {
        if (!areSoundsEnabled) return

        soundPlayer?.release()
        soundPlayer = MediaPlayer.create(appContext, resId).apply {
            setVolume(1f, 1f)
            setOnCompletionListener { it.release() }
            start()
        }
    }

    // Настройки
    fun setVolume(volume: Float) {
        currentVolume = volume
        mainPlayer?.setVolume(volume, volume)
        gamePlayer?.setVolume(volume, volume)
        saveSettings()
    }

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            mainPlayer?.start()
            gamePlayer?.start()
        } else {
            mainPlayer?.pause()
            gamePlayer?.pause()
        }
        saveSettings()
    }

    fun setSoundsEnabled(enabled: Boolean) {
        areSoundsEnabled = enabled
        saveSettings()
    }

    // Геттеры
    fun areSoundsEnabled(): Boolean = areSoundsEnabled
    fun getCurrentVolume(): Float = currentVolume
    fun isMusicEnabled(): Boolean = isMusicEnabled

    private fun saveSettings() {
        prefs.edit().apply {
            putFloat("volume", currentVolume)
            putBoolean("music_enabled", isMusicEnabled)
            putBoolean("sounds_enabled", areSoundsEnabled)
            apply()
        }
    }

    private fun loadSettings() {
        currentVolume = prefs.getFloat("volume", 0.5f)
        isMusicEnabled = prefs.getBoolean("music_enabled", true)
        areSoundsEnabled = prefs.getBoolean("sounds_enabled", true)
    }
}