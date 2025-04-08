package com.example.walkiepaws

import android.annotation.SuppressLint
import android.os.Bundle
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

class ShopActivity : AppCompatActivity() {

    private lateinit var itemsContainer: LinearLayout
    private lateinit var foodCategory: TextView
    private lateinit var clothesCategory: TextView
    private lateinit var roomsCategory: TextView
    private lateinit var imageBack: ImageView

    private var currentCategory = 0

    private val foodItems = mutableListOf(
        Item("Вишня", "50 монет", R.drawable.vish),
        Item("Виноград", "100 монет", R.drawable.vin),
        Item("Банан", "80 монет", R.drawable.banan)
    )

    private val clothesItems = mutableListOf(
        Item("Шапка", "150 монет", R.drawable.cap),
        Item("Шляпа", "250 монет", R.drawable.hat),
        Item("Кепка", "300 монет", R.drawable.kepka),
        Item("Бандана", "550 монет", R.drawable.bandana),
        Item("Шлем", "450 монет", R.drawable.helmet),
        Item("Футболка", "250 монет", R.drawable.tshirt),
        Item("Рубашка", "150 монет", R.drawable.shirt),
        Item("Жилетка", "5330 монет", R.drawable.vest),
        Item("Очки", "450 монет", R.drawable.glasses),
        Item("Шарф", "650 монет", R.drawable.scarf),
        Item("Маска", "450 монет", R.drawable.mask),
        Item("Наушники", "450 монет", R.drawable.headphones),
        Item("Подвеска", "250 монет", R.drawable.pendant)
    )

    private val roomsItems = mutableListOf(
        Item("Спальня 2 lvl", "1000 монет", R.drawable.shop_bedroom_2),
        Item("Кухня 2 lvl", "1000 монет", R.drawable.shop_kitchen_2),
        Item("Гостинная 2 lvl", "1000 монет", R.drawable.shop_living_2),
        Item("Спальня 3 lvl", "1200 монет", R.drawable.shop_bedroom_3),
        Item("Кухня 3 lvl", "1200 монет", R.drawable.shop_kitchen_3),
        Item("Гостнная 3 lvl", "1200 монет", R.drawable.shop_living_3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
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

    private fun initViews() {
        itemsContainer = findViewById(R.id.itemsContainer)
        foodCategory = findViewById(R.id.foodCategory)
        clothesCategory = findViewById(R.id.clothesCategory)
        roomsCategory = findViewById(R.id.roomsCategory)
        imageBack = findViewById(R.id.imageBack)
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
        Toast.makeText(this, "Куплено: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    fun addFoodItem(name: String, price: String, imageRes: Int) {
        foodItems.add(Item(name, price, imageRes))
        if (currentCategory == 0) {
            addItemToContainer(foodItems.last())
        }
    }

    fun addClothesItem(name: String, price: String, imageRes: Int) {
        clothesItems.add(Item(name, price, imageRes))
        if (currentCategory == 1) {
            addItemToContainer(clothesItems.last())
        }
    }

    fun addRoomItem(name: String, price: String, imageRes: Int) {
        roomsItems.add(Item(name, price, imageRes))
        if (currentCategory == 2) {
            addItemToContainer(roomsItems.last())
        }
    }

    data class Item(
        val name: String,
        val price: String,
        val imageRes: Int
    )
}