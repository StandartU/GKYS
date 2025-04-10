package com.example.walkiepaws.main_game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.walkiepaws.R

class LivingRoomFragment : Fragment() {
    private lateinit var characterImage: ImageView
    private lateinit var hatImage: ImageView
    private lateinit var topImage: ImageView
    private lateinit var accessoryImage: ImageView
    private lateinit var shopButton: ImageView
    private lateinit var gameButton: ImageView
    private lateinit var customizeButton: ImageView
    private lateinit var mainLayout: RelativeLayout

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        DataManager.updateRoomsTemplates()
        handler.postDelayed(updateRoom, 1000)
        val view = inflater.inflate(R.layout.activity_living_game_screen, container, false)

        characterImage = view.findViewById(R.id.imageCharacter)
        hatImage = view.findViewById(R.id.hatImage)
        topImage = view.findViewById(R.id.topImage)
        accessoryImage = view.findViewById(R.id.accessoryImage)
        shopButton = view.findViewById(R.id.button_shop)
        gameButton = view.findViewById(R.id.button_game)
        customizeButton = view.findViewById(R.id.button_customize)
        mainLayout = view.findViewById(R.id.main)

        resetCharacterState()

        shopButton.setOnClickListener { openShop() }
        gameButton.setOnClickListener { startGame() }
        customizeButton.setOnClickListener { openCustomization() }
        return view
    }

    override fun onResume() {
        super.onResume()
        showCharacterSmoothly()
    }

    override fun onPause() {
        hideCharacterImmediately()
        super.onPause()
    }

    private fun resetCharacterState() {
        if (::characterImage.isInitialized) {
            characterImage.visibility = View.INVISIBLE
            characterImage.alpha = 0f
            hatImage.visibility = View.INVISIBLE
            hatImage.alpha = 0f
            topImage.visibility = View.INVISIBLE
            topImage.alpha = 0f
            accessoryImage.visibility = View.INVISIBLE
            accessoryImage.alpha = 0f
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
                animate().alpha(1f).setDuration(400).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
            hatImage.apply {
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(400).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
            topImage.apply {
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(400).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
            accessoryImage.apply {
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(400).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
        }
    }

    private fun updateCharacterImage() {
        val items = DataManager.getCharacterWithItems(DataManager.currentCharacterIndex)

        // Загружаем базового персонажа (WebP)
        Glide.with(this)
            .load(items[0]) // Предполагается, что это WebP из res/raw
            .into(characterImage)

        // Сбрасываем предметы
        hatImage.setImageDrawable(null)
        topImage.setImageDrawable(null)
        accessoryImage.setImageDrawable(null)

        // Загружаем WebP для предметов
        if (items.size > 1 && items[1] != 0) {
            Glide.with(this).load(items[1]).into(hatImage)
        }
        if (items.size > 2 && items[2] != 0) {
            Glide.with(this).load(items[2]).into(topImage)
        }
        if (items.size > 3 && items[3] != 0) {
            Glide.with(this).load(items[3]).into(accessoryImage)
        }
    }

    val updateRoom = Runnable {
        mainLayout.setBackgroundResource(DataManager.rooms["living"]?.get(0) ?: 0)
    }

    private fun openShop() {
        val intent = Intent(activity, ShopActivity::class.java)
        startActivity(intent)
    }

    private fun openCustomization() {
        val intent = Intent(activity, CustomizationActivity::class.java)
        startActivity(intent)
    }

    private fun startGame() {
        DataManager.games[DataManager.currentCharacterIndex]()
    }

    companion object {
        fun newInstance() = LivingRoomFragment()
    }
}
