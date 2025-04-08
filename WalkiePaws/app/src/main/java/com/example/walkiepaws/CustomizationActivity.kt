package com.example.walkiepaws

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

class CustomizationActivity : AppCompatActivity() {

    private lateinit var itemsContainer: LinearLayout
    private lateinit var hatsCategory: TextView
    private lateinit var topsCategory: TextView
    private lateinit var accessoriesCategory: TextView
    private lateinit var imageBack: ImageView

    private var currentCategory = 0
    
    private val hatsItems = mutableListOf(
        Item("Шапка", "500 монет", R.drawable.cap),
        Item("Шляпа", "600 монет", R.drawable.hat),
        Item("Кепка", "800 монет", R.drawable.kepka),
        Item("Бандана", "800 монет", R.drawable.bandana),
        Item("Шлем", "800 монет", R.drawable.helmet)
    )

    private val topsItems = mutableListOf(
        Item("Футболка", "1200 монет", R.drawable.tshirt),
        Item("Рубашка", "1500 монет", R.drawable.shirt),
        Item("Жилетка", "2000 монет", R.drawable.vest)
    )

    private val accessoriesItems = mutableListOf(
        Item("Очки", "900 монет", R.drawable.glasses),
        Item("Шарф", "700 монет", R.drawable.scarf),
        Item("Шарф", "800 монет", R.drawable.scarf),
        Item("Наушники", "800 монет", R.drawable.headphones),
        Item("Подвеска", "800 монет", R.drawable.pendant),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customization)
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
        hatsCategory = findViewById(R.id.hatsCategory)
        topsCategory = findViewById(R.id.topsCategory)
        accessoriesCategory = findViewById(R.id.accessoriesCategory)
        imageBack = findViewById(R.id.imageBack)
    }

    private fun setupClickListeners() {
        hatsCategory.setOnClickListener { switchCategory(0) }
        topsCategory.setOnClickListener { switchCategory(1) }
        accessoriesCategory.setOnClickListener { switchCategory(2) }
        imageBack.setOnClickListener { finish() }
    }

    private fun switchCategory(categoryIndex: Int) {
        if (currentCategory == categoryIndex) return
        currentCategory = categoryIndex
        updateCategorySelection()
        loadCategory(categoryIndex)
    }

    private fun updateCategorySelection() {
        listOf(hatsCategory, topsCategory, accessoriesCategory).forEach {
            it.setBackgroundResource(R.drawable.category_button)
            it.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }

        when (currentCategory) {
            0 -> {
                hatsCategory.setBackgroundResource(R.drawable.category_button_selected)
                hatsCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            1 -> {
                topsCategory.setBackgroundResource(R.drawable.category_button_selected)
                topsCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            2 -> {
                accessoriesCategory.setBackgroundResource(R.drawable.category_button_selected)
                accessoriesCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
        }
    }

    private fun loadCategory(categoryIndex: Int) {
        itemsContainer.removeAllViews()

        when (categoryIndex) {
            0 -> displayItems(hatsItems)
            1 -> displayItems(topsItems)
            2 -> displayItems(accessoriesItems)
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
                setImageResource(item.imageRes) // Устанавливаем изображение из ресурсов
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            setOnClickListener { onItemClicked(item) }
        }

        itemsContainer.addView(itemView)
    }

    private fun onItemClicked(item: Item) {
        Toast.makeText(this, "Выбрано: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    fun addHatItem(name: String, price: String, imageRes: Int) {
        hatsItems.add(Item(name, price, imageRes))
        if (currentCategory == 0) {
            addItemToContainer(hatsItems.last())
        }
    }

    fun addTopItem(name: String, price: String, imageRes: Int) {
        topsItems.add(Item(name, price, imageRes))
        if (currentCategory == 1) {
            addItemToContainer(topsItems.last())
        }
    }

    fun addAccessoryItem(name: String, price: String, imageRes: Int) {
        accessoriesItems.add(Item(name, price, imageRes))
        if (currentCategory == 2) {
            addItemToContainer(accessoriesItems.last())
        }
    }

    // Обновленная модель товара с ID изображения вместо цвета
    data class Item(
        val name: String,
        val price: String,
        val imageRes: Int // Теперь храним ID ресурса изображения
    )
}