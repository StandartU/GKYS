package com.example.walkiepaws.main_game

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import com.bumptech.glide.Glide

import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.walkiepaws.R
import java.util.concurrent.TimeUnit


class BedroomFragment : Fragment() {
    private lateinit var characterImage: ImageView
    private lateinit var shopButton: ImageView
    private lateinit var customizeButton: ImageView
    private lateinit var sleepButton: ImageView
    private lateinit var imageBlanket: ImageView
    private lateinit var mainLayout: RelativeLayout

    private val handler = Handler(Looper.getMainLooper())

    private var _isSleeping: Boolean = false

    private var isVisibleToUser: Boolean = true

    var isSleeping: Boolean
        get() = _isSleeping
        set(value) {
            if (_isSleeping != value) {
                _isSleeping = value
                val sharedPreferences = requireContext().getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("is_sleeping", value)
                editor.apply()
                onSleepStatusChanged(value)
                if (value) {
                    MusicManager.getInstance(requireContext()).playSleepSound()
                }
            }
        }

    private fun onSleepStatusChanged(isSleeping: Boolean) {
        if (isSleeping) {
            startSleepTracking()
        } else {
            stopSleepTracking()
        }
    }

    private fun startSleepTracking() {
        val workRequest = OneTimeWorkRequest.Builder(SleepDataUploadWorker::class.java)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .addTag("sleep_tracking")
            .build()


        WorkManager.getInstance(requireContext()).enqueue(workRequest)
        Log.d("SleepTracking", "Started one-time sleep tracking worker")
    }

    private fun stopSleepTracking() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag("sleep_tracking")
        Log.d("SleepTracking", "Stopped sleep tracking worker")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DataManager.updateRoomsTemplates()
        val sharedPreferences = requireContext().getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        isSleeping = sharedPreferences.getBoolean("is_sleeping", false)
        handler.postDelayed(updateRoom, 1000)
        val view = inflater.inflate(R.layout.activity_bedroom_game_screen, container, false)

        initViews(view)
        setupClickListeners()
        restoreState(savedInstanceState)

        return view
    }

    override fun onResume() {
        super.onResume()
        isVisibleToUser = true
        resetCharacterState()
        showCharacterSmoothly()
    }

    override fun onPause() {
        super.onPause()
        hideCharacterImmediately()
        isVisibleToUser = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean("IS_SLEEPING", isSleeping)
            putBoolean("IS_VISIBLE", characterImage.visibility == View.VISIBLE)
        }
    }

    private fun initViews(view: View) {
        characterImage = view.findViewById(R.id.imageCharacter)
        shopButton = view.findViewById(R.id.button_shop)
        customizeButton = view.findViewById(R.id.button_customize)
        sleepButton = view.findViewById(R.id.button_sleep)
        mainLayout = view.findViewById(R.id.main)
        imageBlanket = view.findViewById(R.id.imageBlanket)
    }

    private fun setupClickListeners() {
        shopButton.setOnClickListener {
            resetSleepState()
            openShop()
        }

        customizeButton.setOnClickListener {
            resetSleepState()
            openCustomization()
        }

        sleepButton.setOnClickListener { toggleSleepState() }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            isSleeping = savedInstanceState.getBoolean("IS_SLEEPING", false)
            val isVisible = savedInstanceState.getBoolean("IS_VISIBLE", true)
        }
    }

    private fun resetSleepState() {
        if (isSleeping) {
            isSleeping = false
        }
    }

    private fun toggleSleepState() {
        isSleeping = !isSleeping
        showCharacterSmoothly()
    }

    private fun resetCharacterState() {
        characterImage.visibility = View.INVISIBLE
        characterImage.alpha = 0f
    }

    fun hideCharacterImmediately() {
        characterImage.animate().cancel()
        characterImage.visibility = View.INVISIBLE
        characterImage.alpha = 0f
    }

    fun showCharacterSmoothly() {
        Log.d("КОМНАТА СЛИП", "ПОКАЗ")
        val resId = if (isSleeping) {
            DataManager.getCurrentSleepingCharacter()
        } else {
            DataManager.getChars()[DataManager.currentCharacterIndex]
        }

        fun animateAppearance(view: ImageView, resId: Int) {
            if (resId != 0) {
                view.alpha = 0f
                view.visibility = View.VISIBLE

                Glide.with(this)
                    .load(resId)
                    .into(view)

                view.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            } else {
                view.visibility = View.INVISIBLE
            }
        }

        animateAppearance(characterImage, resId)
    }

    private val updateRoom = Runnable {
        imageBlanket.setImageResource(DataManager.rooms["bedroom"]?.getOrNull(1) ?: 0)
        mainLayout.setBackgroundResource(DataManager.rooms["bedroom"]?.getOrNull(0) ?: 0)
    }

    private fun openShop() {
        startActivity(Intent(activity, ShopActivity::class.java))
    }

    private fun openCustomization() {
        startActivity(Intent(activity, CustomizationActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRoom)
    }

    companion object {
        fun newInstance() = BedroomFragment()
    }
}