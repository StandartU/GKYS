package com.example.walkiepaws.main_game

import com.example.walkiepaws.main_game.DataManager.Item
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkiepaws.R
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.ItemModel
import com.example.walkiepaws.backend.model.MarketModel
import com.example.walkiepaws.backend.model.RoomLvlModel
import com.example.walkiepaws.backend.model.RoomModel
import com.example.walkiepaws.backend.model.dto.request.BuyItemDTO
import com.example.walkiepaws.backend.model.dto.request.BuyRoomLvlDTO
import com.example.walkiepaws.backend.model.dto.request.MarketBuyDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopActivity : AppCompatActivity() {

    private lateinit var itemsContainer: LinearLayout
    private lateinit var foodCategory: TextView
    private lateinit var clothesCategory: TextView
    private lateinit var roomsCategory: TextView
    private lateinit var imageBack: ImageView
    private lateinit var apiService: ApiService

    private var currentCategory = 0

    private lateinit var foodItems: MutableList<Item>

    private lateinit var clothesItems: MutableList<Item>

    private lateinit var roomsItems: MutableList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = RetrofitClient.getApiService()
        setContentView(R.layout.activity_shop)
        initMarkets()
        initViews()
        setupClickListeners()
        loadCategory(currentCategory)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imageBack)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        MusicManager.getInstance(this).play()
    }

    override fun onPause() {
        super.onPause()
        if (!isChangingConfigurations) {
            MusicManager.getInstance(this).pause()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun initViews() {
        itemsContainer = findViewById(R.id.itemsContainer)
        foodCategory = findViewById(R.id.foodCategory)
        clothesCategory = findViewById(R.id.clothesCategory)
        roomsCategory = findViewById(R.id.roomsCategory)
        imageBack = findViewById(R.id.imageBack)
    }

    private fun initMarkets() {
        foodItems = DataManager.foodItems
        clothesItems = DataManager.clothesItems
        roomsItems = DataManager.roomsItems
    }

    private fun setupClickListeners() {
        foodCategory.setOnClickListener { switchCategory(0) }
        clothesCategory.setOnClickListener { switchCategory(1) }
        roomsCategory.setOnClickListener { switchCategory(2) }
        imageBack.setOnClickListener { finish() }
    }

    private fun switchCategory(categoryIndex: Int) {
        if (currentCategory == categoryIndex) return
        currentCategory = categoryIndex
        updateCategorySelection()
        loadCategory(categoryIndex)
    }

    private fun updateCategorySelection() {
        listOf(foodCategory, clothesCategory, roomsCategory).forEach {
            it.setBackgroundResource(R.drawable.category_button)
            it.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }

        when (currentCategory) {
            0 -> {
                foodCategory.setBackgroundResource(R.drawable.category_button_selected)
                foodCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            1 -> {
                clothesCategory.setBackgroundResource(R.drawable.category_button_selected)
                clothesCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            2 -> {
                roomsCategory.setBackgroundResource(R.drawable.category_button_selected)
                roomsCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
        }
    }

    private fun loadCategory(categoryIndex: Int) {
        itemsContainer.removeAllViews()

        when (categoryIndex) {
            0 -> displayItems(foodItems)
            1 -> displayItems(clothesItems)
            2 -> displayItems(roomsItems)
        }
    }

    private fun displayItems(items: List<Item>) {
        items.forEach { item ->
            addItemToContainer(item)
        }
    }

    private fun addItemToContainer(item: Item) {
        val itemView = LayoutInflater.from(this).inflate(R.layout.item_product, itemsContainer, false)

        itemView.apply {
            findViewById<TextView>(R.id.itemName).text = item.name
            findViewById<TextView>(R.id.itemPrice).text = item.price
            findViewById<ImageView>(R.id.itemImage).apply {
                setImageResource(item.imageRes)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            setOnClickListener { onItemClicked(item) }
        }

        itemsContainer.addView(itemView)
    }

    private fun onItemClicked(item: Item) {
        when (item.dto) {
            is ItemModel -> {
                DataManager.clothesItems.remove(item)
                initMarkets()
                apiService.buyItem((applicationContext as App).token, BuyItemDTO(item.dto.id))
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                loadCategory(currentCategory)
                            }
                            else {
                                Toast.makeText(applicationContext, "Недостаточно средств", Toast.LENGTH_SHORT).show()
                                DataManager.clothesItems.add(item)
                                initMarkets()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {}
                    })
                DataManager.initItems()
                DataManager.initPetItems()
            }
            is MarketModel -> {
                DataManager.currentFoodItems.add(DataManager.FoodItem(item.name, item.price, item.imageRes, item.dto.id))
                MusicManager.getInstance(this).playThanksSound()
            }
            is RoomLvlModel -> {
                DataManager.roomsItems.remove(item)
                initMarkets()
                apiService.buyRoom((applicationContext as App).token, BuyRoomLvlDTO(item.dto.room.name, item.dto.lvl))
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                loadCategory(currentCategory)
                            }
                            else {
                                Toast.makeText(applicationContext, "Недостаточно средств", Toast.LENGTH_SHORT).show()
                                DataManager.roomsItems.add(item)
                                initMarkets()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {}
                    })
                DataManager.updateRoomsTemplates()
            }
        }
        Toast.makeText(this, "Выбранно: ${item.name}", Toast.LENGTH_SHORT).show()
    }
}