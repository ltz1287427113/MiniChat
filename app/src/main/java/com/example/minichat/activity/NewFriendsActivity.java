package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.adapter.NewFriendsAdapter;
import com.example.minichat.databinding.ActivityNewFriendsBinding;
import com.example.minichat.viewmodel.NewFriendsViewModel;

import java.util.ArrayList;

public class NewFriendsActivity extends AppCompatActivity {

    private ActivityNewFriendsBinding binding;
    private NewFriendsViewModel viewModel;
    private NewFriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 设置 Toolbar
        binding.toolbar.setTitle("新的朋友");
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 设置 RecyclerView
        adapter = new NewFriendsAdapter(new ArrayList<>(), item -> {
            // 点击接受按钮
            viewModel.handleFriendApplication(item.getApplicationId(), "ACCEPTED", (TextUtils.isEmpty(item.getRemark()) ? item.getNickname() : item.getRemark()));
        });
        binding.rvNewFriends.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNewFriends.setAdapter(adapter);

        // 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(NewFriendsViewModel.class);

        // 观察好友申请列表数据
        viewModel.getApplicationsResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "加载失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (result.data != null) {
                // 更新列表
                adapter.updateList(result.data);
            }
        });

        // 观察处理好友申请的结果
        viewModel.getHandleApplicationResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "操作失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show();
                // 刷新列表
                viewModel.loadApplications();
            }
        });

        // 加载数据
        viewModel.loadApplications();
    }
}