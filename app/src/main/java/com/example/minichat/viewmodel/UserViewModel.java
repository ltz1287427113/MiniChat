package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.repository.UserRepository;

import java.io.File;

public class UserViewModel extends AndroidViewModel {

    private UserRepository repository;
    private MutableLiveData<String> updateResult = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        // 初始化 Repository
        repository = new UserRepository(application);
    }

    public LiveData<String> getUpdateResult() {
        return updateResult;
    }

    /**
     * [修改] 更新用户信息
     */
    public void updateUser(String nickname, String avatarUrl) {
        // 这里的 repository.updateUser 签名没变，所以不用改
        repository.updateUser(nickname, avatarUrl, updateResult);
    }
}