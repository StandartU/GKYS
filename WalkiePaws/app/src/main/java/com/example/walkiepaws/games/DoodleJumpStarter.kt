package com.example.walkiepaws.games

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DoodleJumpStarter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameViewCR(this))
    }
}