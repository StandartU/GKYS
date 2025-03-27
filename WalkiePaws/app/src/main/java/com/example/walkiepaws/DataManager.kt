package com.example.walkiepaws

import android.content.Context
import android.content.SharedPreferences

object DataManager {
    private const val PREFS_NAME = "AppPrefs"
    private const val CHARACTER_INDEX_KEY = "character_index"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var currentCharacterIndex: Int
        get() = prefs.getInt(CHARACTER_INDEX_KEY, 0)
        set(value) = prefs.edit().putInt(CHARACTER_INDEX_KEY, value).apply()

    val characters = listOf(
        R.drawable.gepard,
        R.drawable.kangaroo,
        R.drawable.hamster,
        R.drawable.rabbit
    )
}