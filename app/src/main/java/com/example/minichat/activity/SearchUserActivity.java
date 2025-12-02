package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.databinding.ActivitySearchUserBinding;
import com.example.minichat.viewmodel.SearchUserViewModel;


public class SearchUserActivity extends AppCompatActivity {

    private ActivitySearchUserBinding binding;
    private SearchUserViewModel viewModel;

    // [新增变量] 用于暂存当前搜索到的用户
    private StrangerResponse currentStranger;
    private Observer<AuthRepository.Result<Boolean>> isFriendObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SearchUserViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        // 1. 返回按钮
        binding.ivBack.setOnClickListener(v -> finish());

        // 2. 点击“搜索”文字按钮
        binding.tvSearchBtn.setOnClickListener(v -> performSearch());

        // 3. 键盘上的搜索动作 (点击软键盘回车)
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // 4. 点击“添加到通讯录”
        binding.btnAdd.setOnClickListener(v -> {
            if (currentStranger == null) {
                Toast.makeText(this, "请先搜索用户", Toast.LENGTH_SHORT).show();
                return;
            }
            // 跳转到 AddFriendRequestActivity，并传递陌生人信息
            Intent intent = new Intent(SearchUserActivity.this, AddFriendRequestActivity.class);
            intent.putExtra("username", currentStranger.getUsername());
            intent.putExtra("nickname", currentStranger.getNickname());
            startActivity(intent);
        });
    }

    private void performSearch() {
        String keyword = binding.etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入账号或邮箱", Toast.LENGTH_SHORT).show();
            return;
        }

        // 隐藏之前的搜索结果
        binding.resultContainer.setVisibility(View.GONE);
        binding.tvNotFound.setVisibility(View.GONE);

        // [新增] 清空 isFriendResult，防止旧数据干扰
        viewModel.resetIsFriendResult();

        // 发起搜索
        viewModel.search(keyword);
    }

    private void observeViewModel() {
        viewModel.getSearchResult().observe(this, result -> {
            if (result.error != null) {
                // 搜索失败或查无此人
                binding.tvNotFound.setVisibility(View.VISIBLE);
                binding.tvNotFound.setText(result.error.getMessage());
            } else if (result.data != null) {
                // 搜索成功，显示结果卡片
                StrangerResponse user = result.data;
                currentStranger = user;
                // 每次搜索成功时，先移除旧的 isFriendObserver (如果有的话)
                if (isFriendObserver != null) {
                    viewModel.getIsFriendResult().removeObserver(isFriendObserver);
                }

                // 创建新的 Observer
                isFriendObserver = isFriendResult -> {
                    // 确保这个结果是针对当前搜索到的用户的
                    if (currentStranger == null || !currentStranger.getUsername().equals(user.getUsername())) {
                        Log.d("SearchUserActivity", "isFriendResult is for a stale search, skipping.");
                        return;
                    }

                    if (isFriendResult == null || isFriendResult.data == null) {
                        Log.d("SearchUserActivity", "isFriendResult or its data is null, skipping processing.");
                        return;
                    }

                    boolean isFriend = isFriendResult.data;
                    if (isFriend) {
                        Log.d("SearchUserActivity", "isFriend: " + isFriend);
                        Intent intent = new Intent(SearchUserActivity.this, FriendProfileActivity.class);
                        intent.putExtra("FRIEND_USERNAME", user.getUsername());
                        startActivity(intent);
                    } else {
                        Log.d("SearchUserActivity", "isFriend: " + isFriend);
                        binding.resultContainer.setVisibility(View.VISIBLE);
                        binding.tvNickname.setText(user.getNickname());
                        binding.tvUsername.setText("微信号: " + user.getUsername());
                        // TODO: 如果有 avatarUrl，用 Glide 加载头像
                    }
                };
                // 注册新的 Observer
                viewModel.getIsFriendResult().observe(this, isFriendObserver);
                viewModel.isFriend(user.getUsername());

            }
        });
    }
}