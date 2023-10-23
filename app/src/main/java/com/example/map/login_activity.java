package com.example.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login_activity extends AppCompatActivity {

    TextView register;
    EditText username;
    EditText password;
    Button login;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化控件 init
        register = findViewById(R.id.register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        dbHelper = new DatabaseHelper(login_activity.this);

        // 设置点击事件 Click Action
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_activity.this, signin_activity.class);
                startActivity(intent);
            }
        });

        // 登录事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_str = username.getText().toString().trim();
                String password_str = password.getText().toString().trim();

                // 定义一个正则表达式，只允许字母、数字和下划线
                String regex = "^[a-zA-Z0-9_]+$";
                if (username_str.isEmpty() || password_str.isEmpty()) {
                    Toast.makeText(login_activity.this, "Username or password cannot be empty!", Toast.LENGTH_SHORT).show();
                } else if (!username_str.matches(regex) || !password_str.matches(regex)) {
                    Toast.makeText(login_activity.this, "Username or password contains invalid characters!", Toast.LENGTH_SHORT).show();
                } else {
                    // 登录操作
                    if (dbHelper.validateUser(username_str, password_str)) {
                        // 用户名和密码验证成功，跳转到主页
                        Toast.makeText(login_activity.this, "Login success, redirecting...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(login_activity.this, MainActivity.class);  // 修改这里，设置目标为MainActivity
                        startActivity(intent);
                        finish();  // 结束当前Activity，确保用户按“返回”按钮时不会返回到登录页面
                    } else {
                        // 用户名或密码错误
                        Toast.makeText(login_activity.this, "Incorrect username or password, please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}