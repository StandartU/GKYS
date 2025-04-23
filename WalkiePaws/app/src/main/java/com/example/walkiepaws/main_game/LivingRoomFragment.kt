package com.example.walkiepaws.main_game

import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
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
        handler.post(updateRoom)
        val view = inflater.inflate(R.layout.activity_living_game_screen, container, false)

        initViews(view)
        setupClickListeners()

        return view
    }

    private fun initViews(view: View) {
        characterImage = view.findViewById(R.id.imageCharacterLiving)
        hatImage = view.findViewById(R.id.hatImageLiving)
        topImage = view.findViewById(R.id.topImageLiving)
        accessoryImage = view.findViewById(R.id.accessoryImageLiving)

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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        DataManager.updateRoomsTemplates()
        showCharacterSmoothly()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()
        DataManager.updateRoomsTemplates()
        showCharacterSmoothly()
    }

    override fun onPause() {
        super.onPause()
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

    @RequiresApi(Build.VERSION_CODES.P)
    fun playLoopedAnimations(drawables: List<AnimatedImageDrawable>) {
        drawables.forEach { it.clearAnimationCallbacks() }

        drawables.firstOrNull()?.let { mainDrawable ->
            mainDrawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    drawables.forEach {
                        it.stop()
                        it.start()
                    }
                }
            })
        }

        drawables.forEach { it.start() }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun showCharacterSmoothly() {
        val items = DataManager.getCharacterWithItems()

        val drawables = listOfNotNull(
            items.getOrNull(0)?.let { animateAppearance(characterImage, it) },
            items.getOrNull(1)?.let { animateAppearance(hatImage, it) },
            items.getOrNull(2)?.let { animateAppearance(topImage, it) },
            items.getOrNull(3)?.let { animateAppearance(accessoryImage, it) }
        )

        playLoopedAnimations(drawables)
    }

    val updateRoom = object : Runnable {
        override fun run() {
            mainLayout.setBackgroundResource(DataManager.rooms["living"]?.get(0) ?: 0)
            handler.postDelayed(this, 1000)
        }
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
