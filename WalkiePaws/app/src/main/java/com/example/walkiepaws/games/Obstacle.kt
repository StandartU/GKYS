package com.example.walkiepaws.games

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.example.walkiepaws.R

class Obstacle(
    var x: Float,
    val y: Float,
    private val width: Float,
    private val height: Float,
    private val context: Context,
    val groupId: Int
) {
    // Увеличиваем размеры препятствий
    companion object {
        val DEFAULT_WIDTH = 150f  // Было 90f
        val DEFAULT_HEIGHT = 220f // Было 140f
    }
    var isScored = false
    private val paint = Paint().apply {
        color = Color.RED
    }

    private val obstacleImage: Bitmap? by lazy {
        ContextCompat.getDrawable(context, R.drawable.zabor_cr)?.let { drawable ->
            Bitmap.createBitmap(
                width.toInt(),
                height.toInt(),
                Bitmap.Config.ARGB_8888
            ).apply {
                val canvas = Canvas(this)
                drawable.setBounds(0, 0, width.toInt(), height.toInt())
                drawable.draw(canvas)
            }
        }
    }

    fun update(speed: Float) {
        x -= speed
    }

    fun draw(canvas: Canvas) {
        obstacleImage?.let {
            canvas.drawBitmap(it, x, y, paint)
        } ?: run {
            canvas.drawRect(x, y, x + width, y + height, paint)
        }
    }

    fun isPassed(cheetahX: Float): Boolean {
        return x + width < cheetahX
    }

    fun isOffScreen(): Boolean {
        return x + width < 0
    }
}