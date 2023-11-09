package com.example.sensor.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener
import com.baidu.mapapi.walknavi.model.IWRouteIconInfo
import com.baidu.mapapi.walknavi.model.RouteGuideKind
import com.baidu.mapapi.walknavi.model.WalkNaviDisplayOption
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener
import com.example.sensor.persistence.user.UsersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class WNaviGuideActivity: AppCompatActivity() {

    private val TAG = "WNaviGuideActivity"

    private val mNaviHelper: WalkNavigateHelper = WalkNavigateHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val walkNaviDisplayOption = WalkNaviDisplayOption()
            .showImageToAr(true) // 是否展示AR图片
            .showCalorieLayoutEnable(true) // 是否展示热量消耗布局
            .showLocationImage(true) // 是否展示视角切换资源


        mNaviHelper.setWalkNaviDisplayOption(walkNaviDisplayOption)

        try {
            val view = mNaviHelper.onCreate(this@WNaviGuideActivity)
            view?.let { setContentView(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mNaviHelper.setWalkNaviStatusListener(object : IWNaviStatusListener {
            override fun onWalkNaviModeChange(mode: Int, listener: WalkNaviModeSwitchListener) {
                Log.d(TAG, "onWalkNaviModeChange : $mode")
                mNaviHelper.switchWalkNaviMode(this@WNaviGuideActivity, mode, listener)
            }

            override fun onNaviExit() {
                Log.d(TAG, "onNaviExit")
                runBlocking(Dispatchers.IO) {
                    val random = Random.nextDouble(0.9,3.9)
                    val value = String.format("%.2f", random).toDouble()
                    UsersDatabase.getInstance(applicationContext).updateUserEnergy(value)
                    runOnUiThread {
                        Toast.makeText(this@WNaviGuideActivity, "The navigation generated ${value}kg of green energy", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        mNaviHelper.setTTsPlayer { s, b ->
            Log.d(TAG, "tts: $s")
            0
        }

        val startResult = mNaviHelper.startWalkNavi(this@WNaviGuideActivity)
        Log.e(TAG, "startWalkNavi result : $startResult")

        mNaviHelper.setRouteGuidanceListener(this, object : IWRouteGuidanceListener{

            override fun onRouteGuideIconInfoUpdate(p0: IWRouteIconInfo?) {
                Log.d(TAG, "onRouteGuideIconInfoUpdate")
            }

            override fun onRouteGuideIconUpdate(p0: Drawable?) {
                Log.d(TAG, "onRouteGuideIconUpdate")
            }

            override fun onRouteGuideKind(p0: RouteGuideKind?) {
                Log.d(TAG, "onRouteGuideKind")
            }

            override fun onRoadGuideTextUpdate(p0: CharSequence?, p1: CharSequence?) {
                Log.d(TAG, "onRoadGuideTextUpdate")
            }

            override fun onRemainDistanceUpdate(p0: CharSequence?) {
                Log.d(TAG, "onRemainDistanceUpdate")
            }

            override fun onRemainTimeUpdate(p0: CharSequence?) {
                Log.d(TAG, "onRemainTimeUpdate")
            }

            override fun onGpsStatusChange(p0: CharSequence?, p1: Drawable?) {
                Log.d(TAG, "onGpsStatusChange")
            }

            override fun onRouteFarAway(p0: CharSequence?, p1: Drawable?) {
                Log.d(TAG, "onRouteFarAway")
            }

            override fun onRoutePlanYawing(p0: CharSequence?, p1: Drawable?) {
                Log.d(TAG, "onRoutePlanYawing")
            }

            override fun onReRouteComplete() {
                Log.d(TAG, "onReRouteComplete")
            }

            override fun onArriveDest() {
                Log.d(TAG, "onArriveDest")
                val random = Random.nextDouble(1.9,9.9)
                val value = String.format("%.2f", random).toDouble()
                UsersDatabase.getInstance(applicationContext).updateUserEnergy(value)
                runOnUiThread {
                    Toast.makeText(this@WNaviGuideActivity, "The navigation generated ${value}kg of green energy", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onIndoorEnd(p0: Message?) {
                Log.d(TAG, "onIndoorEnd")
            }

            override fun onFinalEnd(p0: Message?) {
                Log.d(TAG, "onFinalEnd")
            }

            override fun onVibrate() {
                Log.d(TAG, "onVibrate")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mNaviHelper.quit()
    }

    override fun onResume() {
        super.onResume()
        mNaviHelper.resume()
    }

    override fun onPause() {
        super.onPause()
        mNaviHelper.pause()
    }
}