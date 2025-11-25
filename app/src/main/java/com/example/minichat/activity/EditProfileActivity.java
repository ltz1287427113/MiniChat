package com.example.minichat.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.databinding.ActivityEditProfileBinding;
import com.example.minichat.viewmodel.UserViewModel; // [新导入]

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private UserViewModel viewModel; // [新]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // 2. 观察更新结果
        viewModel.getUpdateResult().observe(this, result -> {
            if ("SUCCESS".equals(result)) {
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                finish(); // 关闭页面
            } else {
                Toast.makeText(this, "修改失败: " + result, Toast.LENGTH_SHORT).show();
            }
        });

        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String value = getIntent().getStringExtra("EXTRA_VALUE");

        binding.toolbar.setTitle(title);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.etEditValue.setText(value);
        if (value != null) {
            binding.etEditValue.setSelection(value.length());
        }

        // 3. 点击保存
        binding.btnSave.setOnClickListener(v -> {
            String newValue = binding.etEditValue.getText().toString().trim();
            if (newValue.isEmpty()) {
                Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 判断是修改什么
            if ("修改名字".equals(title) || "修改昵称".equals(title)) {
                // 调用 API 更新昵称
                // (这里 avatarUrl 传 null，假设后端支持只更新昵称)
                viewModel.updateUser(newValue, null);
            } else {
                // 其他类型暂不支持 (比如邮箱接口不一样)
                Toast.makeText(this, "暂不支持修改此项", Toast.LENGTH_SHORT).show();
            }
        });
    }
}