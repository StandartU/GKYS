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
import androidx.viewpager2.widget.ViewPager2
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
    private lateinit var hatImage: ImageView
    private lateinit var topImage: ImageView
    private lateinit var accessoryImage: ImageView
    private lateinit var characterSleeping: ImageView


    private val handler = Handler(Looper.getMainLooper())

    var isSleeping: Boolean = false

    private fun onSleepStatusChanged() {
        if (isSleeping) {
            startSleepTracking()
        } else {
            stopSleepTracking()
        }
    }

    private fun getSleepState(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("game_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_sleeping", false)
    }

    @SuppressLint("CommitPrefEdits")
    private fun setSleepState(bool: Boolean) {
        val editor = requireContext().getSharedPreferences("game_preferences", Context.MODE_PRIVATE).edit()
        editor.putBoolean("is_sleeping", bool).apply()
        Log.d("PUTEDDD", bool.toString())
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
        Log.d("PUTEDDD IN", isSleeping.toString())
        handler.postDelayed(updateRoom, 1000)
        val view = inflater.inflate(R.layout.activity_bedroom_game_screen, container, false)

        initViews(view)
        setupClickListeners()
        restoreState()

        return view
    }

    override fun onResume() {
        super.onResume()
        showCharacterSmoothly()
    }

    override fun onPause() {
        super.onPause()
        isSleeping = false
        hideCharacterImmediately()
    }

    private fun initViews(view: View) {
        characterImage = view.findViewById(R.id.imageCharacterBedroom)
        shopButton = view.findViewById(R.id.button_shop)
        customizeButton = view.findViewById(R.id.button_customize)
        sleepButton = view.findViewById(R.id.button_sleep)
        mainLayout = view.findViewById(R.id.main)
        imageBlanket = view.findViewById(R.id.imageBlanket)

        hatImage = view.findViewById(R.id.hatImageBedroom)
        topImage = view.findViewById(R.id.topImageBedroom)
        accessoryImage = view.findViewById(R.id.accessoryImageBedroom)

        characterSleeping = view.findViewById(R.id.imageCharacterSleep)
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

    private fun restoreState() {
        isSleeping = getSleepState()
    }

    private fun resetSleepState() {
        if (isSleeping) {
            isSleeping = false
            setSleepState(false)
            onSleepStatusChanged()
            showCharacterSmoothly()
        }
    }

    private fun toggleSleepState() {
        isSleeping = !isSleeping
        setSleepState(isSleeping)
        onSleepStatusChanged()
        showCharacterSmoothly()
    }

    private fun resetCharacterState() {
        resetView(characterImage)
        resetView(hatImage)
        resetView(topImage)
        resetView(accessoryImage)
    }

    private fun resetView(view: ImageView) {
        view.visibility = View.INVISIBLE
        view.alpha = 0f
    }

    internal fun hideCharacterImmediately() {
        hideViewImmediately(characterImage)
        hideViewImmediately(hatImage)
        hideViewImmediately(topImage)
        hideViewImmediately(accessoryImage)
    }

    private fun hideViewImmediately(view: ImageView) {
        view.animate().cancel()
        view.visibility = View.INVISIBLE
        view.alpha = 0f
    }

    private fun animateAppearance(view: ImageView, resId: Int) {
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

    fun showCharacterSmoothly() {
        animateAppearance(characterSleeping, DataManager.getCurrentSleepingCharacter())
        val items = DataManager.getCharacterWithItems()

        if (!isSleeping) {
            startSleep(items)
        } else {
            hideSleep()
        }
    }

    private fun hideSleep() {
        characterSleeping.translationX = 0f
        hatImage.translationX = -1000f
        topImage.translationX = -1000f
        accessoryImage.translationX = -1000f
        characterImage.translationX = -1000f
    }

    private fun startSleep(items: List<Int>) {
        hatImage.translationX = 0f
        topImage.translationX = 0f
        accessoryImage.translationX = 0f
        characterImage.translationX = 0f
        characterSleeping.translationX = -1000f
        if (items.isNotEmpty()) animateAppearance(characterImage, items[0])
        if (items.size > 1) animateAppearance(hatImage, items[1])
        if (items.size > 2) animateAppearance(topImage, items[2])
        if (items.size > 3) animateAppearance(accessoryImage, items[3])
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