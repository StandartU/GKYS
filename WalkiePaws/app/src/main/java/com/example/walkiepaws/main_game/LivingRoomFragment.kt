package com.example.walkiepaws.main_game

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

        initViews(view)
        setupClickListeners()

        return view
    }

    private fun initViews(view: View) {
        characterImage = view.findViewById(R.id.imageCharacter)
        hatImage = view.findViewById(R.id.hatImage)
        topImage = view.findViewById(R.id.topImage)
        accessoryImage = view.findViewById(R.id.accessoryImage)

        mainLayout = view.findViewById(R.id.main)
        shopButton = view.findViewById(R.id.button_shop)
        gameButton = view.findViewById(R.id.button_game)
        customizeButton = view.findViewById(R.id.button_customize)
    }

    private fun setupClickListeners() {
        shopButton.setOnClickListener { openShop() }
        gameButton.setOnClickListener { startGame() }
        customizeButton.setOnClickListener { openCustomization() }
    }

    override fun onResume() {
        super.onResume()
        DataManager.updateRoomsTemplates()
        resetCharacterState()
        showCharacterSmoothly()
    }

    override fun onPause() {
        hideCharacterImmediately()
        super.onPause()
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

    fun showCharacterSmoothly() {
        Log.d("КОМНАТА ЛИВИНГ", "ПОКАЗ")
        val items = DataManager.getCharacterWithItems()

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
        if (items.isNotEmpty()) animateAppearance(characterImage, items[0])
        if (items.size > 1) animateAppearance(hatImage, items[1])
        if (items.size > 2) animateAppearance(topImage, items[2])
        if (items.size > 3) animateAppearance(accessoryImage, items[3])
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
        val intent = Intent(activity, MiniGameActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRoom)
    }

    companion object {
        fun newInstance() = LivingRoomFragment()
    }
}
