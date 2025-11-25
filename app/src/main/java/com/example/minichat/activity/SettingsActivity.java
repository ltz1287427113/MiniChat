package com.example.minichat.activity;

import android.content.Intent; // [新导入]
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.databinding.ActivitySettingsBinding; // [新导入]
import com.example.minichat.utils.SpUtils;

/**
 * [注释]
 * 设置页面 Activity。
 * 负责加载 activity_settings.xml 布局并处理点击事件。
 */
public class SettingsActivity extends AppCompatActivity {

    // 1. ViewBinding
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. [注释] 加载布局
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 3. [注释] 设置 Toolbar
        setupToolbar();

        // 4. [注释] 为每一行设置点击事件
        setupClickListeners();
    }

    /**
     * [注释]
     * 设置 Toolbar，包括标题“设置”和返回按钮。
     */
    private void setupToolbar() {
        binding.toolbar.setTitle("设置");

        // (设置返回按钮)
        binding.toolbar.setNavigationOnClickListener(v -> {
            finish(); // 关闭当前 Activity
        });
    }

    /**
     * [注释]
     * 为“个人资料”、“修改密码” 等所有行添加点击监听器。
     */
    private void setupClickListeners() {

        // [注释] 点击“个人资料”行
        binding.rowProfile.setOnClickListener(v -> {
            // [注释] 启动我们之前创建的 ProfileActivity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // [注释] 点击“修改密码”行
        binding.rowChangePassword.setOnClickListener(v -> {
            // [注释] 我们可以复用 EditProfileActivity
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("EXTRA_TITLE", "修改密码");
            intent.putExtra("EXTRA_VALUE", ""); // 密码框初始应为空
            startActivity(intent);
        });
        

        // [注释] 点击“退出”行
        binding.rowLogout.setOnClickListener(v -> {
            // 1. 清除本地用户数据 (Token 和 UserInfo)
            SpUtils.logout(this);

            // 2. 跳转到登录界面
            Intent intent = new Intent(this, LoginActivity.class);
            // 3. 清除所有之前的 Activity，确保用户无法通过返回键回到主界面
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // 关闭当前 SettingsActivity
        });
    }
}