package com.example.sensor.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.sensor.ui.main.MainActivity
import com.example.sensor.databinding.ActivityLoginBinding
import com.example.sensor.persistence.user.UsersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class LoginActivity: AppCompatActivity() {

    private val viewBinding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.register.setOnClickListener{
            startActivity(Intent(this@LoginActivity, SignInActivity::class.java))
        }

        viewBinding.login.setOnClickListener {
            val userName = viewBinding.username.text.trim().toString()
            val psw = viewBinding.password.text.trim().toString()

            // 定义一个正则表达式，只允许字母、数字和下划线
            val regex = "^[a-zA-Z0-9_]+$"
            if (userName.isEmpty() || psw.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Username or password cannot be empty!", Toast.LENGTH_SHORT).show()
            } else if (!userName.matches(regex.toRegex()) || !psw.matches(regex.toRegex())) {
                Toast.makeText(this@LoginActivity, "Username or password contains invalid characters!",Toast.LENGTH_SHORT).show()
            } else {
                runBlocking(Dispatchers.IO) {
                    val users = UsersDatabase.getInstance(applicationContext).queryUsers()
                    if(users.isNullOrEmpty()) {
                        showToast("The user information does not exist")
                    } else {
                        var isSuccess = false
                        users.forEach {
                            if(it.name == userName && it.password == psw) {
                                isSuccess = true
                                UsersDatabase.getInstance(applicationContext).setLoginUser(it)
                                return@forEach
                            }
                        }

                        if(isSuccess) {
                            showToast("Login successful")
                            val sp = getSharedPreferences("remember_user", Context.MODE_PRIVATE)
                            val edit = sp.edit()
                            if(viewBinding.remember.isChecked) {
                                edit.putString("user_name", userName)
                                edit.putString("password", psw)
                                edit.apply()
                            } else {
                                edit.clear()
                            }
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            showToast("Please check whether the user name or password is correct")
                        }
                    }
                }
            }
        }
    }

    private fun showToast(content: String) {
        runOnUiThread {
            Toast.makeText(this@LoginActivity, content, Toast.LENGTH_SHORT).show()
        }
    }
}