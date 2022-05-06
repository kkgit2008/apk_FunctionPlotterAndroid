package com.amrhossam.functionplotter.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class MainViewModelFactory : ViewModelProvider.Factory {
    // We are using view model factory to ensure that app didn't crash when activity not found
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel() as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }

}


