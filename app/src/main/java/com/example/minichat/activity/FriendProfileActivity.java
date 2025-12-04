package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.data.model.response.FriendDetailResponse;
import com.example.minichat.databinding.ActivityFriendProfileBinding;
import com.example.minichat.viewmodel.FriendProfileViewModel;

/**
 * [修复版] FriendProfileActivity
 * 确保正确传递username给ChatDetailActivity
 */
public class FriendProfileActivity extends AppCompatActivity {

    private static final String TAG = "FriendProfileActivity";
    private ActivityFriendProfileBinding binding;
    private FriendProfileViewModel viewModel;
    private String friendUsername;
    private int friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.inflateMenu(com.example.minichat.R.menu.chat_detail_menu);

        // 获取传递的参数
        friendUsername = getIntent().getStringExtra("FRIEND_USERNAME");
        friendId = getIntent().getIntExtra("FRIEND_ID", -1);

        Log.d(TAG, "接收参数: FRIEND_USERNAME=" + friendUsername + ", FRIEND_ID=" + friendId);

        if (friendUsername == null) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(FriendProfileViewModel.class);

        viewModel.getFriendDetailResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "加载失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (result.data != null) {
                updateUI(result.data);
            }
        });

        viewModel.loadFriendDetail(friendUsername);

        setupListeners();
    }

    private void updateUI(FriendDetailResponse friend) {
        // 优先显示备注，没有备注显示昵称
        String displayName = friend.getDisplayName();

        binding.tvName.setText(displayName);

        if (friend.getFriendRemark() != null && !friend.getFriendRemark().isEmpty()) {
            binding.tvNicknameLabel.setText("昵称: " + friend.getNickname());
        } else {
            binding.tvNicknameLabel.setText("");
        }

        binding.tvWechatId.setText("微信号: " + friend.getUsername());

        com.example.minichat.utils.UserDisplayUtils.loadAvatar(this, friend.getAvatarUrl(), binding.ivAvatar);

        Log.d(TAG, "更新UI: displayName=" + displayName + ", username=" + friend.getUsername());
    }

    private void setupListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // [关键修复] 点击"发消息"
        binding.btnSendMsg.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatDetailActivity.class);

            intent.putExtra("FRIEND_ID", friendId);
            intent.putExtra("CHAT_USERNAME", friendUsername); // [确保传递username]
            intent.putExtra("CHAT_NAME", binding.tvName.getText().toString()); // 传递显示名称

            Log.d(TAG, "启动ChatDetailActivity，传递参数: " + "FRIEND_ID=" + friendId + ", CHAT_USERNAME=" + friendUsername + ", CHAT_NAME=" + binding.tvName.getText().toString());

            startActivity(intent);
        });

        binding.btnMoments.setOnClickListener(v -> {
            Toast.makeText(this, "查看朋友圈", Toast.LENGTH_SHORT).show();
        });
    }
}