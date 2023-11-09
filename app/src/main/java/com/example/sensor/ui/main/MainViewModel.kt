package com.example.sensor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val totalText = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val total: LiveData<Double> = totalText

    fun setTotal(double: Double) {
        totalText.value?.plus(double)
    }
}