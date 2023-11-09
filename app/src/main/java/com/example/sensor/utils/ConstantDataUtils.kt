package com.example.sensor.utils

import com.example.sensor.R

object ConstantDataUtils {

    private val lottieList = arrayListOf(R.raw.boy_book, R.raw.girl_book, R.raw.boy_run, R.raw.girl_tree)

    fun getAvatarId(): Int {
        val random = (0..<lottieList.size).random()
        return lottieList[random]
    }
}