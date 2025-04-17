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
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.walkiepaws.R
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.dto.request.MarketBuyDTO
import com.google.android.material.imageview.ShapeableImageView
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DataManager.updateRoomsTemplates()
        handler.postDelayed(updateRoom, 1000)
        val view = inflater.inflate(R.layout.activity_kitchen_game_screen, container, false)

        initViews(view)
        setupClickListeners()

        setupFoodItems()
        return view
    }

    private fun initViews(view: View) {
        characterImage = view.findViewById(R.id.imageCharacter)
        mainLayout = view.findViewById(R.id.main)
        imageTable = view.findViewById(R.id.imageTable)
        shopButton = view.findViewById(R.id.button_shop)
        customizeButton = view.findViewById(R.id.button_customize)
        foodButton = view.findViewById(R.id.button_food)

        hatImage = view.findViewById(R.id.hatImage)
        topImage = view.findViewById(R.id.topImage)
        accessoryImage = view.findViewById(R.id.accessoryImage)

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
        // Получаем координаты кнопки
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

    private var isEating = false

    private fun useFoodItem(foodItem: DataManager.FoodItem) {
        if (isEating) return
        isEating = true
        MusicManager.getInstance(requireContext()).playEatSound()

        val message = when (random.nextInt(3)) {
            0 -> "${foodItem.name} съедено! +${foodItem.healthValue} HP"
            else -> "Ням-ням! +${foodItem.healthValue} HP"
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        characterImage.animate()
            .withEndAction {
                try {
                    val eatingResId = DataManager.getCurrentEatingCharacter()
                    if (eatingResId != 0) {
                        Glide.with(this)
                            .load(eatingResId)
                            .into(characterImage)
                    }
                    characterImage.animate()
                        .setDuration(1000)
                        .withEndAction {
                            characterImage.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(500)
                                .withEndAction {
                                    Glide.with(this)
                                        .load(DataManager.getChars()[DataManager.currentCharacterIndex])
                                        .into(characterImage)

                                    isEating = false
                                    hideFoodPopup()

                                }
                                .start()
                        }
                        .start()
                } catch (e: Exception) {
                    Log.e("FoodFragment", "Error during eating animation", e)
                    isEating = false
                }
            }
            .start()
        resetCharacterState()
        showCharacterSmoothly()
        RetrofitClient.getApiService().buyState((DataManager.appContext as App).token, MarketBuyDTO(foodItem.healthValue))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (!response.isSuccessful) {
                        Toast.makeText(requireContext(), "Недостаточно средств", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })
        DataManager.currentFoodItems.remove(foodItem)
        setupFoodItems()
    }

    override fun onResume() {
        super.onResume()
        showCharacterSmoothly()
        setupFoodItems()
        DataManager.updateRoomsTemplates()
    }

    override fun onPause() {
        resetCharacterState()
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
        Log.d("КОМНАТА КУХНЯ", "ПОКАЗ")
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

    private val updateRoom = Runnable {
        foodItems = DataManager.currentFoodItems
        imageTable.setImageResource(DataManager.rooms["kitchen"]?.get(1) ?: 0)
        mainLayout.setBackgroundResource(DataManager.rooms["kitchen"]?.get(0) ?: 0)
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
