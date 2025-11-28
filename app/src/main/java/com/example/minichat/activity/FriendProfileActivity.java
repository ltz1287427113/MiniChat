package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.data.model.response.FriendDetailResponse;
import com.example.minichat.databinding.ActivityFriendProfileBinding;
import com.example.minichat.viewmodel.FriendProfileViewModel;

public class FriendProfileActivity extends AppCompatActivity {

    private ActivityFriendProfileBinding binding;
    private FriendProfileViewModel viewModel;
    private String friendUsername; // 当前查看的好友微信号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.inflateMenu(com.example.minichat.R.menu.chat_detail_menu);

        // 1. 获取传递过来的 username (这是查询的关键)
        friendUsername = getIntent().getStringExtra("FRIEND_USERNAME");
        if (friendUsername == null) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(FriendProfileViewModel.class);

        // 3. 观察数据
        viewModel.getFriendDetailResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "加载失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (result.data != null) {
                // 填充 UI
                updateUI(result.data);
            }
        });

        // 4. 发起请求
        viewModel.loadFriendDetail(friendUsername);

        setupListeners();
    }

    private void updateUI(FriendDetailResponse friend) {
        // 设置大标题名字 (有备注显示备注，没备注显示昵称)
        if (friend.getFriendRemark() != null && !friend.getFriendRemark().isEmpty()) {
            binding.tvName.setText(friend.getFriendRemark());
            binding.tvNicknameLabel.setText("昵称: " + friend.getNickname());
        } else {
            binding.tvName.setText(friend.getNickname());
            binding.tvNicknameLabel.setText(""); // 如果没有备注，昵称已经显示在大标题，这里可以隐藏
        }

        // 设置微信号
        binding.tvWechatId.setText("微信号: " + friend.getUsername());

        // TODO: 加载头像 (friend.getAvatarUrl())
    }

    private void setupListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 点击 "发消息"
        binding.btnSendMsg.setOnClickListener(v -> {
            // 跳转到聊天页面
            Intent intent = new Intent(this, ChatDetailActivity.class);
            // 传递必要参数
            intent.putExtra("CHAT_USERNAME", friendUsername); // 这里的 ID 其实就是 username
            // 传递显示的名字 (优先显示 UI 上那个大标题)
            intent.putExtra("CHAT_NAME", binding.tvName.getText().toString());

            // 清除栈顶，防止用户点返回又回到资料页 (看需求)
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        });

        // 点击 "朋友圈"
        binding.btnMoments.setOnClickListener(v -> {
            Toast.makeText(this, "查看朋友圈", Toast.LENGTH_SHORT).show();
        });
    }
}