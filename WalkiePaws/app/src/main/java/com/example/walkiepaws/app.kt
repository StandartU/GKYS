package com.example.walkiepaws
import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
    }
}