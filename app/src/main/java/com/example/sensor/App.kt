package com.example.sensor

import android.app.Application
import com.baidu.mapapi.SDKInitializer

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SDKInitializer.setAgreePrivacy(this, true)
        SDKInitializer.initialize(this)
    }
}