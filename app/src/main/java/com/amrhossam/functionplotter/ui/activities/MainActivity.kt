package com.amrhossam.functionplotter.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
            seriesList.clear()
            binding.lineChart.clear()
            setDataToLineChart(min, max, exp)

        }
        //OBSERVING GENERATED DATA FROM THE VIEW MODEL
        viewModel.generatedListLiveData.observe(this) {
            // it is a pointer pointing in our generated list
            // passing it to showChart Method to start drawing points
            showChart(it)
        }
    }

    private fun initLineChart() {
//      enable grid line
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
//        xAxis.axisMaximum = 20f
//        xAxis.axisMinimum = 1f
        binding.lineChart.isDragEnabled = true
        binding.lineChart.setScaleEnabled(true)
        binding.lineChart.setPinchZoom(true)
        //to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)
        //Size on x,y diffrence
        xAxis.granularity = 1f
        yAxis.granularity = 1f

        xAxis.labelRotationAngle = +90f
    }

    private fun setDataToLineChart(min: Int, max: Int, exp: String) {
        binding.loading.progress.visibility = View.VISIBLE
        //seriesList = getSeriesList(min, max, exp)
        viewModel.generateSeries(min, max, exp)

        //back again to ui thread
        //showChart(seriesList)
    }

    private fun showChart(entries: ArrayList<Entry>) {
        //setting progress visibility gone
        binding.loading.progress.visibility = View.GONE
        //give line chart the dataset entries
        val lineDataSet = LineDataSet(entries, "")
        lineDataSet.lineWidth = 3f
        lineDataSet.circleRadius = 4f
        lineDataSet.valueTextSize = 8f
        val data = LineData(lineDataSet)
        binding.lineChart.data = data
        binding.lineChart.invalidate()
    }
//
//    @DelicateCoroutinesApi
//    private suspend fun getSeriesList(min: Int, max: Int, exp: String): ArrayList<Entry> {
//        // running thread to start calculating points(x,y) using kotlin Coroutines
//        val ex = Expression(exp)
//        return GlobalScope.async(Dispatchers.IO) {
//            Log.e("Exp", exp)
//            for (x in min..max) {
//                // assign x = values from min..to...max
//                val arg = Argument("x = $x")
//                //adding arguments to our expression
//                ex.addArguments(arg)
//                //evaluate expression to get y points
//                val expressionResult = ex.calculate()
//                //adding x,y to series list
//                seriesList.add(Entry(x.toFloat(), expressionResult.toFloat()))
//                Log.d("Series,", "$x || $expressionResult")
//                //remove argument after calculating
//                ex.removeArguments(arg)
//            }
//            return@async seriesList
//        }.await()
//    }

}