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
    private lateinit var viewPager: ViewPager2

    private var hungerLevel = 100
    private var sleepLevel = 100
    private var happyLevel = 100

    private val handler = Handler(Looper.getMainLooper())

    private val decreaseHungerRunnable = object : Runnable {
        override fun run() {
            if (hungerLevel > 0) {
                hungerLevel -= 1
                hungerBar.progress = hungerLevel
                updateProgressBarStyle(hungerBar, hungerLevel)
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val decreaseSleepRunnable = object : Runnable {
        override fun run() {
            if (sleepLevel > 0) {
                sleepLevel -= 1
                sleepBar.progress = sleepLevel
                updateProgressBarStyle(sleepBar, sleepLevel)
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val decreaseHappyRunnable = object : Runnable {
        override fun run() {
            if (happyLevel > 0) {
                happyLevel -= 1
                happyBar.progress = happyLevel
                updateProgressBarStyle(happyBar, happyLevel)
            }
            handler.postDelayed(this, 1000)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateProgressBarStyle(progressBar: ProgressBar, value: Int) {
        when {
            value >= 70 -> progressBar.progressDrawable = getDrawable(R.drawable.progress_green)
            value >= 20 -> progressBar.progressDrawable = getDrawable(R.drawable.progress_yellow)
            else -> progressBar.progressDrawable = getDrawable(R.drawable.progress_red)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_game_screen)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ScreenSlidePagerAdapter(this)
        viewPager.setCurrentItem(1, false)
        viewPager.offscreenPageLimit = 1

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> hideActiveCharacter()
                    ViewPager2.SCROLL_STATE_IDLE -> updateCurrentCharacter()
                    ViewPager2.SCROLL_STATE_SETTLING -> {
                    }
                }
            }
        })

        findViewById<ShapeableImageView>(R.id.buttonStatistics).setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        findViewById<ShapeableImageView>(R.id.buttonSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        hungerBar = findViewById(R.id.hungerBar)
        sleepBar = findViewById(R.id.sleepBar)
        happyBar = findViewById(R.id.happyBar)

        handler.postDelayed(decreaseHungerRunnable, 1000)
        handler.postDelayed(decreaseSleepRunnable, 1000)
        handler.postDelayed(decreaseHappyRunnable, 1000)
    }

    private fun hideActiveCharacter() {
        supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")?.let { currentFragment ->
            when (currentFragment) {
                is KitchenFragment -> currentFragment.hideCharacterImmediately()
                is LivingRoomFragment -> currentFragment.hideCharacterImmediately()
                is BedroomFragment -> currentFragment.hideCharacterImmediately()
            }
        }
    }

    private fun updateCurrentCharacter() {
        supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")?.let { currentFragment ->
            when (currentFragment) {
                is KitchenFragment -> currentFragment.showCharacterSmoothly()
                is LivingRoomFragment -> currentFragment.showCharacterSmoothly()
                is BedroomFragment -> currentFragment.showCharacterSmoothly()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(decreaseHungerRunnable)
        handler.removeCallbacks(decreaseSleepRunnable)
        handler.removeCallbacks(decreaseHappyRunnable)
    }
}