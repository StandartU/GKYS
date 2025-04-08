package com.example.walkiepaws

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.model.dto.responce.LoginDTO
import com.google.android.material.imageview.ShapeableImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.Utils
import com.example.walkiepaws.backend.model.dto.responce.CashDTO
import com.example.walkiepaws.backend.model.dto.responce.UserStateDTO

class MainGameScreen : AppCompatActivity() {
    private lateinit var hungerBar: ProgressBar
    private lateinit var sleepBar: ProgressBar
    private lateinit var happyBar: ProgressBar
    private lateinit var viewPager: ViewPager2
    private lateinit var textSteps: TextView
    private lateinit var apiService: ApiService

    private var hungerLevel = 100
    private var sleepLevel = 100
    private var happyLevel = 100

    private val handler = Handler(Looper.getMainLooper())

    private val updateBars = object : Runnable {
        override fun run() {
            textSteps = findViewById(R.id.number_of_steps)
            apiService.getState("${(applicationContext as App).token}")
                .enqueue(object : Callback<UserStateDTO> {
                    override fun onResponse(call: Call<UserStateDTO>, response: Response<UserStateDTO>) {
                        if (response.isSuccessful) {
                            val userStateDTO = response.body()
                            val userStates = userStateDTO?.userStates

                            (userStates ?: emptyList()).forEach { userState ->
                                if (userState.state.name.equals("hunger")) {hungerLevel = userState.value}
                                else if (userState.state.name.equals("sleep")) {sleepLevel = userState.value}
                                else if (userState.state.name.equals("happiness")) {happyLevel = userState.value}
                            }

                            hungerBar.progress = hungerLevel
                            sleepBar.progress = sleepLevel
                            happyBar.progress = happyLevel
                            updateProgressBarStyle(sleepBar, sleepLevel)
                            updateProgressBarStyle(hungerBar, hungerLevel)
                            updateProgressBarStyle(happyBar, happyLevel)

                        } else {
                            Log.e("DEBUG", "Error: ${response.code()} ${(applicationContext as App).token}")
                        }
                    }

                    override fun onFailure(call: Call<UserStateDTO>, t: Throwable) {
                        Log.e("DEBUG", "Failure: ${t.message}")
                    }
                })

            apiService.getCash((applicationContext as App).token).enqueue(object: Callback<CashDTO> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<CashDTO>, response: Response<CashDTO>) {
                    textSteps.text = response.body()?.cash.toString();
                }

                override fun onFailure(call: Call<CashDTO>, t: Throwable) {
                    Log.e("DEBUG", "Failure: ${t.message}")
                }

            })

            DataManager.updateCharList()
            val currentFragment = supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")
            when (currentFragment) {
                is KitchenFragment -> currentFragment.showCharacterSmoothly()
                is LivingRoomFragment -> currentFragment.showCharacterSmoothly()
                is BedroomFragment -> currentFragment.showCharacterSmoothly()
            }
            handler.postDelayed(this, 10000)
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

        apiService = RetrofitClient.getApiService()
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

        findViewById<ShapeableImageView>(R.id.buttonTasks).setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
        }

        hungerBar = findViewById(R.id.hungerBar)
        sleepBar = findViewById(R.id.sleepBar)
        happyBar = findViewById(R.id.happyBar)

        handler.post(updateBars)
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

    fun onCustomizationItemSelected(itemName: String) {
        Toast.makeText(this, "Выбрано: $itemName", Toast.LENGTH_SHORT).show()
        
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}