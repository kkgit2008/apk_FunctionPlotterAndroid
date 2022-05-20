package com.amrhossam.functionplotter.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.amrhossam.functionplotter.R
import com.amrhossam.functionplotter.databinding.ActivityMainBinding
import com.amrhossam.functionplotter.ui.utils.DialogUtils
import com.amrhossam.functionplotter.ui.utils.ValidationHelper
import com.amrhossam.functionplotter.ui.viewModel.MainViewModel
import com.amrhossam.functionplotter.ui.viewModel.MainViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var seriesList = ArrayList<Entry>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    @DelicateCoroutinesApi
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

    @DelicateCoroutinesApi
    private fun handleOnPlot() {
        binding.plot.setOnClickListener {
            val min: String = binding.min.text.toString()
            val max: String = binding.max.text.toString()
            val exp: String = binding.enterExp.text.toString().trim()
            if (ValidationHelper.isNotEmpty(this@MainActivity, exp, min, max)) {
                //re initiating dataset and lien chart
                seriesList.clear()
                binding.lineChart.clear()
                // start generating data set
                setDataToLineChart(min.toFloat(), max.toFloat(), exp.lowercase(Locale.getDefault()))
            }
        }
        //Start observing data
        observingGeneratedData()
    }

    private fun initLineChart() {
//      Enable grid line
        binding.lineChart.axisLeft.setDrawGridLines(true)
        val xAxis: XAxis = binding.lineChart.xAxis
        val yAxis: YAxis = binding.lineChart.axisLeft

        xAxis.setDrawGridLines(true)
        xAxis.setDrawAxisLine(true)
        //Enabling right Y-axis
        binding.lineChart.axisRight.isEnabled = false
        //Remove legend
        binding.lineChart.legend.isEnabled = false
        //Remove description label
        binding.lineChart.description.isEnabled = false
        //Setting background for line chart
        binding.lineChart.setBackgroundColor(ContextCompat.getColor(this, R.color.darkerGray2))
        //Drawing point over time and use animation
        binding.lineChart.animateX(1500, Easing.EaseInExpo)
        //Scaling options
        binding.lineChart.isDragEnabled = true
        binding.lineChart.setScaleEnabled(true)
        binding.lineChart.setPinchZoom(true)
        binding.lineChart.setTouchEnabled(true)
        //To draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)
        //Size on x,y difference
        xAxis.granularity = 1f
        yAxis.granularity = 1f
        //changing text colors
        binding.lineChart.xAxis.textColor = ContextCompat.getColor(
            this, R.color.white
        )
        yAxis.textColor = ContextCompat.getColor(
            this, R.color.white
        )
        //No data text
        binding.lineChart.setNoDataText("")
        //xAxis Label rotation
        xAxis.labelRotationAngle = +90f
    }

    private fun observingGeneratedData() {
        //OBSERVING GENERATED DATA FROM THE VIEW MODEL
        viewModel.generatedListLiveData.observe(this) {
            // "it" holding our generated list
            // passing it to showChart Method to start drawing points
            if (it.isNotEmpty()) {
                binding.noChartDataView.getRoot().visibility = View.GONE
                binding.lineChart.visibility = View.VISIBLE
            }
            showChart(it)
        }
        viewModel.isEmptyData.observe(this) {
            if (it) {
                binding.noChartDataView.getRoot().visibility = View.VISIBLE

                binding.lineChart.visibility = View.GONE
                binding.loading.progress.visibility = View.GONE
            }
        }
    }

    @DelicateCoroutinesApi
    private fun setDataToLineChart(min: Float, max: Float, exp: String) {
        //binding.loading.progress.visibility = View.VISIBLE
        //start generating data set in background using threads
        viewModel.generateSeries(
            this,
            min,
            max,
            exp,
            binding.loading.progress
        )
    }

    private fun showChart(entries: ArrayList<Entry>) {
        //setting progress visibility gone
       // Log.e("entries size", entries.size.toString() + " l")
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
        lineData.color = ContextCompat.getColor(this@MainActivity, R.color.primary)
        //values text color
        lineData.valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.white)
        //change the color of the circle
        lineData.setCircleColor(ContextCompat.getColor(this@MainActivity, R.color.primary))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    @DelicateCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about -> {
                DialogUtils.showErrorDialog(
                    this,
                    getString(R.string.author),
                    getString(R.string.about_the_author)
                )
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}