package com.example.minichat.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.minichat.databinding.ActivityScanQrCodeBinding;
import com.example.minichat.utils.SpUtils;
import com.example.minichat.viewmodel.ScanViewModel;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.List;

/**
 * 扫一扫页面
 * 功能：
 * 1. 扫描二维码
 * 2. 调用后端接口验证二维码
 * 3. 跳转到 AddFriendRequestActivity 发送好友申请
 */
public class ScanQrCodeActivity extends AppCompatActivity {

    private static final String TAG = "ScanQrCodeActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private ActivityScanQrCodeBinding binding;
    private ScanViewModel viewModel;
    private boolean hasScanned = false; // 防止重复扫描
    private int myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取当前用户ID
        myUserId = SpUtils.getUser(this).getUserId();

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(ScanViewModel.class);

        setupToolbar();
        observeViewModel();
        checkCameraPermission();
    }

    private void setupToolbar() {
        binding.toolbar.setTitle("扫一扫");
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        // 观察扫码结果
        viewModel.getScanResult().observe(this, result -> {
            if (result.error != null) {
                Log.e(TAG, "扫码失败: " + result.error.getMessage());
                Toast.makeText(this, "扫码失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();
                hasScanned = false; // 允许重新扫描
            } else if (result.data != null) {
                // 扫码成功，跳转到添加好友页面
                jumpToAddFriend(result.data);
            }
        });
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            startScanning();
        }
    }

    private void startScanning() {
        binding.barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (hasScanned) {
                    return; // 已经扫描过了，忽略后续结果
                }

                if (result.getText() != null && !result.getText().isEmpty()) {
                    hasScanned = true;
                    handleScanResult(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List resultPoints) {
                // 可以在这里显示扫描框的提示点
            }
        });

        binding.barcodeView.resume();
    }

    /**
     * 处理扫描结果
     */
    private void handleScanResult(String qrContent) {
        Log.d(TAG, "扫描结果: " + qrContent);

        // 暂停扫描
        binding.barcodeView.pause();

        // 准备默认的message和remark
        String message = "我是 " + SpUtils.getUser(this).getUsername();
        String remark = "";

        // 调用后端接口验证二维码
        viewModel.scanQrcode(qrContent, myUserId, message, remark);
    }

    /**
     * 跳转到添加好友页面
     */
    private void jumpToAddFriend(com.example.minichat.data.model.response.ScanResponse scanResponse) {
        Intent intent = new Intent(this, AddFriendRequestActivity.class);
        intent.putExtra("username", scanResponse.getUsername());
        intent.putExtra("nickname", scanResponse.getNickname());
        intent.putExtra("from_qrcode", true);

        Log.d(TAG, "跳转到添加好友页面: username=" + scanResponse.getUsername() + ", nickname=" + scanResponse.getNickname());

        startActivity(intent);
        finish(); // 关闭扫码页面
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(this, "需要相机权限才能扫描二维码", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.barcodeView != null && !hasScanned) {
            binding.barcodeView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (binding.barcodeView != null) {
            binding.barcodeView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}