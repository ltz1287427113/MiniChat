package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.minichat.data.model.response.ScanResponse;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

/**
 * 扫码ViewModel
 */
public class ScanViewModel extends AndroidViewModel {

    private FriendRepository repository;
    private MutableLiveData<AuthRepository.Result<ScanResponse>> scanResult = new MutableLiveData<>();

    public ScanViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendRepository(application);
    }

    public LiveData<AuthRepository.Result<ScanResponse>> getScanResult() {
        return scanResult;
    }

    /**
     * 扫描二维码
     */
    public void scanQrcode(String content, int currentUserId, String message, String remark) {
        repository.scanQrcode(content, currentUserId, message, remark, scanResult);
    }
}