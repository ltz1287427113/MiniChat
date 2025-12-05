package com.example.minichat.activity; // (确保这是你的包名)

import android.Manifest;
import android.content.Intent; // [新导入]
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.minichat.R;
import com.example.minichat.data.model.response.UserLoginResponse;
import com.example.minichat.databinding.ActivityProfileBinding;
import com.example.minichat.utils.SpUtils;
import com.example.minichat.utils.UriUtils;
import com.example.minichat.viewmodel.UserViewModel;

import java.io.File;

/**
 * [注释]
 * 个人资料页面 Activity。
 * (已更新，包含跳转到 EditProfileActivity 的逻辑)
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UserViewModel userViewModel;
    private Uri currentPhotoUri; // 用于暂存拍照后的 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // [新] 观察上传结果
        userViewModel.getUpdateResult().observe(this, result -> {
            if ("SUCCESS".equals(result)) {
                Toast.makeText(this, "头像修改成功", Toast.LENGTH_SHORT).show();
                // 重新加载用户信息(刷新头像)
                setupViews();
            } else {
                Toast.makeText(this, "修改失败: " + result, Toast.LENGTH_SHORT).show();
            }
        });

        setupToolbar();
        // 4. [修改] 为每一行设置点击事件
        setupClickListeners();
        setupViews();
    }

    private void setupViews() {
        // 1. 从本地读取
        UserLoginResponse user = SpUtils.getUser(this);

        if (user != null) {
            // 2. 设置文字信息 (保持不变)
            String displayName = (user.getNickname() != null && !user.getNickname().isEmpty())
                    ? user.getNickname()
                    : user.getUsername();
            binding.tvNameValue.setText(displayName);
            binding.tvWechatIdValue.setText(user.getUsername());
            binding.tvEmailValue.setText(user.getEmail());

            // 3. [核心修改] 使用 UserDisplayUtils 加载头像
            com.example.minichat.utils.UserDisplayUtils.loadAvatar(this, user.getAvatarUrl(), binding.ivAvatarValue);
        }
    }

    /**
     * [注释]
     * 设置 Toolbar，包括标题和返回按钮。
     */
    private void setupToolbar() {
        binding.toolbar.setTitle("个人资料");
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * [注释]
     * [核心修改]
     * 为“头像”、“名字” 等所有行添加点击监听器。
     */
    private void setupClickListeners() {
        // [注释] 点击"头像"行
        binding.rowAvatar.setOnClickListener(v -> showImagePickerInfo());

        // [注释] 点击"名字"行
        binding.rowName.setOnClickListener(v -> {
            String currentName = binding.tvNameValue.getText().toString();
            launchEditor("修改名字", currentName);
        });

        // [注释] 点击"邮箱"行
        binding.rowEmail.setOnClickListener(v -> {
            String currentEmail = binding.tvEmailValue.getText().toString();
            Toast.makeText(this, "邮箱暂不支持修改", Toast.LENGTH_SHORT).show();
        });

        // [注释] 点击"微信号"行
        binding.rowWechatId.setOnClickListener(v -> {
            Toast.makeText(this, "微信号不支持修改", Toast.LENGTH_SHORT).show();
        });

        // [注释] 点击"我的二维码"行 - 新增功能
        binding.rowQrCode.setOnClickListener(v -> {
            // 跳转到二维码页面
            Intent intent = new Intent(this, MyQrCodeActivity.class);
            startActivity(intent);
        });
    }

    // 1. 相册选择器
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uploadAvatar(uri);
                }
            });

    // 2. 相机启动器
    private final ActivityResultLauncher<Uri> takePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
                if (isSuccess && currentPhotoUri != null) {
                    uploadAvatar(currentPhotoUri);
                }
            });

    // 3. 权限请求器 (用于相机)
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * 显示选择框：拍照 or 相册
     */
    private void showImagePickerInfo() {
        String[] options = {"拍照", "从相册选择"};
        new AlertDialog.Builder(this)
                .setTitle("修改头像")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // 拍照 -> 先请求权限
                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                    } else {
                        // 相册
                        pickImageLauncher.launch("image/*");
                    }
                })
                .show();
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        // 1. 创建一个临时文件用于保存照片
        File photoFile = new File(getExternalCacheDir(), "avatar_temp.jpg");

        // 2. 获取 URI (使用 FileProvider)
        // 注意：这里的 authority 必须和 AndroidManifest.xml 里的一致
        currentPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);

        // 3. 启动相机
        takePhotoLauncher.launch(currentPhotoUri);
    }

    /**
     * [核心] 上传头像
     */
    private void uploadAvatar(Uri uri) {
        // 1. 将 Uri 转换为 File
        File file = UriUtils.uriToFile(this, uri);

        if (file != null) {
            Toast.makeText(this, "正在上传...", Toast.LENGTH_SHORT).show();
            // 2. 调用 ViewModel (nickname 传 null)
            userViewModel.updateUser(null, file.getAbsolutePath());
        } else {
            Toast.makeText(this, "图片处理失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * [注释]
     * [新方法]
     * 一个可复用的方法，用于启动 EditProfileActivity。
     *
     * @param title 页面的标题 (例如 "修改名字")
     * @param value 传递给 EditText 的当前值
     */
    public void launchEditor(String title, String value) {
        // 1. 创建 Intent，目标是 EditProfileActivity
        Intent intent = new Intent(this, EditProfileActivity.class);

        // 2. [核心] 使用 .putExtra() 来传递数据
        intent.putExtra("EXTRA_TITLE", title);
        intent.putExtra("EXTRA_VALUE", value);
        intent.putExtra("EXTRA_UPDATE_TYPE", "UPDATE_TYPE_USER_PROFILE");
        // 3. 启动 Activity
        startActivity(intent);
    }
    // 当 EditProfileActivity 返回时刷新数据
    @Override
    protected void onResume() {
        super.onResume();
        setupViews();
    }
}