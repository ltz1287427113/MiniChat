package com.example.minichat.activity;

import android.os.Bundle;
import android.view.MenuItem; // 导入 MenuItem

import androidx.annotation.NonNull; // 导入 NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.minichat.R; // 导入 R
import com.example.minichat.adapter.MainViewPagerAdapter; // 导入 Adapter
import com.example.minichat.databinding.ActivityMainBinding; // [核心] 导入 ViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView; // 导入
import com.google.android.material.navigation.NavigationBarView; // 导入

public class MainActivity extends AppCompatActivity {

    // 1. ViewBinding
    private ActivityMainBinding binding;

    // 2. ViewPager2 的适配器
    private MainViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 3. ViewBinding 标准写法
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 4. 初始化 ViewPager2
        adapter = new MainViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // --- 核心：实现“联动” ---

        // 5. [联动-A] 监听 ViewPager2 的滑动，切换底部导航栏
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        binding.bottomNavView.setSelectedItemId(R.id.nav_chat);
                        break;
                    case 1:
                        binding.bottomNavView.setSelectedItemId(R.id.nav_contacts);
                        break;
                    case 2:
                        binding.bottomNavView.setSelectedItemId(R.id.nav_discover);
                        break;
                    case 3:
                        binding.bottomNavView.setSelectedItemId(R.id.nav_me);
                        break;
                }
            }
        });

        // 6. [联动-B] 监听底部导航栏的点击，切换 ViewPager2
        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_chat) {
                    binding.viewPager.setCurrentItem(0, true); // false 代表切换时不带滑动动画
                } else if (itemId == R.id.nav_contacts) {
                    binding.viewPager.setCurrentItem(1, true);
                } else if (itemId == R.id.nav_discover) {
                    binding.viewPager.setCurrentItem(2, true);
                } else if (itemId == R.id.nav_me) {
                    binding.viewPager.setCurrentItem(3, true);
                }
                return true; // true 代表消费了此事件
            }
        });
    }
}