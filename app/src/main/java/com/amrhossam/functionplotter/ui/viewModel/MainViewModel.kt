package com.amrhossam.functionplotter.ui.viewModel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amrhossam.functionplotter.R
import com.amrhossam.functionplotter.ui.utils.DialogUtils
import com.amrhossam.functionplotter.ui.utils.ValidationHelper
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.*
import org.mariuszgromada.math.mxparser.Argument
import org.mariuszgromada.math.mxparser.Expression

class MainViewModel : ViewModel() {
    var generatedListLiveData = MutableLiveData<ArrayList<Entry>>()
    var isEmptyData = MutableLiveData(false)

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    @DelicateCoroutinesApi
    fun generateSeries(
        ctx: Context,
        min: Int,
        max: Int,
        exp: String,
        loading: View,
    ) {
        // running thread to start calculating points(x,y) using kotlin Coroutines
        if (ValidationHelper.validateExpression(ctx, exp, min, max)) {
            loading.visibility = View.VISIBLE
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
                    Log.d("Series,", "$x || $expressionResult")
                    //In case of getting wrong values because of wrong expression
                    //Validating expression again
                    if (expressionResult.toFloat().isNaN()) {
                        //Back again to ui thread to make hide progress bar
//                        GlobalScope.launch(Dispatchers.Main) {
//                            loading.visibility = View.GONE
//                            noViewChart.visibility = View.VISIBLE
//                        }
                        DialogUtils.showErrorDialog(
                            ctx,
                            ctx.getString(R.string.wrong_expression),
                            ctx.getString(R.string.wrong_formula)
                        )
                        isEmptyData.postValue(true)
                        return@launch
                    }
                    //Adding points to seriesList
                    seriesList.add(Entry(x.toFloat(), expressionResult.toFloat()))
                    //remove argument after calculating
                    ex.removeArguments(arg)
                }
                // passing data to livedata
                // using postValue instead of value = seriesList because it's called asynchronous
                // and we running a background thread
                generatedListLiveData.postValue(seriesList)
                //we have data
                isEmptyData.postValue(false)
            }

        } else {
            // there is no data we should notify ui !
            isEmptyData.postValue(true)
        }
    }

}
