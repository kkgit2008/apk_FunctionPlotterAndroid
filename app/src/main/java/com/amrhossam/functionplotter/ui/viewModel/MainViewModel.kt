package com.amrhossam.functionplotter.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression

class MainViewModel : ViewModel() {
    var generatedListLiveData = MutableLiveData<ArrayList<Entry>>()

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun generateSeries(min: Int, max: Int, exp: String) {
        // running thread to start calculating points(x,y) using kotlin Coroutines
        uiScope.launch {
            val seriesList = ArrayList<Entry>()
            val ex = Expression(exp)
            Log.e("Exp", exp)
            for (x in min..max) {
                // assign x = values from min..to...max
                val arg = Argument("x = $x")
                //adding arguments to our expression
                ex.addArguments(arg)
                //evaluate expression to get y points
                val expressionResult = ex.calculate()
                //adding x,y to series list
                //line chart takes array of entry object
                seriesList.add(Entry(x.toFloat(), expressionResult.toFloat()))
                Log.d("Series,", "$x || $expressionResult")
                //remove argument after calculating
                ex.removeArguments(arg)
            }
            // passing data to livedata
            // using postValue instead of value = seriesList because it's called asynchronous
            // and we running a background thread
            generatedListLiveData.postValue(seriesList)
        }

    }

}
