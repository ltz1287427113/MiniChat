package com.example.minichat.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.databinding.ActivityAcceptFriendBinding;
import com.example.minichat.viewmodel.AcceptFriendViewModel;

public class AcceptFriendActivity extends AppCompatActivity {

    private ActivityAcceptFriendBinding binding;
    private AcceptFriendViewModel viewModel;
    private int applicationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAcceptFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 获取传递过来的申请ID 和 默认备注
        applicationId = getIntent().getIntExtra("APPLICATION_ID", -1);
        String defaultRemark = getIntent().getStringExtra("DEFAULT_REMARK");

        if (applicationId == -1) {
            Toast.makeText(this, "无效的申请", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // [新增] 设置默认备注
        if (defaultRemark != null) {
            binding.etRemark.setText(defaultRemark);
        }

        // 2. 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(AcceptFriendViewModel.class);

        setupToolbar();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        binding.btnComplete.setOnClickListener(v -> {
            String remark = binding.etRemark.getText().toString().trim();

            // 禁用按钮防止重复点击
            binding.btnComplete.setEnabled(false);
            binding.btnComplete.setText("处理中...");

            // 3. 调用接口 (同意申请)
            viewModel.acceptFriend(applicationId, remark);
        });
    }

    private void observeViewModel() {
        viewModel.getHandleResult().observe(this, result -> {
            // 恢复按钮状态
            binding.btnComplete.setEnabled(true);
            binding.btnComplete.setText("完 成");

            if (result.error != null) {
                Toast.makeText(this, "操作失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "已添加好友！", Toast.LENGTH_SHORT).show();
                // 成功后关闭页面，返回到“新的朋友”列表
                finish();
            }
        });
    }
}