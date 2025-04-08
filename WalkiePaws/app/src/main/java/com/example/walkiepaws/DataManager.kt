package com.example.walkiepaws

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.Utils
import com.example.walkiepaws.backend.model.dto.request.GetPetDTO
import com.example.walkiepaws.backend.model.dto.responce.PetDTO
import com.example.walkiepaws.backend.model.dto.responce.UserRoomDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch

object DataManager {
    private const val PREFS_NAME = "AppPrefs"
    private const val CHARACTER_INDEX_KEY = "character_index"

    private lateinit var prefs: SharedPreferences
    private lateinit var appContext: Context
    private lateinit var apiService: ApiService
    private var charactersIds: List<Int> = listOf(
        R.drawable.gepard_norm,
        R.drawable.kangaroo_norm,
        R.drawable.hamster_norm,
        R.drawable.rabbit_norm
    )

    fun init(context: Context) {
        apiService = RetrofitClient.getApiService()
        appContext = context.applicationContext
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun updateData() {
        updateCharList()
        updateRoomsTemplates()
    }

    var currentCharacterIndex: Int
        get() = prefs.getInt(CHARACTER_INDEX_KEY, 0)
        set(value) = prefs.edit().putInt(CHARACTER_INDEX_KEY, value).apply()


    val characters = listOf(
        R.drawable.gepard_norm,
        R.drawable.kangaroo_norm,
        R.drawable.hamster_norm,
        R.drawable.rabbit_norm
    )

    val rooms: MutableMap<String, List<Int>> = mutableMapOf(
        "living" to listOf(R.drawable.living_1),
        "kitchen" to listOf(R.drawable.kitchen_1, R.drawable.kitchen_table_1),
        "bedroom" to listOf(R.drawable.bedroom_1, R.drawable.blanket_1)
    )

    private val sleepingCharacters = listOf(
        R.drawable.gepard_sleep,
        R.drawable.kangaroo_sleep,
        R.drawable.hamster_sleep,
        R.drawable.rabbit_sleep
    )

    fun getCurrentSleepingCharacter(): Int = sleepingCharacters[currentCharacterIndex]

    fun getChars(): List<Int> {
        return charactersIds
    }

    fun updateCharList(onComplete: (() -> Unit)? = null) {
        val characterNames = listOf("gepard", "kangaroo", "hamster", "rabbit")
        val charList = MutableList(characterNames.size) { 0 }
        val latch = CountDownLatch(characterNames.size)

        for ((index, char) in characterNames.withIndex()) {
            val reqDTO = GetPetDTO(char)
            apiService.getPet((appContext as App).token, reqDTO)
                .enqueue(object : Callback<PetDTO> {
                    override fun onResponse(call: Call<PetDTO>, response: Response<PetDTO>) {
                        val name = response.body()?.template
                        val drawableId = Utils().getDrawableIdByName(appContext, name.toString())
                        charList[index] = drawableId
                        latch.countDown()
                    }

                    override fun onFailure(call: Call<PetDTO>, t: Throwable) {
                        Log.e("DEBUG_DATAMANAGER", "Ошибка загрузки персонажа: $char", t)
                        latch.countDown()
                    }
                })
        }

        Thread {
            latch.await()
            charactersIds = charList
            onComplete?.invoke()
        }.start()
    }

    fun updateRoomsTemplates () {
        apiService.getRooms((appContext as App).token).enqueue(object : Callback<List<UserRoomDTO>> {
            override fun onResponse(
                call: Call<List<UserRoomDTO>>,
                response: Response<List<UserRoomDTO>>
            ) {
                val dataRooms = response.body()
                dataRooms?.forEach { room ->
                    when (room.room.name) {
                        "livingroom" -> rooms["living"] = room.templates.map { template ->
                            Utils().getDrawableIdByName(appContext, template)
                        }.toList()
                        "sleeproom" -> rooms["bedroom"] = room.templates.map { template ->
                            Utils().getDrawableIdByName(appContext, template)
                        }.toList()
                        "kitchen" -> rooms["kitchen"] = room.templates.map { template ->
                            Utils().getDrawableIdByName(appContext, template)
                        }.toList()
                    }
                }
            }

            override fun onFailure(call: Call<List<UserRoomDTO>>, t: Throwable) {}

        })
    }
}

