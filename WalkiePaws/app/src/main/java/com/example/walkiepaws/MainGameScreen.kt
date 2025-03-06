package com.example.walkiepaws

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.imageview.ShapeableImageView

class MainGameScreen : AppCompatActivity() {
    private lateinit var hungerBar: ProgressBar
    private lateinit var sleepBar: ProgressBar
    private lateinit var happyBar: ProgressBar
    private var hungerLevel = 100
    private var sleepLevel = 100
    private var happyLevel = 100

    private val handler = Handler(Looper.getMainLooper())
    private val decreaseHungerRunnable = object : Runnable {
        override fun run() {
            if (hungerLevel > 0) {
                hungerLevel -= 15
                hungerBar.progress = hungerLevel
                handler.postDelayed(this, 7000)
            }
        }
    }
    private val decreaseSleepRunnable = object : Runnable {
        override fun run() {
            if (sleepLevel > 0) {
                sleepLevel -= 10
                sleepBar.progress = sleepLevel
                handler.postDelayed(this, 7000) // Уменьшение каждые 7 секунд
            }
        }
    }
    private val decreaseHappyRunnable = object : Runnable {
        override fun run() {
            if (happyLevel > 0) {
                happyLevel -= 5
                happyBar.progress = happyLevel
                handler.postDelayed(this, 7000) // Уменьшение каждые 6 секунд
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_game_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        hungerBar = findViewById(R.id.hungerBar)
        sleepBar = findViewById(R.id.sleepBar)
        happyBar = findViewById(R.id.happyBar)

        handler.postDelayed(decreaseHungerRunnable, 500)
        handler.postDelayed(decreaseSleepRunnable, 500)
        handler.postDelayed(decreaseHappyRunnable, 500)

        val imageView: ShapeableImageView = findViewById(R.id.button_game)
        imageView.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(decreaseHungerRunnable)
        handler.removeCallbacks(decreaseSleepRunnable)
        handler.removeCallbacks(decreaseHappyRunnable)


    }


}
