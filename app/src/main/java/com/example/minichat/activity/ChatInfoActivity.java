package com.example.minichat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

/**
 * [重构版] 聊天信息页面
 * <p>
 * 数据来源：
 * - 从Intent接收：friendId, friendUsername
 * - 从ViewModel加载：好友的详细信息（备注、昵称、头像等）
 * <p>
 * 功能：
 * - 显示好友信息
 * - 查找聊天记录（TODO）
 * - 清空聊天记录（TODO）
 * - 修改好友备注
 * - 删除好友
 */
public class ChatInfoActivity extends AppCompatActivity {

    private static final String TAG = "ChatInfoActivity";

    // Intent参数的Key
    public static final String EXTRA_FRIEND_ID = "EXTRA_FRIEND_ID";
    public static final String EXTRA_FRIEND_USERNAME = "EXTRA_FRIEND_USERNAME";

    private ActivityChatInfoBinding binding;
    private ChatMemberAdapter memberAdapter;
    private ChatInfoViewModel viewModel;

    // 好友信息
    private int friendId;
    private String friendUsername;
    private String currentDisplayName; // 当前显示的名称（备注或昵称）
    private String currentAvatarUrl;   // 当前头像URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 获取Intent参数
        if (!getIntentData()) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(ChatInfoViewModel.class);

        // 3. 设置UI
        setupToolbar();
        setupMemberGrid();
        setupListeners();
        observeViewModel();

        // 4. 加载好友详细信息
        loadFriendDetail();
    }

    /**
     * [核心] 获取Intent传递的参数
     */
    private boolean getIntentData() {
        friendId = getIntent().getIntExtra(EXTRA_FRIEND_ID, -1);
        friendUsername = getIntent().getStringExtra(EXTRA_FRIEND_USERNAME);

        Log.d(TAG, "接收参数: friendId=" + friendId + ", friendUsername=" + friendUsername);

        // 参数校验
        if (friendId == -1 || friendUsername == null || friendUsername.isEmpty()) {
            Log.e(TAG, "参数校验失败");
            return false;
        }

        return true;
    }

    /**
     * [核心] 加载好友详细信息
     */
    private void loadFriendDetail() {
        // 从FriendRepository获取好友详情
        // 这个接口会返回：nickname, friendRemark, avatarUrl等
        viewModel.loadFriendDetail(friendUsername);
    }

    private void setupToolbar() {
        // 初始显示username，等数据加载后更新
        binding.toolbar.setTitle(friendUsername);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * [核心] 设置成员网格（私聊时只显示对方）
     */
    private void setupMemberGrid() {
        List<ContactItem> members = new ArrayList<>();

        // 暂时添加一个占位数据，等待加载完成后更新
        members.add(new ContactItem(friendId, friendUsername, "加载中...", null));

        memberAdapter = new ChatMemberAdapter(members);
        binding.rvMemberList.setLayoutManager(new GridLayoutManager(this, 5));
        binding.rvMemberList.setAdapter(memberAdapter);

        // 隐藏群聊相关选项
        binding.groupOptionsContainer.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // 查找聊天记录
        binding.rowSearchHistory.setOnClickListener(v -> {
            Toast.makeText(this, "查找聊天记录功能开发中", Toast.LENGTH_SHORT).show();
            // TODO: 跳转到搜索聊天记录页面
        });

        // 清空聊天记录
        binding.rowClearHistory.setOnClickListener(v -> {
            showClearHistoryDialog();
        });

        // 修改备注
        binding.rowRemarkFriend.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("EXTRA_TITLE", "修改备注");
            intent.putExtra("EXTRA_VALUE", currentDisplayName);
            intent.putExtra(EditProfileActivity.EXTRA_UPDATE_TYPE, EditProfileActivity.UPDATE_TYPE_FRIEND_REMARK);
            intent.putExtra("EXTRA_FRIEND_USERNAME", friendUsername);
            startActivity(intent);
        });

        // 删除好友
        binding.tvDeleteFriend.setOnClickListener(v -> {
            showDeleteFriendDialog();
        });
    }

    /**
     * [核心] 观察ViewModel数据变化
     */
    private void observeViewModel() {
        // 观察好友详情加载结果
        viewModel.getFriendDetailResult().observe(this, result -> {
            if (result.error != null) {
                Log.e(TAG, "加载好友详情失败: " + result.error.getMessage());
                Toast.makeText(this, "加载失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (result.data != null) {
                // 成功加载好友信息
                updateUIWithFriendDetail(result.data);
            }
        });

        // 观察删除好友结果
        viewModel.getDeleteFriendResult().observe(this, result -> {
            if (result.error != null) {
                Toast.makeText(this, "删除失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "已删除好友", Toast.LENGTH_SHORT).show();

                // 删除成功后，关闭当前页面并返回结果
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * [核心] 用好友详情更新UI
     */
    private void updateUIWithFriendDetail(com.example.minichat.data.model.response.FriendDetailResponse friendDetail) {
        // 获取显示名称（优先备注，其次昵称）
        currentDisplayName = friendDetail.getDisplayName();
        currentAvatarUrl = friendDetail.getAvatarUrl();

        Log.d(TAG, "更新UI: displayName=" + currentDisplayName + ", avatarUrl=" + currentAvatarUrl);

        // 更新标题
        binding.toolbar.setTitle(currentDisplayName);

        // 更新成员列表
        List<ContactItem> members = new ArrayList<>();
        members.add(new ContactItem(friendId, friendUsername, currentDisplayName, currentAvatarUrl));

        // 重新设置适配器数据
        memberAdapter = new ChatMemberAdapter(members);
        binding.rvMemberList.setAdapter(memberAdapter);
    }

    /**
     * [新增] 显示清空聊天记录确认对话框
     */
    private void showClearHistoryDialog() {
        new AlertDialog.Builder(this).setTitle("清空聊天记录").setMessage("确定要清空与 " + currentDisplayName + " 的聊天记录吗？").setPositiveButton("清空", (dialog, which) -> {
            // TODO: 调用ViewModel清空聊天记录
            Toast.makeText(this, "清空聊天记录功能开发中", Toast.LENGTH_SHORT).show();
        }).setNegativeButton("取消", null).show();
    }

    /**
     * [新增] 显示删除好友确认对话框
     */
    private void showDeleteFriendDialog() {
        new AlertDialog.Builder(this).setTitle("删除好友").setMessage("确定要删除好友 " + currentDisplayName + " 吗？\n删除后将同时清空聊天记录。").setPositiveButton("删除", (dialog, which) -> {
            // 调用ViewModel删除好友
            viewModel.deleteFriend(friendUsername);
        }).setNegativeButton("取消", null).show();
    }

    /**
     * [重写] 从EditProfileActivity返回时刷新数据
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载好友信息，以获取最新的备注
        loadFriendDetail();
    }
}