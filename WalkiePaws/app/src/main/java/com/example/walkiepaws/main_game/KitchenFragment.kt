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

    // Элементы popup окна
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
        characterImage = view.findViewById(R.id.imageCharacter)

        hatImage = view.findViewById(R.id.hatImage)
        topImage = view.findViewById(R.id.topImage)
        accessoryImage = view.findViewById(R.id.accessoryImage)

        mainLayout = view.findViewById(R.id.main)

        imageTable = view.findViewById(R.id.imageTable)

        shopButton = view.findViewById(R.id.button_shop)
        customizeButton = view.findViewById(R.id.button_customize)

        resetCharacterState()

        shopButton.setOnClickListener {
            openShop()
        }

        customizeButton.setOnClickListener {
            openCustomization()
        }

        initViews(view)
        setupClickListeners()
        setupFoodItems()
        resetCharacterState()
        return view
    }

    private fun initViews(view: View) {
        characterImage = view.findViewById(R.id.imageCharacter)
        mainLayout = view.findViewById(R.id.main)
        imageTable = view.findViewById(R.id.imageTable)
        shopButton = view.findViewById(R.id.button_shop)
        customizeButton = view.findViewById(R.id.button_customize)
        foodButton = view.findViewById(R.id.button_food)

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
        DataManager.updateRoomsTemplates()
        setupFoodItems()
        handler.postDelayed(updateRoom, 1000)
    }

    override fun onPause() {
        super.onPause()
        hideCharacterImmediately()
        handler.removeCallbacks(updateRoom)
    }

    private fun resetCharacterState() {
        characterImage.visibility = View.INVISIBLE
        characterImage.alpha = 0f
    }

    internal fun hideCharacterImmediately() {
        characterImage.animate().cancel()
        characterImage.visibility = View.INVISIBLE
        characterImage.alpha = 0f
    }

    internal fun showCharacterSmoothly() {
        characterImage.visibility = View.VISIBLE
        characterImage.alpha = 0f
        characterImage.animate()
            .alpha(1f)
            .setDuration(400)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        updateCharacterImage()
    }

    private fun updateCharacterImage() {
        val items = DataManager.getCharacterWithItems(DataManager.currentCharacterIndex)


        Glide.with(this)
            .load(items[0])
            .into(characterImage)

        hatImage.setImageDrawable(null)
        topImage.setImageDrawable(null)
        accessoryImage.setImageDrawable(null)

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

    private val updateRoom = Runnable {
        foodItems = DataManager.currentFoodItems
        imageTable.setImageResource(DataManager.rooms["kitchen"]?.get(1) ?: 0)
        mainLayout.setBackgroundResource(DataManager.rooms["kitchen"]?.get(0) ?: 0)
    }

    private fun openShop() {
        startActivity(Intent(activity, ShopActivity::class.java))
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun openCustomization() {
        startActivity(Intent(activity, CustomizationActivity::class.java))
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        fun newInstance() = KitchenFragment()
    }
}
