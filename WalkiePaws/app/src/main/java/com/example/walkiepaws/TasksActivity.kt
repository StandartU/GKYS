package com.example.walkiepaws
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class TasksActivity : AppCompatActivity() {

    private lateinit var task1Progress: ProgressBar
    private lateinit var task2Progress: ProgressBar
    private lateinit var task3Progress: ProgressBar
    private lateinit var task1Checkbox: CheckBox
    private lateinit var task2Checkbox: CheckBox
    private lateinit var task3Checkbox: CheckBox
    private lateinit var handler: Handler
    private val updateProgress = object : Runnable {
        override fun run() {
            updateTaskProgress()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        initViews()
        setupClickListeners()
        startProgressUpdates()
    }

    private fun initViews() {
        task1Progress = findViewById(R.id.task1Progress)
        task2Progress = findViewById(R.id.task2Progress)
        task3Progress = findViewById(R.id.task3Progress)
        task1Checkbox = findViewById(R.id.task1Checkbox)
        task2Checkbox = findViewById(R.id.task2Checkbox)
        task3Checkbox = findViewById(R.id.task3Checkbox)

        task1Progress.max = 3
        task2Progress.max = 5
        task3Progress.max = 15
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.imageBack).setOnClickListener {
            finish() //
        }
    }

    private fun startProgressUpdates() {
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(updateProgress, 500)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTaskProgress() {
        if (task1Progress.progress < task1Progress.max) {
            task1Progress.progress += 1
            if (task1Progress.progress == task1Progress.max) {
                task1Checkbox.isChecked = true
            }
        }

        if (task2Progress.progress < task2Progress.max) {
            task2Progress.progress += 1
            if (task2Progress.progress == task2Progress.max) {
                task2Checkbox.isChecked = true
            }
        }

        if (task3Progress.progress < task3Progress.max) {
            task3Progress.progress += 1
            if (task3Progress.progress == task3Progress.max) {
                task3Checkbox.isChecked = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgress)
    }
}