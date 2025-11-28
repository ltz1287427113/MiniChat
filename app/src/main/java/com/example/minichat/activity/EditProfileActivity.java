package com.example.minichat.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.databinding.ActivityEditProfileBinding;
import com.example.minichat.viewmodel.ChatInfoViewModel;
import com.example.minichat.viewmodel.UserViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private UserViewModel userViewModel;
    private ChatInfoViewModel chatInfoViewModel;

    // 定义更新类型常量
    public static final String EXTRA_UPDATE_TYPE = "EXTRA_UPDATE_TYPE";
    public static final String UPDATE_TYPE_USER_PROFILE = "UPDATE_TYPE_USER_PROFILE";
    public static final String UPDATE_TYPE_FRIEND_REMARK = "UPDATE_TYPE_FRIEND_REMARK";

    private String updateType; // 用于存储当前的更新类型
    private String friendUsername; // 用于存储好友的 username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. 获取 Intent 传递的数据
        String title = getIntent().getStringExtra("EXTRA_TITLE");
        String value = getIntent().getStringExtra("EXTRA_VALUE");
        updateType = getIntent().getStringExtra(EXTRA_UPDATE_TYPE); // 获取更新类型
        friendUsername = getIntent().getStringExtra("EXTRA_FRIEND_USERNAME"); // 获取好友用户名

        binding.toolbar.setTitle(title);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.etEditValue.setText(value);
        if (value != null) {
            binding.etEditValue.setSelection(value.length());
        }

        // 2. 根据更新类型初始化不同的 ViewModel 并观察结果
        if (UPDATE_TYPE_USER_PROFILE.equals(updateType)) {
            userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            userViewModel.getUpdateResult().observe(this, result -> {
                if ("SUCCESS".equals(result)) {
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "修改失败: " + result, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (UPDATE_TYPE_FRIEND_REMARK.equals(updateType)) {
            chatInfoViewModel = new ViewModelProvider(this).get(ChatInfoViewModel.class);
            chatInfoViewModel.getUpdateFriendremarkResult().observe(this, result -> {
                if (result.error != null) {
                    Toast.makeText(this, "修改备注失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (result.data != null) {
                    Toast.makeText(this, "修改备注成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            // 默认情况下，也初始化 UserViewModel，以防万一
            userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            userViewModel.getUpdateResult().observe(this, result -> {
                if ("SUCCESS".equals(result)) {
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "修改失败: " + result, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 3. 点击保存
        binding.btnSave.setOnClickListener(v -> {
            String newValue = binding.etEditValue.getText().toString().trim();
            if (newValue.isEmpty()) {
                Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (UPDATE_TYPE_USER_PROFILE.equals(updateType)) {
                if ("修改名字".equals(title) || "修改昵称".equals(title)) {
                    userViewModel.updateUser(newValue, null);
                } else {
                    Toast.makeText(this, "暂不支持修改此项", Toast.LENGTH_SHORT).show();
                }
            } else if (UPDATE_TYPE_FRIEND_REMARK.equals(updateType)) {
                if (friendUsername != null) {
                    chatInfoViewModel.updateFriendremark(friendUsername, newValue);
                    finish();
                } else {
                    Toast.makeText(this, "无法获取好友信息，无法修改备注", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("EditProfileActivity", "未匹配到任何更新类型");
            }
        });
    }
}
