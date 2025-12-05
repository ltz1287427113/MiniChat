package com.example.minichat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.minichat.data.model.response.FriendQrcodeResponse;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    private UserRepository repository;
    private MutableLiveData<String> updateResult = new MutableLiveData<>();
    private MutableLiveData<AuthRepository.Result<FriendQrcodeResponse>> qrCodeResult = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public LiveData<String> getUpdateResult() {
        return updateResult;
    }

    public LiveData<AuthRepository.Result<FriendQrcodeResponse>> getQrCodeResult() {
        return qrCodeResult;
    }

    /**
     * 更新用户信息
     */
    public void updateUser(String nickname, String avatarUrl) {
        repository.updateUser(nickname, avatarUrl, updateResult);
    }

    /**
     * 生成二维码
     */
    public void generateQrCode() {
        repository.generateQrCode(qrCodeResult);
    }
}