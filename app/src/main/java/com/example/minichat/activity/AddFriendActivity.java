package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.databinding.ActivityAddFriendBinding;

/**
 * [注释]
 * "添加朋友" 页面
 */
public class AddFriendActivity extends AppCompatActivity {

    private ActivityAddFriendBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupListeners();
    }

    private void setupToolbar() {
        // 设置返回按钮点击事件
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        // 1. 点击搜索条
        binding.btnSearch.setOnClickListener(v -> {
            // [修改] 跳转到 SearchUserActivity
            Intent intent = new Intent(this, SearchUserActivity.class);
            startActivity(intent);
        });

        // 2. 点击 "扫一扫"
        binding.btnScan.setOnClickListener(v -> {
            // TODO: 跳转到扫码页面 (ScanActivity)
            // 你可以直接复用之前的 MyQrCodeActivity 或者是打开相机的扫码页
            Toast.makeText(this, "打开扫一扫", Toast.LENGTH_SHORT).show();
        });
    }
}