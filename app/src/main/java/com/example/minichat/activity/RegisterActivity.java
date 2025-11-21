package com.example.minichat.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.databinding.ActivityRegisterBinding;
import com.example.minichat.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;
    private CountDownTimer timer; // 倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // 1. 观察发送验证码结果
        viewModel.getSendCodeResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "发送失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.btnSendCode.setEnabled(true);
                binding.btnSendCode.setText("发送验证码");
            } else {
                Toast.makeText(this, "验证码已发送，请查收邮箱", Toast.LENGTH_SHORT).show();
                startCountDown(); // 开始倒计时
            }
        });

        // 2. 观察注册结果
        viewModel.getRegisterResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "注册失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "注册成功！请登录", Toast.LENGTH_SHORT).show();
                finish(); // 注册成功，返回登录页
            }
        });

        setupListeners();
    }

    private void setupListeners() {
        // 点击“发送验证码”
        binding.btnSendCode.setOnClickListener(v -> {
            Log.d("RegisterActivity", "点击了发送按钮");
            String email = binding.etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                binding.tilEmail.setError("请输入邮箱");
                return;
            }
            binding.tilEmail.setError(null);

            binding.btnSendCode.setEnabled(false); // 防止重复点击
            binding.btnSendCode.setText("发送中...");
            viewModel.sendCode(email);
        });

        // 点击“立即注册”
        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String code = binding.etCode.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(code) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.register(username, email, password, code);
        });
    }

    // 简单的60秒倒计时
    private void startCountDown() {
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.btnSendCode.setEnabled(false);
                binding.btnSendCode.setText(millisUntilFinished / 1000 + "s 后重试");
            }

            @Override
            public void onFinish() {
                binding.btnSendCode.setEnabled(true);
                binding.btnSendCode.setText("发送验证码");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel(); // 防止内存泄漏
    }
}