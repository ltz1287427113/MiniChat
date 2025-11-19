package com.example.minichat.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.databinding.ActivityChatInfoBinding;

public class ChatInfoActivity extends AppCompatActivity {

    private ActivityChatInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 获取传递过来的聊天对象名字
        String chatName = getIntent().getStringExtra("CHAT_NAME");
        if (chatName == null) chatName = "用户";

        // 2. 初始化 Toolbar
        setupToolbar();

        // 3. 初始化视图数据
        setupViews(chatName);

        // 4. 设置点击事件
        setupListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews(String name) {
        // 设置头像下的名字
        binding.tvName.setText(name);
    }

    private void setupListeners() {

        // 点击 "查找聊天记录"
        binding.rowSearchHistory.setOnClickListener(v -> {
            Toast.makeText(this, "点击了查找聊天记录", Toast.LENGTH_SHORT).show();
            // TODO:
        });

        // 点击 "清空聊天记录"
        binding.rowClearHistory.setOnClickListener(v -> {
            Toast.makeText(this, "点击了清空聊天记录", Toast.LENGTH_SHORT).show();
            // TODO: 调用 ViewModel -> Repository -> DAO 来删除数据库中的消息
        });
    }
}