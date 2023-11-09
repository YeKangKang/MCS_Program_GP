package com.example.sensor.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sensor.databinding.ActivitySigninBinding
import com.example.sensor.persistence.user.User
import com.example.sensor.persistence.user.UsersDatabase
import com.example.sensor.utils.ConstantDataUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class SignInActivity: AppCompatActivity() {

    private val viewBinding: ActivitySigninBinding by lazy {
        ActivitySigninBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.toolbar.setOnClickListener{
            finish()
        }

        viewBinding.register.setOnClickListener {
            val userName = viewBinding.username.text.trim().toString()
            val email = viewBinding.email.text.trim().toString()
            val password = viewBinding.password.text.trim().toString()
            val doublePassword = viewBinding.doublePassword.text.trim().toString()

            // 定义一个正则表达式，只允许字母、数字和下划线
            val regex = "^[a-zA-Z0-9_]+$"
            if (userName.isEmpty() || password.isEmpty() || email.isEmpty()) { // 是否为空
                showToast("Username, email or password cannot be empty!")
            } else if (!userName.matches(regex.toRegex()) || !password.matches(regex.toRegex())) {  // 是否有非法字符
                showToast("Username or password contains invalid characters!")
            } else if (password != doublePassword) {  // 密码二次确认
                showToast("Passwords do not match!")
            } else {
                runBlocking(Dispatchers.IO) {
                    val userList = UsersDatabase.getInstance(applicationContext).queryUsers()
                    if(userList.isNullOrEmpty()) {
                        val user = User(email, userName, getGender(), ConstantDataUtils.getAvatarId(), password, 0.0)
                        UsersDatabase.getInstance(applicationContext).insertUser(user)
                        showToast("Registered successfully!")
                        finish()
                    } else {
                        var regState = true
                        userList.forEach{
                            if (it.name == userName) {
                                showToast("Duplicate user name!")
                                regState = false
                                return@forEach
                            }

                            if (it.email == email) {
                                showToast("Do not use the same email address to register again!")
                                regState = false
                                return@forEach
                            }
                        }

                        if(regState) {
                            val user = User(email, userName, getGender(), ConstantDataUtils.getAvatarId(), password, 0.0)
                            UsersDatabase.getInstance(applicationContext).insertUser(user)
                            showToast("Registered successfully!")
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun getGender(): String {
        return if(viewBinding.radioMale.isChecked) {
            "Male"
        } else if(viewBinding.radioFemale.isChecked) {
            "Female"
        } else {
            "Refuse to reveal"
        }
    }

    private fun showToast(content: String) {
        runOnUiThread {
            Toast.makeText(this@SignInActivity, content, Toast.LENGTH_SHORT).show()
        }
    }
}