package com.example.minichat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.databinding.ActivitySearchUserBinding;
import com.example.minichat.viewmodel.SearchUserViewModel;
public class SearchUserActivity extends AppCompatActivity {

    private ActivitySearchUserBinding binding;
    private SearchUserViewModel viewModel;

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
            Toast.makeText(this, "已发送好友申请", Toast.LENGTH_SHORT).show();
            // TODO: 调用发送好友申请的接口 (我们之后再做)
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

                binding.resultContainer.setVisibility(View.VISIBLE);
                binding.tvNickname.setText(user.getNickname());
                binding.tvUsername.setText("微信号: " + user.getUsername());

                // TODO: 如果有 avatarUrl，用 Glide 加载头像
            }
        });
    }
}