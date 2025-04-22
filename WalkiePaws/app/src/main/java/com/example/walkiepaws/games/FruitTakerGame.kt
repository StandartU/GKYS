package com.example.walkiepaws.games

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.media.AudioAttributes
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
import com.example.walkiepaws.main_game.MusicManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

enum class ObjectType(val score: Int) {
    ВИШНЯ(5),
    БАНАН(10),
    ВИНОГРАД(15),
    БОМБА(-30)
}

class FruitTakerActivity : AppCompatActivity() {
    private lateinit var gameView: GameViewFT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameViewFT(this)
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }
}

class GameViewFT(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private val musicManager = MusicManager.getInstance(context)
    private val prefs: SharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    // Game dimensions
    private val playerWidth = 200f
    private val playerHeight = 250f
    private val objectSize = 120f

    // Game parameters
    private var score = 0
    private var lives = 3
    private var gameSpeed = 1f
    private var difficultyLevel = 0
    private val difficultyInterval = 5000L
    private var gifStartTime: Long = 0

    // Spawn control
    private var lastSpawnTime = 0L
    private var spawnInterval = 1000L
    private var lastDifficultyIncreaseTime = 0L

    // Game state
    private var isPaused = false
    private var pauseStartTime = 0L
    private var resumeCountdown = 0
    private var pausedBackground: Bitmap? = null
    private var isRunning = false
    private var isGameOver = false

    // Game objects
    private val playerRect = RectF()
    private val fallingObjects = mutableListOf<FallingObject>()
    private var gameThread: Thread? = null
    private val surfaceHolder: SurfaceHolder = holder
    private val paint = Paint().apply { isAntiAlias = true }

    // Graphics
    private lateinit var backgroundBitmap: Bitmap
    private lateinit var playerGif: Movie
    private lateinit var grapeBitmap: Bitmap
    private lateinit var cherryBitmap: Bitmap
    private lateinit var bananaBitmap: Bitmap
    private lateinit var bombBitmap: Bitmap
    private lateinit var pauseButtonRect: RectF

    // Sound
    private lateinit var soundPool: SoundPool
    private var fruitSoundId = 0
    private var bombSoundId = 0
    private var loseSoundId = 0

    // API
    private lateinit var apiService: ApiService

    init {
        isFocusable = true
        holder.addCallback(this)
        loadGraphics()
        initSounds()
        apiService = RetrofitClient.getApiService()
    }

    @SuppressLint("ResourceType")
    private fun loadGraphics() {
        try {
            backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.fon_ft)

            resources.openRawResource(R.drawable.personazh_ft).use { stream ->
                playerGif = Movie.decodeStream(stream)
            }

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

        if (musicManager.isMusicEnabled()) {
            musicManager.playGameMusic(R.raw.fon_ft)
        }
    }

    override fun run() {
        gifStartTime = System.currentTimeMillis()
        lastDifficultyIncreaseTime = System.currentTimeMillis()

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
                Thread.currentThread().interrupt()
                return
            }
        }
    }

    private fun update(currentTime: Long) {
        if (resumeCountdown > 0) {
            handleResumeCountdown(currentTime)
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

        updateFallingObjects()
    }

    private fun handleResumeCountdown(currentTime: Long) {
        val elapsed = currentTime - pauseStartTime
        val newCountdown = 3 - elapsed / 1000
        if (newCountdown.toInt() != resumeCountdown) {
            resumeCountdown = newCountdown.toInt().coerceAtLeast(0)
            if (resumeCountdown == 0) {
                isPaused = false
                pausedBackground = null
                if (musicManager.isMusicEnabled()) {
                    musicManager.playGameMusic(R.raw.fon_ft)
                }
            }
            postInvalidate()
        }
    }

    private fun updateFallingObjects() {
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
        val bitmap = getBitmapForType(type)

        fallingObjects.add(FallingObject(
            x = Random.nextInt(0, width - objectSize.toInt()).toFloat(),
            y = -objectSize,
            width = objectSize,
            height = objectSize,
            speed = speed.toFloat(),
            type = type,
            bitmap = bitmap
        ))
    }

    private fun getBitmapForType(type: ObjectType): Bitmap {
        return when (type) {
            ObjectType.ВИШНЯ -> cherryBitmap
            ObjectType.БАНАН -> bananaBitmap
            ObjectType.ВИНОГРАД -> grapeBitmap
            ObjectType.БОМБА -> bombBitmap
        }
    }

    private fun handleCollision(type: ObjectType) {
        score = (score + type.score).coerceAtLeast(0)
        if (type.score < 0) {
            lives--
            if (musicManager.areSoundsEnabled()) {
                soundPool.play(bombSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
            }
            if (lives <= 0) {
                endGame()
            }
        } else if (musicManager.areSoundsEnabled()) {
            soundPool.play(fruitSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }

    private fun endGame() {
        isGameOver = true
        if (musicManager.areSoundsEnabled()) {
            soundPool.play(loseSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
        musicManager.pauseGameMusic()
        showGameOver()
    }

    private fun drawFrame() {
        val canvas = surfaceHolder.lockCanvas() ?: return

        try {
            synchronized(surfaceHolder) {
                drawBackground(canvas)

                if (!isPaused || resumeCountdown > 0) {
                    drawFallingObjects(canvas)
                    drawPlayer(canvas)
                }

                drawUI(canvas)
                drawGameStateOverlays(canvas)
            }
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        if (isPaused && pausedBackground != null) {
            canvas.drawBitmap(pausedBackground!!, 0f, 0f, paint)
        } else {
            canvas.drawBitmap(backgroundBitmap, null, Rect(0, 0, width, height), paint)

            if (isPaused && pausedBackground == null) {
                savePausedBackground()
            }
        }
    }

    private fun savePausedBackground() {
        pausedBackground = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val tempCanvas = Canvas(this)
            tempCanvas.drawBitmap(backgroundBitmap, null, Rect(0, 0, width, height), paint)
            fallingObjects.forEach { obj ->
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

    private fun drawFallingObjects(canvas: Canvas) {
        fallingObjects.forEach { obj ->
            canvas.drawBitmap(obj.bitmap, obj.rect.left, obj.rect.top, paint)
        }
    }

    private fun drawPlayer(canvas: Canvas) {
        playerGif.setTime((System.currentTimeMillis() - gifStartTime).toInt() % playerGif.duration())
        canvas.save()
        canvas.translate(playerRect.left, playerRect.top)
        canvas.scale(
            playerRect.width() / playerGif.width(),
            playerRect.height() / playerGif.height()
        )
        playerGif.draw(canvas, 0f, 0f)
        canvas.restore()
    }

    private fun drawUI(canvas: Canvas) {
        // Pause button
        pauseButtonRect = RectF(30f, 30f, 130f, 130f)
        paint.color = Color.argb(150, 100, 100, 100)
        canvas.drawRoundRect(pauseButtonRect, 20f, 20f, paint)
        paint.color = Color.WHITE
        paint.textSize = 60f
        canvas.drawText("II", pauseButtonRect.left + 35f, pauseButtonRect.top + 80f, paint)

        // Score and lives
        paint.color = Color.WHITE
        paint.textSize = 50f
        canvas.drawText("Очки: $score", 30f, 180f, paint)
        canvas.drawText("Жизни: $lives", width - 250f, 80f, paint)
    }

    private fun drawGameStateOverlays(canvas: Canvas) {
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        playerRect.set(
            width / 2f - playerWidth / 2,
            height - playerHeight - 50f,
            width / 2f + playerWidth / 2,
            height - 50f
        )

        if (!isRunning) {
            isRunning = true
            gameThread = Thread(this).apply { start() }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) = stopGame()

    fun pauseGame() {
        isPaused = true
        musicManager.pauseGameMusic()
        showPauseMenu()
    }

    fun resumeGame() {
        if (!isPaused && !isGameOver && musicManager.isMusicEnabled()) {
            musicManager.playGameMusic(R.raw.fon_ft)
        }
    }

    private fun showPauseMenu() {
        (context as FruitTakerActivity).runOnUiThread {
            AlertDialog.Builder(context)
                .setView(createPauseMenuView())
                .setPositiveButton("Вернуться") { _, _ -> resumeAfterDelay() }
                .setNegativeButton("В меню") { _, _ -> exitToMenu() }
                .setCancelable(false)
                .create()
                .apply {
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    show()
                    styleDialogButtons(this)
                }
        }
    }

    private fun createPauseMenuView(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            setBackgroundColor(Color.argb(220, 40, 40, 40))

            addView(TextView(context).apply {
                text = "ПАУЗА"
                setTextColor(Color.YELLOW)
                textSize = 28f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 0, 0, 30)
            })

            addView(TextView(context).apply {
                text = "Ваш счет: $score"
                setTextColor(Color.WHITE)
                textSize = 24f
                gravity = android.view.Gravity.CENTER
            })
        }
    }

    private fun styleDialogButtons(dialog: AlertDialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.argb(100, 0, 150, 0))
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.argb(100, 150, 0, 0))
        }
    }

    private fun resumeAfterDelay() {
        isPaused = true
        pauseStartTime = System.currentTimeMillis()
        resumeCountdown = 3
        postInvalidate()
    }

    private fun exitToMenu() {
        submitScore()
        context.startActivity(Intent(context, MainGameScreen::class.java))
    }

    private fun showGameOver() {
        (context as FruitTakerActivity).runOnUiThread {
            AlertDialog.Builder(context)
                .setView(createGameOverView())
                .setPositiveButton("Заново") { _, _ -> resetGame() }
                .setNegativeButton("В меню") { _, _ -> exitToMenu() }
                .setCancelable(false)
                .create()
                .apply {
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    show()
                    styleDialogButtons(this)
                }
        }
    }

    private fun createGameOverView(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            setBackgroundColor(Color.argb(220, 40, 40, 40))

            addView(TextView(context).apply {
                text = "ИГРА ОКОНЧЕНА"
                setTextColor(Color.RED)
                textSize = 28f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 0, 0, 30)
            })

            addView(TextView(context).apply {
                text = "Ваш счет: $score"
                setTextColor(Color.WHITE)
                textSize = 24f
                gravity = android.view.Gravity.CENTER
            })
        }
    }

    private fun submitScore() {
        val token = (DataManager.appContext as App).token
        apiService.addCash(token, UserAddCashDTO(score, false)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {}
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
        apiService.setState(token, UserSetStateDTO("happiness", 10)).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {}
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
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
        if (musicManager.isMusicEnabled()) {
            musicManager.playGameMusic(R.raw.fon_ft)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameOver) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (pauseButtonRect.contains(event.x, event.y) && !isPaused) {
                    pauseGame()
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isPaused) {
                    updatePlayerPosition(event.x)
                    return true
                }
            }
        }
        return true
    }

    private fun updatePlayerPosition(touchX: Float) {
        playerRect.offsetTo(touchX - playerRect.width() / 2, playerRect.top)

        when {
            playerRect.left < 0 -> playerRect.offsetTo(0f, playerRect.top)
            playerRect.right > width -> playerRect.offsetTo(width - playerRect.width(), playerRect.top)
        }
    }

    private fun stopGame() {
        isRunning = false
        gameThread?.join()
        releaseResources()
    }

    private fun releaseResources() {
        soundPool.release()
        musicManager.stopGameMusic()
        backgroundBitmap.recycle()
        cherryBitmap.recycle()
        bananaBitmap.recycle()
        grapeBitmap.recycle()
        bombBitmap.recycle()
        pausedBackground?.recycle()
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