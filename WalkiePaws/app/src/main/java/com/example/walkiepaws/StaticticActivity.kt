package com.example.walkiepaws

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class StaticticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statictic)



        val lineChart: LineChart = findViewById(R.id.lineChart)


        val entries = ArrayList<Entry>()
        entries.add(Entry(0f, 0f))
        entries.add(Entry(1f, 5f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 6f))
        entries.add(Entry(4f, 3f))
        entries.add(Entry(5f, 7f))
        entries.add(Entry(6f, 4f))


        val lineDataSet = LineDataSet(entries, "Шаги")
        lineDataSet.color = resources.getColor(R.color.main_color_auth)
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = resources.getColor(R.color.ddd)


        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.invalidate()


        lineChart.setBackgroundColor(resources.getColor(R.color.bg_auth))


        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.isDragEnabled = true

    }
}
