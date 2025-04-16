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
        restoreState(savedInstanceState, isSleeping)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Задержка для плавного появления персонажа
        handler.postDelayed({
            if (isVisibleToUser) {
                showCharacterSmoothly()
            }
        }, 300)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser && isAdded) {
            showCharacterSmoothly()
        }
    }

    override fun onResume() {
        super.onResume()
        isVisibleToUser = true
        showCharacterSmoothly()
    }

    override fun onPause() {
        super.onPause()
        resetCharacterState(false)
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

    private fun restoreState(savedInstanceState: Bundle?, isOpen: Boolean) {
        if (savedInstanceState != null) {
            isSleeping = savedInstanceState.getBoolean("IS_SLEEPING", false)
            val isVisible = savedInstanceState.getBoolean("IS_VISIBLE", true)
            if (!isVisible) {
                characterImage.visibility = View.INVISIBLE
                characterImage.alpha = 0f
            }
        } else {
            resetCharacterState(isOpen)
        }
    }

    private fun resetSleepState() {
        if (isSleeping) {
            isSleeping = false
            updateCharacterImage()
            characterImage.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start()
        }
    }

    private fun toggleSleepState() {
        isSleeping = !isSleeping
        updateCharacterImage()

        characterImage.animate()
            .scaleX(if (isSleeping) 0.9f else 1f)
            .scaleY(if (isSleeping) 0.9f else 1f)
            .setDuration(300)
            .start()
    }

    private fun resetCharacterState(isOpen: Boolean) {
        if (::characterImage.isInitialized) {
            characterImage.visibility = View.INVISIBLE
            characterImage.alpha = 0f
            if (!isOpen){
                isSleeping = false
            }
            characterImage.scaleX = 1f
            characterImage.scaleY = 1f
        }
    }

    fun hideCharacterImmediately() {
        if (::characterImage.isInitialized) {
            characterImage.animate().cancel()
            characterImage.visibility = View.INVISIBLE
            characterImage.alpha = 0f
        }
    }

    fun showCharacterSmoothly() {
        if (::characterImage.isInitialized && isVisibleToUser && isAdded) {
            updateCharacterImage()
            characterImage.apply {
                if (visibility != View.VISIBLE) {
                    visibility = View.VISIBLE
                    alpha = 0f
                    animate()
                        .alpha(1f)
                        .setDuration(400)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()
                }
            }
        }
    }

    private fun updateCharacterImage() {
        if (::characterImage.isInitialized) {
            try {
                val resId = if (isSleeping) {
                    DataManager.getCurrentSleepingCharacter()
                } else {
                    DataManager.getChars()[DataManager.currentCharacterIndex]
                }

                if (characterImage.tag != resId) {
                    characterImage.tag = resId
                    Glide.with(this)
                        .load(resId)
                        .into(characterImage)
                }
            } catch (e: Exception) {
                Log.e("BedroomFragment", "Error loading character image", e)
            }
        }
    }

    private val updateRoom = Runnable {
        if (isAdded) {
            imageBlanket.setImageResource(DataManager.rooms["bedroom"]?.getOrNull(1) ?: 0)
            mainLayout.setBackgroundResource(DataManager.rooms["bedroom"]?.getOrNull(0) ?: 0)
        }
    }

    private fun openShop() {
        startActivity(Intent(activity, ShopActivity::class.java))
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun openCustomization() {
        startActivity(Intent(activity, CustomizationActivity::class.java))
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRoom)
    }

    companion object {
        fun newInstance() = BedroomFragment()
    }
}