package com.example.minichat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.minichat.R;
import com.example.minichat.adapter.ChatMemberAdapter;
import com.example.minichat.databinding.ActivityChatInfoBinding;
import com.example.minichat.model.ContactItem;
import com.example.minichat.viewmodel.ChatInfoViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatInfoActivity extends AppCompatActivity {

    private ActivityChatInfoBinding binding;
    private ChatMemberAdapter memberAdapter;
    private ChatInfoViewModel viewModel;
    private String friendUsername; // 当前聊天对象的 username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 获取数据
        String chatName = getIntent().getStringExtra("CHAT_NAME");
        friendUsername = getIntent().getStringExtra("CHAT_USERNAME"); // 获取好友 username

        if (chatName == null) chatName = "用户";

        // 2. 判断是否为群聊 (简单判断：如果名字里有"群"或者是"技术交流群")
        // 在真实项目中，你应该传递一个 boolean isGroup = intent.getBooleanExtra(...)
        boolean isGroup = chatName.contains("群") || chatName.contains("Group");

        // 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(ChatInfoViewModel.class);

        setupToolbar(chatName, isGroup);
        setupMemberGrid(chatName, isGroup);
        setupGroupOptions(chatName, isGroup);
        setupCommonListeners();
        observeViewModel();
    }

    private void setupToolbar(String title, boolean isGroup) {
        binding.toolbar.setTitle(title + (isGroup ? "(10)" : "")); // 群聊显示人数
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupMemberGrid(String remark, boolean isGroup) {
        List<ContactItem> members = new ArrayList<>();

        if (isGroup) {
            // --- 群聊模式：添加多个假数据 ---
            members.add(new ContactItem("1", "群主", null));
            members.add(new ContactItem("2", "管理员",null));
            members.add(new ContactItem("3", "小明",null));
            members.add(new ContactItem("4", "小红",null));
            members.add(new ContactItem("5", "小刚",null));
            members.add(new ContactItem("6", "张三",null));
            members.add(new ContactItem("7", "李四",null));
            members.add(new ContactItem("8", "王五",null));
            members.add(new ContactItem("9", "赵六",null));
        } else {
            // --- 私聊模式：只添加对方 ---
            members.add(new ContactItem("id", remark,null));
        }

        // 初始化 Adapter
        memberAdapter = new ChatMemberAdapter(members);

        // 设置 RecyclerView 为 5 列网格
        binding.rvMemberList.setLayoutManager(new GridLayoutManager(this, 5));
        binding.rvMemberList.setAdapter(memberAdapter);
    }

    private void setupGroupOptions(String chatName, boolean isGroup) {
        if (isGroup) {
            // 如果是群聊，显示群管理选项
            binding.groupOptionsContainer.setVisibility(View.VISIBLE);
            binding.tvGroupNameValue.setText(chatName);
            binding.tvDeleteFriend.setText("退出群聊");

            // 点击群聊名称
            binding.rowGroupName.setOnClickListener(v -> Toast.makeText(this, "修改群聊名称", Toast.LENGTH_SHORT).show());

            // 点击群二维码
            binding.rowGroupQr.setOnClickListener(v -> Toast.makeText(this, "查看群二维码", Toast.LENGTH_SHORT).show());
        } else {
            // 私聊则隐藏
            binding.groupOptionsContainer.setVisibility(View.GONE);
        }
    }

    private void setupCommonListeners() {
        binding.rowSearchHistory.setOnClickListener(v -> {
            Toast.makeText(this, "点击了查找聊天记录", Toast.LENGTH_SHORT).show();
        });

        binding.rowClearHistory.setOnClickListener(v -> {
            Toast.makeText(this, "点击了清空聊天记录", Toast.LENGTH_SHORT).show();
        });
        binding.tvDeleteFriend.setOnClickListener(v -> {
            if (friendUsername != null) {
                viewModel.deleteFriend(friendUsername);
            } else {
                Toast.makeText(this, "无法获取好友信息", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getDeleteFriendResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "删除好友失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "好友已删除", Toast.LENGTH_SHORT).show();
                finish(); // 删除成功后关闭当前页面
            }
        });
    }
}