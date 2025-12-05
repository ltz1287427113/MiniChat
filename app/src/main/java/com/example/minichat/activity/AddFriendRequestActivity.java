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
    private boolean fromQrcode; // 是否来自二维码扫描

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
        fromQrcode = getIntent().getBooleanExtra("from_qrcode", false);

        Log.d("AddFriendRequest", "获取Intent数据: username=" + targetUsername + ", nickname=" + targetNickname + ", fromQrcode=" + fromQrcode);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 如果是从二维码来的，修改标题
        if (fromQrcode) {
            binding.toolbar.setTitle("扫码添加好友");
        }
    }

    private void setupViews() {
        // 设置默认的打招呼内容
        String currentUsername = SpUtils.getUser(this).getUsername();
        String greeting = "我是" + (TextUtils.isEmpty(currentUsername) ? "" : currentUsername);
        binding.etGreeting.setText(greeting);

        // 设置默认的备注内容
        String remark = TextUtils.isEmpty(targetNickname) ? "" : targetNickname;
        binding.etRemark.setText(remark);

        // 如果是从二维码来的，可以添加提示
        if (fromQrcode) {
            Log.d("AddFriendRequest", "来自二维码扫描，目标用户: " + targetNickname);
        }
    }

    private void setupListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String greeting = binding.etGreeting.getText().toString().trim();
            String remark = binding.etRemark.getText().toString().trim();

            if (TextUtils.isEmpty(targetUsername)) {
                Toast.makeText(this, "用户信息错误，无法发送请求", Toast.LENGTH_SHORT).show();
                return;
            }

            // 验证必填项
            if (TextUtils.isEmpty(greeting)) {
                Toast.makeText(this, "请输入打招呼内容", Toast.LENGTH_SHORT).show();
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
                String successMsg = fromQrcode ? "好友请求已发送！" : "好友请求已发送！";
                Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}