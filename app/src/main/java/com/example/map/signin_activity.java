package com.example.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class signin_activity extends AppCompatActivity {
    Toolbar toolbar;
    EditText username;
    EditText password;
    EditText doublePassword;
    Button register;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // 初始化控件
        toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        doublePassword = findViewById(R.id.double_password);
        register = findViewById(R.id.register);
        dbHelper = new DatabaseHelper(signin_activity.this);

        // 返回事件
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // 当点击返回后，结束注册页面
            }
        });

        // 注册事件
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_str = username.getText().toString().trim();
                String password_str = password.getText().toString().trim();
                String doublePassword_str = doublePassword.getText().toString().trim();

                // 定义一个正则表达式，只允许字母、数字和下划线
                String regex = "^[a-zA-Z0-9_]+$";
                if (username_str.isEmpty() || password_str.isEmpty()) { // 是否为空
                    Toast.makeText(signin_activity.this, "Username or password cannot be empty!", Toast.LENGTH_SHORT).show();
                } else if (!username_str.matches(regex) || !password_str.matches(regex)) {  // 是否有非法字符
                    Toast.makeText(signin_activity.this, "Username or password contains invalid characters!", Toast.LENGTH_SHORT).show();
                } else if (!password_str.equals(doublePassword_str)) {  // 密码二次确认
                    Toast.makeText(signin_activity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                } else {
                    // 检查用户名是否已存在
                    if (dbHelper.usernameExists(username_str)) {
                        Toast.makeText(signin_activity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        // 注册用户并添加到数据库
                        dbHelper.addUser(username_str, password_str);
                        Toast.makeText(signin_activity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }
}