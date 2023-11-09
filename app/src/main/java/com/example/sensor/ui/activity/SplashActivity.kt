package com.example.sensor.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sensor.databinding.ActivitySplashBinding
import com.example.sensor.R
import com.example.sensor.persistence.user.UsersDatabase
import com.example.sensor.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SplashActivity: AppCompatActivity() {

    private val viewBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.splash.setAnimation(R.raw.car_loading)
        viewBinding.splash.playAnimation()
        lifecycleScope.launch {
            delay(1000)
            val sp = getSharedPreferences("remember_user", Context.MODE_PRIVATE)
            val userName = sp.getString("user_name", "")
            val password = sp.getString("password", "")
            runBlocking(Dispatchers.IO) {
                if (userName == null || password == null) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val users = UsersDatabase.getInstance(applicationContext).queryUsers()
                    if(users.isNullOrEmpty()) {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        var isSuccess = false
                        users.forEach {
                            if(it.name == userName && it.password == password) {
                                isSuccess = true
                                UsersDatabase.getInstance(applicationContext).setLoginUser(it)
                                return@forEach
                            }
                        }

                        if(isSuccess) {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        } else {
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }
    }
}