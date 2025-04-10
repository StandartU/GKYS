package com.example.walkiepaws.main_game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.walkiepaws.R
import com.example.walkiepaws.games.CheetahRunnerStarter
import com.example.walkiepaws.games.FruitTakerActivity
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.Utils
import com.example.walkiepaws.backend.model.Model
import com.example.walkiepaws.backend.model.UserTaskModel
import com.example.walkiepaws.backend.model.dto.request.GetPetDTO
import com.example.walkiepaws.backend.model.dto.responce.GetPetItemDTO
import com.example.walkiepaws.backend.model.dto.responce.GetTasksDTO
import com.example.walkiepaws.backend.model.dto.responce.MarketAllDTO
import com.example.walkiepaws.backend.model.dto.responce.PetDTO
import com.example.walkiepaws.backend.model.dto.responce.UserItemDTO
import com.example.walkiepaws.backend.model.dto.responce.UserRoomDTO
import com.example.walkiepaws.backend.model.dto.responce.WeekStepsDTO
import com.example.walkiepaws.games.CheetahJumping
import com.example.walkiepaws.games.DoodleJumpStarter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch

object DataManager {
    private const val PREFS_NAME = "AppPrefs"
    private const val CHARACTER_INDEX_KEY = "character_index"

    lateinit var tasks: MutableMap<String, UserTaskModel>
    private lateinit var prefs: SharedPreferences
    lateinit var appContext: Context
    private lateinit var apiService: ApiService
    lateinit var hatsItems: MutableList<Item>
    lateinit var topsItems: MutableList<Item>
    lateinit var accessoriesItems: MutableList<Item>
    lateinit var foodItems: MutableList<Item>
    lateinit var clothesItems: MutableList<Item>
    lateinit var roomsItems: MutableList<Item>
    lateinit var weekSteps: List<Int>
    lateinit var petItems: MutableList<Item>

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
        initPetItems()
        updateTasks()
        updateCharList()
        updateRoomsTemplates()
        initItems()
        initMarkets()
        initWeekSteps()
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

    val charactersName = listOf(
        "gepard",
        "kangaroo",
        "hamster",
        "rabbit"
    )

    val games = listOf(
        { appContext.startActivity(Intent(appContext, CheetahRunnerStarter::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })},
        { appContext.startActivity(Intent(appContext, DoodleJumpStarter::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })},
        { appContext.startActivity(Intent(appContext, FruitTakerActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })},
        { appContext.startActivity(Intent(appContext, CheetahJumping::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })}
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

    private fun initMarkets() {
        foodItems = emptyList<Item>().toMutableList()
        clothesItems = emptyList<Item>().toMutableList()
        roomsItems = emptyList<Item>().toMutableList()
        apiService.getAllMarkets((appContext as App).token).enqueue(object : Callback<MarketAllDTO> {
            override fun onResponse(call: Call<MarketAllDTO>, response: Response<MarketAllDTO>) {
                val markets = response.body()?.marketModels
                val rooms = response.body()?.roomModels
                val items = response.body()?.itemModels
                markets?.forEach{market ->
                    foodItems.add(
                        Item(market.name, market.price.toString(), Utils().getDrawableIdByName(
                            appContext, market.template), market)
                    )
                }
                rooms?.forEach{room ->
                    roomsItems.add(
                        Item(room.room.surname, room.price.toString(), Utils().getDrawableIdByName(
                            appContext, room.marketTemplate
                        ), room)
                    )
                }
                items?.forEach{item ->
                    clothesItems.add(
                        Item(item.surname, item.price.toString(), Utils().getDrawableIdByName(
                            appContext, item.name), item)
                    )
                }
                Log.d("DATAMANAGER", "ON MARKET CODE" + response.code().toString())
            }

            override fun onFailure(call: Call<MarketAllDTO>, t: Throwable) {}

        })
    }

    private fun initPetItems() {
        petItems = mutableListOf()
        apiService.getPetItems((appContext as App).token).enqueue(object : Callback<GetPetItemDTO> {
            override fun onResponse(call: Call<GetPetItemDTO>, response: Response<GetPetItemDTO>) {
                Log.d("WEAR", response.code().toString())
                response.body()?.petItemModels?.forEach{
                    petItem ->
                    petItems.add(Item(petItem.item.name, petItem.item.price.toString(),
                        Utils().getDrawableIdByName(appContext, petItem.template),
                        petItem
                    ))
                }
            }

            override fun onFailure(call: Call<GetPetItemDTO>, t: Throwable) {}

        })
    }

    fun initWeekSteps() {
        apiService.getWeekSteps((appContext as App).token).enqueue(object : Callback<WeekStepsDTO> {
            override fun onResponse(call: Call<WeekStepsDTO>, response: Response<WeekStepsDTO>) {
                weekSteps = response.body()?.weekSteps ?: listOf(0, 0, 0, 0, 0, 0, 0)
            }
            override fun onFailure(call: Call<WeekStepsDTO>, t: Throwable) {}
        })
    }

    fun updateTasks() {
        apiService.getTasks((appContext as App).token).enqueue(object : Callback<GetTasksDTO> {
            override fun onResponse(call: Call<GetTasksDTO>, response: Response<GetTasksDTO>) {
                response.body()?.userTasks?.forEach { userTask -> tasks[userTask.task.name] = userTask }
            }

            override fun onFailure(call: Call<GetTasksDTO>, t: Throwable) {}

        })
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

    private val eatingCharacters = listOf(
        R.drawable.gepard_eats,
        R.drawable.kangaroo_eats,
        R.drawable.hamster_eats,
        R.drawable.rabbit_eats
    )

    fun getCurrentEatingCharacter(): Int = eatingCharacters[currentCharacterIndex]

    var currentHat: Item? = null
    var currentTop: Item? = null
    var currentAccessory: Item? = null

    // Обновляем класс Item, добавляя поле для WebP-ресурса
    data class Item(
        val name: String,
        val price: String,
        val imageRes: Int,
        val dto: Model,
    )


    fun initItems() {
        topsItems = emptyList<Item>().toMutableList()
        hatsItems = emptyList<Item>().toMutableList()
        accessoriesItems = emptyList<Item>().toMutableList()
        apiService.getUserItems((appContext as App).token).enqueue(object : Callback<UserItemDTO> {
            override fun onResponse(call: Call<UserItemDTO>, response: Response<UserItemDTO>) {
                response.body()?.userItemModels?.forEach { item ->
                    val iconRes = Utils().getDrawableIdByName(appContext, item.item.name)
                    when (item.item.category) {
                        "top" -> topsItems.add(Item(item.item.surname, item.item.price.toString(), iconRes, item.item))
                        "hat" -> hatsItems.add(Item(item.item.surname, item.item.price.toString(), iconRes, item.item))
                        "accs" -> accessoriesItems.add(Item(item.item.surname, item.item.price.toString(), iconRes, item.item))
                    }
                }
            }
            override fun onFailure(call: Call<UserItemDTO>, t: Throwable) {}
        })
    }

    fun getCharacterWithItems(characterIndex: Int): List<Int> {
        val characterRes = getChars()[characterIndex] // Предполагается, что это базовый WebP персонажа
        val items = mutableListOf(characterRes)

        currentHat?.imageRes?.let { items.add(it) }
        currentTop?.imageRes?.let { items.add(it) }
        currentAccessory?.imageRes?.let { items.add(it) }

        return items
    }
}

