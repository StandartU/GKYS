package com.example.walkiepaws.main_game

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkiepaws.R
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient

class MiniGameActivity : AppCompatActivity() {

    private lateinit var itemsContainer: LinearLayout
    private lateinit var imageBack: ImageView
    private lateinit var apiService: ApiService

    data class Game(val name: String, val price: String, val characterIndex: Int, val imageResId: Int)

    private lateinit var games: MutableList<Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = RetrofitClient.getApiService()
        setContentView(R.layout.activity_mini_game)
        initViews()
        setupClickListeners()
        initializeGames()
        loadGames()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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
        imageBack = findViewById(R.id.imageBack)
    }

    private fun setupClickListeners() {
        imageBack.setOnClickListener { finish() }
    }

    private fun initializeGames() {
        games = mutableListOf()
        // Add games with their corresponding image resources
        games.add(Game("Прыгающий кролик", "Развивает реакцию", 3, R.drawable.ico_jumprabbit))
        games.add(Game("Ловец фруктов", "Тренирует ловкость", 2, R.drawable.ico_fruitcatcher))
        games.add(Game("Прыжки по платформам", "Улучшает координацию", 1, R.drawable.ico_jumping_on_platforms))
        games.add(Game("Прыжки через предметы", "Развивает внимательность", 0, R.drawable.ico_jumping_over_items))
    }

    private fun loadGames() {
        itemsContainer.removeAllViews()
        val currentGame = games.find { it.characterIndex == DataManager.currentCharacterIndex }
        if (currentGame != null) {
            displayGame(currentGame)
        } else {
            games.forEach { displayGame(it) }
        }
    }

    private fun displayGame(game: Game) {
        val gameView = LayoutInflater.from(this).inflate(R.layout.item_product, itemsContainer, false)

        gameView.apply {
            findViewById<TextView>(R.id.itemName).text = game.name
            findViewById<TextView>(R.id.itemPrice).text = game.price
            findViewById<ImageView>(R.id.itemImage).apply {
                setImageResource(game.imageResId)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            setOnClickListener { onGameClicked(game) }
        }

        itemsContainer.addView(gameView)
    }

    private fun onGameClicked(game: Game) {
        Toast.makeText(this, "Выбрана: ${game.name}", Toast.LENGTH_SHORT).show()
        DataManager.games[game.characterIndex]()
    }
}