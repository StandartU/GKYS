package com.example.walkiepaws.main_game

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.walkiepaws.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.NumberFormat
import java.util.Locale
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter


class DayOfWeekFormatter : ValueFormatter() {
    private val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    override fun getFormattedValue(value: Float): String {
        return if (value >= 0 && value < daysOfWeek.size) daysOfWeek[value.toInt()] else ""
    }
}

class StatisticsActivity : AppCompatActivity() {
    private lateinit var lineChartStatistics: LineChart
    private lateinit var textNumberOfSteps: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()

        setupChart()

        loadTestStepData()

        setupBackButton()
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


    private fun initViews() {
        lineChartStatistics = findViewById(R.id.lineChartStatistics)
        textNumberOfSteps = findViewById(R.id.textNumberOfSteps)
    }

    private fun setupChart() {
        with(lineChartStatistics) {
            setBackgroundColor(getColorCompat(R.color.bg_auth))
            setExtraOffsets(50f, 5f, 50f, 5f)
            description.isEnabled = false
            axisRight.isEnabled = false
            xAxis.setDrawLabels(true)
            axisLeft.setDrawLabels(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)

            xAxis.valueFormatter = DayOfWeekFormatter()
            xAxis.granularity = 1f
            xAxis.textSize = 12f
            xAxis.labelRotationAngle = 0f

            setScaleEnabled(true)
            setPinchZoom(true)
        }
    }

    private fun loadTestStepData() {
        val testTotalSteps = DataManager.weekSteps.sum()
        val testDailySteps = DataManager.weekSteps

        updateStepCount(testTotalSteps)
        updateChart(testDailySteps)
    }

    private fun updateStepCount(count: Int) {
        textNumberOfSteps.text =
            getString(R.string.steps_count_template).format(count.formatWithSpaces())
    }

    private fun updateChart(dailySteps: List<Int>) {
        val entries = dailySteps.mapIndexed { index, steps ->
            Entry(index.toFloat(), steps.toFloat())
        }

        val dataSet = LineDataSet(entries, getString(R.string.steps_label)).apply {
            color = getColorCompat(R.color.ddd)
            setDrawCircles(true)
            setDrawFilled(true)
            fillColor = getColorCompat(R.color.ddd1)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 15f
            valueTextColor = getColorCompat(R.color.ddd)
        }

        lineChartStatistics.apply {
            legend.isEnabled = false
            alpha = 0f
            data = LineData(dataSet)
            animateY(2000, Easing.EaseInOutQuad)
            animate().alpha(1f).setDuration(1500).start()
            invalidate()
        }
    }
    private fun setupBackButton() {
        findViewById<ImageView>(R.id.imageBack).setOnClickListener {
            finish()
        }
    }

    private fun Int.formatWithSpaces(): String =
        NumberFormat.getNumberInstance(Locale.getDefault()).format(this)

    private fun Context.getColorCompat(@ColorRes color: Int): Int =
        ContextCompat.getColor(this, color)
}

