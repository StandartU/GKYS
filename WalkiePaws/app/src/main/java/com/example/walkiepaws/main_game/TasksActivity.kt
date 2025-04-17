package com.example.walkiepaws.main_game
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkiepaws.R

class TasksActivity : AppCompatActivity() {

    private lateinit var task1Progress: ProgressBar
    private lateinit var task2Progress: ProgressBar
    private lateinit var task3Progress: ProgressBar
    private lateinit var task1Checkbox: CheckBox
    private lateinit var task2Checkbox: CheckBox
    private lateinit var task3Checkbox: CheckBox
    private lateinit var task1text: TextView
    private lateinit var task2text: TextView
    private lateinit var task3text: TextView
    private lateinit var task1Reward: TextView
    private lateinit var task2Reward: TextView
    private lateinit var task3Reward: TextView
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        updateTaskProgress()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.getInstance(this).play()
    }

    override fun onPause() {
        super.onPause()
        if (!isChangingConfigurations) {
            MusicManager.getInstance(this).pause()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


    @SuppressLint("SetTextI18n")
    private fun initViews() {
        task1Progress = findViewById(R.id.task1Progress)
        task2Progress = findViewById(R.id.task2Progress)
        task3Progress = findViewById(R.id.task3Progress)
        task1Checkbox = findViewById(R.id.task1Checkbox)
        task2Checkbox = findViewById(R.id.task2Checkbox)
        task3Checkbox = findViewById(R.id.task3Checkbox)
        task1text = findViewById(R.id.task1Text)
        task2text = findViewById(R.id.task2Text)
        task3text = findViewById(R.id.task3Text)
        task1Reward = findViewById(R.id.task1Reward)
        task2Reward = findViewById(R.id.task2Reward)
        task3Reward = findViewById(R.id.task3Reward)

        task1text.text = DataManager.tasks["feed"]!!.task.description
        task2text.text = DataManager.tasks["game"]!!.task.description
        task3text.text = DataManager.tasks["walk"]!!.task.description
        task1Reward.text = "Приз: " + DataManager.tasks["feed"]!!.task.revard.toString() + " монет"
        task2Reward.text = "Приз: " + DataManager.tasks["game"]!!.task.revard.toString() + " монет"
        task3Reward.text = "Приз: " + DataManager.tasks["walk"]!!.task.revard.toString() + " монет"

        task1Progress.max = DataManager.tasks["feed"]!!.task.target
        task2Progress.max = DataManager.tasks["game"]!!.task.target
        task3Progress.max = DataManager.tasks["walk"]!!.task.target
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.imageBack).setOnClickListener {
            finish() //
        }
    }

    private fun updateTaskProgress() {
        if (task1Progress.progress < task1Progress.max) {
            task1Progress.progress = DataManager.tasks["feed"]?.value!!
            if (task1Progress.progress >= task1Progress.max) {
                task1Checkbox.isChecked = true
            }
        }

        if (task2Progress.progress < task2Progress.max) {
            task2Progress.progress = DataManager.tasks["game"]?.value!!
            if (task2Progress.progress >= task2Progress.max) {
                task2Checkbox.isChecked = true
            }
        }

        if (task3Progress.progress < task3Progress.max) {
            task3Progress.progress = DataManager.tasks["walk"]?.value!!
            if (task3Progress.progress >= task3Progress.max) {
                task3Checkbox.isChecked = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}