package com.example.walkiepaws

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Инициализация музыки
        MusicManager.getInstance(this).initialize(this)

        val imageCharacter = findViewById<ImageView>(R.id.imageCharacter)
        updateCharacter(imageCharacter)

        // Обработчики для кнопок персонажа
        findViewById<ImageView>(R.id.imageViewLeftButton).setOnClickListener {
            changeCharacter(-1, imageCharacter)
        }

        findViewById<ImageView>(R.id.imageViewRightButton).setOnClickListener {
            changeCharacter(1, imageCharacter)
        }

        // Кнопка назад
        findViewById<ImageView>(R.id.imageBack).setOnClickListener { finish() }

        // Настройка управления музыкой
        setupMusicControls()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupMusicControls() {
        val musicManager = MusicManager.getInstance(this)
        val seekBarMusic = findViewById<SeekBar>(R.id.seekBarMusic)
        val switchSound = findViewById<MaterialSwitch>(R.id.material_switchSound)

        // Установка начальных значений
        seekBarMusic.progress = (musicManager.getCurrentVolume() * 10).toInt()
        switchSound.isChecked = musicManager.isMusicEnabled()

        // Обработчик изменения громкости
        seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 10f
                musicManager.setVolume(volume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Обработчик включения/выключения музыки
        switchSound.setOnCheckedChangeListener { _, isChecked ->
            musicManager.setMusicEnabled(isChecked)
        }
    }

    private fun changeCharacter(direction: Int, imageView: ImageView) {
        var newIndex = DataManager.currentCharacterIndex + direction

        when {
            newIndex < 0 -> newIndex = DataManager.characters.size - 1
            newIndex >= DataManager.characters.size -> newIndex = 0
        }

        DataManager.currentCharacterIndex = newIndex
        updateCharacter(imageView)
    }

    private fun updateCharacter(imageView: ImageView) {
        imageView.setImageResource(DataManager.characters[DataManager.currentCharacterIndex])
    }

    override fun onResume() {
        super.onResume()
        MusicManager.getInstance(this).play()
    }

    override fun onPause() {
        super.onPause()
        MusicManager.getInstance(this).pause()
    }
}