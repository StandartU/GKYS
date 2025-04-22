package com.example.walkiepaws.main_game

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
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

    // from main-dialog -> game-screen-activity
    @RequiresApi(Build.VERSION_CODES.P)
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

    // from fragment -> fragment
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        initCharsAnimations()
        startShowChar()
    }

    // from fragment -> fragment
    override fun onPause() {
        super.onPause()
        isSleeping = false

    }

    // from move through non-fragment-activity -> fragment
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()
        initCharsAnimations()
        startShowChar()
    }
    
    @RequiresApi(Build.VERSION_CODES.P)
    private fun startShowChar() {
        if (isSleeping) {
            startSleep()
        } else {
            hideSleep()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initCharsAnimations() {

        val items = DataManager.getCharacterWithItems()
        animateCharNotSleep(items)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun animateCharNotSleep(items: List<Int>) {

        val drawables = listOfNotNull(
            items.getOrNull(0)?.let { animateAppearance(characterImage, it) },
            items.getOrNull(1)?.let { animateAppearance(hatImage, it) },
            items.getOrNull(2)?.let { animateAppearance(topImage, it) },
            items.getOrNull(3)?.let { animateAppearance(accessoryImage, it) }
        )

        drawables.forEach { it.start() }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startSleep() {
        val resId = DataManager.getCurrentSleepingCharacter()
        animateAppearance(characterImage, resId)?.start()
        accessoryImage.setImageDrawable(null)
        topImage.setImageDrawable(null)
        hatImage.setImageDrawable(null)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun hideSleep() {
        initCharsAnimations()
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

    }

    @RequiresApi(Build.VERSION_CODES.P)
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
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun toggleSleepState() {
        isSleeping = !isSleeping
        setSleepState(isSleeping)
        onSleepStatusChanged()
        startShowChar()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun animateAppearance(view: ImageView, resId: Int): AnimatedImageDrawable? {
        if (resId != 0) {
            val drawable = ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(
                    requireContext().resources,
                    resId
                )
            ) as AnimatedImageDrawable
            view.setImageDrawable(drawable)
            return drawable
        }
        else return null
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