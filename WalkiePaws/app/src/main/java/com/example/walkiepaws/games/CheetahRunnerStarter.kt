package com.example.walkiepaws.games

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.walkiepaws.games.GameViewCR

class CheetahRunnerStarter : AppCompatActivity() {

    private lateinit var gameViewCR: GameViewCR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewCR = GameViewCR(this)
        setContentView(gameViewCR)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Передаем событие касания в GameView
        return gameViewCR.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onBackPressed() {
        // Обработка кнопки назад - можно добавить подтверждение выхода
        super.onBackPressed()
    }
}