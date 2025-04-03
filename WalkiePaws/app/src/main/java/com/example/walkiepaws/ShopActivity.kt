package com.example.walkiepaws

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ShopActivity : AppCompatActivity() {

    data class Product(val name: String, val price: String)

    private lateinit var foodCategory: TextView
    private lateinit var clothesCategory: TextView
    private lateinit var roomsCategory: TextView
    private lateinit var itemsContainer: LinearLayout

    private val productsByCategory = mapOf(
        "food" to listOf(
            Product("Яблоко", "50 монет"),
            Product("Бутерброд", "100 монет"),
            Product("Сок", "80 монет")
        ),
        "clothes" to listOf(
            Product("Футболка", "200 монет"),
            Product("Джинсы", "350 монет")
        ),
        "rooms" to listOf(
            Product("Спальня", "1000 монет"),
            Product("Кухня", "1200 монет")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupClickListeners()
        showCategory("food")
    }

    private fun initViews() {
        foodCategory = findViewById(R.id.foodCategory)
        clothesCategory = findViewById(R.id.clothesCategory)
        roomsCategory = findViewById(R.id.roomsCategory)
        itemsContainer = findViewById(R.id.itemsContainer)
        findViewById<ImageView>(R.id.imageBack).setOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        foodCategory.setOnClickListener { showCategory("food") }
        clothesCategory.setOnClickListener { showCategory("clothes") }
        roomsCategory.setOnClickListener { showCategory("rooms") }
    }

    private fun showCategory(category: String) {
        resetCategoryButtons()

        when (category) {
            "food" -> {
                foodCategory.setBackgroundResource(R.drawable.category_button_selected)
                foodCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            "clothes" -> {
                clothesCategory.setBackgroundResource(R.drawable.category_button_selected)
                clothesCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
            "rooms" -> {
                roomsCategory.setBackgroundResource(R.drawable.category_button_selected)
                roomsCategory.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            }
        }

        itemsContainer.removeAllViews()
        productsByCategory[category]?.forEach { product ->
            addProductView(product)
        }
    }

    private fun resetCategoryButtons() {
        val defaultTextColor = ContextCompat.getColor(this, R.color.black)

        foodCategory.setBackgroundResource(R.drawable.category_button)
        foodCategory.setTextColor(defaultTextColor)

        clothesCategory.setBackgroundResource(R.drawable.category_button)
        clothesCategory.setTextColor(defaultTextColor)

        roomsCategory.setBackgroundResource(R.drawable.category_button)
        roomsCategory.setTextColor(defaultTextColor)
    }

    @SuppressLint("SetTextI18n")
    private fun addProductView(product: Product) {
        TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12.dpToPx()
            }

            text = "${product.name} (${product.price})"
            textSize = 18f
            setTextColor(ContextCompat.getColor(context, R.color.main_color_auth))
            setTypeface(typeface, Typeface.BOLD)

            itemsContainer.addView(this)
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}