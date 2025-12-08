package com.example.minichat.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
 * 2. 弹出对话框让用户输入验证信息
 * 3. 调用后端接口发送好友申请
 */
public class ScanQrCodeActivity extends AppCompatActivity {

    private static final String TAG = "ScanQrCodeActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private ActivityScanQrCodeBinding binding;
    private ScanViewModel viewModel;
    private boolean hasScanned = false;
    private int myUserId;
    private String scannedContent; // 保存扫描的内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myUserId = SpUtils.getUser(this).getUserId();
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
        viewModel.getScanResult().observe(this, result -> {
            if (result.error != null) {
                Log.e(TAG, "扫码失败: " + result.error.getMessage());
                Toast.makeText(this, "扫码失败: " + result.error.getMessage(), Toast.LENGTH_SHORT).show();

                // 恢复扫描状态
                hasScanned = false;
                if (binding.barcodeView != null) {
                    binding.barcodeView.resume();
                }
            } else {
                // TODO 扫码成功，跳转到AddFriendDetailedPageActivity，并传递好友信息

                finish();
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
                    return;
                }

                if (result.getText() != null && !result.getText().isEmpty()) {
                    hasScanned = true;
                    scannedContent = result.getText();
                    binding.barcodeView.pause();

                    // 显示对话框让用户输入验证信息
                    showInputDialog();
                }
            }

            @Override
            public void possibleResultPoints(List resultPoints) {
            }
        });

        binding.barcodeView.resume();
    }

    /**
     * 扫描二维码
     */
    private void scanQrcode(String message, String remark) {
        Log.d(TAG, "扫描二维码: content=" + scannedContent + ", message=" + message + ", remark=" + remark);
        viewModel.scanQrcode(scannedContent, myUserId, message, remark);
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