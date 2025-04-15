package com.example.walkiepaws.main_game

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkiepaws.R
import com.google.android.material.materialswitch.MaterialSwitch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        MusicManager.getInstance(this).initialize(this)

        val imageCharacter = findViewById<ImageView>(R.id.imageCharacter)
        updateCharacter(imageCharacter)

        findViewById<ImageView>(R.id.imageViewLeftButton).setOnClickListener {
            changeCharacter(-1, imageCharacter)
        }

        findViewById<ImageView>(R.id.imageViewRightButton).setOnClickListener {
            changeCharacter(1, imageCharacter)
        }

        findViewById<ImageView>(R.id.imageBack).setOnClickListener {
            finish()
        }

        setupMusicControls()
        setupSoundControls()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupMusicControls() {
        val musicManager = MusicManager.getInstance(this)
        val seekBarMusic = findViewById<SeekBar>(R.id.seekBarMusic)

        seekBarMusic.progress = (musicManager.getCurrentVolume() * 10).toInt()

        seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 10f
                musicManager.setVolume(volume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupSoundControls() {
        val musicManager = MusicManager.getInstance(this)
        val switchSounds = findViewById<MaterialSwitch>(R.id.material_switchSound)

        switchSounds.isChecked = musicManager.areSoundsEnabled()

        switchSounds.setOnCheckedChangeListener { _, isChecked ->
            musicManager.setSoundsEnabled(isChecked)
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