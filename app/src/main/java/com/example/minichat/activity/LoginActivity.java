package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.data.model.LoginData;
import com.example.minichat.databinding.ActivityLoginBinding; // [新导入]
import com.example.minichat.utils.SpUtils;
import com.example.minichat.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel; // [新]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [核心代码] 检查是否已经登录
        if (SpUtils.isLoggedIn(this)) {
            // 如果有 Token，直接跳转主页，跳过登录页
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // 关闭登录页
            return;   // 必须 return，不执行后面的 setContentView
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 2. [核心] 观察登录结果
        viewModel.getLoginResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "登录失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (result.data != null) {
                // [修改] result.data 现在是 LoginData 对象
                LoginData loginData = result.data;
                String token = loginData.getToken(); // 从对象里取出 Token

                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();

                // 保存 Token
                SpUtils.saveToken(this, token);

                // 跳转
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        // 设置点击事件
        setupListeners();
    }

    private void setupListeners() {
        // 1. 点击登录
        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                performLogin(username, password);
            }
        });

        // 2. 点击注册
        binding.tvRegister.setOnClickListener(v -> {
            binding.tvRegister.setOnClickListener(vv -> {
                startActivity(new Intent(this, RegisterActivity.class));
            });
        });
    }

    /**
     * 简单的输入校验
     */
    private boolean validateInput(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError("账号不能为空");
            return false;
        }
        // 清除错误提示
        binding.tilUsername.setError(null);

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("密码不能为空");
            return false;
        }
        binding.tilPassword.setError(null);

        return true;
    }

    /**
     * 执行登录逻辑
     */
    private void performLogin(String username, String password) {
        // [修改] 不再直接跳转，而是发起网络请求
        // 显示一个 Loading 提示 (可选)
        Toast.makeText(this, "正在登录...", Toast.LENGTH_SHORT).show();

        // 调用 ViewModel
        viewModel.login(username, password);
    }
}