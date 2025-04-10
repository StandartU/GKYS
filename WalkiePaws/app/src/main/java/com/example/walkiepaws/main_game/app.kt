package com.example.walkiepaws.main_game
import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
    }
    var token: String? = null
}