package com.example.walkiepaws

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class BedroomFragment : Fragment() {
    private lateinit var characterImage: ImageView
    private lateinit var shopButton: ImageView
    private lateinit var customizeButton: ImageView
    private lateinit var sleepButton: ImageView
    private lateinit var imageBlanket: ImageView
    private lateinit var mainLayout: RelativeLayout

    private val handler = Handler(Looper.getMainLooper())

    private var isSleeping = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DataManager.updateRoomsTemplates()
        handler.post(updateRoom)
        val view = inflater.inflate(R.layout.activity_bedroom_game_screen, container, false)
        characterImage = view.findViewById(R.id.imageCharacter)
        shopButton = view.findViewById(R.id.button_shop)
        customizeButton = view.findViewById(R.id.button_customize)
        sleepButton = view.findViewById(R.id.button_sleep)

        mainLayout = view.findViewById(R.id.main)
        imageBlanket = view.findViewById(R.id.imageTable)

        resetCharacterState()

        shopButton.setOnClickListener {
            resetSleepState()
            openShop()
        }

        customizeButton.setOnClickListener {
            resetSleepState()
            openCustomization()
        }

        sleepButton.setOnClickListener { toggleSleepState() }
        return view
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

    override fun onPause() {
        super.onPause()
        resetSleepState()
        hideCharacterImmediately()
    }

    private fun resetCharacterState() {
        if (::characterImage.isInitialized) {
            characterImage.visibility = View.INVISIBLE
            characterImage.alpha = 0f
            isSleeping = false
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
        if (::characterImage.isInitialized && isVisible) {
            updateCharacterImage()
            characterImage.apply {
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
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

            if (characterImage.id != resId) {
                Glide.with(this)
                    .asDrawable()
                    .load(resId)
                    .into(characterImage)
                Glide.with(this)
                    .load(resId)
                    .into(characterImage)
            }
            } catch (e: Exception) {
                Log.e("BedroomFragment", "Error loading character image", e)
            }
        }
    }

    val updateRoom = Runnable {
        imageBlanket.setImageResource(DataManager.rooms["bedroom"]?.getOrNull(1) ?: 0)
        mainLayout.setBackgroundResource(DataManager.rooms["bedroom"]?.getOrNull(0) ?: 0)
    }

    private fun openShop() {
        val intent = Intent(activity, ShopActivity::class.java)
        startActivity(intent)
    }

    private fun openCustomization() {
        val intent = Intent(activity, CustomizationActivity::class.java)
        startActivity(intent)
    }

    companion object {
        fun newInstance() = BedroomFragment()
    }
}