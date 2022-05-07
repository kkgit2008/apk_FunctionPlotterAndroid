package com.amrhossam.functionplotter.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.amrhossam.functionplotter.R
import com.amrhossam.functionplotter.databinding.ActivityMainBinding
import com.amrhossam.functionplotter.ui.viewModel.MainViewModel
import com.amrhossam.functionplotter.ui.viewModel.MainViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MainActivity : AppCompatActivity() {
    private var seriesList = ArrayList<Entry>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initLineChart()
        handleOnPlot()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, MainViewModelFactory()
        )[MainViewModel::class.java]
    }

    private fun handleOnPlot() {
        binding.plot.setOnClickListener {
            val min = binding.min.text.toString().toInt()
            val max = binding.max.text.toString().toInt()
            val exp = binding.enterExp.text.toString().trim()
            //re initiating dataset and lien chart
            seriesList.clear()
            binding.lineChart.clear()
            // start generating data set
            setDataToLineChart(min, max, exp)
        }
        //OBSERVING GENERATED DATA FROM THE VIEW MODEL
        viewModel.generatedListLiveData.observe(this) {
            // "it" is a pointer pointing in our generated list
            // passing it to showChart Method to start drawing points
            showChart(it)
        }
    }

    private fun initLineChart() {
//      Enable grid line
        binding.lineChart.axisLeft.setDrawGridLines(true)
        val xAxis: XAxis = binding.lineChart.xAxis
        val yAxis: YAxis = binding.lineChart.axisLeft
        xAxis.setDrawGridLines(true)
        xAxis.setDrawAxisLine(true)
        //enabling right Y-axis
        binding.lineChart.axisRight.isEnabled = true
        //remove legend
        binding.lineChart.legend.isEnabled = false
        //remove description label
        binding.lineChart.description.isEnabled = false
        //Drawing point over time and use animation
        binding.lineChart.animateX(1500, Easing.EaseInExpo)
//       xAxis.axisMaximum = 20f
//       xAxis.axisMinimum = 1f
        binding.lineChart.isDragEnabled = true
        binding.lineChart.setScaleEnabled(true)
        binding.lineChart.setPinchZoom(true)
        // binding.lineChart.xAxis.textColor = ContextCompat.getColor(this@MainActivity, R.color.yellow)
        //to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)
        //Size on x,y difference
        xAxis.granularity = 1f
        yAxis.granularity = 1f

        xAxis.labelRotationAngle = +90f
    }

    private fun setDataToLineChart(min: Int, max: Int, exp: String) {
        binding.loading.progress.visibility = View.VISIBLE
        //start generating data set in background using threads
        viewModel.generateSeries(min, max, exp)
    }

    private fun showChart(entries: ArrayList<Entry>) {
        //setting progress visibility gone
        binding.loading.progress.visibility = View.GONE
        //give line chart the dataset entries
        val lineDataSet = LineDataSet(entries, "")
        //init lineDataSet
        initChart(lineDataSet)
        //init line data
        val lineData = LineData(lineDataSet)
        //start viewing
        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }

    private fun initChart(lineData: LineDataSet) {
        //set line width
        lineData.lineWidth = 5f
        //circle radius
        lineData.circleRadius = 6f
        //value text size
        lineData.valueTextSize = 8f
        //change the color of the line
        lineData.color = ContextCompat.getColor(this@MainActivity, R.color.yellow)
        //change the color of the circle
        lineData.setCircleColor(ContextCompat.getColor(this@MainActivity, R.color.yellow))
    }
}