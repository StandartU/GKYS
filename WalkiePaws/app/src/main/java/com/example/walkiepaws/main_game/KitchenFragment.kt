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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.walkiepaws.R
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.dto.request.MarketBuyDTO
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random


class KitchenFragment : Fragment() {
    private lateinit var characterImage: ImageView
    private lateinit var shopButton: ShapeableImageView
    private lateinit var customizeButton: ShapeableImageView
    private lateinit var foodButton: ShapeableImageView
    private lateinit var imageTable: ImageView
    private lateinit var mainLayout: RelativeLayout
    private lateinit var hatImage: ImageView
    private lateinit var topImage: ImageView
    private lateinit var accessoryImage: ImageView

    private lateinit var foodPopup: View
    private lateinit var foodItemsContainer: LinearLayout
    private lateinit var closeFoodPopup: ImageView
    private lateinit var foodPopupTitle: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val random = Random.Default

    private lateinit var foodItems: List<DataManager.FoodItem>


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DataManager.updateRoomsTemplates()
        handler.post(updateRoom)
        val view = inflater.inflate(R.layout.activity_kitchen_game_screen, container, false)

        initViews(view)
        setupClickListeners()

        setupFoodItems()
        return view
    }

    private fun initViews(view: View) {
        characterImage = view.findViewById(R.id.imageCharacterKitchen)
        mainLayout = view.findViewById(R.id.main)
        imageTable = view.findViewById(R.id.imageTable)
        shopButton = view.findViewById(R.id.button_shop)
        customizeButton = view.findViewById(R.id.button_customize)
        foodButton = view.findViewById(R.id.button_food)

        hatImage = view.findViewById(R.id.hatImageKitchen)
        topImage = view.findViewById(R.id.topImageKitchen)
        accessoryImage = view.findViewById(R.id.accessoryImageKitchen)

        foodPopup = view.findViewById(R.id.foodPopup)
        foodPopup = view.findViewById(R.id.foodPopup)
        foodItemsContainer = view.findViewById(R.id.foodItemsScrollView)
        closeFoodPopup = view.findViewById(R.id.closeFoodPopup)

    }

    private fun setupClickListeners() {
        shopButton.setOnClickListener { openShop() }
        customizeButton.setOnClickListener { openCustomization() }
        foodButton.setOnClickListener { showFoodPopup() }
        closeFoodPopup.setOnClickListener { hideFoodPopup() }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupFoodItems() {
        val inflater = LayoutInflater.from(context)
        foodItemsContainer.removeAllViews()
        foodItems = DataManager.currentFoodItems

        foodItems.forEach { foodItem ->
            val foodView = inflater.inflate(R.layout.item_product, foodItemsContainer, false)

            foodView.findViewById<ImageView>(R.id.itemImage).setImageResource(foodItem.imageRes)
            foodView.findViewById<TextView>(R.id.itemName).text = foodItem.name
            foodView.findViewById<TextView>(R.id.itemPrice).text = foodItem.price

            foodView.setOnClickListener {
                useFoodItem(foodItem)
            }

            foodItemsContainer.addView(foodView)
        }
    }

    private fun showFoodPopup() {
        val location = IntArray(2)
        foodItems = DataManager.currentFoodItems
        foodButton.getLocationOnScreen(location)

        foodPopup.visibility = View.VISIBLE
        foodPopup.alpha = 0f
        foodPopup.animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    private fun hideFoodPopup() {
        foodPopup.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                foodPopup.visibility = View.GONE
            }
            .start()
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun useFoodItem(foodItem: DataManager.FoodItem) {
        RetrofitClient.getApiService().buyState((DataManager.appContext as App).token, MarketBuyDTO(foodItem.healthValue))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Недостаточно средств", Toast.LENGTH_SHORT).show()
                    } else {
                        MusicManager.getInstance(requireContext()).playEatSound()

                        val message = when (random.nextInt(3)) {
                            0 -> "${foodItem.name} съедено!"
                            else -> "Ням-ням!"
                        }
                        lifecycleScope.launch {
                            showEatingChar()
                            delay(3000)
                            showCharacterSmoothly()
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })

        DataManager.currentFoodItems.remove(foodItem)
        setupFoodItems()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        setupFoodItems()
        DataManager.updateRoomsTemplates()
        showCharacterSmoothly()
    }

    // from move through non-fragment-activity -> fragment
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()
        setupFoodItems()
        DataManager.updateRoomsTemplates()
        showCharacterSmoothly()
    }

    override fun onPause() {
        characterImage.setImageDrawable(null)
        topImage.setImageDrawable(null)
        accessoryImage.setImageDrawable(null)
        hatImage.setImageDrawable(null)
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
    fun showCharacterSmoothly() {
        val items = DataManager.getCharacterWithItems()

        characterImage.setImageDrawable(null)
        hatImage.setImageDrawable(null)
        topImage.setImageDrawable(null)
        accessoryImage.setImageDrawable(null)

        val drawables = listOfNotNull(
            items.getOrNull(0)?.let { animateAppearance(characterImage, it) },
            items.getOrNull(1)?.let { animateAppearance(hatImage, it) },
            items.getOrNull(2)?.let { animateAppearance(topImage, it) },
            items.getOrNull(3)?.let { animateAppearance(accessoryImage, it) }
        )

        playLoopedAnimations(drawables)
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
    fun showEatingChar() {
        val eatItems = DataManager.getCharacterEating()

        characterImage.setImageDrawable(null)
        hatImage.setImageDrawable(null)
        topImage.setImageDrawable(null)
        accessoryImage.setImageDrawable(null)

        val drawables = listOfNotNull(
            eatItems.getOrNull(0)?.let { animateAppearance(characterImage, it) },
            eatItems.getOrNull(1)?.let { animateAppearance(hatImage, it) },
            eatItems.getOrNull(2)?.let { animateAppearance(topImage, it) },
            eatItems.getOrNull(3)?.let { animateAppearance(accessoryImage, it) }
        )

        playLoopedAnimations(drawables)
    }

    val updateRoom = object : Runnable {
        override fun run() {
            foodItems = DataManager.currentFoodItems
            imageTable.setImageResource(DataManager.rooms["kitchen"]?.get(1) ?: 0)
            mainLayout.setBackgroundResource(DataManager.rooms["kitchen"]?.get(0) ?: 0)
            handler.postDelayed(this, 1000)
        }
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
        fun newInstance() = KitchenFragment()
    }
}
