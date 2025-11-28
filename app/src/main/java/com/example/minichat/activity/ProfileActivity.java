package com.example.minichat.activity; // (确保这是你的包名)

import android.content.Intent; // [新导入]
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minichat.data.model.response.UserLoginResponse;
import com.example.minichat.databinding.ActivityProfileBinding;
import com.example.minichat.utils.SpUtils;

/**
 * [注释]
 * 个人资料页面 Activity。
 * (已更新，包含跳转到 EditProfileActivity 的逻辑)
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        // 4. [修改] 为每一行设置点击事件
        setupClickListeners();
        setupViews();
    }
 
    private void setupViews() {
        // 1. 从本地读取
        UserLoginResponse user = SpUtils.getUser(this);

        if (user != null) {
            // 2. 设置 UI
            String displayName = (user.getNickname() != null && !user.getNickname().isEmpty())
                    ? user.getNickname()
                    : user.getUsername();

            binding.tvNameValue.setText(displayName);
            binding.tvWechatIdValue.setText(user.getUsername());
            binding.tvEmailValue.setText(user.getEmail()); // 显示邮箱
        }
    }

    /**
     * [注释]
     * 设置 Toolbar，包括标题和返回按钮。
     */
    private void setupToolbar() {
        binding.toolbar.setTitle("个人资料");
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * [注释]
     * [核心修改]
     * 为“头像”、“名字” 等所有行添加点击监听器。
     */
    private void setupClickListeners() {

        // [注释] 点击“头像”行
        binding.rowAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "点击了 头像", Toast.LENGTH_SHORT).show();
            // TODO: 启动图片选择器 (这是一个复杂功能，我们稍后做)
        });

        // [注释] 点击“名字”行
        binding.rowName.setOnClickListener(v -> {
            // (获取当前显示的名字)
            String currentName = binding.tvNameValue.getText().toString();
            // (启动通用的编辑页)
            launchEditor("修改名字", currentName);
        });

        // [注释] 点击“邮箱”行 (替换了“手机号”)
        binding.rowEmail.setOnClickListener(v -> {
            // (获取当前显示的邮箱)
            String currentEmail = binding.tvEmailValue.getText().toString();
            // (启动通用的编辑页)
            Toast.makeText(this, "邮箱暂不支持修改", Toast.LENGTH_SHORT).show();
        });

        // [注释] 点击“微信号”行
        binding.rowWechatId.setOnClickListener(v -> {
            Toast.makeText(this, "微信号不支持修改", Toast.LENGTH_SHORT).show();
        });

        // [注释] 点击“我的二维码”行
        binding.rowQrCode.setOnClickListener(v -> {
            Toast.makeText(this, "点击了 我的二维码", Toast.LENGTH_SHORT).show();
            // TODO: 启动 MyQrCodeActivity (这是一个新页面，我们稍后做)
        });
    }

    /**
     * [注释]
     * [新方法]
     * 一个可复用的方法，用于启动 EditProfileActivity。
     *
     * @param title 页面的标题 (例如 "修改名字")
     * @param value 传递给 EditText 的当前值
     */
    public void launchEditor(String title, String value) {
        // 1. 创建 Intent，目标是 EditProfileActivity
        Intent intent = new Intent(this, EditProfileActivity.class);

        // 2. [核心] 使用 .putExtra() 来传递数据
        intent.putExtra("EXTRA_TITLE", title);
        intent.putExtra("EXTRA_VALUE", value);
        intent.putExtra("EXTRA_UPDATE_TYPE", "UPDATE_TYPE_USER_PROFILE");
        // 3. 启动 Activity
        startActivity(intent);
    }
}