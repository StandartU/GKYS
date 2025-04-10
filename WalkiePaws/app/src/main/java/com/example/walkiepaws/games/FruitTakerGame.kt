package com.example.walkiepaws.games

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.graphics.Movie
import com.example.walkiepaws.main_game.App
import com.example.walkiepaws.main_game.DataManager
import com.example.walkiepaws.main_game.MainGameScreen
import com.example.walkiepaws.R
import com.example.walkiepaws.backend.ApiService
import com.example.walkiepaws.backend.RetrofitClient
import com.example.walkiepaws.backend.model.dto.request.UserAddCashDTO
import com.example.walkiepaws.backend.model.dto.request.UserSetStateDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream

// Перечисление типов объектов с русскими названиями
enum class ObjectType(val score: Int) {
    ВИШНЯ(5),
    БАНАН(10),
    ВИНОГРАД(15),
    БОМБА(-30)
}

class FruitTakerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameViewFT(this))
    }
}

class GameViewFT(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    // Размеры объектов
    private val playerWidth = 200f
    private val playerHeight = 250f
    private val objectSize = 120f

    // Игровые параметры
    private var score = 0
    private var lives = 3
    private var gameSpeed = 1f
    private var difficultyLevel = 0
    private val difficultyInterval = 5000L
    private var gifStartTime: Long = 0

    // Контроль спавна
    private var lastSpawnTime = 0L
    private var spawnInterval = 1000L
    private var lastDifficultyIncreaseTime = 0L

    // Состояние паузы
    private var isPaused = false
    private var pauseStartTime = 0L
    private var resumeCountdown = 0
    private var pausedBackground: Bitmap? = null

    // Игровые объекты
    private val playerRect = RectF()
    private val fallingObjects = mutableListOf<FallingObject>()
    private var gameThread: Thread? = null
    private var isRunning = false
    private var isGameOver = false
    private val surfaceHolder: SurfaceHolder = holder
    private val paint = Paint().apply { isAntiAlias = true }

    // Графика
    private lateinit var backgroundBitmap: Bitmap
    private lateinit var playerGif: Movie
    private lateinit var grapeBitmap: Bitmap
    private lateinit var cherryBitmap: Bitmap
    private lateinit var bananaBitmap: Bitmap
    private lateinit var bombBitmap: Bitmap
    private lateinit var pauseButtonRect: RectF
    private lateinit var apiService: ApiService

    // Звуки
    private lateinit var soundPool: SoundPool
    private var fruitSoundId = 0
    private var bombSoundId = 0
    private var loseSoundId = 0
    private lateinit var backgroundMusic: MediaPlayer

    init {
        isFocusable = true
        holder.addCallback(this)
        loadGraphics()
        initSounds()
    }

    @SuppressLint("ResourceType")
    private fun loadGraphics() {
        try {
            backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.fon_ft)

            val inputStream: InputStream = resources.openRawResource(R.drawable.personazh_ft)
            playerGif = Movie.decodeStream(inputStream)
            inputStream.close()

            cherryBitmap = BitmapFactory.decodeResource(resources, R.drawable.vishnya_ft)
                .let { Bitmap.createScaledBitmap(it, objectSize.toInt(), objectSize.toInt(), true) }

            bananaBitmap = BitmapFactory.decodeResource(resources, R.drawable.banan_ft)
                .let { Bitmap.createScaledBitmap(it, objectSize.toInt(), objectSize.toInt(), true) }

            grapeBitmap = BitmapFactory.decodeResource(resources, R.drawable.vinograd_ft)
                .let { Bitmap.createScaledBitmap(it, objectSize.toInt(), objectSize.toInt(), true) }

            bombBitmap = BitmapFactory.decodeResource(resources, R.drawable.bomba_ft)
                .let { Bitmap.createScaledBitmap(it, objectSize.toInt(), objectSize.toInt(), true) }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun initSounds() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        fruitSoundId = soundPool.load(context, R.raw.fruit_ft, 1)
        bombSoundId = soundPool.load(context, R.raw.bomba_ft, 1)
        loseSoundId = soundPool.load(context, R.raw.lose_ft, 1)

        backgroundMusic = MediaPlayer.create(context, R.raw.fon_ft)
        backgroundMusic.isLooping = true
        backgroundMusic.setVolume(0.5f, 0.5f)
    }

    override fun run() {
        gifStartTime = System.currentTimeMillis()
        lastDifficultyIncreaseTime = System.currentTimeMillis()
        backgroundMusic.start()

        while (isRunning) {
            if (!surfaceHolder.surface.isValid) continue

            val currentTime = System.currentTimeMillis()
            if (!isGameOver && !isPaused) {
                update(currentTime)
            }

            drawFrame()

            try {
                Thread.sleep(16)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun update(currentTime: Long) {
        // Обработка отсчета возобновления игры
        if (resumeCountdown > 0) {
            val elapsed = currentTime - pauseStartTime
            val newCountdown = 3 - elapsed / 1000
            if (newCountdown.toInt() != resumeCountdown) {
                resumeCountdown = newCountdown.toInt().coerceAtLeast(0)
                if (resumeCountdown == 0) {
                    isPaused = false
                    pausedBackground = null
                    backgroundMusic.start() // Добавляем возобновление музыки
                }
                // Принудительно запрашиваем перерисовку при изменении счетчика
                postInvalidate()
            }
            return
        }

        if (currentTime - lastDifficultyIncreaseTime > difficultyInterval) {
            increaseDifficulty()
            lastDifficultyIncreaseTime = currentTime
        }

        if (currentTime - lastSpawnTime > spawnInterval) {
            spawnRandomObject()
            lastSpawnTime = currentTime
        }

        val iterator = fallingObjects.iterator()
        while (iterator.hasNext()) {
            val obj = iterator.next()
            obj.y += obj.speed * gameSpeed
            obj.rect.set(obj.x, obj.y, obj.x + obj.width, obj.y + obj.height)

            if (RectF.intersects(obj.rect, playerRect)) {
                handleCollision(obj.type)
                iterator.remove()
                continue
            }

            if (obj.y > height) {
                iterator.remove()
            }
        }
    }

    private fun increaseDifficulty() {
        difficultyLevel++
        gameSpeed = 1f + (difficultyLevel * 0.12f)
        spawnInterval = (1000L / gameSpeed).toLong().coerceAtLeast(500)
    }

    private fun spawnRandomObject() {
        val bombChance = 25 + (difficultyLevel * 1.5).toInt().coerceAtMost(40)
        val cherryChance = (35 - difficultyLevel).coerceAtLeast(15)
        val bananaChance = (30 - difficultyLevel).coerceAtLeast(15)
        val grapeChance = (25 - difficultyLevel).coerceAtLeast(10)

        val type = when (Random.nextInt(100)) {
            in 0 until cherryChance -> ObjectType.ВИШНЯ
            in cherryChance until cherryChance + bananaChance -> ObjectType.БАНАН
            in cherryChance + bananaChance until cherryChance + bananaChance + grapeChance -> ObjectType.ВИНОГРАД
            else -> ObjectType.БОМБА
        }

        val baseSpeed = when (type) {
            ObjectType.БОМБА -> Random.nextInt(15, 22)
            else -> Random.nextInt(8, 15)
        }

        val speed = baseSpeed * gameSpeed

        val bitmap = when (type) {
            ObjectType.ВИШНЯ -> cherryBitmap
            ObjectType.БАНАН -> bananaBitmap
            ObjectType.ВИНОГРАД -> grapeBitmap
            ObjectType.БОМБА -> bombBitmap
        }

        fallingObjects.add(
            FallingObject(
            x = Random.nextInt(0, width - objectSize.toInt()).toFloat(),
            y = -objectSize,
            width = objectSize,
            height = objectSize,
            speed = speed.toFloat(),
            type = type,
            bitmap = bitmap
        )
        )
    }

    private fun handleCollision(type: ObjectType) {
        score = (score + type.score).coerceAtLeast(0)
        when {
            type.score > 0 -> {
                soundPool.play(fruitSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
            }
            else -> {
                lives--
                soundPool.play(bombSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                if (lives <= 0) {
                    isGameOver = true
                    soundPool.play(loseSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                    backgroundMusic.pause()
                    showGameOver()
                }
            }
        }
    }

    private fun drawFrame() {
        val canvas = surfaceHolder.lockCanvas()
        try {
            synchronized(surfaceHolder) {
                canvas.drawColor(Color.BLACK)

                if (isPaused && pausedBackground != null) {
                    // Рисуем замороженный фон при паузе
                    canvas.drawBitmap(pausedBackground!!, 0f, 0f, paint)
                } else {
                    // Рисуем обычный фон
                    canvas.drawBitmap(backgroundBitmap, null, Rect(0, 0, width, height), paint)

                    // Сохраняем текущий фон для паузы
                    if (isPaused && pausedBackground == null) {
                        pausedBackground = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        val tempCanvas = Canvas(pausedBackground!!)
                        tempCanvas.drawBitmap(backgroundBitmap, null, Rect(0, 0, width, height), paint)
                        for (obj in fallingObjects) {
                            tempCanvas.drawBitmap(obj.bitmap, obj.rect.left, obj.rect.top, paint)
                        }
                        tempCanvas.translate(playerRect.left, playerRect.top)
                        tempCanvas.scale(
                            playerRect.width() / playerGif.width(),
                            playerRect.height() / playerGif.height()
                        )
                        playerGif.setTime((System.currentTimeMillis() - gifStartTime).toInt() % playerGif.duration())
                        playerGif.draw(tempCanvas, 0f, 0f)
                    }
                }

                if (!isPaused || resumeCountdown > 0) {
                    // Рисуем движущиеся объекты только если не на паузе или во время отсчета
                    for (obj in fallingObjects) {
                        canvas.drawBitmap(obj.bitmap, obj.rect.left, obj.rect.top, paint)
                    }

                    val now = System.currentTimeMillis()
                    playerGif.setTime((now - gifStartTime).toInt() % playerGif.duration())
                    canvas.save()
                    canvas.translate(playerRect.left, playerRect.top)
                    canvas.scale(
                        playerRect.width() / playerGif.width(),
                        playerRect.height() / playerGif.height()
                    )
                    playerGif.draw(canvas, 0f, 0f)
                    canvas.restore()
                }

                // Рисуем кнопку паузы
                pauseButtonRect = RectF(30f, 30f, 130f, 130f)
                paint.color = Color.argb(150, 100, 100, 100)
                canvas.drawRoundRect(pauseButtonRect, 20f, 20f, paint)
                paint.color = Color.WHITE
                paint.textSize = 60f
                canvas.drawText("II", pauseButtonRect.left + 35f, pauseButtonRect.top + 80f, paint)

                // Рисуем счет очков ниже кнопки паузы
                paint.color = Color.WHITE
                paint.textSize = 50f
                canvas.drawText("Очки: $score", 30f, 180f, paint)
                canvas.drawText("Жизни: $lives", width - 250f, 80f, paint)

                if (isGameOver) {
                    paint.color = Color.RED
                    paint.textSize = 100f
                    val text = "ИГРА ОКОНЧЕНА"
                    val textWidth = paint.measureText(text)
                    canvas.drawText(text, width / 2f - textWidth / 2, height / 2f, paint)
                }

                if (isPaused && resumeCountdown > 0) {
                    paint.color = Color.argb(180, 0, 0, 0)
                    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                    paint.color = Color.WHITE
                    paint.textSize = 150f
                    val countdownText = resumeCountdown.toString()
                    val textWidth = paint.measureText(countdownText)
                    canvas.drawText(countdownText, width / 2f - textWidth / 2, height / 2f, paint)
                }
            }
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        playerRect.set(
            width / 2f - playerWidth / 2,
            height - playerHeight - 50f,
            width / 2f + playerWidth / 2,
            height - 50f
        )

        if (!isRunning) {
            isRunning = true
            gameThread = Thread(this)
            gameThread?.start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGame()
    }

    private fun showPauseMenu() {
        (context as FruitTakerActivity).runOnUiThread {
            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 50, 50, 50)
                setBackgroundColor(Color.argb(220, 40, 40, 40))

                val title = TextView(context).apply {
                    text = "ПАУЗА"
                    setTextColor(Color.YELLOW)
                    textSize = 28f
                    gravity = android.view.Gravity.CENTER
                    setPadding(0, 0, 0, 30)
                }
                addView(title)

                val scoreText = TextView(context).apply {
                    text = "Ваш счет: $score"
                    setTextColor(Color.WHITE)
                    textSize = 24f
                    gravity = android.view.Gravity.CENTER
                }
                addView(scoreText)
            }

            AlertDialog.Builder(context)
                .setView(layout)
                .setPositiveButton("Вернуться") { _, _ ->
                    resumeAfterDelay()
                }
                .setNegativeButton("В меню") { _, _ ->
                    apiService = RetrofitClient.getApiService()
                    apiService.addCash((DataManager.appContext as App).token, UserAddCashDTO(score, false)).enqueue(
                        object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                            override fun onFailure(call: Call<Void>, t: Throwable) {}
                        }
                    )
                    context.startActivity(Intent(DataManager.appContext, MainGameScreen::class.java))
                }
                .setCancelable(false)
                .create()
                .also { dialog ->
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.show()

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                        setTextColor(Color.WHITE)
                        setBackgroundColor(Color.argb(100, 0, 150, 0))
                    }
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                        setTextColor(Color.WHITE)
                        setBackgroundColor(Color.argb(100, 150, 0, 0))
                    }
                }
        }
    }

    private fun resumeAfterDelay() {
        isPaused = true
        postDelayed({
            isPaused = false
            backgroundMusic.start()
        }, 2000) // 2 секунды задержки
    }

    private fun showGameOver() {
        (context as FruitTakerActivity).runOnUiThread {
            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 50, 50, 50)
                setBackgroundColor(Color.argb(220, 40, 40, 40))

                val title = TextView(context).apply {
                    text = "ИГРА ОКОНЧЕНА"
                    setTextColor(Color.RED)
                    textSize = 28f
                    gravity = android.view.Gravity.CENTER
                    setPadding(0, 0, 0, 30)
                }
                addView(title)

                val scoreText = TextView(context).apply {
                    text = "Ваш счет: $score"
                    setTextColor(Color.WHITE)
                    textSize = 24f
                    gravity = android.view.Gravity.CENTER
                }
                addView(scoreText)
            }
            apiService = RetrofitClient.getApiService()
            apiService.addCash((DataManager.appContext as App).token, UserAddCashDTO(score, false)).enqueue(
                object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                }
            )
            apiService.setState((DataManager.appContext as App).token, UserSetStateDTO("happiness", 10)).enqueue(
                object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {}
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                }
            )


            AlertDialog.Builder(context)
                .setView(layout)
                .setPositiveButton("Заново") { _, _ ->
                    resetGame()
                    backgroundMusic.start()
                }
                .setNegativeButton("В меню") { _, _ ->
                    context.startActivity(Intent(context, MainGameScreen::class.java))
                }
                .setCancelable(false)
                .create()
                .also { dialog ->
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.show()

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                        setTextColor(Color.WHITE)
                        setBackgroundColor(Color.argb(100, 0, 150, 0))
                    }
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                        setTextColor(Color.WHITE)
                        setBackgroundColor(Color.argb(100, 150, 0, 0))
                    }
                }
        }
    }

    private fun resetGame() {
        fallingObjects.clear()
        score = 0
        lives = 3
        difficultyLevel = 0
        gameSpeed = 1f
        spawnInterval = 1000L
        isGameOver = false
        isPaused = false
        resumeCountdown = 0
        pausedBackground = null
        gifStartTime = System.currentTimeMillis()
        lastDifficultyIncreaseTime = System.currentTimeMillis()
        backgroundMusic.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameOver) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (pauseButtonRect.contains(event.x, event.y) && !isPaused) {
                    isPaused = true
                    backgroundMusic.pause()
                    showPauseMenu()
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isPaused) {
                    playerRect.offsetTo(event.x - playerRect.width() / 2, playerRect.top)

                    if (playerRect.left < 0) playerRect.offsetTo(0f, playerRect.top)
                    if (playerRect.right > width) playerRect.offsetTo(width - playerRect.width(), playerRect.top)
                    return true
                }
            }
        }
        return true
    }

    private fun stopGame() {
        isRunning = false
        gameThread?.join()
        soundPool.release()
        backgroundMusic.release()
    }
}

data class FallingObject(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val speed: Float,
    val type: ObjectType,
    val bitmap: Bitmap
) {
    val rect = RectF(x, y, x + width, y + height)
}