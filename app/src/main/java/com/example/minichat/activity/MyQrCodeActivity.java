package com.example.minichat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.minichat.databinding.ActivityMyQrCodeBinding;
import com.example.minichat.utils.QrCodeUtils;
import com.example.minichat.viewmodel.UserViewModel;
import com.google.zxing.WriterException;

/**
 * 我的二维码页面
 * 功能：
 * 1. 生成个人二维码（2小时有效）
 * 2. 显示用户名和二维码
 */
public class MyQrCodeActivity extends AppCompatActivity {

    private static final String TAG = "MyQrCodeActivity";
    private ActivityMyQrCodeBinding binding;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setupToolbar();
        observeViewModel();
        loadQrCode();
    }

    private void setupToolbar() {
        binding.toolbar.setTitle("我的二维码");
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        // 观察二维码生成结果
        viewModel.getQrCodeResult().observe(this, result -> {
            if (result.error != null) {
                Log.e(TAG, "二维码加载失败: " + result.error.getMessage());
                Toast.makeText(this, "二维码加载失败: " + result.error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            } else if (result.data != null) {
                displayQrCode(result.data);
            }
        });
    }

    private void loadQrCode() {
        binding.tvHint.setText("正在加载二维码...");
        viewModel.generateQrCode();
    }

    private void displayQrCode(com.example.minichat.data.model.response.FriendQrcodeResponse qrCodeData) {
        try {
            // 获取二维码URL
            String qrCodeUrl = qrCodeData.getQrcodeUrl();

            Log.d(TAG, "生成二维码内容: " + qrCodeUrl);

            // 生成二维码图片
            Bitmap qrBitmap = QrCodeUtils.generateQrCode(qrCodeUrl, 512, 512);

            if (qrBitmap != null) {
                binding.ivQrCode.setImageBitmap(qrBitmap);

                // 计算过期时间
                long expireSeconds = qrCodeData.getExpireSeconds();
                long expireMinutes = expireSeconds / 60;

                binding.tvHint.setText("扫描二维码，添加我为好友\n有效期：" + expireMinutes + "分钟");
            } else {
                binding.tvHint.setText("二维码生成失败");
                Toast.makeText(this, "二维码生成失败", Toast.LENGTH_SHORT).show();
            }

        } catch (WriterException e) {
            Log.e(TAG, "二维码生成异常", e);
            binding.tvHint.setText("二维码生成失败");
            Toast.makeText(this, "二维码生成失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}