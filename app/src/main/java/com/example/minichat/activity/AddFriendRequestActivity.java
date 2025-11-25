package com.example.minichat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.databinding.ActivityAddFriendRequestBinding;
import com.example.minichat.utils.SpUtils;
import com.example.minichat.viewmodel.AddFriendRequestViewModel;

public class AddFriendRequestActivity extends AppCompatActivity {

    private ActivityAddFriendRequestBinding binding;
    private AddFriendRequestViewModel viewModel;
    private String targetUsername;
    private String targetNickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFriendRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AddFriendRequestViewModel.class);

        getIntentData();
        setupToolbar();
        setupViews();
        setupListeners();
        observeViewModel();
    }

    private void getIntentData() {
        targetUsername = getIntent().getStringExtra("username");
        targetNickname = getIntent().getStringExtra("nickname");
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        // 设置默认的打招呼内容
        binding.etGreeting.setText("我是" +(TextUtils.isEmpty(SpUtils.getUser(this).getUsername()) ? "" : SpUtils.getUser(this).getUsername()));
        // 设置默认的备注内容
        binding.etRemark.setText((TextUtils.isEmpty(targetNickname) ? "" : targetNickname));
    }

    private void setupListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String greeting = binding.etGreeting.getText().toString().trim();
            String remark = binding.etRemark.getText().toString().trim();

            if (TextUtils.isEmpty(targetUsername)) {
                Toast.makeText(this, "用户信息错误，无法发送请求", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnSend.setEnabled(false);
            binding.btnSend.setText("发送中...");

            viewModel.sendFriendRequest(targetUsername, greeting, remark);
        });
    }

    private void observeViewModel() {
        viewModel.getAddFriendResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "发送失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("AddFriendRequestActivity", "发送失败: " + result.error.getMessage());
                binding.btnSend.setEnabled(true);
                binding.btnSend.setText("发送");
            } else {
                Toast.makeText(this, "好友请求已发送！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}