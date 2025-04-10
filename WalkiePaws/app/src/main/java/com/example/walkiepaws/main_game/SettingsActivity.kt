package com.example.walkiepaws.main_game

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkiepaws.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val imageCharacter = findViewById<ImageView>(R.id.imageCharacter)
        updateCharacter(imageCharacter)

        findViewById<ImageView>(R.id.imageViewLeftButton).setOnClickListener {
            changeCharacter(-1, imageCharacter)
        }

        findViewById<ImageView>(R.id.imageViewRightButton).setOnClickListener {
            changeCharacter(1, imageCharacter)
        }

        findViewById<ImageView>(R.id.imageBack).setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
}
