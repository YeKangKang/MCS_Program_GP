package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    ForumFragment forumFragment;
    PostFragment postFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 默认选中首页
        selectedFragment(0);

        // bottomNavigationView 切换点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment(0);
                } else if (item.getItemId() == R.id.nav_forum) {
                    selectedFragment(1);
                } else {
                    selectedFragment(2);
                }
                return true;
            }
        });
    }

    // 处理选中fragment和剩余fragment的显示或隐藏
    public void  selectedFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);

        if (position == 0) {    // 选中Home选项，如果没有创建则创建并绑定，如果已经创建，则显示
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.content, homeFragment);
            } else {
                fragmentTransaction.show(homeFragment);
            }
        } else if (position == 1) { // 选中Forum选项
            if (forumFragment == null) {
                forumFragment = new ForumFragment();
                fragmentTransaction.add(R.id.content, forumFragment);
            } else {
                fragmentTransaction.show(forumFragment);
            }
        } else {    // 选中Post
            if (postFragment == null) {
                postFragment = new PostFragment();
                fragmentTransaction.add(R.id.content, postFragment);
            } else {
                fragmentTransaction.show(postFragment);
            }
        }

        // 提交
        fragmentTransaction.commit();
    }

    // 隐藏方法
    public void hideFragment(FragmentTransaction fragmentTransaction) {
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        }

        if (forumFragment != null) {
            fragmentTransaction.hide(forumFragment);
        }

        if (postFragment != null) {
            fragmentTransaction.hide(postFragment);
        }
    }
}