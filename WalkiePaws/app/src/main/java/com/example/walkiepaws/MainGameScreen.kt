package com.example.walkiepaws


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.imageview.ShapeableImageView


class MainGameScreen : AppCompatActivity() {
    private lateinit var hungerBar: ProgressBar
    private lateinit var sleepBar: ProgressBar
    private lateinit var happyBar: ProgressBar
    private var hungerLevel = 100
    private var sleepLevel = 100
    private var happyLevel = 100

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateProgressBarStyle(progressBar: ProgressBar, value: Int) {
        when {
            value >= 70 -> {
                progressBar.progressDrawable = getDrawable(R.drawable.progress_green)
            }

            value >= 20 -> {
                progressBar.progressDrawable = getDrawable(R.drawable.progress_yellow)
            }

            else -> {
                progressBar.progressDrawable = getDrawable(R.drawable.progress_red)
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val decreaseHungerRunnable = object : Runnable {
        override fun run() {
            if (hungerLevel > 0) {
                hungerLevel -= 10
                hungerBar.progress = hungerLevel
                updateProgressBarStyle(hungerBar, hungerLevel)
                handler.postDelayed(this, 500)
            }
        }
    }

    private val decreaseSleepRunnable = object : Runnable {
        override fun run() {
            if (sleepLevel > 0) {
                sleepLevel -= 10
                sleepBar.progress = sleepLevel
                updateProgressBarStyle(sleepBar, sleepLevel)
                handler.postDelayed(this, 500)
            }
        }
    }

    private val decreaseHappyRunnable = object : Runnable {
        override fun run() {
            if (happyLevel > 0) {
                happyLevel -= 10
                happyBar.progress = happyLevel
                updateProgressBarStyle(happyBar, happyLevel)
                handler.postDelayed(this, 500)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_game_screen)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = ScreenSlidePagerAdapter(this)
        viewPager.setCurrentItem(1, false)


        val buttonStatistics = findViewById<ShapeableImageView>(R.id.buttonStatistics)

        val buttonSettings = findViewById<ShapeableImageView>(R.id.buttonSettings)

        buttonStatistics.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        hungerBar = findViewById(R.id.hungerBar)
        sleepBar = findViewById(R.id.sleepBar)
        happyBar = findViewById(R.id.happyBar)

        handler.postDelayed(decreaseHungerRunnable, 500)
        handler.postDelayed(decreaseSleepRunnable, 500)
        handler.postDelayed(decreaseHappyRunnable, 500)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(decreaseHungerRunnable)
        handler.removeCallbacks(decreaseSleepRunnable)
        handler.removeCallbacks(decreaseHappyRunnable)


    }


}
