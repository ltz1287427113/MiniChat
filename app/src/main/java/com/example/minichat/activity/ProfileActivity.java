package com.example.minichat.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.databinding.ActivityProfileBinding;

/**
 * [注释]
 * 个人资料页面 Activity。
 * 负责加载 activity_profile.xml 布局并处理所有点击事件。
 */
public class ProfileActivity extends AppCompatActivity {

    // 1. ViewBinding
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. [注释] 加载布局
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 3. [注释] 设置 Toolbar
        // (我们复用 ChatDetailActivity 的 Toolbar 样式)
        setupToolbar();

        // 4. [注释] 为每一行设置点击事件
        setupClickListeners();
    }

    /**
     * [注释]
     * 设置 Toolbar，包括标题和返回按钮。
     */
    private void setupToolbar() {
        // (设置标题为“个人资料”)
        binding.toolbar.setTitle("个人资料");

        // (设置返回按钮)
        binding.toolbar.setNavigationOnClickListener(v -> {
            finish(); // 关闭当前 Activity
        });
    }

    /**
     * [注释]
     * 为“头像”、“名字” 等所有行添加点击监听器。
     */
    private void setupClickListeners() {

        // [注释] 点击“头像”行
        binding.rowAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "点击了 头像", Toast.LENGTH_SHORT).show();
            // TODO: 在这里启动图片选择器或查看器
        });

        // [注释] 点击“名字”行
        binding.rowName.setOnClickListener(v -> {
            Toast.makeText(this, "点击了 名字", Toast.LENGTH_SHORT).show();
            // TODO: 在这里启动一个用于“文本编辑”的新 Activity
        });

        // [注释] 点击“邮箱”行 (你要求把“手机号”改成“邮箱”)
        binding.rowEmail.setOnClickListener(v -> {
            Toast.makeText(this, "点击了 邮箱", Toast.LENGTH_SHORT).show();
            // TODO: 在这里启动一个用于“文本编辑”的新 Activity
        });

        // [注释] 点击“微信号”行
        binding.rowWechatId.setOnClickListener(v -> {
            Toast.makeText(this, "点击了 微信号", Toast.LENGTH_SHORT).show();
            // (微信号通常不允许修改，可以只显示或提示)
            Toast.makeText(this, "微信号不支持修改", Toast.LENGTH_SHORT).show();
        });

        // [注释] 点击“我的二维码”行
        binding.rowQrCode.setOnClickListener(v -> {
            Toast.makeText(this, "点击了 我的二维码", Toast.LENGTH_SHORT).show();
            // TODO: 在这里启动显示二维码的 Activity
        });
    }
}